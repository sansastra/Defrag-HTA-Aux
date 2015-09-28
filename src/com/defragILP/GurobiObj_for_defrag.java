/**
 * Created by Sandeep on 18.08.2015.
 */
package com.defragILP;
import gurobi.*;

import java.util.ArrayList;

public class GurobiObj_for_defrag {
    // This ILP model minimizes the Maximum Link Utilization
    ArrayList<Integer> maxSlotList;

    Parameter_Provider_for_ILP param;
   // int[][] cap;
    double acceptable_gap = 0.01;

    public GurobiObj_for_defrag(Parameter_Provider_for_ILP parameters){
        this.param = parameters;
    //    this.cap = capacities.capacity;
    }

    // Simple function to determine the MIP gap
    private static double gap(GRBModel model) throws GRBException {
        if (model.get(GRB.IntAttr.SolCount) == 0)
            return 111111.111;
        if (model.get(GRB.DoubleAttr.ObjBound) < 0.0001)
            return 0.0001;
        return Math.abs(model.get(GRB.DoubleAttr.ObjBound) -
                model.get(GRB.DoubleAttr.ObjVal)) /
                Math.abs(model.get(GRB.DoubleAttr.ObjVal));
    }


    boolean minimize_utilization_cost(){
        boolean return_value = false;

        try {
            GRBEnv    env   = new GRBEnv("Traffic_Engineering_for_min_of_util_cost.log");
            GRBModel  model = new GRBModel(env);
//			model.set(GRB.IntAttr.ModelSense, -1);	// -1 maximizes the objective function
//			model.getEnv().set(GRB.DoubleParam.MIPGap, 0.03);
            //model.getEnv().set(GRB.IntParam.Threads, 6);
            model.getEnv().set(GRB.DoubleParam.TimeLimit, 1000);

            ////////////////////////////////////
            // Create variables
            ////////////////////////////////////

            GRBVar[][] R = new GRBVar[param.MPLS_P][param.number_of_slots];				// R[p] is true if path p is in use
            GRBVar[][] R_max = new GRBVar[param.MPLS_P][param.number_of_slots];				// integer to define largest used slot for demand f
            GRBVar[] max_index_of_used_slots = new GRBVar[param.L];				// maximum number of used slots on all links

            //max_number_of_used_slots = model.addVar(0, param.number_of_slots, 1, GRB.INTEGER, "max_number_of_used_slots");
            int varcnt=1;
            System.out.print("Generating variable i_max[l], ");
            for (int l=0; l<param.L; l++){
                max_index_of_used_slots[l]= model.addVar(0, param.number_of_slots-1, 1, GRB.INTEGER, "largest used slots on link_" + l );
                varcnt++;
                }

            System.out.print("Generating variable R[p][s], ");
            for (int p=0; p<param.MPLS_P; p++){
                for (int s=0; s<param.number_of_slots;s++) { //new
                    R[p][s]=model.addVar(0, 1, 0, GRB.BINARY, "routing_via_path_" + p + "over starting slot" + s);
                    varcnt++;
                }
            }
            System.out.print("Generating variables for largest used slots: ");
            for (int p=0; p<param.MPLS_P; p++){
                for (int s=0; s<param.number_of_slots;s++) { //new
                    R_max[p][s]=model.addVar(0, 1, 0, GRB.BINARY, "routing_via_path_" + p + "over largest slot" + s);
                    varcnt++;
                }
            }
            System.out.println(varcnt + " Variables.");

            // Integrate new variables
            model.update();
            GRBLinExpr expr1,expr2;





            ////////////////////////////////////
            // Constraints
            ////////////////////////////////////

            int constrCnt;

            constrCnt=0;

            // single path routing constraint
            System.out.print("Generating constraints 0:");
            for (int f=0; f<param.F; f++) {

                expr1 = new GRBLinExpr();
                for (int p = 0; p < param.MPLS_P; p++) {
                    if (param.fitting[f][p]) {
                        for (int s = 0; s < param.number_of_slots; s++)
                            expr1.addTerm(1, R_max[p][s]); //
                    }
                }
                    model.addConstr(expr1, GRB.EQUAL, 1, "single path routing for flow " + f);

                    constrCnt ++;

            }
            System.out.println(constrCnt + " done.");
     //bandwidth satisfaction constraint
            System.out.print("Generating constraints new:");
            for (int f=0; f<param.F; f++) {

                expr1 = new GRBLinExpr();
                for (int p = 0; p < param.MPLS_P; p++) {
                    if (param.fitting[f][p]) {
                        for (int s = 0; s < param.number_of_slots; s++)
                            expr1.addTerm(1, R[p][s]); //
                    }
                }
                model.addConstr(expr1, GRB.EQUAL, param.Demand[f], "single path routing for flow " + f);

                constrCnt ++;

            }
            System.out.println(constrCnt + " done.");


// demand satisfaction constraint
           System.out.print("Generating constraints 1:");
            for (int p = 0; p < param.MPLS_P; p++) {
                int f = param.flow_of_path_p[p];
  //              System.out.println("traffic for path "+ p+ " is "+param.Demand[f]);
                for (int s=0; s<param.number_of_slots; s++) {
                    expr1 = new GRBLinExpr();
                    for (int z = 0; z < param.Demand[f]; z++) {
                        if (s - z >= 0)
                            expr1.addTerm(1, R[p][s - z]);
                    }
                    expr2 = new GRBLinExpr();
                    expr2.addTerm(param.Demand[f], R_max[p][s]);
                    model.addConstr(expr1, GRB.GREATER_EQUAL, expr2, "demand satisfaction constraint for flow" + f);
                        constrCnt++;
                }
            }


            System.out.println(constrCnt + " done.");




// single slot usage constraint
            System.out.print("Generating constraints 2:");
            for (int s=0; s<param.number_of_slots; s++) {
                for (int l=0; l<param.L; l++) {
                    expr1 = new GRBLinExpr();
                    for (int p = 0; p < param.MPLS_P; p++) {
                        if (param.traversing[p][l])
                            expr1.addTerm(1, R[p][s]); //
                    }
                    model.addConstr(expr1, GRB.LESS_EQUAL, 1, "slot"+s+"is used for link"+l);
                    constrCnt ++;

                }
            }
            System.out.println(constrCnt + " done.");

// maximum index determination constraint

            System.out.print("Generating constraints 3:");
            for(int s=0; s<param.number_of_slots; s++){
                for (int l=0; l<param.L; l++){
                    for (int p=0; p<param.MPLS_P; p++) {
                        expr1 = new GRBLinExpr();
                        if (param.traversing[p][l]){
                            expr1.addTerm(s, R_max[p][s]);
                            model.addConstr(expr1, GRB.LESS_EQUAL, max_index_of_used_slots[l], "maximum index on link" + l + "is" + max_index_of_used_slots[l]);
                            constrCnt++;
                        }
                    }
                }
            }
            System.out.println(constrCnt + " done.");




            System.out.println("\r\nStarting Optimization.");

            // Optimize model
            model.optimize();
            System.out.println("The remaining gap is " + gap(model));
            if (gap(model) < acceptable_gap)
                return_value = true;

            // Result:

            System.out.println("MPLS Routing:");
            for (int f=0; f<param.F; f++){
              //  System.out.print("Flow " + f + " from " + param.nodenames[param.src_of_flow[f]] + " to " + param.nodenames[param.dst_of_flow[f]] + ": ");
                for (int p=0; p<param.MPLS_P; p++){
                    if (param.fitting[f][p]) {
                        for (int s = 0; s < param.number_of_slots; s++)
                            if (R[p][s].get(GRB.DoubleAttr.X) > 0.5) {
                                   System.out.print("(Flow number " + param.flow_of_path_p[p] + ")");
                                for (int l = 0; l < param.L; l++)
                                    if (param.traversing[p][l])
                                        System.out.print("(" + param.nodelist.get(param.src_of_link[l]) + "," + param.nodelist.get(param.dst_of_link[l]) + ")Slot " + s);
                                if(R_max[p][s].get(GRB.DoubleAttr.X) > 0.5){
                                    maxSlotList.add(f,s);
                                    System.out.println("(max used slot for flow " + param.flow_of_path_p[p]+" is slot"+ s);
                                }
                            }
                    }
                }


                System.out.println();
            }

            System.out.println("maximum slot index per link:");
            int max_utilized_index =0 ;
            for (int l=0; l<param.L; l++){
                int max_index_slot  = (int) max_index_of_used_slots[l].get(GRB.DoubleAttr.X);

                System.out.println("Maximum slot index used on link " + param.nodelist.get(param.src_of_link[l]) + "-" + param.nodelist.get(param.dst_of_link[l])
                        + " is  " + max_index_slot);
                if(max_index_slot >= max_utilized_index )
                    max_utilized_index = max_index_slot;
            }
            System.out.println();
            System.out.println("Maximum slot Utilization: " + max_utilized_index);

            model.dispose();
            env.dispose();
        }

        catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". "+ e.getMessage());
        }

        return return_value;
    }

    ArrayList<Integer> getMaxIndexSlotForAllDemand(){return maxSlotList;}
 }
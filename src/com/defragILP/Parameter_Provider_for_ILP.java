package com.defragILP;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.*;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.inputdata.InputParameters;
import com.graph.path.PathElement;
import com.inputdata.reader.ImportTopologyFromSNDFile;
import com.launcher.Launcher;
import com.launcher.SimulatorParameters;
import com.auxiliarygraph.elements.Path;
import com.auxiliarygraph.elements.FiberLink;

import java.util.*;

/**
 * Created by Sandeep on 9/18/2015.
 */

public class Parameter_Provider_for_ILP {

        // Nodes
        int N;
    Set<String> nodenames;
    List<String>nodelist;
    Set<VertexElement> nodeElementsSet;
    List<VertexElement> nodeElementsList;
    Set<EdgeElement> links;
    Map<Double, Connection> connectioTomap;
    //    int N;					// Number of nodes
     //   String[] nodenames;		// Names of the nodes according to the SNDlib file

        // Flows
        int F;					// Number of flows
        int[] Demand;			// Traffic demand of the flows
        int[] src_of_flow;		// src_of_flow[f] stores the node number of the source node of flow f
        int[] dst_of_flow;		// dst_of_flow[f] stores the node number of the destination node of flow f
        boolean[][] src_f;		// src_f[f][n] is true if flow f has node n as source node
        boolean[][] dst_f;		// dst_f[f][n] is true if flow f has node n as destination node
        int[][] flowNo;			// flowNo[s][d] stores the number of the flow from s to d, or -1 in case that flow doesn't exist

        // Links
        int L;					// Number of all ***directed*** OSPF and SDN links (i.e., 2 * number of bidirectional links)
        //	int[] Capacity;			// Capacity of the links (in terms of maximum number of Slots)
        int[] src_of_link;		// src_of_link[l] stores the node number of the source node of link l
        int[] dst_of_link;		// dst_of_link[l] stores the node number of the destination node of link l
        boolean[][] src_l;		// src_p[l][n] is true if link l has node n as source node
        boolean[][] dst_l;		// dst_p[l][n] is true if link l has node n as destination node
        int[][] linkNo;		// linkNo[i][j] stores the link number of link i-j, or -1 in case link i-j doesn't exist
        int number_of_slots;


        // Routing
        int MPLS_P;							// Total number of paths
        boolean[][] fitting;			// MPLS_fitting[f][p] is true if flow f can be routed via path p
        boolean[][] traversing;		// MPLS_traversing[p][l] is true if path p traverses link l
        int[] flow_of_path_p;			// flow_of_MPLS_path_p[p] stores the flow number for path p

        // Instances from other Objects
     //   Real_Traffic demand;
     //   SNDlib topologyObj;
      //  LinkedList<Path> all_paths;
        //LinkCapacities capacities;
      //  K_Shortest_Paths k_path;

        public Parameter_Provider_for_ILP(){
          //  this.demand = demand;
           // this.topologyObj = topologyObj;
          //  this.nodenames = topologyObj.get_nodenames();
            //	this.capacities = capacities;
         //   this.k_path = k_path;
            // Nodes
            nodenames = InputParameters.getSetOfVertexIDSets();
            nodeElementsSet =InputParameters.getSetOfVertices();
            N = nodenames.size();
            nodelist = new ArrayList<>(nodenames);
            nodeElementsList = new ArrayList<>(nodeElementsSet);
            // Flows
            F = N * (N-1);
            Demand = new int[F];
            flowNo = new int[N][N];
            src_of_flow = new int[F];
            dst_of_flow = new int[F];
            src_f = new boolean[F][N];
            dst_f = new boolean[F][N];
            int f=0;
            int s =0;
            int d =0;
            VertexElement ver1 =nodeElementsList.get(0), ver2=nodeElementsList.get(0);
            List<LightPath> lightpaths;
            //InputParameters.getIfConnectiongEdge(node,node1)
            for (String node : nodelist) {
                for (String node1 : nodelist) {
                    if (!node.equals(node1)) {
                        int demand =0;
                         for (int i = 0; i < N; i++) {
                            if (nodeElementsList.get(i).getVertexID().equals(node))
                                ver1 = nodeElementsList.get(i);
                            if (nodeElementsList.get(i).getVertexID().equals(node1))
                                ver2 = nodeElementsList.get(i);
                        }
                        lightpaths= NetworkState.getListOfLightPaths(ver1, ver2);
                        int num= lightpaths.size();
                        for (int i = 0; i < num; i++) {
                            connectioTomap= lightpaths.get(i).getConnectionMap();
                           for (Map.Entry<Double,Connection> entry : connectioTomap.entrySet())
                              demand += entry.getValue().getBw();
                        }
                        Demand[f] = demand;
                        flowNo[s][d] = f;
                        src_of_flow[f] = s;
                        dst_of_flow[f] = d;
                        src_f[f][s] = true;
                        dst_f[f][d] = true;
                        f++;
                    } else
                        flowNo[s][d] = -1;
                    d++;
                }
                d = 0;
                s++;
            }
            // Links
            links = InputParameters.getSetOfEdges();
            L = links.size();
            List<EdgeElement> linklist = new ArrayList<>(links);
            number_of_slots = NetworkState.getFiberLink(linklist.get(0).getEdgeID()).getTotalNumberOfMiniGrids();
            //	Capacity = new int[L];
            src_of_link = new int[L];
            dst_of_link = new int[L];
            src_l = new boolean[L][N];
            dst_l = new boolean[L][N];
            linkNo = new int[N][N];
            int l = 0;
            s =0;
            d =0;
            for (int i = 0; i <N; i++) {
                for (int j = 0; j <N; j++) {
                    linkNo[i][j]=-1;
                }
            }
            for (EdgeElement edge:linklist) {
                for (int i = 0; i < N; i++) {
                    if (edge.getSourceVertex().getVertexID().equals(nodelist.get(i))) {
                        s = i;
                        src_of_link[l] = s;
                    }
                    else if(edge.getDestinationVertex().getVertexID().equals(nodelist.get(i))){
                        d = i;
                        dst_of_link[l] = d;
                    }
                }
                linkNo[s][d] = l;
                l++;
            }
            // Demands of all flows

            // MPLS Paths
            List<Path> listOfpaths;
            //Set<PathElement> setOfPaths = SimulatorParameters.setPaths();
            listOfpaths= NetworkState.getListOfPaths();
            MPLS_P = listOfpaths.size();
            fitting = new boolean[F][MPLS_P];
            traversing = new boolean[MPLS_P][L];
            flow_of_path_p = new int[MPLS_P];
            String src, dst;
            int flowno=-1,pathNo=0;
            for (Path p: listOfpaths){
                src = p.getPathElement().getSourceID();
                s = nodelist.indexOf(src);
                dst = p.getPathElement().getDestinationID();
                d = nodelist.indexOf(dst);
                flowno = flowNo[s][d];
                fitting[flowno][pathNo]=true;
                flow_of_path_p[pathNo] = flowno;
                List<EdgeElement> ee= p.getPathElement().getTraversedEdges();
                int size = ee.size();
                for (int i = 0; i < size; i++) {
                    String edgeid = ee.get(i).getEdgeID();
                    for (int j = 0; j < L; j++) {
                        if (linklist.get(j).getEdgeID().equals(edgeid))
                            traversing[pathNo][j]=true;
                        else
                            traversing[pathNo][j]=false;
                    }
                }
                pathNo++;
            }
        }


}
     /*       for (RoutingTreeNode s : k_path.nodes)
                for (LinkedList<LinkedList<RoutingTreeNode>> paths_from_s_to_d : s.k_shortest_paths.values())
                    MPLS_P += paths_from_s_to_d.size();
            MPLS_fitting = new boolean[F][MPLS_P];
            MPLS_traversing = new boolean[MPLS_P][L];
            flow_of_MPLS_path_p = new int[MPLS_P];
            int p = 0;
            for (RoutingTreeNode s : k_path.nodes)
                for (Integer d : s.k_shortest_paths.keySet()){
                    LinkedList<LinkedList<RoutingTreeNode>> the_paths_from_s_to_d = s.k_shortest_paths.get(d);
                    for (LinkedList<RoutingTreeNode> next_path : the_paths_from_s_to_d){
                        flow_of_MPLS_path_p[p] = flowNo[s.number][d];
                        MPLS_fitting[flowNo[s.number][d]][p] = true;
                        for (int e=0; e<next_path.size()-1; e++){
                            int i = next_path.get(e).number;
                            int j = next_path.get(e+1).number;
                            MPLS_traversing[p][linkNo[i][j]] = true;
                        }
                        p++;
                    }
                } */
     //   }
  //  }

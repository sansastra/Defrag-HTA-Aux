package com.defragILP;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.inputdata.InputParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class MainDefragClass {
	
	private static int N;
	Set<String> nodenames;
    Set<EdgeElement> links;
	//private static Real_Traffic demand;
	private static Parameter_Provider_for_ILP parameters;
	private static GurobiObj_for_defrag gurobi_defrag_Obj;
	//private static K_Shortest_Paths k_paths_Obj;
	public MainDefragClass() {
        //	k_paths_Obj = new K_Shortest_Paths(topologyObj, 3);
        //	demand = new Real_Traffic(N); // initialize constructor
        //	demand.generate_random_traffic(4, seed); // generate traffic matrix



	parameters = new Parameter_Provider_for_ILP();
	gurobi_defrag_Obj = new GurobiObj_for_defrag(parameters);
	gurobi_defrag_Obj.minimize_utilization_cost();
    }
/* //private static SNDlib topologyObj;
//	private static OSPF_Link_Metrics linkmetrics;
//	private static Routing initial_ospf_routing;
	private static Real_Traffic demand;
	//private static LinkLoads linkloadObj;
	//private static LinkCapacities capacities;
   private static long seed = 123456789;
	private static String[] sndfile = {
			"atlanta-15nodes.txt",		// 0
			"cost266-37nodes.txt",		// 1
			"janos-us-22nodes.txt",		// 2
			"polska-12nodes.txt",		// 3
			"ring-6nodes.txt",			// 4
			"ring-10nodes.txt",			// 5
			"ta2-65nodes.txt",			// 6
			"nobel-eu-28nodes.txt",		// 7
			"3segments-9nodes.txt",		// 8
			"4nodes_square.txt",        // 9
			"2nodes_link.txt"           // 10
	};

	
	public static void main(String[] args) {
		topologyObj = new SNDlib(sndfile[9]);
		if (!topologyObj.readSuccess())
			System.exit(0);
		N = topologyObj.number_of_nodes();
		k_paths_Obj = new K_Shortest_Paths(topologyObj, 3); // list of all to all max K=7 shortest paths. this class uses routingTreeNode class
		linkmetrics = new OSPF_Link_Metrics(topologyObj); // call constructor; and contains make_random_link_weights method
		linkmetrics.make_random_link_weights(); // assigns link weights (random + 100)
		initial_ospf_routing = new Routing(topologyObj); // gets source and destination for all links
		initial_ospf_routing.compute_SPF_Routing(linkmetrics); //compute shortest paths
		demand = new Real_Traffic(N); // initialize constructor
		demand.generate_random_traffic(4, seed); // generate traffic matrix
		parameters = new Parameter_Provider_for_TE_Model_1(demand, topologyObj, k_paths_Obj);
		gurobi_defrag_Obj = new GurobiObj_for_defrag(parameters);
		gurobi_defrag_Obj.minimize_utilization_cost();
	}
*/
}

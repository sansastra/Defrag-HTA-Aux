package com.defragILP ;
import java.util.LinkedList;

public class Parameter_Provider_for_TE_Model_1 {

	// Nodes
	int N;					// Number of nodes
	String[] nodenames;		// Names of the nodes according to the SNDlib file

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
	int[][] linkNo;			// linkNo[i][j] stores the link number of link i-j, or -1 in case link i-j doesn't exist
	int number_of_slots = 10;


	// Routing
	int MPLS_P;							// Total number of paths
	boolean[][] MPLS_fitting;			// MPLS_fitting[f][p] is true if flow f can be routed via path p
	boolean[][] MPLS_traversing;		// MPLS_traversing[p][l] is true if path p traverses link l
	int[] flow_of_MPLS_path_p;			// flow_of_MPLS_path_p[p] stores the flow number for path p

	// Instances from other Objects
	Real_Traffic demand;
	SNDlib topologyObj; 
	LinkedList<Path> all_paths;
	//LinkCapacities capacities;
	K_Shortest_Paths k_path;

	public Parameter_Provider_for_TE_Model_1(Real_Traffic demand, SNDlib topologyObj, K_Shortest_Paths k_path){
		this.demand = demand;
		this.topologyObj = topologyObj;
		this.nodenames = topologyObj.get_nodenames();
	//	this.capacities = capacities;
		this.k_path = k_path;

		// Nodes
		N = topologyObj.number_of_nodes();

		// Flows
		F = N * (N-1);
		Demand = new int[F];
		flowNo = new int[N][N];
		src_of_flow = new int[F];
		dst_of_flow = new int[F];
		src_f = new boolean[F][N];
		dst_f = new boolean[F][N];
		int f=0;
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d){
					Demand[f] = demand.get_original_traffic(s, d);
					flowNo[s][d] = f;
					src_of_flow[f] = s;
					dst_of_flow[f] = d;
					src_f[f][s] = true;
					dst_f[f][d] = true;
					f++;
				}
				else
					flowNo[s][d] = -1;

		// Links
		L = 2 * topologyObj.number_of_links();
	//	Capacity = new int[L];
		src_of_link = new int[L];
		dst_of_link = new int[L];
		src_l = new boolean[L][N];
		dst_l = new boolean[L][N];
		int[][] linkNo = new int[N][N];
		int l = 0;
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (topologyObj.connected(i,j)){
		//			Capacity[l] = capacities.capacity[i][j];
					linkNo[i][j] = l;
					src_of_link[l] = i;
					dst_of_link[l] = j;
					src_l[l][i] = true;
					dst_l[l][j] = true;
					l++;
				}
				else
					linkNo[i][j] = -1;

		// MPLS Paths
		MPLS_P = 0;
		for (RoutingTreeNode s : k_path.nodes)
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
			}
	}
}
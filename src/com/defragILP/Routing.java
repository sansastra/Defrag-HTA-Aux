package com.defragILP ;
import java.util.LinkedList;


public class Routing {

	private int N;									// number of nodes
	private int L;									// number of links
	private String[] nodenames;
	private boolean[][] adjacent;					// topology
	private Path[][] path;							// result array
	private int pathcount;							// total number of paths in this routing object
	private boolean[][][][] R;						// R(s,d,i,j) true if flow from s to d uses link (i,j)
	private LinkedList<Integer> link_source;		// to get the source node of a link from its link number 
	private LinkedList<Integer> link_destination;	// to get the destination node of a link from its link number
	
	public Routing(SNDlib topology){
		this.N = topology.number_of_nodes();
		this.adjacent = topology.connected();
		this.L = topology.number_of_links();
		this.nodenames = topology.get_nodenames();
		link_source = new LinkedList<Integer>();
		link_destination = new LinkedList<Integer>();
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (this.adjacent[i][j]){
					link_source.add(i);
					link_destination.add(j);
				}
	}
	
	public Routing(){
	}
	
	// for test purposes only:
	public Routing(boolean[][][][] r, boolean[][] adj){
		this.R = r;
		this.adjacent = adj;
	}
	
	public void compute_SPF_Routing(OSPF_Link_Metrics ospf){
		path = new Path[N][N];
		LinkedList<Path> previousPaths;
		LinkedList<Path> currentPaths = new LinkedList<Path>();
		int missingPaths = N*(N-1);
		
		// make all one hop paths
		for (int a=0; a<N; a++)
			for (int b=0; b<N; b++)
				if (this.adjacent[a][b]){
					Path newPath = new Path(a, b, ospf.linkmetric[a][b]);
					path[a][b] = newPath;
					currentPaths.add(newPath);
					missingPaths--;
				}
		
		// compute all other paths
		int current_hop_count = 2;
		while(current_hop_count < 20){
			current_hop_count++;
			previousPaths = currentPaths;
			currentPaths = new LinkedList<Path>();
			for (int s=0; s<N; s++)
				for (int d=0; d<N; d++){
					if (s==d || this.path[s][d]!=null)
						continue;
					LinkedList<Path> candidate_paths_to_d = new LinkedList<Path>();
					for (Path p : previousPaths)
						if (p.getDestination()==d && this.adjacent[s][p.getSource()])
							candidate_paths_to_d.add(p.extend_to_new_source(s, ospf.linkmetric[s][p.getSource()]));
//					System.out.println("candidate_paths_to_d.size() for " + s + " to " + d + ": " + candidate_paths_to_d.size());
					if (candidate_paths_to_d.size()==0)
						continue;
					Path bestPath = candidate_paths_to_d.getFirst();
					for (Path p : candidate_paths_to_d)
						if (p.getCost() < bestPath.getCost())
							bestPath = p;
					this.path[s][d] = bestPath;
					currentPaths.add(bestPath);
					missingPaths--;
				}
		}
		this.pathcount = N*(N-1)-missingPaths;
		if (missingPaths>0)
			System.out.println("Number of Paths: " + this.pathcount + ", number of node pairs without a routing path: " + missingPaths);
		this.compute_boolean_routing_parameter();
	}
	
	public void printRouting(){
		System.out.println("\r\n Routing ");
		System.out.println("=========");
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (this.path[s][d] != null)
					System.out.println("From "+s+" to "+d+": "+this.path[s][d].toString());
	}
	
	public void printRouting_withNames(){
		System.out.println("\r\n Routing ");
		System.out.println("=========");
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (this.path[s][d] != null)
					System.out.println("From "+this.nodenames[s]+" to "+this.nodenames[d]+": "+this.path[s][d].toString(nodenames));
	}
	
	public void print_routing_matrix(){
		System.out.println("\r\nRouting Matrix:");
		String firstline = "         ";
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d)
					firstline += String.format("%7s", s+"-"+d);
		System.out.println(firstline);
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (this.adjacent[i][j]){
					System.out.print(String.format("%9s", "("+i+","+j+")"));
					for (int s=0; s<N; s++)
						for (int d=0; d<N; d++)
							if (s!=d)
								if (this.R[s][d][i][j])
									System.out.print(String.format("%7s", 1));
								else
									System.out.print(String.format("%7s", 0));
					System.out.println();
				}
	}
	
	private void compute_boolean_routing_parameter(){
		this.R = new boolean[N][N][N][N];
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d)
					for (int i=0; i<N; i++)
						for (int j=0; j<N; j++)
							if (this.path[s][d]!=null && this.path[s][d].contains_link(i, j))
								R[s][d][i][j] = true;
	}
	
	public void print_routing_for_the_lab(SNDlib topology, Real_Traffic traffic){
		boolean entryexists[][] = new boolean[N][N];
		String[] forwardingtable = new String[N];
		for (int n=0; n<N; n++)
			forwardingtable[n] = "Router " + topology.nodename(n);
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d)
					for (int i=0; i<N; i++)
						if (i!=d)
							for (int j=0; j<N; j++)
								if (j!=i && j!=s)
									if (this.R[s][d][i][j])
										if (!entryexists[i][d]){
											forwardingtable[i] += "\r\nto " + topology.nodename(d)
													+ " forward_to_link: " + topology.nodename(i)
													+ "-" + topology.nodename(j);
											entryexists[i][d] = true;
										}
		for (int n=0; n<N; n++)
			System.out.println("\r\n" + forwardingtable[n]);
		
		// print for each link the flows on it
		String[][] flowlist = new String[N][N];
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (this.adjacent[i][j])
					flowlist[i][j] = "Link " + topology.nodename(i) + "-" + topology.nodename(j);
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d)
					for (int i=0; i<N; i++)
						if (i!=d)
							for (int j=0; j<N; j++)
								if (j!=i && j!=s)
									if (this.R[s][d][i][j])
										flowlist[i][j] += "\r\n" + topology.nodename(s) + "-"
												+ topology.nodename(d) + " " + traffic.get_original_traffic(s, d);
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				if (this.adjacent[i][j])
					System.out.println("\r\n" + flowlist[i][j]);
		
		boolean[][] warsaw_flows = new boolean[N][N];
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d)
					for (int i=0; i<N; i++)
						if (i!=d)
							for (int j=0; j<N; j++)
								if (j!=i && j!=s)
									if (this.R[s][d][i][j])
										if (s==10 || d==10 || i==10 || j==10)
											warsaw_flows[s][d] = true;
		System.out.println("\r\nFlows from/to/via Warsaw:");
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (warsaw_flows[s][d])
					System.out.println(topology.nodename(s) + "-" + topology.nodename(d)
							+ " " + traffic.get_original_traffic(s, d));

	}
	
	public boolean isConnected(int i, int j){
		return this.adjacent[i][j];
	}
	
	public int source_of_link(int l){
		return this.link_source.get(l);
	}
	
	public int destination_of_link(int l){
		return this.link_destination.get(l);
	}
	
	public boolean flow_is_routed_via_link(int s, int d, int i, int j){
		return this.R[s][d][i][j];
	}
	
	public int number_of_nodes(){
		return this.N;
	}
	
	public int number_of_links(){
		return this.L;
	}
	
 	public int get_number_of_paths() {
		return this.pathcount;
	}
 	
	public Path get_Path(int s, int d) {
		return path[s][d];
	}

 	public double get_path_metric(int s, int d){
 		return this.path[s][d].getCost();
 	}
	
 	public static void main(String[] args) {
		
		// test case
		//
		//			#######      10.01 >      #######      10.05 >      #######
		//			## 0 ##------------------>## 1 ##------------------>## 2 ##
		//			#######     < 10.56       #######     < 10.23       #######
		//			   |                         |                         |
		//			   |                         |                         |
		//		       |  /\                     |  /\                     |  /\
		//	     10.21 | 10.69             10.20 | 10.11             10.78 | 10.51
		//		   \/  |                     \/  |                     \/  |
		//			   |                         |                         |
		//			   |                         |                         |
		//			#######      10.82 >      #######      10.31 >      #######
		//			## 3 ##------------------>## 4 ##------------------>## 5 ##
		//			#######     < 10.55       #######    < 10.80        #######
		
		Routing bla = new Routing();
		bla.N = 6;
//		boolean[][] x = {
//				{false, true,  false, true,  false, false},
//				{true,  false, true,  false, true,  false},
//				{false, true,  false, false, false, true },
//				{true,  false, false, false, true,  false},
//				{false, true,  false, true,  false, true },
//				{false, false, true,  false, true,  false}	};
		boolean[][] x = {
				{false, true,  false, true,  false, false},
				{true,  false, false, false, true,  false},
				{false, false, false, false, false, false},
				{true,  false, false, false, true,  false},
				{false, true,  false, true,  false, true },
				{false, false, false, false, true,  false}	};

		double NAN = 0.01;	// not a number
		double[][] y = {
				{NAN,   10.01, NAN,   10.21, NAN,   NAN  },
				{10.56, NAN,   NAN,   NAN,   10.20, NAN  },
				{NAN,   NAN,   NAN,   NAN,   NAN,   NAN  },
				{10.69, NAN,   NAN,   NAN,   10.82, NAN  },
				{NAN,   10.11, NAN,   10.55, NAN,   10.31},
				{NAN,   NAN,   NAN,   NAN,   10.80, NAN  }  };
		bla.adjacent = x;
//		bla.linkmetric = y;
//		bla.compute_SPF_Routing();
		bla.printRouting();
		bla.print_routing_matrix();
	}


	
}

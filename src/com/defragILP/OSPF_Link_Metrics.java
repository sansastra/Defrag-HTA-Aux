package com.defragILP;
import java.util.Random;


public class OSPF_Link_Metrics {
	
	SNDlib topology;
	double[][] linkmetric;
	int N;
	
	public OSPF_Link_Metrics(SNDlib topology){
		this.topology = topology;
		this.N = topology.number_of_nodes();
	}
	
	public void make_random_link_weights(){
		linkmetric = new double[N][N];
		//Random r = new Random(seed);
		for (int s=0; s<topology.number_of_nodes(); s++)
			for (int d=s; d<topology.number_of_nodes(); d++)
				if (topology.connected(s, d)){
					this.linkmetric[s][d] =  100; //r.nextDouble() + 100;
					this.linkmetric[d][s] = this.linkmetric[s][d];
				}
				else{
					this.linkmetric[s][d] = 999999999999.9999999;
					this.linkmetric[d][s] = 999999999999.9999999;
				}
	}
	
	public void make_random_link_weights_but_prefer_sdn_links(long seed){
		linkmetric = new double[N][N];
		Random r = new Random(seed);
		double diff = -2.0;
		for (int s=0; s<N; s++)
			for (int d=s; d<N; d++)
				if (topology.connected(s, d)){
					this.linkmetric[s][d] = r.nextDouble() + 100;
					this.linkmetric[d][s] = this.linkmetric[s][d];
					if (topology.is_a_border_node(s)){
						this.linkmetric[s][d] += diff;
						this.linkmetric[d][s] += diff;
					}
				}
				else{
					this.linkmetric[s][d] = 999999999999.9999999;
					this.linkmetric[d][s] = 999999999999.9999999;
				}
	}
	
	public void make_link_weights_for_the_lab(long seed, SNDlib topology){
		linkmetric = new double[N][N];
		Random r = new Random(seed);
		for (int s=0; s<N; s++)
			for (int d=s; d<N; d++)
				if (topology.connected(s, d)){
					this.linkmetric[s][d] = r.nextDouble() + 100;
					this.linkmetric[d][s] = this.linkmetric[s][d];
					boolean bypass =
							   (topology.nodename(s).equals("Poznan")   && topology.nodename(d).equals("Szczecin"))
							|| (topology.nodename(s).equals("Gdansk")   && topology.nodename(d).equals("Bialystok"))
							|| (topology.nodename(s).equals("Katowice") && topology.nodename(d).equals("Lodz"));
					if (bypass){
						this.linkmetric[s][d] = r.nextDouble() + 99;
						this.linkmetric[d][s] = this.linkmetric[s][d];
					}
				}
				else{
					this.linkmetric[s][d] = 999999999999.9999999;
					this.linkmetric[d][s] = 999999999999.9999999;
				}
	}

}

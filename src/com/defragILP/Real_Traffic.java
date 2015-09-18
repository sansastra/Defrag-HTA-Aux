package com.defragILP ;
import java.util.Random;


public class Real_Traffic {
	
	private int N;
	private int[][] original_traffic;

	
	public Real_Traffic(int N){
		this.N = N;
		this.original_traffic = new int[N][N];
	}
	
	public void generate_random_traffic(int max, long seed){
		Random r = new Random(seed);
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d) {
					this.original_traffic[s][d] = r.nextInt(max) + 1;
					System.out.println("traffic for source "+s+ "and destination "+ d+ "is: "+this.original_traffic[s][d]);
				}
				else
					this.original_traffic[s][d] = 0;
	}

	public void generate_uniform_traffic(int value){
		for (int s=0; s<N; s++)
			for (int d=0; d<N; d++)
				if (s!=d)
					this.original_traffic[s][d] = value;
				else
					this.original_traffic[s][d] = 0;
	}

	public void print_traffic_matrix(){
		System.out.println("Traffic Matrix:");
		for (int s=0; s<N; s++){
			for (int d=0; d<N; d++)
				System.out.print(String.format("%5s", this.original_traffic[s][d]));
			System.out.println();
		}
	}
	
	public void use_Alexanders_traffic(){
		int[][] alexandersTraffic = 
			{	{   0,  100,  600,  300,  600,  400,  700, 1500, 1300, 1400, 1200, 1400},
				{ 900,    0, 4200, 2300,  200,  500,  400,  800,  200,  900,  700, 2900},
				{ 200,  700,    0,  700, 3100, 3900,  800, 1100, 2700, 3100,  200,  900},
				{1400, 2300, 3900,    0,  100, 2000,  900, 1100, 1100,  900, 2600,  500},
				{ 400,  800,  200,  200,    0,  300, 4500, 2200,  500,  100,  800, 2200},
				{1200,  800, 2000, 2500, 2700,    0,  400, 1600,  600, 1500, 2400,  500},
				{1500,  200, 2900,  800, 1700,  300,    0,  200,  400, 1100,  500,  400},
				{ 100,  400, 1100, 1300,  200,  700,  900,    0, 2100,  900, 1100, 2000},
				{ 500,  500,  100,  200, 2500,  900, 1100, 3100,    0, 1900, 1000,  400},
				{1100, 1500,  400, 1500, 2100, 1400, 1700,  200, 1700,    0,  400,  800},
				{ 100, 1800,  700,  200,  200, 2400,  700,  700,  200,  600,    0,  900},
				{2200,  100,  200,  900, 1000,  600,  600,  200, 1000, 1300,  700,    0}};
		this.original_traffic = alexandersTraffic;
	}
	
	public int get_original_traffic(int s, int d) {
		return this.original_traffic[s][d];
	}

}

package com.defragILP ;
import java.util.LinkedList;


public class Path {

	private LinkedList<Integer> nodes;
	private int source;
	private int destination;
	private int pathlength;
	private double cost;
	
	
	
	// constructor for one-hop-paths
	public Path(int s, int d, double cost){
		this.nodes = new LinkedList<Integer>();
		this.nodes.addFirst(s);
		this.nodes.addLast(d);
		this.source = s;
		this.destination = d;
		this.pathlength = 2;
		this.cost = cost;
	}
	
	private Path(LinkedList<Integer> nodelist, double cost){
		this.nodes = nodelist;
		this.source = nodelist.getFirst();
		this.destination = nodelist.getLast();
		this.pathlength = nodelist.size();
		this.cost = cost;
	}
	
	public int getSource(){
		return this.source;
	}
	
	public int getDestination(){
		return this.destination;
	}
	
	public int length(){
		return this.pathlength;
	}
	
	public LinkedList<Integer> getNodeList(){
		LinkedList<Integer> new_nodelist = new LinkedList<Integer>();
		new_nodelist.addAll(this.nodes);
		return new_nodelist;
	}
	
	public Path extend_to_new_source(int new_source, double additional_cost){
		LinkedList<Integer> extended_nodelist = this.getNodeList();
		extended_nodelist.addFirst(new_source);
		double new_cost = this.cost + additional_cost;
		Path newPath = new Path(extended_nodelist, new_cost);
		return newPath;
	}
	
	public double getCost(){
		return this.cost;
	}
	
	public String toString(){
		String out = "" + this.source;
		for (int i=1; i<this.pathlength; i++)
			out += "-" + nodes.get(i);
		return out;
	}
	
	public String toString(String[] nodenames){
		String out = "" + nodenames[this.source];
		for (int i=1; i<this.pathlength; i++)
			out += " - " + nodenames[nodes.get(i)];
		return out;
	}
	
	public boolean contains_link(int i, int j){
		int ii = nodes.indexOf(i);
		int jj = nodes.indexOf(j);
		if (ii<0 || jj<0 || jj-ii!=1)
			return false;
		return true;
	}
	
	
}

package com.defragILP ;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class RoutingTreeNode {
	int number;
	RoutingTreeNode father;
	boolean is_root;
	LinkedList<RoutingTreeNode> children;
	LinkedList<RoutingTreeNode> neighbors;
	LinkedList<RoutingTreeNode> leafs;
	LinkedList<RoutingTreeNode> path_from_the_root;
	HashMap<Integer, LinkedList<LinkedList<RoutingTreeNode>>> paths;
	HashMap<Integer, LinkedList<LinkedList<RoutingTreeNode>>> k_shortest_paths;
	
	public RoutingTreeNode(int number){
		this.number = number;
		this.children = new LinkedList<RoutingTreeNode>();
		this.neighbors = new LinkedList<RoutingTreeNode>();
		this.is_root = false;
	}
	
	public RoutingTreeNode(int number, LinkedList<RoutingTreeNode> neighbors){
		this.number = number;
		this.children = new LinkedList<RoutingTreeNode>();
		this.neighbors = neighbors;
		this.is_root = false;
	}
	
	public void make_it_root(){
		this.is_root = true;
		this.leafs = new LinkedList<RoutingTreeNode>();
		this.paths = new HashMap<Integer, LinkedList<LinkedList<RoutingTreeNode>>>();
	}
	
	public void add_leaf(RoutingTreeNode leaf){
		if (this.is_root)
			this.leafs.add(leaf);
		else
			this.father.add_leaf(leaf);
	}
	
	private RoutingTreeNode copy(){
		int new_number = 0 + this.number;
		return new RoutingTreeNode(new_number, this.neighbors);
	}
	
	public void attach_all_neighbors(){
		boolean this_is_a_leaf = true;
		for (RoutingTreeNode n : this.neighbors)
			if (!this.has_ancestor(n)){
				attach_node(n);
				this_is_a_leaf = false;
			}
		if (this_is_a_leaf)
			this.add_leaf(this);
	}
	
	private void attach_node(RoutingTreeNode child){
		RoutingTreeNode x = child.copy();
		x.is_root = false;
		x.father = this;
		x.build_path_from_the_root();
		x.children = new LinkedList<RoutingTreeNode>(); 
		this.children.add(x);
		x.attach_all_neighbors();
	}
	
	public boolean equals(RoutingTreeNode node){
		if (node.number == this.number)
			return true;
		return false;
	}
	
	private boolean has_ancestor(RoutingTreeNode new_baby){
		if (new_baby.equals(this))
			return true;
		if (this.is_root)
			return false;
		return this.father.has_ancestor(new_baby);
	}
	
	public String branchString(){
		if (this.is_root)
			return "" + this.number;
		return this.father.branchString() + "-" + this.number;
	}
	
	public String toString(){
		String retString = "All paths from " + this.number + ":\r\n";
		for (RoutingTreeNode n : this.leafs)
			retString += n.branchString() + "\r\n";
		return retString;
	}
	
	public LinkedList<RoutingTreeNode> find_all_offsprings_with_number(int d){
		LinkedList<RoutingTreeNode> theList = new LinkedList<RoutingTreeNode>();
		if (this.number == d)
			return theList;
		for (RoutingTreeNode n : this.children)
			n.go_down_to(d, theList);
		return theList;
	}
	
	public void go_down_to(int d, LinkedList<RoutingTreeNode> nodes_found){
		if (this.number == d){
			nodes_found.add(this);
			return;
		}
		for (RoutingTreeNode n : this.children)
			n.go_down_to(d, nodes_found);
	}
	
	public void build_path_from_the_root(){
		this.path_from_the_root = new LinkedList<RoutingTreeNode>();
		RoutingTreeNode n = this;
		while (!n.is_root){
			this.path_from_the_root.addFirst(n);
			n = n.father;
		}
		this.path_from_the_root.addFirst(n);
	}
	
	public String pathString(){
		String out = "Path:";
		for (RoutingTreeNode i : this.path_from_the_root)
			out += " " + i.number;
		return out;
	}
	
	public void make_paths_to(int d){
		LinkedList<LinkedList<RoutingTreeNode>> list_of_paths = new LinkedList<LinkedList<RoutingTreeNode>>();
		for (RoutingTreeNode i : this.find_all_offsprings_with_number(d))
			list_of_paths.add(i.path_from_the_root);
		this.paths.put(d, list_of_paths);
	}

	public void determine_k_shortest_paths(int k) {
		k_shortest_paths = new HashMap<Integer, LinkedList<LinkedList<RoutingTreeNode>>>();
		for (Integer i : paths.keySet()){
			LinkedList<LinkedList<RoutingTreeNode>> paths_to_i = paths.get(i);
			if (paths_to_i.size() <= k){
				k_shortest_paths.put(i, paths_to_i);
				continue;
			}
			LinkedList<LinkedList<RoutingTreeNode>> ordered_paths_to_i = order_list(paths_to_i);
			for (int t=ordered_paths_to_i.size()-1; t>=k; t--)
				ordered_paths_to_i.removeLast();
			k_shortest_paths.put(i, ordered_paths_to_i);
		}
	}
	
	public LinkedList<LinkedList<RoutingTreeNode>> order_list(LinkedList<LinkedList<RoutingTreeNode>> in){
		Random r = new Random(123123123);
		LinkedList<LinkedList<RoutingTreeNode>> out = new LinkedList<LinkedList<RoutingTreeNode>>();
		int[] pathlength = new int[in.size()];
		for (int p = 0; p  < in.size(); p++)
			pathlength[p] = in.get(p).size();
		int currentpathlength = 0;
		LinkedList<LinkedList<RoutingTreeNode>> candidates = new LinkedList<LinkedList<RoutingTreeNode>>();
		while (out.size() < in.size()){
			candidates.clear();
			for (int p = 0; p  < in.size(); p++)
				if (in.get(p).size() == currentpathlength)
					candidates.add(in.get(p));
			while (candidates.size() > 0)
				out.add(candidates.remove(r.nextInt(candidates.size())));
			currentpathlength++;
		}
		return out;
	}
	
}

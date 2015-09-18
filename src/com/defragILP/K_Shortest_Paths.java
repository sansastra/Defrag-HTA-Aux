package com.defragILP ;
/**
 * Created by Sandeep on 18.08.2015.
 */
import java.util.HashMap;
import java.util.LinkedList;


public class K_Shortest_Paths {
    LinkedList<RoutingTreeNode> nodes;

    public K_Shortest_Paths(SNDlib topology, int k){
        nodes = new LinkedList<RoutingTreeNode>();
        for (int number=0; number<topology.number_of_nodes(); number++){
            RoutingTreeNode nextnode = new RoutingTreeNode(number);
            nextnode.make_it_root();
            nodes.add(nextnode);
        }
        for (RoutingTreeNode i : this.nodes)
            for (RoutingTreeNode j : this.nodes)
                if (topology.connected(i.number, j.number))
                    i.neighbors.add(j);

        // build the depth search tree for each node
        for (RoutingTreeNode i : this.nodes)
            i.attach_all_neighbors();
        for (RoutingTreeNode i : this.nodes)
            for (int d=0; d<topology.number_of_nodes(); d++)
                if (i.number != d)
                    i.make_paths_to(d);
        for (RoutingTreeNode i : this.nodes)
            i.determine_k_shortest_paths(k);
    }

    void printlist(LinkedList<RoutingTreeNode> listObj){
        System.out.print("Path:");
        for (RoutingTreeNode n : listObj)
            System.out.print(" " + n.number);
        System.out.println();
    }

    public static void main(String[] args) {
        SNDlib topologyObj = new SNDlib("3segments-9nodes.txt");
        K_Shortest_Paths bla = new K_Shortest_Paths(topologyObj, 4);

//		for (RoutingTreeNode i : bla.nodes)
//			System.out.println(i.toString());
//		for (RoutingTreeNode i : bla.nodes.get(0).find_all_offsprings_with_number(6))
//			System.out.println(i.pathString());
        for (LinkedList<RoutingTreeNode> n : bla.nodes.get(0).k_shortest_paths.get(6))
            bla.printlist(n);
    }

}

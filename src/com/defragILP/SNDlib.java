package com.defragILP ;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;


public class SNDlib {
	
	private int N;
	private int L;
	private int bordernodes;
	private boolean[][] adjacent;
	private boolean[] is_a_border_node;
	private String[] nodenames;
	private boolean read_success;
	private BitSet is_border_n = new BitSet();
	
	public SNDlib(String filename){
		this.read_success = true;
        System.out.print("Reading "+filename+": ");
        BufferedReader f;
        String line;
        int status = 0;
        this.L = 0;
        List<String> nodes = new LinkedList<String>();
        try{
            f = new BufferedReader(new FileReader(filename));
            // use line = f.readLine() to read all lines:
            while ((line = f.readLine()) != null){
            	line = line.trim();
            	
            	// go to NODES section
            	if (status == 0){
            		if (line.startsWith("NODES"))
            			status = 1;
            		continue;
            	}
            	
            	// read NODES section
            	if (status == 1){
            		if (line.equals(")")){
            			this.N = nodes.size();
            			this.adjacent = new boolean[this.N][this.N];
            			this.is_a_border_node = new boolean[this.N];
            			for (int n=0; n<this.N; n++)
            				if (this.is_border_n.get(n))
            					this.is_a_border_node[n] = true;
            			System.out.print(this.N + " nodes, " + this.bordernodes + " bordernodes, ");
            			this.nodenames = new String[this.N];
            			for (int i=0; i<this.N; i++)
            				this.nodenames[i] = nodes.get(i);
                		status = 2;
            			continue;
            		}
            		String[] part = line.split(" ");
            		nodes.add(part[0]);
            		if (line.contains("BORDERNODE")){
            			is_border_n.set(nodes.indexOf(part[0]));
            			this.bordernodes++;
            		}
            		continue;
            	}
            	
            	// go to LINKS section
            	if (status == 2){
            		if (line.startsWith("LINKS"))
            			status = 3;
            		continue;
            	}
            	
            	// read LINKS section
            	if (status == 3){
            		if (line.equals(")")){
                		status = 4;
                		System.out.println(this.L + " links.");
            			continue;
            		}
            		String[] part = line.substring(line.indexOf("(")+1, line.indexOf(")")).trim().split(" ");
            		int i = -1; int j = -1;
            		for (int n = 0; n < N; n++){
            			if (nodes.get(n).equals(part[0]))
            				i = n;
            			if (nodes.get(n).equals(part[1]))
            				j = n;
            		}
            		if (!this.adjacent[i][j]){
                		this.adjacent[i][j] = true;
                		this.adjacent[j][i] = true;
                		this.L++;
            		}
            	}
            	
            }
            f.close();
        }
        // If the file can't be opened:
        catch (IOException e){
        	this.read_success = false;
            System.out.println("\r\nERROR reading file " + filename + ": no such file or read error.");
        }
	}
	
	public int number_of_nodes(){
		return this.N;
	}
	
	public int number_of_links(){
		return this.L;
	}
	
	public int number_of_border_nodes(){
		return this.bordernodes;
	}
	
	public boolean[][] connected(){
		return this.adjacent;
	}
	
	public boolean connected(int i, int j){
		return this.adjacent[i][j];
	}
	
	public boolean[] is_a_border_node(){
		return this.is_a_border_node;
	}
	
	public boolean is_a_border_node(int n){
		return this.is_a_border_node[n];
	}
	
	public String nodename(int i){
		return this.nodenames[i];
	}
	
	public String[] get_nodenames(){
		return this.nodenames;
	}
	
	public boolean readSuccess(){
		return this.read_success;
	}
	

}

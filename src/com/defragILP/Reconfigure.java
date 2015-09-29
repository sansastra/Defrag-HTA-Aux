package com.defragILP;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.Connection;
import com.auxiliarygraph.elements.LightPath;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.inputdata.InputParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sandeep on 28-Sep-15.
 */
public class Reconfigure {
    ArrayList<Integer> maxSlotList;
    int N;
    Set<String> nodenames;
    List<String> nodelist;
    Set<VertexElement> nodeElementsSet;
    List<VertexElement> nodeElementsList;
    Set<EdgeElement> links;
    Map<Double, Connection> connectioTomap;
    int intialMinigridID;
   // List<Integer> miniGridIds;

    // Flows
    int F;					// Number of flows
    int[] Demand;			// Traffic demand of the flows
    int[][] flowNo ;

    public void Reconfigure() {};

    public void setTheConnectionToNewLightpath(ArrayList<Integer> maxSlotList){
        this.maxSlotList= maxSlotList;
        nodenames = InputParameters.getSetOfVertexIDSets();
        nodeElementsSet =InputParameters.getSetOfVertices();
        N = nodenames.size();
        nodelist = new ArrayList<>(nodenames);
        nodeElementsList = new ArrayList<>(nodeElementsSet);
            // Flows
        F = N * (N-1);
        Demand = new int[F];
        flowNo = new int[N][N];
        int demand;
        VertexElement ver1 =nodeElementsList.get(0), ver2=nodeElementsList.get(0);
        List<LightPath> lightpaths;
        //InputParameters.getIfConnectiongEdge(node,node1)
        for (String node : nodelist) {
            for (String node1 : nodelist) {
                if (!node.equals(node1)) {
                    for (int i = 0; i < N; i++) {
                        if (nodeElementsList.get(i).getVertexID().equals(node))
                            ver1 = nodeElementsList.get(i);
                        if (nodeElementsList.get(i).getVertexID().equals(node1))
                            ver2 = nodeElementsList.get(i);
                    }
                    lightpaths = NetworkState.getListOfLightPaths(ver1, ver2);
                    // int num= lightpaths.size();
                    for (LightPath lp : lightpaths) {
                        demand =0;
                        connectioTomap= lp.getConnectionMap();
                        for (Map.Entry<Double,Connection> entry : connectioTomap.entrySet())
                            demand += entry.getValue().getBw();
                        lp.releaseAllMiniGrids();
                        lp.removeAllMinigridIDs();
                    }
                }
            }

        }

// set minigrid for lightpaths
        int f=0;
        int s =0;
        int d =0;
        for (String node : nodelist) {
            for (String node1 : nodelist) {
                if (!node.equals(node1)) {
                    for (int i = 0; i < N; i++) {
                        if (nodeElementsList.get(i).getVertexID().equals(node))
                            ver1 = nodeElementsList.get(i);
                        if (nodeElementsList.get(i).getVertexID().equals(node1))
                            ver2 = nodeElementsList.get(i);
                    }
                    lightpaths = NetworkState.getListOfLightPaths(ver1, ver2);
                    // int num= lightpaths.size();
                    intialMinigridID =maxSlotList.get(f);
                    for (LightPath lp : lightpaths){
                        demand =0;
                        connectioTomap= lp.getConnectionMap();
            //getPathElement().getTraversedEdges().get(i)..getConnectionMap();
                        for (Map.Entry<Double,Connection> entry : connectioTomap.entrySet())
                            demand += entry.getValue().getBw();
                        intialMinigridID = intialMinigridID-demand+1;
                        lp.setMinigridIDs(intialMinigridID, demand);
                        lp.setAllMiniGrids();
                    }
                    f++;
                }

            }
        }

    }
}
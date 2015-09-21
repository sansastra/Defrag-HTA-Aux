package com.inputdata;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.graphcontroller.impl.GcontrollerImpl;
import com.inputdata.elements.Source;
import com.inputdata.elements.TrafficClass;
import com.inputdata.elements.TrafficDemand;
import com.inputdata.reader.ImportTopologyFromSNDFile;
import com.launcher.SimulatorParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class to load the input parameters.*
 *
 * @author Fran Carpio
 */
public class InputParameters {

    private static Gcontroller graph;
    private static List<TrafficClass> listOfTrafficClasses;
    private static double arrivalCoefficient;
    private static String arrivalProcess;
    private static List<TrafficDemand> listOfTrafficDemands;
    private static List<Source> listOfSources;

    /**
     * Constructor class
     *
     * @param networkTopologyFileName the SNDlib file name
     */
    public InputParameters(String networkTopologyFileName) {

        /** Import topology*/
        graph = new GcontrollerImpl();
        ImportTopologyFromSNDFile importer = new ImportTopologyFromSNDFile();
        importer.importTopology(graph, networkTopologyFileName);
        /** Import traffic matrix*/
        setTrafficDemands();
    }

    /**
     * Read the input parameters
     */
    public static void readNetworkParameters() {
        listOfTrafficClasses = new ArrayList<>();
        List<String> parameters = ImportTopologyFromSNDFile.getParameters();
        String[] arrivalProcessArray = parameters.get(0).trim().split("-");
        arrivalProcess = arrivalProcessArray[0];
        arrivalCoefficient = Double.parseDouble(arrivalProcessArray[1]);
        int numberOfPortClasses = Integer.parseInt(parameters.get(1));
        String[] bandwidths = parameters.get(2).trim().split(" ");
        String[] scaling = parameters.get(3).trim().split(" ");
        String[] holdingTimes = parameters.get(4).trim().split(" ");
        String[] connectionFeature = parameters.get(5).trim().split(" ");
        for (int i = 0; i < numberOfPortClasses; i++) {
            String[] ht = holdingTimes[i].split("-");
            listOfTrafficClasses.add(new TrafficClass(i, Double.parseDouble(bandwidths[i]), ht[0], Double.parseDouble(ht[1]), Double.parseDouble(ht[2]), Double.parseDouble(connectionFeature[i]), Double.parseDouble(scaling[i + ((SimulatorParameters.getNumberOfRuns() - 1)* SimulatorParameters.get_runNumber())])));
        }
    }

    /**
     * Set the nodes in the network for the statistics
     *
     */
    public static void setNodes() {
        listOfSources = new ArrayList<>();
        List<TrafficDemand> listOfTrafficDemandsPerGenerator = new ArrayList<>();
        List<TrafficDemand> listOfTrafficDemands = InputParameters.getTrafficDemands();

        for (int i = 0; i < listOfTrafficDemands.size(); i++) {

            if (i == listOfTrafficDemands.size() - 1) {
                listOfTrafficDemandsPerGenerator.add(listOfTrafficDemands.get(i));
                listOfSources.add(new Source(listOfTrafficDemands.get(i).getSource(), listOfTrafficDemandsPerGenerator));
                break;
            } else if (listOfTrafficDemands.get(i).getSource().equals(listOfTrafficDemands.get(i + 1).getSource())) {
                listOfTrafficDemandsPerGenerator.add(listOfTrafficDemands.get(i));
            } else {
                listOfTrafficDemandsPerGenerator.add(listOfTrafficDemands.get(i));
                listOfSources.add(new Source(listOfTrafficDemands.get(i).getSource(), listOfTrafficDemandsPerGenerator));
                listOfTrafficDemandsPerGenerator.clear();
            }
        }
    }

    /**
     * Set the traffic demands
     */
    public static void setTrafficDemands() {

        VertexElement src = null;
        listOfTrafficDemands = new ArrayList<>();
        List<List<String>> demands = ImportTopologyFromSNDFile.getListOfDemands();

        int pointer = 0;
        for (int j = 0; j + pointer < demands.get(0).size(); j++) {
            String[] demand = demands.get(0).get(j + pointer).split("-");
            String[] previousDemand;
            if (j > 0) {
                previousDemand = demands.get(0).get((j - 1) + pointer).split("-");
                if (!demand[0].equals(previousDemand[0])) {
                    pointer += j;
                    j = -1;
                } else {
                    listOfTrafficDemands.add(new TrafficDemand(src, graph.getVertex(demand[1])));
                    for (List<String> demand1 : demands) {
                        demand = demand1.get(j + pointer).split("-");
                        listOfTrafficDemands.get(listOfTrafficDemands.size() - 1).getTrafficValues().add(Double.valueOf(demand[2]));
                    }
                }
            } else {
                src = graph.getVertex(demand[0]);
                listOfTrafficDemands.add(new TrafficDemand(graph.getVertex(demand[0]), graph.getVertex(demand[1])));
                for (List<String> demand1 : demands) {
                    demand = demand1.get(j + pointer).split("-");
                    listOfTrafficDemands.get(listOfTrafficDemands.size() - 1).getTrafficValues().add(Double.valueOf(demand[2]));
                }
            }
        }
    }

    /**
     * Get the graph controller
     *
     * @return Gcontroller
     */
    public static Gcontroller getGraph() {
        return graph;
    }

    /**
     * Get the set of nodes of the network
     *
     * @return set of vertex com.inputdata.elements of the network
     */
    public static Set<String> getSetOfVertexIDSets() {
        return graph.getVertexIDSet();
    }
    public static Set<VertexElement> getSetOfVertices() {
        return graph.getVertexSet();
    }
    /**
     * Get the set of links of the network
     *
     * @return set of edge com.inputdata.elements of the network
     */
    public static Set<EdgeElement> getSetOfEdges() {
        return graph.getEdgeSet();
    }
    public static boolean getIfConnectiongEdge(String src, String dst) {
        return graph.aConnectingEdge(src,dst);
    }
    /**
     * Get the list of traffic classes
     *
     * @return the list of traffic classes
     */
    public static List<TrafficClass> getTrafficClasses() {
        return listOfTrafficClasses;
    }

    /**
     * Get the arrival coefficient of variation
     *
     * @return the coefficient of variation
     */
    public static double getArrivalCoefficient() {
        return arrivalCoefficient;
    }

    /**
     * Get the arrival process name
     *
     * @return the arrival process as a name
     */
    public static String getArrivalProcess() {
        return arrivalProcess;
    }

    /**
     * Get the number of traffic classes
     *
     * @return the total number of traffic classes
     */
    public static int getNumberOfTrafficClasses() {
        return listOfTrafficClasses.size();
    }

    /**
     * Get list of traffic demands
     *
     * @return the list of traffic demands (traffic matrix)
     */
    public static List<TrafficDemand> getTrafficDemands() {
        return listOfTrafficDemands;
    }

    /**
     * Get the set of nodes of the network
     *
     * @return the set of nodes of the network (used for statistics)
     */
    public static List<Source> getListOfSources() {
        return listOfSources;
    }

    /**
     * Get the net arrival rate
     *
     * @return net arrival rate value
     */
    public double getNetArrivalRate() {
        double netArrivalRate = 0;
        for (Source s : listOfSources)
            netArrivalRate += s.getArrivalRate();
        return netArrivalRate;
    }
}

package com.inputdata.elements;

import com.graph.elements.vertex.VertexElement;
import com.inputdata.InputParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to get the statistics for each source in the network.
 *
 * @author Fran Carpio
 */
public class Source {

    private VertexElement vertex;
    private List<TrafficDemand> listOfTrafficDemands = new ArrayList<>();
    private List<Double> trafficClassProb;
    private List<List<Double>> destinationProb;
    private double lambda;
    private List<Double> lambdaIJ;
    private List<Double> lambdaI;

    /**
     * Constructor class
     *
     * @param vertex               vertex element object
     * @param listOfTrafficDemands list of traffic demands of the source
     */
    public Source(VertexElement vertex, List<TrafficDemand> listOfTrafficDemands) {
        this.vertex = vertex;
        this.listOfTrafficDemands.addAll(listOfTrafficDemands);
        this.trafficClassProb = new ArrayList<>();
        this.destinationProb = new ArrayList<>();
        initializeRNGForArrivalProcess();
    }

    /**
     * Initialize distribution for arrival process
     *
     */
    public void initializeRNGForArrivalProcess() {

        lambdaIJ = new ArrayList<>();
        for (int i = 0; i < InputParameters.getNumberOfTrafficClasses(); i++)
            for (TrafficDemand listOfTrafficDemand : listOfTrafficDemands)
                lambdaIJ.add(listOfTrafficDemand.getTrafficValues().get(i) * InputParameters.getTrafficClasses().get(i).getScalingFactor() / InputParameters.getTrafficClasses().get(i).getMeanHoldingTime());

        lambdaI = new ArrayList<>();
        for (int i = 0; i < InputParameters.getNumberOfTrafficClasses(); i++) {
            double tmp = 0;
            for (int j = 0; j < listOfTrafficDemands.size(); j++)
                tmp += lambdaIJ.get(j + (listOfTrafficDemands.size() * i));
            lambdaI.add(tmp);
        }

        for (Double d : lambdaI)
            lambda += d;

        for (int i = 0; i < InputParameters.getNumberOfTrafficClasses(); i++)
            trafficClassProb.add(lambdaI.get(i) / lambda);

        for (int i = 0; i < InputParameters.getNumberOfTrafficClasses(); i++) {
            destinationProb.add(new ArrayList<>());
            for (int j = 0; j < listOfTrafficDemands.size(); j++)
                destinationProb.get(i).add(lambdaIJ.get(j + (i * listOfTrafficDemands.size())) / lambdaI.get(i));
        }
    }

    /**
     * Get the traffic of the source node (Ai)
     *
     * @return traffic value
     */
    public double getTraffic() {

        double traffic = 0;
        for (TrafficDemand td : listOfTrafficDemands)
            for (Double trafficPerClass : td.getTrafficValues())
                traffic += trafficPerClass;
        return traffic;
    }

    /**
     * Get the list of arrival rates
     *
     * @return list of arrival rates
     */
    public List<Double> getArrivalRateLambdaIJ() {
        return lambdaIJ;
    }

    /**
     * Get the list of arrival rates per source node and class
     *
     * @return list of arrival rates
     */
    public List<Double> getArrivalRatePerSourceAndTrafficClass() {
        return lambdaI;
    }

    /**
     * Get the lambda value
     *
     * @return lambda value
     */
    public double getArrivalRate() {
        return lambda;
    }

    /**
     * Get the traffic class probabilities
     *
     * @return the list of traffic class probabilities
     */
    public List<Double> getTrafficClassProb() {
        return trafficClassProb;
    }

    /**
     * Get the traffic destination probabilities
     *
     * @return the list of traffic destination probabilities
     */
    public List<List<Double>> getDestinationProb() {
        return destinationProb;
    }

    /**
     * Get the vertex element of the source
     *
     * @return vertex element
     */
    public VertexElement getVertex() {
        return vertex;
    }

    /**
     * Get the traffic demands for the source
     *
     * @return the list of traffic demands of the source
     */
    public List<TrafficDemand> getListOfTrafficDemands() {
        return listOfTrafficDemands;
    }


}

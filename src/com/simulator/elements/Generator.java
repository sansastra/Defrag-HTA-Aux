package com.simulator.elements;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.Path;
import com.graph.elements.vertex.VertexElement;
import com.inputdata.InputParameters;
import com.inputdata.elements.TrafficClass;
import com.inputdata.elements.TrafficDemand;
import com.launcher.SimulatorParameters;
import com.rng.Distribution;
import com.rng.distribution.ContinuousUniformDistribution;
import com.rng.distribution.ExponentialDistribution;
import com.simulator.Scheduler;
import com.simulator.event.CircuitRequestEvent;
import jsim.event.Entity;
import jsim.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to represent a generator of requests in the network
 *
 * @author Fran
 */
public class Generator {

    /**
     * Set of Flows for the generator
     */
    private List<TrafficFlow> listOfTrafficFlows;

    /**
     * Belonging vertex element
     */
    private VertexElement vertex;

    /**
     * Distribution for the generation of requests
     */
    private Distribution requestDistribution;

    /**
     * Distribution for destinations
     */
    private Distribution destinationDistribution;

    /**
     * Distribution for port class
     */
    private Distribution portDistribution;

    /**
     * Distribution for known or unknown
     */
    private Distribution unknownDistribution;

    /**
     * Traffic class probabilities
     */
    private List<Double> trafficClassProb;

    /**
     * Destination probabilities
     */
    private List<List<Double>> destinationProb;

    /**
     *
     */
    private byte[] seedForArrivalRNG;

    private static final Logger log = LoggerFactory.getLogger(Generator.class);

    /**
     * Constructor class
     */
    public Generator(VertexElement vertex, List<TrafficDemand> listOfTrafficDemands, double lambda, List<Double> trafficClassProb, List<List<Double>> destinationProb) {
        this.vertex = vertex;
        this.listOfTrafficFlows = new ArrayList<>();
        this.trafficClassProb = trafficClassProb;
        this.destinationProb = destinationProb;
        listOfTrafficFlows.addAll(listOfTrafficDemands.stream().map(td -> new TrafficFlow(td.getDestination())).collect(Collectors.toList()));
        seedForArrivalRNG = SimulatorParameters.getSeed();
        destinationDistribution = new ContinuousUniformDistribution(0, 1.0, SimulatorParameters.getSeed());
        portDistribution = new ContinuousUniformDistribution(0.0, 1.0, SimulatorParameters.getSeed());
        unknownDistribution = new ContinuousUniformDistribution(0.0, 1.0, SimulatorParameters.getSeed());
        requestDistribution = new ExponentialDistribution(lambda, seedForArrivalRNG);
    }

    /**
     * Function to initialize the generator and generate the first event
     */
    public void initialize() {

        for (TrafficFlow flow : listOfTrafficFlows) {
            List<Path> paths = NetworkState.getListOfPaths(vertex.getVertexID(), flow.getDstNode().getVertexID());
            flow.getListOfPaths().addAll(paths);
        }
        double interArrivalTime = requestDistribution.execute();
        Event event = new CircuitRequestEvent(new Entity(interArrivalTime), this, getRandomPort());
        Scheduler.schedule(event, interArrivalTime);
    }

    public TrafficClass getRandomPort() {
        TrafficClass trafficClass = null;

        double decisionValue = portDistribution.execute();
        double threshold = 0;
        for (int i = 0; i < InputParameters.getNumberOfTrafficClasses(); i++) {
            threshold += trafficClassProb.get(i);
            if (decisionValue <= threshold) {
                trafficClass = InputParameters.getTrafficClasses().get(i);
                break;
            }
        }

        return trafficClass;
    }

    public TrafficFlow getRandomFlow(int portClass) {
        TrafficFlow flow = null;

        double decisionValue = destinationDistribution.execute();
        double threshold = 0;
        for (int i = 0; i < listOfTrafficFlows.size(); i++) {
            threshold += destinationProb.get(portClass).get(i);
            if (decisionValue <= threshold) {
                flow = listOfTrafficFlows.get(i);
                break;
            }
        }
        return flow;
    }

    public boolean getRandomUnknown(int portClass) {
        double decisionValue = unknownDistribution.execute();

        if (decisionValue < InputParameters.getTrafficClasses().get(portClass).getConnectionFeature())
            return false;
        else
            return true;
    }

    /**
     * Getters
     */
    public List<TrafficFlow> getListOfTrafficFlows() {
        return listOfTrafficFlows;
    }

    public VertexElement getVertex() {
        return vertex;
    }

    public Distribution getRequestDistribution() {
        return requestDistribution;
    }
}

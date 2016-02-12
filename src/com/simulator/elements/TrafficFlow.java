package com.simulator.elements;

import com.auxiliarygraph.elements.Path;
import com.filemanager.Counter;
import com.graph.elements.vertex.VertexElement;
import com.graph.path.PathElement;
import com.inputdata.InputParameters;
import com.launcher.Launcher;
import com.launcher.SimulatorParameters;
import com.simulator.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to represent a Flow in the network
 *
 * @author Fran
 */
public class TrafficFlow {

    /**
     * Destination node
     */
    private VertexElement dstNode;
    /**
     * Set of counters per traffic flow
     */
    private List<Counter> listOfCounters;
    /**
     * Static boolean to known if grooming is allowed or not
     */
    private List<Path> listOfPaths;

    private static final Logger log = LoggerFactory.getLogger(TrafficFlow.class);

    /**
     * Constructor class
     */
    public TrafficFlow(VertexElement dstNode) {
        this.dstNode = dstNode;
        this.listOfPaths = new ArrayList<>();
        this.listOfCounters = new ArrayList<>();
        for (int i = 0; i < InputParameters.getNumberOfTrafficClasses(); i++)
            this.listOfCounters.add(new Counter(i));
    }

    /**
     * Increase the request counter for distribution port
     */
    public void increaseFlowRequestCounter(int portType) {
        listOfCounters.get(portType).increaseFlowRequestCounter();
    }

    /**
     * Increase the blocking counter for distribution port
     */
    public void increaseBlockingCounter(int portType, boolean isNotKnown) {
        listOfCounters.get(portType).increaseBlockingCounter();
//        if (isNotKnown)
//            listOfCounters.get(portType).increaseBlockingCounterForUnknownHT();
    }

//    public void increaseBlockingCounter(int portType, boolean blockedDueToFragment) {
//        listOfCounters.get(portType).increaseBlockingCounter();
//        if (blockedDueToFragment) // if blocked due to  fragmentation or resource unavailability
//            listOfCounters.get(portType).increaseFragmentBlockingCounter();
//        else
//            listOfCounters.get(portType).increaseResourceBlockingCounter();
//    }

    public void increaseReconfigurationCounter(int portType, int nc) {
        listOfCounters.get(portType).increaseReconfigCounter (nc);
    }

    /**
     * Getters
     */
    public VertexElement getDstNode() {
        return dstNode;
    }

    public List<Counter> getListOfCounters() {
        return listOfCounters;
    }

    public List<Path> getListOfPaths() {
        return listOfPaths;
    }
}

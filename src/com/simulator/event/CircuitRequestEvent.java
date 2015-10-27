package com.simulator.event;

import com.auxiliarygraph.AuxiliaryGraph;
import com.defragILP.MainDefragClass;
import com.filemanager.Results;
import com.inputdata.elements.TrafficClass;
import com.launcher.SimulatorParameters;
import com.simulator.Scheduler;
import com.simulator.elements.Generator;
import com.simulator.elements.TrafficFlow;
import jsim.event.Entity;
import jsim.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a new request event in the simulator
 *
 * @author Fran
 */
public class CircuitRequestEvent extends Event {

    /**
     * Generator responsible of the event
     */
    private Generator generator;
    /**
     * TrafficClass that generates the request
     */
    private TrafficClass trafficClass;

    private static final Logger log = LoggerFactory.getLogger(CircuitRequestEvent.class);

    /**
     * Constructor class
     */
    public CircuitRequestEvent(Entity entity, Generator generator,
                               TrafficClass trafficClass) {
        super(entity);
        this.generator = generator;
        this.trafficClass = trafficClass;

    }

    @Override
    public void occur() {
    //    int counter =0;
        double holdingTime;
        boolean isUnKnown = generator.getRandomUnknown(trafficClass.getType());

        holdingTime = trafficClass.getHoldingTimeDistribution().execute();

        /** If it is unknown, get the mean holding time*/
//        if (isUnKnown)
//            holdingTime = trafficClass.getMeanHoldingTime();

        /** Get a random destination following a uniform distribution */
        TrafficFlow selectedFlow = generator.getRandomFlow(trafficClass.getType());

        int numberOfMiniGrids = (int) (trafficClass.getBw() / (SimulatorParameters.getModulationFormat() *SimulatorParameters.getGridGranularity()));
        /** Create a new Auxiliary Graph*/
        AuxiliaryGraph auxiliaryGraph = new AuxiliaryGraph(generator.getVertex().getVertexID(), selectedFlow.getDstNode().getVertexID(), numberOfMiniGrids, Scheduler.currentTime(), holdingTime, isUnKnown);

        /**If path is found, then add release event*/
        if (auxiliaryGraph.runShortestPathAlgorithm(selectedFlow.getListOfPaths())) {
            Event event = new CircuitReleaseEvent(new Entity(holdingTime), generator, selectedFlow, auxiliaryGraph.getNewConnection());
            Scheduler.schedule(event, holdingTime);
            log.debug("Added release event: " + generator.getVertex().getVertexID() + "-" + selectedFlow.getDstNode().getVertexID());
//            Results.writeHoldingTime(generator,selectedFlow,trafficClass.getType(),isUnKnown,holdingTime);
        } else { /**if not, increase blocking counter*/
            selectedFlow.increaseBlockingCounter(trafficClass.getType(), isUnKnown);
            log.debug("Connection is blocked");
            /** test ILP */
//           if(SimulatorParameters.getBlockingCounter() < 2)
//               SimulatorParameters.increaseBlockingCounter();
//           else {
//               SimulatorParameters.setBlockingCounter(0);
//               MainDefragClass mainILP = new MainDefragClass();
//               // if (mainILP.getIfILPcanReconfigure())
//           }
        }

        /** Increase request counter for this flow */
        selectedFlow.increaseFlowRequestCounter(trafficClass.getType());

        /*********************** Results *************************/
        Results.writeBlockingResults(generator, selectedFlow);
        Results.writeLinkUtilizationResults();
        Results.increaseRequestCounter();

        /** Add a new request event */
        TrafficClass nextTrafficClass = generator.getRandomPort();
        double nextInterArrivalTime = generator.getRequestDistribution().execute();
        Event event = new CircuitRequestEvent(new Entity(nextInterArrivalTime), generator, nextTrafficClass);
        Scheduler.schedule(event, nextInterArrivalTime);
        log.debug("Added request event: " + generator.getVertex().getVertexID() + "-" + selectedFlow.getDstNode().getVertexID());
//        Results.writeInterArrivalTime(generator, selectedFlow,trafficClass.getType(),nextInterArrivalTime);
    }
}

package com.filemanager;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.elements.FiberLink;
import com.launcher.SimulatorParameters;
import com.simulator.Scheduler;
import com.simulator.elements.Generator;
import com.simulator.elements.TrafficFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Results {

    private static WriteFile blockingWriteFile;
    private static WriteFile linkUtilizationWriteFile;
    private static WriteFile interArrivalWriteFile;
    private static WriteFile holdingTimeWriteFile;
    private static int linkRequestCounter;
    private static int totalRequestCounter;
    private static final Logger log = LoggerFactory.getLogger(Results.class);

    public Results() {

        try {
            totalRequestCounter = 0;
            linkRequestCounter = 0;
            SimpleDateFormat MY_FORMAT = new SimpleDateFormat(
                    "dd-MM-yyyy HH-mm-ss", Locale.getDefault());
            Date date = new Date();
            blockingWriteFile = new WriteFile("BlockingProb-run-"
                    + SimulatorParameters.get_runNumber(), false);
            blockingWriteFile.write(MY_FORMAT.format(date) + "\n");
            blockingWriteFile.write("Blocking per SRC-DST nodes\n\n");
            blockingWriteFile.write("S	D	T	Req	Blocked	U	SimTime\n");

            linkUtilizationWriteFile = new WriteFile("LinkUtilization-run-"
                    + SimulatorParameters.get_runNumber(), false);
            linkUtilizationWriteFile.write(MY_FORMAT.format(date) + "\n");
            linkUtilizationWriteFile.write("Fiber utilization\n\n");
            linkUtilizationWriteFile
                    .write("Fiber	Requests	SimTime	Utilization(%)\n");

            interArrivalWriteFile = new WriteFile(
                    "MeanInterarrivalTimes-run-" + SimulatorParameters.get_runNumber(),
                    false);
            interArrivalWriteFile.write(MY_FORMAT.format(date) + "\n");
            interArrivalWriteFile.write("Requests \n\n");
            interArrivalWriteFile.write("S	D	T	lambda(i)	simTime\n");

            holdingTimeWriteFile = new WriteFile("MeanHoldingTimes-run-"
                    + SimulatorParameters.get_runNumber(), false);
            holdingTimeWriteFile.write(MY_FORMAT.format(date) + "\n");
            holdingTimeWriteFile.write("Releases \n\n");
            holdingTimeWriteFile.write("S	D	T	K/U	ht(i)	simTime\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void increaseRequestCounter() {
        totalRequestCounter++;
        if (totalRequestCounter >= SimulatorParameters.getNumberOfTotalRequests()) {
            log.info("Processed Requests: " + totalRequestCounter / 1000 + "K");
            SimulatorParameters.runSimulation();
        }
        if (totalRequestCounter % 10000 == 0)
            log.info("Processed Requests: " + totalRequestCounter / 1000 + "K");
    }

    public static void writeLinkUtilizationResults() {

        linkRequestCounter++;
        if (linkRequestCounter >= 1000) {
            for (Map.Entry<String, FiberLink> entry : NetworkState.getFiberLinksMap().entrySet())
                linkUtilizationWriteFile.write(entry.getValue().getEdgeElement().getSourceVertex().getVertexID()
                        .substring(1)
                        + " "
                        + entry.getValue().getEdgeElement().getDestinationVertex().getVertexID().substring(1)
                        + "	"
                        + linkRequestCounter
                        + "	"
                        + Scheduler.currentTime()
                        + "	"
                        + entry.getValue().getNetUtilization()
                        + "	"
                        + entry.getValue().getUtilization()
                        + "\n");

            linkRequestCounter = 0;
        }
    }

    public static void writeBlockingResults(Generator gen, TrafficFlow flow) {

        int totalRequestCounter = 0;

        for (Counter counter : flow.getListOfCounters())
            totalRequestCounter += counter.getFlowRequestCounter();

        if (totalRequestCounter >= 100) {
            for (int i = 0; i < flow.getListOfCounters().size(); i++) {
                blockingWriteFile.write(gen.getVertex().getVertexID()
                        .substring(1)
                        + "	"
                        + flow.getDstNode().getVertexID().substring(1)
                        + "	"
                        + i
                        + "	"
                        + flow.getListOfCounters().get(i).getFlowRequestCounter()
                        + "	"
                        + flow.getListOfCounters().get(i).getBlockingCounter()
                        + "	"
                        + flow.getListOfCounters().get(i).getBlockingCounterForUnknownHT()
                        + "	"
                        + Scheduler.currentTime() + "\n");
                flow.getListOfCounters().get(i).resetBlockingCounter();
                flow.getListOfCounters().get(i).resetBlockingCounterForUnknownHT();
                flow.getListOfCounters().get(i).resetFlowRequestCounter();
            }
        }
    }

    public static void writeHoldingTime(Generator gen, TrafficFlow flow, int portType,
                                        boolean isKnown, double ht) {
        String knownOrNot;
        if (isKnown)
            knownOrNot = "U";
        else
            knownOrNot = "K";

        holdingTimeWriteFile.write(gen.getVertex().getVertexID().substring(1)
                + "	" + flow.getDstNode().getVertexID().substring(1) + "	"
                + portType + "	" + knownOrNot + "	" + ht + "	"
                + Scheduler.currentTime() + "\n");
    }

    public static void writeInterArrivalTime(Generator gen, TrafficFlow flow,
                                             int portType, double interArrivalTime) {

        interArrivalWriteFile.write(gen.getVertex().getVertexID().substring(1)
                + "	" + flow.getDstNode().getVertexID().substring(1) + "	"
                + portType + "	" + interArrivalTime + "	"
                + Scheduler.currentTime() + "\n");
    }

}

package com.filemanager;

/**
 * Created by Fran on 4/29/2015.
 */
public class Counter {

    /** Counter used to count the number of blocked request for this distribution of port*/
    private int blockingCounter;
    /** Counter used to count the number of unknown blocked request for this distribution of port*/
    private int blockingCounterForUnknownHT;
    /** Counter used to count the number of processed requests for this distribution of port*/
    private int flowRequestCounter;
    /** TrafficClass distribution*/
    private int portType;

    /** Constructor class*/
    public Counter(int portType) {
        this.portType = portType;
        this.blockingCounter = 0;
        this.blockingCounterForUnknownHT = 0;
        this.flowRequestCounter = 0;
    }

    /** Methods to increase the counters*/
    public void increaseBlockingCounter(){
        blockingCounter++;
    }

    public void increaseBlockingCounterForUnknownHT(){
        blockingCounterForUnknownHT++;
    }

    public void increaseFlowRequestCounter(){
        flowRequestCounter++;
    }
    /** Methods to reset the counters*/
    public void resetBlockingCounter(){
        blockingCounter = 0;
    }

    public void resetBlockingCounterForUnknownHT(){
        blockingCounterForUnknownHT = 0;
    }
    public void resetFlowRequestCounter(){
        flowRequestCounter = 0;
    }

    /** Getters*/
    public int getPortType() {
        return portType;
    }

    public int getBlockingCounter() {
        return blockingCounter;
    }

    public int getBlockingCounterForUnknownHT() {
        return blockingCounterForUnknownHT;
    }

    public int getFlowRequestCounter() {
        return flowRequestCounter;
    }


}

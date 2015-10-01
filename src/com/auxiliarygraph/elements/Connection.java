package com.auxiliarygraph.elements;

import com.simulator.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to represent a connection between two end-points
 *
 * @author Fran
 */
public class Connection {

    /**
     * Starting simulation time for the connection
     */
    private double startingTime;
    /**
     * Holding time for the connection
     */
    private double holdingTime;
    /**
     * Required bandwidth for the connection
     */
    private int bw;
    /**
     * Boolean to specify if the incoming holding time is known or unknown
     */
    private boolean isUnKnown;

    /**
     * Integer to specify at which mini grid (starting from) the connection is allocated
     */
    private int miniGrid;

    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    /**
     * Constructor class
     */
    public Connection(double startingTime, double holdingTime, int bw, boolean isUnKnown, int miniGrid) {
        this.startingTime = startingTime;
        this.holdingTime = holdingTime;
        this.bw = bw;
        this.isUnKnown = isUnKnown;
        this.miniGrid = miniGrid;
    }

    /**
     * Function to get the residual time until the connection finalizes
     */
    public double getResidualTime() {
        if (isUnKnown)
            log.error("Getting residual time for an unknown connection");
        return (startingTime + holdingTime) - Scheduler.currentTime();
    }

    /**
     * Function to get the spent time since the connection is created
     */
    public double getSpentTime() {
        return Scheduler.currentTime() - startingTime;
    }

    /**
     * Getters and setters
     */
    public double getHoldingTime() {
        return holdingTime;
    }

    public int getBw() {
        return bw;
    }

    public boolean isUnKnown() {
        return isUnKnown;
    }

    public double getStartingTime() {
        return startingTime;
    }

    public int getMiniGrid() {
        return miniGrid;
    }
    public void setMiniGrid(int initialGrid){
        miniGrid = initialGrid;
    }
}

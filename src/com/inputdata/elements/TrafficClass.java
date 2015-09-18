package com.inputdata.elements;

import com.launcher.SimulatorParameters;
import com.rng.Distribution;
import com.rng.distribution.ExponentialDistribution;
import com.rng.distribution.LogNormalDistribution;

/**
 * Class to represent a traffic class
 *
 * @author Fran Carpio
 */
public class TrafficClass {

    private int type;
    private double bw;
    private Distribution holdingTimeDistribution;
    private double meanHoldingTime;
    private double cov;
    private double connectionFeature;
    private double scalingFactor;

    /**
     * Constructor class
     *
     * @param type              type value for the class
     * @param bw                bandwidth value
     * @param holdingTimeDist   distribution for the holding time
     * @param meanHoldingTime   mean value of the holding time
     * @param cov               coefficient of variation for the holding time distribution
     * @param connectionFeature connection feature value
     * @param scalingFactor     scaling factor value
     */
    public TrafficClass(int type, double bw, String holdingTimeDist, double meanHoldingTime, double cov, double connectionFeature, double scalingFactor) {

        this.type = type;
        this.bw = bw;
        this.meanHoldingTime = meanHoldingTime;
        this.cov = cov;
        this.connectionFeature = connectionFeature;
        this.scalingFactor = scalingFactor;
        /**Initialize distribution for holding times*/
        switch (holdingTimeDist) {
            case "EXP":
                holdingTimeDistribution = new ExponentialDistribution(1 / meanHoldingTime, SimulatorParameters.getSeed());
                break;
            case "LOGN":
                holdingTimeDistribution = new LogNormalDistribution(meanHoldingTime, cov * meanHoldingTime, SimulatorParameters.getSeed());
                break;
        }
    }

    /**
     * Get the type class
     *
     * @return integer value of the type class
     */
    public int getType() {
        return type;
    }

    /**
     * Get bandwidth of the traffic class
     *
     * @return bandwidth value
     */
    public double getBw() {
        return bw;
    }

    /**
     * Get the connection feature value
     *
     * @return connection feature value
     */
    public double getConnectionFeature() {
        return connectionFeature;
    }

    /**
     * Get mean holding time value
     *
     * @return mean holding time value
     */
    public double getMeanHoldingTime() {
        return meanHoldingTime;
    }

    /**
     * Get coefficient of variation
     *
     * @return cov value
     */
    public double getCov() {
        return cov;
    }

    /**
     * Get the distribution for the holding time
     *
     * @return distribution of the holding time
     */
    public Distribution getHoldingTimeDistribution() {
        return holdingTimeDistribution;
    }

    /**
     * Get scaling factor value of the traffic class
     *
     * @return scaling factor value
     */
    public double getScalingFactor() {
        return scalingFactor;
    }
}

package com.auxiliarygraph.edges;

import com.auxiliarygraph.Weights;
import com.auxiliarygraph.elements.LightPath;

/**
 * Created by Fran on 6/11/2015.
 */
public class LightPathEdge {

    private double cost;
    private LightPath lightPath;

    public LightPathEdge(LightPath lightPath) {
        this.lightPath = lightPath;
        cost = Weights.getLightPathEdgeCost(lightPath);
    }

    public LightPath getLightPath() {
        return lightPath;
    }

    public double getCost() {
        return cost;
    }

}

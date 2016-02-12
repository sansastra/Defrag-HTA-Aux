package com.auxiliarygraph.edges;

import com.auxiliarygraph.NetworkState;
import com.auxiliarygraph.Weights;
import com.graph.elements.edge.EdgeElement;

/**
 * Created by Fran on 6/11/2015.
 */
public class SpectrumEdge {

    private int spectrumLayerIndex;
    private EdgeElement edgeElement;
    private double cost;
    boolean isUnknown; //true means connections with known holding time
    public SpectrumEdge(EdgeElement edgeElement, int spectrumLayerIndex, int hopsOfThePath, int bwWithGB, double ht, boolean feature) {
        this.edgeElement = edgeElement;
        this.spectrumLayerIndex = spectrumLayerIndex;
        isUnknown = feature;
        cost = Weights.getSpectrumEdgeCost(edgeElement.getEdgeID(), spectrumLayerIndex, hopsOfThePath,bwWithGB,ht, isUnknown);

    }

    public int getSpectrumLayerIndex() {
        return spectrumLayerIndex;
    }

    public EdgeElement getEdgeElement() {
        return edgeElement;
    }

    public double getCost() {
        return cost;
    }
}

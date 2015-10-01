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
    private double ht;
    public SpectrumEdge(EdgeElement edgeElement, int spectrumLayerIndex, int hopsOfThePath, int bwWithGB, double ht) {

        cost = Weights.getSpectrumEdgeCost(edgeElement.getEdgeID(), spectrumLayerIndex, hopsOfThePath,bwWithGB,ht);
        this.edgeElement = edgeElement;
        this.spectrumLayerIndex = spectrumLayerIndex;
        this.ht = ht;
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

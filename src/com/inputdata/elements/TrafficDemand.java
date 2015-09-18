package com.inputdata.elements;

import com.graph.elements.vertex.VertexElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a traffic demand
 *
 * @author Fran Carpio
 */
public class TrafficDemand {

    private VertexElement source;
    private VertexElement destination;
    private List<Double> trafficValues;

    /**
     * Constructor class
     *
     * @param source      source vertex element
     * @param destination destination vertex element
     */
    public TrafficDemand(VertexElement source, VertexElement destination) {
        this.source = source;
        this.destination = destination;
        this.trafficValues = new ArrayList<>();
    }

    /**
     * Get the source vertex element
     *
     * @return source vertex element
     */
    public VertexElement getSource() {
        return source;
    }

    /**
     * Get the destination vertex element
     *
     * @return destination vertex element
     */
    public VertexElement getDestination() {
        return destination;
    }

    /**
     * Get the list of traffic values from the source to destination
     *
     * @return list of traffic values
     */
    public List<Double> getTrafficValues() {
        return trafficValues;
    }
}

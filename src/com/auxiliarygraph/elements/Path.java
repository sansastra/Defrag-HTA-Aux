package com.auxiliarygraph.elements;

import com.auxiliarygraph.NetworkState;
import com.graph.elements.edge.EdgeElement;
import com.graph.path.PathElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fran on 6/12/2015.
 */
public class Path {

    private PathElement pathElement;
    private List<FiberLink> listOfFiberLinks;

    public Path(PathElement pathElement) {
        this.pathElement = pathElement;
        listOfFiberLinks = new ArrayList<>();
        for (EdgeElement e : pathElement.getTraversedEdges())
            listOfFiberLinks.add(NetworkState.getFiberLinksMap().get(e.getEdgeID()));
    }

    public List<FiberLink> getListOfFiberLinks() {
        return listOfFiberLinks;
    }

    public PathElement getPathElement() {
        return pathElement;
    }
}

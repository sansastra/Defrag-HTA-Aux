package com.auxiliarygraph;

import com.auxiliarygraph.elements.Connection;
import com.auxiliarygraph.elements.FiberLink;
import com.auxiliarygraph.elements.LightPath;
import com.auxiliarygraph.elements.Path;
import com.graph.elements.edge.EdgeElement;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.graph.path.PathElement;
import com.inputdata.InputParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Fran on 6/12/2015.
 */
public class NetworkState {

    private static Map<String, FiberLink> fiberLinksMap;
    private static List<LightPath> listOfLightPaths;
    private static List<Path> listOfPaths;
    private static int transponderCapacity;
    private static int numOfMiniGridsPerGB;

    private static final Logger log = LoggerFactory.getLogger(NetworkState.class);

    public NetworkState(Gcontroller graph, int granularity, int txCapacityOfTransponders, int numOfMiniGridsPerGB, Set<PathElement> setOfPathElements, int policy) {

        this.fiberLinksMap = new HashMap<>();
        this.listOfLightPaths = new ArrayList<>();
        this.listOfPaths = new ArrayList<>();
        this.transponderCapacity = txCapacityOfTransponders / granularity;
        this.numOfMiniGridsPerGB = numOfMiniGridsPerGB;

        for (PathElement pe : setOfPathElements)
            listOfPaths.add(new Path(pe));

        for (EdgeElement edgeElement : graph.getEdgeSet())
            fiberLinksMap.put(edgeElement.getEdgeID(), new FiberLink(granularity, (int) edgeElement.getEdgeParams().getMaxCapacity(), edgeElement));

        new Weights(policy);
    }

    public static List<LightPath> getListOfLightPaths(List<Path> listOfCandidatePaths) {
        List<LightPath> listOfLightPaths = new ArrayList<>();

        for (Path p : listOfCandidatePaths) {
            List<VertexElement> vertexElements = p.getPathElement().getTraversedVertices();
//            for (int i = 0; i < vertexElements.size() - 1; i++)
//                for (int j = i + 1; j < vertexElements.size(); j++) {
//                    List<LightPath> tmpListOfLP = getListOfLightPaths(vertexElements.get(i), vertexElements.get(j));
            List<LightPath> tmpListOfLP = getListOfLightPaths(vertexElements.get(0), vertexElements.get(vertexElements.size() - 1));
                    for (LightPath lp : tmpListOfLP)
                        if (!listOfLightPaths.contains(lp))
                            listOfLightPaths.add(lp);
                }
      //  }

        return listOfLightPaths;
    }

    public static List<LightPath> getListOfLightPaths(VertexElement src, VertexElement dst) {
        List<LightPath> lightPaths = new ArrayList<>();

        for (LightPath lp : listOfLightPaths)
            if (lp.getPathElement().getSource().equals(src) && lp.getPathElement().getDestination().equals(dst))
                lightPaths.add(lp);

        return lightPaths;
    }

    public static PathElement getPathElement(List<VertexElement> vertexes) {

        for (Path path : listOfPaths)
            if (path.getPathElement().getTraversedVertices().equals(vertexes))
                return path.getPathElement();

        return null;
    }

    public static List<Path> getListOfPaths(String src, String dst) {

        List<Path> listOfCandidatePaths = new ArrayList<>();
        for (Path p : listOfPaths)
            if (p.getPathElement().getSourceID().equals(src) && p.getPathElement().getDestinationID().equals(dst))
                listOfCandidatePaths.add(p);

        return listOfCandidatePaths;
    }

    public static FiberLink getFiberLink(String edgeID) {
        return fiberLinksMap.get(edgeID);
    }

    public static Map<String, FiberLink> getFiberLinksMap() {
        return fiberLinksMap;
    }

    public static List<LightPath> getListOfLightPaths() {
        return listOfLightPaths;
    }

    public static int getTransponderCapacity() {
        return transponderCapacity;
    }

    public static int getNumOfMiniGridsPerGB() {
        return numOfMiniGridsPerGB;
    }
    public static List<Path> getListOfPaths() {return listOfPaths;}
    /**
     * Experimental
     */
   /* public void applyDefragmentation(LightPath leavingLP, Connection leavingConnection) {

        Set<LightPath> candidateLightPathsToReconfigure = new HashSet<>();

        for (LightPath lp : listOfLightPaths)
            if (lp.getFirstMiniGrid() > leavingConnection.getMiniGrid())
                for (EdgeElement e : leavingLP.getPathElement().getTraversedEdges())
                    if (lp.getPathElement().isLinktraversed(e))
                        candidateLightPathsToReconfigure.add(lp);

    }
*/
    public static Set<FiberLink> getNeighborsFiberLinks(VertexElement src, VertexElement dst) {

        Set<FiberLink> neighboursFiberLinks = new HashSet<>();
        for (EdgeElement e : InputParameters.getGraph().getEdgeSet())
            if (e.getDestinationVertex().getVertexID().equals(src.getVertexID()) || e.getSourceVertex().getVertexID().equals(dst.getVertexID()))
                neighboursFiberLinks.add(fiberLinksMap.get(e.getEdgeID()));

        return neighboursFiberLinks;
    }

    public static List<LightPath> getListOfTraversingLightPaths(EdgeElement link) {
        List<LightPath> listOfLPs = new ArrayList<>();
        List<LightPath> listOfExistingLPs = getListOfLightPaths();
        for (LightPath lp : listOfExistingLPs) {
            if (lp.getPathElement().getTraversedEdges().contains(link))
                listOfLPs.add(lp);
        }

        return listOfLPs;
    }
}

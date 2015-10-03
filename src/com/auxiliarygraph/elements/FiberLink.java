package com.auxiliarygraph.elements;

import com.auxiliarygraph.NetworkState;
import com.graph.elements.edge.EdgeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Fran on 6/11/2015.
 */
public class FiberLink {

    /**
     * Map<Integer, Integer> miniGrids
     * id, 0 ==> free
     * id, 1 ==> used
     * id, 2 ==> guard band
     * id, 3 ==> reserved
     */
    private Map<Integer, Integer> miniGrids;
    private EdgeElement edgeElement;
    private int totalNumberOfMiniGrids;

    private static final Logger log = LoggerFactory.getLogger(FiberLink.class);

    public FiberLink(int granularity, int spectrumWidth, EdgeElement edgeElement) {
        this.edgeElement = edgeElement;
        miniGrids = new HashMap<>();
        totalNumberOfMiniGrids = spectrumWidth / granularity;
        for (int i = 1; i <= totalNumberOfMiniGrids; i++) {
            miniGrids.put(i, 0);
        }
    }
// get all initial minigrids that can hold the demand
    public List<Integer> getFreeMiniGrids(int n) {

        List<Integer> freeMiniGrids = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet()) {
            if (entry.getValue() == 0)
                freeMiniGrids.add(entry.getKey());
        }

        if (n > 1) {
            for (int i = 0; i < freeMiniGrids.size() -n + 1; i++)
                for (int j = i ; j < i + n-1; j++) {
                    if (freeMiniGrids.get(j) != freeMiniGrids.get(j + 1) - 1) {
                        freeMiniGrids.remove(i);
                        break;
                    }
                }
            for (int i = freeMiniGrids.size() -n + 1; i < freeMiniGrids.size(); i++)
                freeMiniGrids.remove(i);

        }

        return freeMiniGrids;
    }

    public boolean areNextMiniGridsAvailable(int startingPoint, int additionalMiniGrids) {

        for (int i = startingPoint; i < startingPoint + additionalMiniGrids; i++) {
            if (!miniGrids.containsKey(i))
                return false;
            if (miniGrids.get(i) != 0)
                return false;
        }
        return true;
    }

    public boolean arePreviousMiniGridsAvailable(int startingPoint, int additionalMiniGrids) {

        for (int i = startingPoint; i > startingPoint - additionalMiniGrids; i--) {
            if (!miniGrids.containsKey(i))
                return false;
            if (miniGrids.get(i) != 0)
                return false;
        }
        return true;
    }

    public double getUtilization() {

        int usedMiniGrids = 0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 1 || entry.getValue() == 2)
                usedMiniGrids++;

        return (double) usedMiniGrids / miniGrids.size();
    }

    public double getNetUtilization() {

        int usedMiniGrids = 0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 1)
                usedMiniGrids++;

        return (double) usedMiniGrids / miniGrids.size();
    }

    public void setFreeMiniGrid(int id) {
//        if (miniGrids.get(id) == 0)
//            log.error("BUG: setting free an already free Mini-Grid");
        miniGrids.replace(id, miniGrids.get(id), 0);
    }

    public void setUsedMiniGrid(int id) {
//        if (miniGrids.get(id) == 1)
//            log.error("BUG: setting as used an already used Mini-Grid");
        miniGrids.replace(id, miniGrids.get(id), 1);
    }

    public int getMiniGrid(int index) {
        return miniGrids.get(index);
    }

    public void setGuardBandMiniGrid(int id) {
//        if (miniGrids.get(id) == 1)
//            log.error("BUG: setting as guard band an already used Mini-Grid");
        miniGrids.replace(id, miniGrids.get(id), 2);
    }

    public int getNumberOfMiniGridsUsed() {
        int usedMiniGrids = 0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 1 || entry.getValue() == 2)
                usedMiniGrids++;

        return usedMiniGrids;
    }
    public double getLinkFragmentationIndex(int spectrumLayerIndex, int bwWithGB) {
        int usedMiniGrids = 0;
        int freeMiniGrids = 0;
        double fragmentationIndex =0;

        for (Map.Entry<Integer, Integer> entry : miniGrids.entrySet())
            if (entry.getValue() == 0 && !(((entry.getKey()>= spectrumLayerIndex))&&(entry.getKey()< spectrumLayerIndex + bwWithGB)))
                freeMiniGrids++;
            else {
                fragmentationIndex += (double)(freeMiniGrids*(freeMiniGrids+1)*(freeMiniGrids+2)) ;
                usedMiniGrids++;
                freeMiniGrids = 0;
            }
        fragmentationIndex += (double)(freeMiniGrids*(freeMiniGrids+1)*(freeMiniGrids+2)) ;
        freeMiniGrids = totalNumberOfMiniGrids - usedMiniGrids ;
        fragmentationIndex = fragmentationIndex/(freeMiniGrids*(freeMiniGrids+1)*(freeMiniGrids+2));
        if (fragmentationIndex > 1)
            log.error("BUG: fragmentation index is greater than 1");
        return (1-fragmentationIndex);
    }

    public double getLinkTimeFragmentationIndex(int spectrumLayerIndex, int bw,double ht) {
        if(spectrumLayerIndex+bw-1 <= totalNumberOfMiniGrids) {
            double timefragmentationIndex =0;
            int start = getStartingIndexOfBlock(spectrumLayerIndex);
            int end = getEndingIndexOfBlock(spectrumLayerIndex, bw);
            // wont work with guard band
            List<Double> holdingTime ;
            if (start!=end) {
                holdingTime = getHoldingTimeOfBlock(start, end);
                if (holdingTime.size() != end - start - bw + 1) {
                    log.error("BUG: fragmentation time block indices is in error 2");
                    System.exit(0);
                }
            }
            else
            holdingTime=new ArrayList<>();
            for (int i = 0; i < bw; i++)
                holdingTime.add(ht);

            double[] htArray = new double[holdingTime.size()];
            double max1= Collections.max(holdingTime);
            for (int i = 0; i < holdingTime.size(); i++) {
                htArray[i]= Math.pow((max1 - holdingTime.get(i)), 2);
                timefragmentationIndex += htArray[i];
                timefragmentationIndex = timefragmentationIndex/htArray.length;
            }


            return (timefragmentationIndex);
        }
        else
            return (Double.MAX_VALUE); // spectrum index will not be used
        }

    public int getStartingIndexOfBlock(int initialId) {
        int start=initialId;
        int temp = initialId;
        while(miniGrids.get(temp)!=0){
            if (temp !=0) {
                start = temp;
                temp--;
            }
            else {
                start = temp;
                break;
            }
        }
        return start;
    }

    public int getEndingIndexOfBlock(int initialId, int bw) {
        int end=initialId;
        int temp = initialId + bw - 1;
        while (miniGrids.get(temp) != 0) {
            if (temp != totalNumberOfMiniGrids) {
                end = temp;
                temp++;
            } else {
                end = temp;
                break;
            }
        }
        return end;
    }

    public List<Double> getHoldingTimeOfBlock(int start,int end){
        List<LightPath> listOfLPs = NetworkState.getListOfTraversingLightPaths(this.edgeElement);
        List<Double> holdingTime = new ArrayList<>();
        while (start!=end+1)
                for (LightPath lp :  listOfLPs)
                    if(lp.containsMiniGrid(start))
                        for (Map.Entry<Double,Connection> entry : lp.getConnectionMap().entrySet())
                            for (int i = 0; i < entry.getValue().getBw(); i++) {
                                holdingTime.add(entry.getKey());
                                start++;
                            }

        return holdingTime;
    }

    public void setReservedMiniGrid(int id) {
        miniGrids.replace(id, miniGrids.get(id), 3);
    }

    public int getTotalNumberOfMiniGrids() {
        return totalNumberOfMiniGrids;
    }

    public EdgeElement getEdgeElement() {
        return edgeElement;
    }
}

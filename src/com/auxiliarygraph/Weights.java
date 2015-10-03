package com.auxiliarygraph;

import com.auxiliarygraph.elements.LightPath;

/**
 * Created by Fran on 7/3/2015.
 */
public class Weights {

    private static int POLICY;
    private static double transponderEdgeCost;
    private static double seFactor;
    private static double lpeFactor1;
    private static double lpeFactor2;

    public Weights(int policy) {

        POLICY = policy;

        switch (POLICY) {
            /** MinLP */
            case 1:
                transponderEdgeCost = 1e3;
                seFactor = 0;
                lpeFactor1 = 1;
                lpeFactor2 = 10e-7;
                break;
            /** MinHops */
            case 2:
                transponderEdgeCost = 0.5;
                seFactor = 0;
                lpeFactor1 = 1;
                lpeFactor2 = 10e-7;
                break;
            /** MinTHP*/
            case 3:
                transponderEdgeCost = 0;
                seFactor = 1;
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                break;
            /** LB*/
            case 4:
                transponderEdgeCost = 0;
                break;
            /** Spectral Fragmentation*/
            case 5:
                lpeFactor1 =1e-5;
                lpeFactor2 = 1;
                transponderEdgeCost = 1e9;
                break;
            /** Time Fragmentation*/
            case 6:
                lpeFactor1 =1e-5;
                lpeFactor2 = 1;
                transponderEdgeCost = 1e9;
                break;
            /** FirstFit*/
            case 7:
                transponderEdgeCost = 1e9;
                break;
        }
    }

    public static double getSpectrumEdgeCost(String edgeID, int spectrumLayerIndex, int hopsOfThePath, int bwWithGB, double ht) {

        if (POLICY == 4)
            seFactor = NetworkState.getFiberLink(edgeID).getNumberOfMiniGridsUsed();
        else if (POLICY == 5)
            seFactor = 1e5*NetworkState.getFiberLink(edgeID).getLinkFragmentationIndex(spectrumLayerIndex, bwWithGB);
        else if (POLICY == 6)
            seFactor = 1e5*NetworkState.getFiberLink(edgeID).getLinkTimeFragmentationIndex(spectrumLayerIndex, bwWithGB,ht);
        else if (POLICY == 7)
            return spectrumLayerIndex ;
        return seFactor + 1e-5 * spectrumLayerIndex;
    }

    public static double getLightPathEdgeCost(LightPath lp) {

        if (POLICY == 4)
            return lp.getNumberOfMiniGridsUsedAlongLP();
        if ((POLICY == 5) || (POLICY == 6))
            return lpeFactor1*lp.getFirstMiniGrid() + lp.getPathElement().getTraversedEdges().size() * lpeFactor2;
        if (POLICY==7)
            return lp.getFirstMiniGrid();

        return lpeFactor1 + lp.getPathElement().getTraversedEdges().size() * lpeFactor2;
    }

    public static double getTransponderEdgeCost() {
        return transponderEdgeCost * 2;
    }


}

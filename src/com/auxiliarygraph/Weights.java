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
    private static double timeFactor;
    private static double fragmentFactor;

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
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                transponderEdgeCost = 1e9;
                timeFactor =0;
                fragmentFactor=1;
                break;
            /** Time Fragmentation*/
            case 6:
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                transponderEdgeCost = 1e9;
                timeFactor =1;
                fragmentFactor=0;
                break;
            /** Spectral and Time Fragmentation */
            case 7:
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                transponderEdgeCost = 1e9;
                timeFactor =0.5;
                fragmentFactor= 1- timeFactor;
                break;
            /** FirstFit*/
            case 8:
                lpeFactor1 = 0;
                lpeFactor2 = 1;
                transponderEdgeCost = 1e9;
                break;
        }
    }

    public static double getSpectrumEdgeCost(String edgeID, int spectrumLayerIndex, int hopsOfThePath, int bwWithGB, double ht) {
        if (POLICY == 8)
            return spectrumLayerIndex ;
//        if (POLICY == 4)
//            seFactor = NetworkState.getFiberLink(edgeID).getNumberOfMiniGridsUsed();

        return (timeFactor*(NetworkState.getFiberLink(edgeID).getLinkTimeFragmentationIndex(spectrumLayerIndex, bwWithGB,ht))+
                fragmentFactor* (NetworkState.getFiberLink(edgeID).getLinkFragmentationIndex(spectrumLayerIndex, bwWithGB))) +1e-5 * spectrumLayerIndex;
        //return seFactor + 1e-5 * spectrumLayerIndex;
    }

    public static double getLightPathEdgeCost(LightPath lp) {

        if (POLICY == 4)
            return lp.getNumberOfMiniGridsUsedAlongLP();
//        if ((POLICY == 5) || (POLICY == 6)|| (POLICY == 7))
//            return lpeFactor1*lp.getFirstMiniGrid() + lp.getPathElement().getTraversedEdges().size() * lpeFactor2;
//        if (POLICY==8)
//            return lp.getFirstMiniGrid();

        return 0; //lpeFactor1 + lp.getPathElement().getTraversedEdges().size() * lpeFactor2;
    }

    public static double getTransponderEdgeCost() {
        return transponderEdgeCost * 2;
    }

}

package com.inputdata.reader;

import com.graph.elements.edge.EdgeElement;
import com.graph.elements.edge.params.EdgeParams;
import com.graph.elements.edge.params.impl.BasicEdgeParams;
import com.graph.elements.vertex.VertexElement;
import com.graph.graphcontroller.Gcontroller;
import com.launcher.SimulatorParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to import SNDLib topology
 */
public class ImportTopologyFromSNDFile extends com.graph.topology.importers.ImportTopology {


    private static List<List<String>> demands;
    private static List<String> paths;
    private static List<String> parameters;

    public static List<String> getParameters() {
        return parameters;
    }

    public static List<List<String>> getListOfDemands() {
        return demands;
    }

    public static List<String> getPaths() {
        return paths;
    }

    @Override
    public void importTopology(Gcontroller graph, String filename) {

        demands = new ArrayList<>();
        paths = new ArrayList<>();
        parameters = new ArrayList<>();

        new ReadFile(filename);
        String temp;
        VertexElement vertex1, vertex2;

        // Read till we get to META definition
        while ((temp = ReadFile.readLine()).trim().compareTo("META (") != 0) {
        }
        // read till we reach the end of meta definitions
        while ((temp = ReadFile.readLine()) != null) {
            temp = temp.trim();
            if (temp.trim().compareTo(")") == 0) {
                break;
            }

            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(temp);
            String[] temp1 = new String[SimulatorParameters.getNumberOfRuns()];
            int count = 0;
            while (m.find()) {
                temp1[count] = m.group(1).trim();
                count++;
            }
            if (count > 1) {
                String tmp = "";
                for (int i = 0; i < count; i++) {
                    tmp += temp1[i]+" ";
                }
                parameters.add(tmp);
            } else
                parameters.add(temp1[0]);

        }

        // Read till we get to Source definition)
        while ((temp = ReadFile.readLine()).trim().compareTo("NODES (") != 0) {
        }

        // read till we reach the end of node definitions
        while ((temp = ReadFile.readLine()) != null) {
            temp = temp.trim();
            // System.out.println(temp);
            // if (temp.length()==1){
            // break;
            // }
            if (temp.trim().compareTo(")") == 0) {
                break;
            }

            Pattern p;
            Matcher m;

            String sourceID = "";
            p = Pattern.compile("[a-zA-Z0-9\\.]+");
            m = p.matcher(temp);
            if (m.find()) {
                sourceID = m.group(0);
            }

            // p = Pattern.compile("[0-9\\.]+");
            // m = p.matcher(temp);
            double[] temp1 = new double[2];
            int count = 0;
            while (m.find()) {
                temp1[count] = Double.parseDouble(m.group(0));
                count++;
                if (count == 2)
                    break;
            }

            vertex1 = new VertexElement(sourceID, graph, temp1[0], temp1[1]);
            graph.addVertex(vertex1);
            // System.out.println("Vertex Added: VertexID=" +
            // vertex1.getVertexID()+ ", X=" + vertex1.getXCoord() + ", Y="
            // + vertex1.getYCoord());
        }

        // Read till we get to Edge definition)
        while ((temp = ReadFile.readLine()).trim().compareTo("LINKS (") != 0) {
        }

        // read till we reach the end of the edge definition
        while ((temp = ReadFile.readLine()) != null) {
            temp = temp.trim();
            if (temp.length() == 1) {
                break;
            }

            Pattern p;
            Matcher m;

            p = Pattern.compile("[a-zA-Z0-9\\.]+");
            m = p.matcher(temp);
            String[] temp1 = new String[7];
            int count = 0;
            while (m.find()) {
                temp1[count] = m.group(0);
                count++;
                if (count == 7)
                    break;
            }

            vertex1 = graph.getVertex(temp1[1]);
            vertex2 = graph.getVertex(temp1[2]);

            EdgeElement edge1 = new EdgeElement(temp1[0] + ".1", vertex1, vertex2,
                    graph);
            EdgeElement edge2 = new EdgeElement(temp1[0] + ".2", vertex2, vertex1,
                    graph);


            double distance = Math.sqrt(Math.pow(vertex1.getXCoord()
                    - vertex2.getXCoord(), 2)
                    + Math
                    .pow(vertex1.getYCoord() - vertex2.getYCoord(),
                            2));

            double delay = distance / 29.9792458; // (in ms)

            EdgeParams params1 = new BasicEdgeParams(edge1, delay, 1, Double.valueOf(temp1[5]));
            EdgeParams params2 = new BasicEdgeParams(edge2, delay, 1, Double.valueOf(temp1[5]));
            edge1.setEdgeParams(params1);
            edge2.setEdgeParams(params2);
            graph.addEdge(edge1);
            graph.addEdge(edge2);
        }

        for (int i = 0; i < Integer.valueOf(parameters.get(1)); i++) {
            demands.add(new ArrayList<>());
            // Read till we get to Edge definition)
            while ((temp = ReadFile.readLine()).trim().compareTo("DEMANDS (") != 0) {
            }
            // read till we reach the end of the demands definition
            while ((temp = ReadFile.readLine()) != null) {
                temp = temp.trim();
                if (temp.length() == 1) {
                    break;
                }
                Pattern p;
                Matcher m;

                p = Pattern.compile("[a-zA-Z0-9\\.]+");
                m = p.matcher(temp);
                String[] temp1 = new String[6];
                int count = 0;
                while (m.find()) {
                    temp1[count] = m.group(0);
                    count++;
                    if (count == 6)
                        break;
                }
                demands.get(i).add(temp1[1] + "-" + temp1[2] + "-" + temp1[4]);
            }
        }

        // Read till we get to Path definition)
        while ((temp = ReadFile.readLine()).trim().compareTo("ADMISSIBLE_PATHS (") != 0) {
        }
        // read till we reach the end of the demands definition
        while ((temp = ReadFile.readLine()) != null) {
            temp = temp.trim();
            if (temp.length() == 1) {
                break;
            }
            paths.add(temp);
        }
    }

    @Override
    public void importTopologyFromString(Gcontroller graph, String[] topology) {
    }

}

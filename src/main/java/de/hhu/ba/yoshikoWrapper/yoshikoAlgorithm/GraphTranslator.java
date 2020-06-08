package de.hhu.ba.yoshikoWrapper.yoshikoAlgorithm;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.core.NetworkParsingException;
import de.hhu.ba.yoshikoWrapper.core.ParameterSet;
import de.hhu.ba.yoshikoWrapper.cytoUtil.GraphAnalyzer;
import de.hhu.ba.yoshikoWrapper.cytoUtil.NodeMap;
import de.hhu.ba.yoshikoWrapper.cytoUtil.StyleManager;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoSolution;
import de.hhu.ba.yoshikoWrapper.swing.components.MainPanel;
import org.cytoscape.model.*;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.JOptionPane.*;

public final class GraphTranslator {
    private CyNetwork network;
    private ParameterSet parameterSet;

    private boolean containsOnlyPositiveWeigths;
    private double maxWeight;
    private double minWeight;

    private double deletionCostDefault;
    private double insertionCostDefault;
    private double threshold;

    CyColumn weightColumn;

    private Map<Integer, CyNode> nodeMap;

    private YoshikoEdge[][] edgeArray;

    public GraphTranslator(ParameterSet parameterSet) throws NetworkParsingException {
        this.parameterSet = parameterSet;
        this.network = parameterSet.net;
        this.weightColumn = parameterSet.getWeightColumn();
        this.deletionCostDefault = parameterSet.defaultDeletionCost;
        this.insertionCostDefault = parameterSet.defaultInsertionCost;
        this.threshold = parameterSet.threshold;

        this.nodeMap = new HashMap<>();
        this.containsOnlyPositiveWeigths = true;
    }

    public YoshikoEdge[][] translateGraph() throws NetworkParsingException {
        containsOnlyPositiveWeigths = true;
        this.makeGraph();
        this.makeGraphComplete();
        parameterSet.containsOnlyPositiveEdges = containsOnlyPositiveWeigths;
        parameterSet.recomendetTreshold = (minWeight + maxWeight)/2;

         return edgeArray;
    }

    private void makeGraph() throws NetworkParsingException {
        List<CyNode> nodeList = network.getNodeList();
        List<CyEdge> edgeList = network.getEdgeList();

        int size = nodeList.size();

        this.edgeArray = new YoshikoEdge[size][size];

        Map<CyNode, Integer> reverseNodeMap = new HashMap();
        int i = nodeList.size()-1;
        for (CyNode n : nodeList){
            System.out.println(i+":"+network.getRow(n).get(CyNetwork.NAME, String.class));
            nodeMap.put(i, n);
            reverseNodeMap.put(n,i);
            i--;
        }

        for (CyEdge e : edgeList){
            int u = reverseNodeMap.get(e.getSource());
            int v = reverseNodeMap.get(e.getTarget());

            double weight;
            weight = extractValue(e);
            weight -= threshold;
            if (weight < 0){
                containsOnlyPositiveWeigths = false;
            }
            YoshikoEdge yoshikoEdge = new YoshikoEdge(e.getSUID(), weight, u, v);

           edgeArray[yoshikoEdge.source][yoshikoEdge.target]= yoshikoEdge;
        }
    }

    private void makeGraphComplete(){
        for (int i = 1; i < edgeArray.length; i++){
            for (int j = 0; j < i; j++){
                if (edgeArray[i][j] == null){
                    YoshikoEdge yoshikoEdge = new YoshikoEdge(-1, insertionCostDefault-threshold, i, j);
                    edgeArray[i][j] = yoshikoEdge;
                }
            }
        }
    }

    private double extractValue(CyEdge edge) throws NetworkParsingException {
        //Parse editing costs
        double weight = deletionCostDefault;

        //Fetch entry and check if it exists
        CyRow edgeEntry = network.getRow(edge);

        //Check if the column contains an entry for the respective edge
        //It is possible, that there are missing entries
        if (weightColumn != null){
            if (edgeEntry.get(weightColumn.getName(), weightColumn.getType()) != null){
                if (weightColumn.getType() == Integer.class) {
                    weight = 1.0*edgeEntry.get(weightColumn.getName(), Integer.class);
                }
                else if (weightColumn.getType() == Double.class) {
                    weight = edgeEntry.get(weightColumn.getName(), Double.class);
                }
            }

        }


        CyColumn permanentColumn = parameterSet.getPermanentColumn();
        CyColumn forbiddenColumn = parameterSet.getForbiddenColumn();

        //Parse Forbidden/Permanent markers
        boolean forbidden = false;
        boolean permanent = false;

        if (permanentColumn != null) {
            //Additional check as it is not required to have a value in every row
            if (edgeEntry.get(permanentColumn.getName(), Boolean.class) != null) {
                permanent =  (boolean)edgeEntry.get(permanentColumn.getName(), Boolean.class);
            }
        }
        if (forbiddenColumn != null) {
            //Additional check as it is not required to have a value in every row
            if (edgeEntry.get(forbiddenColumn.getName(), Boolean.class) != null) {
                forbidden =  (boolean)edgeEntry.get(forbiddenColumn.getName(), Boolean.class);
            }
        }

        //Check for edges that are forbidden AND permanent -> Throw exception
        if (forbidden && permanent) {
            throw new NetworkParsingException("dualInfinityError");
        }


        if(permanent){
            return Double.POSITIVE_INFINITY;
        }else if (forbidden){
            return Double.NEGATIVE_INFINITY;
        }

        return weight;
    }

    public void  transateClusters(List<List<Integer>> clusters, YoshikoSolution solution){
        int i = 0;
        for (List<Integer> cluster : clusters){
            YoshikoCluster yoshikoCluster = new YoshikoCluster(solution, i);
            i++;
            solution.addCluster(yoshikoCluster);
            for (Integer nodeId : cluster){
                yoshikoCluster.addNode(nodeMap.get(nodeId));
            }
        }
    }

    public void writeClusters(List<List<Integer>> clusters, int k) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        String s = network.getRow(network).get(CyNetwork.NAME, String.class);

        s = s.substring(0, s.indexOf("."));

        if (k < 0) {
            s = s + "yoshikoClustering";
        } else {
            s = s+ "k-Clustering";
        }

        try {
            File file = new File("C:\\Users\\User\\Desktop\\relevantClustEval\\optimalThreshold\\"+s);
            file.createNewFile();
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < clusters.size(); i++) {
                List<Integer> cluster = clusters.get(i);
                for (Integer nodeId : cluster) {
                    bufferedWriter.write(network.getRow(nodeMap.get(nodeId)).get(CyNetwork.NAME, String.class) + " " + i + "\n");
                }
            }
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

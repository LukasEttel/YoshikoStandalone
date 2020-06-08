package de.hhu.ba.yoshikoWrapper.yoshikoAlgorithm;

import de.hhu.ba.yoshikoWrapper.core.NetworkParsingException;
import de.hhu.ba.yoshikoWrapper.core.ParameterSet;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoResult;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoSolution;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;

import java.util.List;

public class YoshikoAlgoritmController {

    private int k;
    private CyNetwork network;
    private ParameterSet parameterSet;
    private TaskMonitor taskMonitor;
    private YoshikoResult result;

    public YoshikoAlgoritmController(ParameterSet parameterSet, TaskMonitor taskMonitor, YoshikoResult result){
         this.parameterSet = parameterSet;
         this.network = parameterSet.net;
         this.k = parameterSet.clusterCount;
         this.taskMonitor = taskMonitor;
         this.result = result;
    }

    public void controllAlgorithm()throws Exception {
        GraphTranslator translator = new GraphTranslator(parameterSet);
        YoshikoEdge[][] edgeArray = translator.translateGraph();

        ClusteringAlgorithm clusteringAlgorithm = new ClusteringAlgorithm(edgeArray, taskMonitor);
        clusteringAlgorithm.k = k;

        List<List<Integer>> clusters;

        taskMonitor.setStatusMessage("Clustering the Graph");
        clusters = clusteringAlgorithm.runClusteringAlgorithm();

        YoshikoSolution solution = new YoshikoSolution(result, 0, clusteringAlgorithm.getClusteringCost());
        result.addSolution(solution);

        taskMonitor.setStatusMessage("Processing Clusters");
        translator.writeClusters(clusters, k);
        translator.transateClusters(clusters, solution);
    }
}
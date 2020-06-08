package de.hhu.ba.yoshikoWrapper.yoshikoAlgorithm;


import de.hhu.ba.yoshikoWrapper.core.NetworkParsingException;
import org.cytoscape.work.TaskMonitor;

import java.util.ArrayList;
import java.util.List;

public class ClusteringAlgorithm {

    private BinaryHeap bHeap;
    private double clusteringCost;
    private YoshikoEdge[][] edgeArray;
    private int numberOfNodes;
    private List<Integer> nodeList;
    private List<List<Integer>> clusters;
    int k=-1;
    private int nodeInClusters;
    private TaskMonitor taskMonitor;


    public ClusteringAlgorithm(YoshikoEdge[][] edgeArray, TaskMonitor taskMonitor){
        this.edgeArray = edgeArray;
        numberOfNodes = edgeArray.length;
        clusters = new ArrayList<>();
        this.taskMonitor = taskMonitor;
    }

    public List<List<Integer>> runClusteringAlgorithm() throws NetworkParsingException{
        initializeQueue();
        double initialBHeapSize = bHeap.size();

        if (k < 0) {
            while (bHeap.size() > 0) {
                double progress = (initialBHeapSize-bHeap.size())/initialBHeapSize;
                taskMonitor.setProgress(progress);

                workHeap();
            }
        }else {
            while (numberOfNodes-nodeInClusters > k) {
                double progress = (initialBHeapSize-bHeap.size())/initialBHeapSize;
                taskMonitor.setProgress(progress);

                workHeap();
            }
        }
        addSingleNodesToClusters();

        this.calculateClusteringCost();

        return clusters;
    }

    private void initializeQueue(){
        bHeap = new BinaryHeap();
        bHeap.k = k;

        for (int i=1; i<numberOfNodes; i++){
            for (int j=0; j<i; j++){
                YoshikoEdge edge = calculateIcfIcp(i,j);
                if (edge != null) {
                    if (k < 1) {
                        bHeap.addMaxIcfIcp(edge);
                    } else {
                        bHeap.addIcfMinusIcp(edge);
                    }
                }
            }
        }
        initializeNodeList();
    }

    private void initializeNodeList(){
        this.nodeList = new ArrayList<>();
        for(int i = 0; i < numberOfNodes; i++){
            nodeList.add(i);
        }
    }

    private YoshikoEdge calculateIcfIcp(int u, int v){
        double icf = 0;
        double icp = 0;

        YoshikoEdge edge = edgeArray[u][v];

        if (edge.weight == Double.NEGATIVE_INFINITY){
            return null;
        }

        if (edge.weight > 0){
            icf = edge.weight;
        }
        if (edge.weight < 0){
            icp = -edge.weight;
        }

        for (int w=0; w < numberOfNodes; w++){
            if (w == u || w == v){
                continue;
            }

            YoshikoEdge e1 = edgeArray[u][w];
            if (e1 == null){
                e1 = edgeArray[w][u];
            }
            YoshikoEdge e2 = edgeArray[v][w];
            if (e2 == null){
                e2 = edgeArray[w][v];
            }

            if (e1.weight > 0 && e2.weight > 0){
                icf = icf + Math.min(e1.weight, e2.weight);
            }else if (e1.weight > 0 && e2.weight < 0){
                icp = icp + Math.min(e1.weight, -e2.weight);
            }else if (e1.weight < 0 && e2.weight > 0) {
                icp = icp + Math.min(-e1.weight, e2.weight);
            }
        }
        edge.icf = icf;
        edge.icp = icp;
        edge.maxIcfIcp = Math.max(icf, icp);
        edge.icfMinusIcp = icf-icp;

        bHeap.updateEdge(edge);

        return edge;
    }

    private int makeEdgeId(int u, int v){
        if (v>u){
            return v*(v-1)+u;
        }
        return u*(u-1)+v;
    }


    private void workHeap()throws  NetworkParsingException{
        YoshikoEdge e=bHeap.popMax();

        if (e == null){
            throw new NetworkParsingException("The given number of clusters is not reachable");
        }

        if (e.icf >= e.icp|| k > -1){
            makeEdgePermanent(e);
        }
        else{
            makeEdgeForbidden(e);
        }
    }

    private YoshikoEdge getEdge(int edgeId){
        int u = (int) Math.ceil(Math.sqrt(2*(edgeId-1)+0.25)-0.5);
        int v = edgeId - (u*(u-1))/2;

        return edgeArray[u][v];
    }

    private YoshikoEdge getEdge(int u, int v){
        if (u > v){
            return edgeArray[u][v];
        }
        return edgeArray[v][u];
    }

    private void makeEdgePermanent(YoshikoEdge e){
        substactInfluenceOfVerticesOnIcfIcp(e);

        mergeVertexes(e);

        calculateNewEdgesIcfIcp(e.source);

        addInfluenceOfVertexOnIcfIcp(e.source);

        putInCluster(e);
    }

    private void mergeVertexes(YoshikoEdge e){
        e.weight = Double.POSITIVE_INFINITY;

        int n1 = e.source;
        int n2 = e.target;

        for (int v: nodeList){
            if (v == n1|| v== n2){
                continue;
            }
            YoshikoEdge e1 = edgeArray[n1][v];
            if (e1 == null){
                e1 = edgeArray[v][n1];
            }
            double e1Val = e1.weight;

            YoshikoEdge e2 = edgeArray[n2][v];
            if (e2 == null){
                e2 = edgeArray[v][n2];
            }
            double e2Val = e2.weight;
            bHeap.remove(e2);

            double val = e1Val + e2Val;
            e1.weight = val;
            if (val == Double.NEGATIVE_INFINITY){
                bHeap.remove(e1);
            }
        }
        Integer o = n2;
        nodeList.remove(o);
    }

    private void calculateNewEdgesIcfIcp(int node1){
        for (int node2 : nodeList){
            if (node1 == node2){
                continue;
            }
            if (node2 > node1){
                int tmp = node1;
                node1 = node2;
                node2 = tmp;
            }
            YoshikoEdge e = edgeArray[node1][node2];
            if (e.weight == Double.NEGATIVE_INFINITY){
                continue;
            }
            calculateIcfIcp(node1, node2);
        }
    }

    private void substactInfluenceOfVerticesOnIcfIcp(YoshikoEdge e){
        int p1 = e.source;
        int p2 = e.target;

        for (YoshikoEdge edge : bHeap.map.values()){
            if (edge == null){
                continue;
            }
            int v1 = edge.source;
            int v2 = edge.target;

            if (v1==p1||v1==p2||v2==p1||v2==p2){
                continue;
            }

            //Subtraiere Einfluss des Dreiecks v1p1v2 auf icf
            YoshikoEdge ux = getEdge(v1, p1);
            YoshikoEdge vx = getEdge(v2, p1);

            if (ux.weight > 0 && vx.weight > 0){
                edge.icf -= Math.min(ux.weight, vx.weight);
            }

            //Subtraiere Einfluss des Dreiecks v1p1v2 auf icp

            if (ux.weight > 0 && vx.weight < 0){
                edge.icp -= Math.min(ux.weight, -vx.weight);
            }else if (ux.weight < 0 && vx.weight > 0){
                edge.icp -= Math.min(-ux.weight, vx.weight);
            }

            //Subtraiere Einfluss des Dreiecks v1p2v2 auf icf
            ux = getEdge(v1, p2);
            vx = getEdge(v2, p2);

            if (ux.weight > 0 && vx.weight > 0){
                edge.icf -= Math.min(ux.weight, vx.weight);
            }

            //Subtraiere Einfluss des Dreiecks v1p2v2 auf icp

            if (ux.weight > 0 && vx.weight < 0){
                edge.icp -= Math.min(ux.weight, -vx.weight);
            }else if (ux.weight < 0 && vx.weight > 0){
                edge.icp -= Math.min(-ux.weight, vx.weight);
            }

            edge.maxIcfIcp = Math.max(edge.icf, edge.icp);
            edge.icfMinusIcp = edge.icf-edge.icp;
            bHeap.updateEdge(edge);
        }
    }

    private void addInfluenceOfVertexOnIcfIcp(int mergedVertex) {
        for (YoshikoEdge edge : bHeap.map.values()){
            if(edge == null){
                continue;
            }
            int v1 = edge.source;
            int v2 = edge.target;

            if (v1==mergedVertex||v2==mergedVertex){
                continue;
            }

            //Addiere Einfluss des Dreiecks v1-MergedVertex-v2 auf icf
            YoshikoEdge ux = getEdge(v1, mergedVertex);
            YoshikoEdge vx = getEdge(v2, mergedVertex);

            if (ux.weight > 0 && vx.weight > 0){
                edge.icf += Math.min(ux.weight, vx.weight);
            }


            //Addiere Einfluss des Dreiecks v1-MergedVertex-v2 auf icp
            if (ux.weight > 0 && vx.weight < 0){
                edge.icp += Math.min(ux.weight, -vx.weight);
            }else if (ux.weight < 0 && vx.weight > 0){
                edge.icp += Math.min(-ux.weight, vx.weight);
            }

            edge.maxIcfIcp = Math.max(edge.icf, edge.icp);
            edge.icfMinusIcp = edge.icf-edge.icp;
            bHeap.updateEdge(edge);
        }
    }


    private void makeEdgeForbidden(YoshikoEdge e) {
        editInfluenzeOfForbiddenEdge(e);

        e.weight=Double.NEGATIVE_INFINITY;
        bHeap.remove(e);
    }

    private void editInfluenzeOfForbiddenEdge(YoshikoEdge forbiddenEdge){
        int x1 = forbiddenEdge.source;
        int x2 = forbiddenEdge.target;

        for (int v : nodeList){
            if (x1==v||x2==v){
                continue;
            }

            YoshikoEdge edge1 = edgeArray[x1][v];
            if (edge1 == null){
                edge1 = edgeArray[v][x1];
            }
            YoshikoEdge edge2 = edgeArray[x2][v];
            if (edge2 == null){
                edge2 = edgeArray[v][x2];
            }

            if (forbiddenEdge.weight>0 && edge2.weight > 0){
                edge1.icf -= Math.min(forbiddenEdge.weight, edge2.weight);
                edge1.icp += edge2.weight;
            }

            if (forbiddenEdge.weight > 0 && edge2.weight < 0){
                edge1.icp -= Math.min(forbiddenEdge.weight, -edge2.weight);
            } else if (forbiddenEdge.weight < 0 && edge2.weight > 0){
                if (forbiddenEdge.weight*(-1) < edge2.weight){
                    edge1.icp += edge2.weight+forbiddenEdge.weight;
                }
            }
            bHeap.updateEdge(edge1);

            if (forbiddenEdge.weight>0 && edge1.weight > 0){
                edge2.icf -= Math.min(forbiddenEdge.weight, edge1.weight);
                edge2.icp += edge1.weight;
            }

            if (forbiddenEdge.weight > 0 && edge1.weight < 0){
                edge2.icp -= Math.min(forbiddenEdge.weight, -edge1.weight);
            } else if (forbiddenEdge.weight < 0 && edge1.weight > 0){
                if (forbiddenEdge.weight*(-1) < edge1.weight){
                    edge2.icp += edge1.weight+forbiddenEdge.weight;
                }
            }
            bHeap.updateEdge(edge2);
        }
    }

    private void putInCluster(YoshikoEdge edge){
        nodeInClusters++;
        Integer x1 = edge.source;
        Integer x2 = edge.target;

        int indexOfx1 = -1;
        int indexOfx2 = -1;
        for (List<Integer> cluster : clusters){
            if (cluster.contains(x1)){
                indexOfx1 = clusters.indexOf(cluster);
            }else if (cluster.contains(x2)){
                indexOfx2 = clusters.indexOf(cluster);
            }
        }

        if (indexOfx1 >= 0){
            if (indexOfx2 >= 0){
                List<Integer> cluster1 = clusters.get(indexOfx1);
                List<Integer> cluster2 = clusters.get(indexOfx2);

                cluster1.addAll(cluster2);
                clusters.remove(cluster2);
            }else {
                List<Integer> cluster1 = clusters.get(indexOfx1);
                cluster1.add(x2);
            }
        }else if (indexOfx2 >= 0){
            List<Integer> cluster2 = clusters.get(indexOfx2);
            cluster2.add(x1);
        }else{
            List<Integer> newCluster = new ArrayList<>();
            newCluster.add(x1);
            newCluster.add(x2);

            clusters.add(newCluster);
        }

    }

    private void addSingleNodesToClusters(){
        for (int i = 0; i < numberOfNodes; i++){
            nodeIsInCluster(i);
        }
    }

    private void nodeIsInCluster(int i){
        for (List<Integer> cluster : clusters){
            if (cluster.contains(i)){
                return;
            }
        }
        List<Integer> newCluster = new ArrayList<>();
        newCluster.add(i);
        clusters.add(newCluster);
    }

    private void calculateClusteringCost(){
        clusteringCost = 0;

        for (List<Integer> cluster : clusters){
            for (int i : cluster){
                for (int j : cluster){
                    if (i > j){
                        if (edgeArray[i][j].startingWeight < 0){
                            clusteringCost -= edgeArray[i][j].startingWeight;
                            System.out.println("Insert edge:("+i+","+ j+") with cost "+edgeArray[i][j].startingWeight);
                        }
                        edgeArray[i][j].startingWeight = 0;
                    }
                }
            }
        }

        for (int i = 1; i < edgeArray.length; i++){
            for (int j = 0; j < i; j++){
                if (edgeArray[i][j].startingWeight > 0){
                    clusteringCost += edgeArray[i][j].startingWeight;
                    System.out.println("Delete edge:("+i+","+ j+") with cost "+(-edgeArray[i][j].startingWeight));
                }
            }
        }
    }

    public double getClusteringCost() {
        return clusteringCost;
    }

}
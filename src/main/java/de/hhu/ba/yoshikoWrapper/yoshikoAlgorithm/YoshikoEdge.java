package de.hhu.ba.yoshikoWrapper.yoshikoAlgorithm;

public class YoshikoEdge {
    long suid;
    int edgeId;

    int source;
    int target;

    double startingWeight;
    double weight;
    double icf;
    double icp;
    double maxIcfIcp;
    double icfMinusIcp;

    public YoshikoEdge(long suid, double weight, int edgeId){
        this.suid = suid;
        this.weight = weight;
        this.edgeId = edgeId;
        makeSourceAndTarget(edgeId);
    }

    public YoshikoEdge(long suid, double weight, int u, int v){
        this.suid = suid;
        this.weight = weight;
        this.startingWeight = weight;
        this.edgeId = this.makeEdgeId(u, v);
    }

    private int makeEdgeId(int u, int v){
        if (v>u){
            this.source = v;
            this.target = u;
            return (v*(v-1))/2+u;
        }
        this.source = u;
        this.target = v;
        return (u*(u-1))/2+v;
    }

    private void makeSourceAndTarget(int edgeId){
        this.source =  (int) Math.ceil(Math.sqrt(2*(edgeId-1)+0.25)-0.5);
        this.target = edgeId - (source*(source-1))/2;
    }

}

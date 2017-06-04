package edu.ucla.cs.scai.aztec.summarization;

/**
 * the weighted edge graph class
 *
 * @author Giuseppe M. Mazzeo <mazzeo@cs.ucla.edu>
 */
public class WeightedEdgeGraph {

    double[][] weights;
    double[] outgoingWeights;

    int n; // number of nodes

    /**
     * Constructor class
      * @param n number of nodes
     */
    public WeightedEdgeGraph(int n) {
        this.n = n;
        weights = new double[n][n];
        outgoingWeights = new double[n];
    }

    /**
     * Set the weight of the edges
     * @param i edge from node
     * @param j edge to node
     * @param w the weight of the edge
     */
    public void setWeight(int i, int j, double w) {
        double diff = w - weights[i][j];
        outgoingWeights[i] += diff; // to calculate the total weight give by this node
        outgoingWeights[j] += diff;
        weights[i][j] = w;
        weights[j][i] = w;
    }

    /**
     * Add a weight to an existing one
     * @param i edge from node
     * @param j edge to node
     * @param w the weight to be added
     */
    public void addWeight(int i, int j, double w) {
        outgoingWeights[i] += w;
        outgoingWeights[j] += w;
        weights[i][j] += w;
        weights[j][i] += w;
    }

    /**
     * Compute the shortest distance of each two nodes
     *
     * @return a double distance showing the distances between each two nodes
     */
    public double[][] computeShortestDis() {
        double[][] dis = new double[n][n];
        // initialization
        for(int i =0;i<n;i++){
            for(int j =0;j<n;j++){
                if(i == j){
                    dis[i][j] = 0;
                    continue;
                }
                if(weights[i][j]!=0){
                    dis[i][j] = weights[i][j];
                }
                else{
                    dis[i][j]= Integer.MAX_VALUE;
                }
            }
        }
        for(int i = 0;i<n;i++){
            for(int j=0;j<n;j++){
                for(int k = 0;k<n;k++){
                    if(dis[i][j] > dis[i][k] + dis[k][j]){
                        dis[i][j] = dis[i][k] + dis[k][j];
                    }
                }
            }
        }
        return dis;

    }

    /**
     * Compute the rank of any two nodes
     * @param d the scale to initialize adjustedWeights
     * @param est the initialization of res
     * @param eps the small number to verify the converge
     * @return the node ranks
     */
    public double[] computeNodeRank(double d, double est, double eps) {
        double[][] adjustedWeights = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjustedWeights[i][j] = outgoingWeights[i] == 0 ? 0 : (d * weights[i][j] / outgoingWeights[i]);
            }
        }
        double[] res = new double[n];
        for (int i = 0; i < n; i++) {
            res[i] = est;
        }
        boolean steady = false;
        while (!steady) {
            double[] newRes = new double[n];
            for (int v = 0; v < n; v++) {
                //update rank of v
                newRes[v] = 1 - d; //this should be divided by the number of nodes!
                for (int u = 0; u < n; u++) {
                    if (u != v) {
                        newRes[v] += res[u] * adjustedWeights[u][v];
                    }
                }
            }
            steady = true;
            for (int i = 0; i < n; i++) {
                if (Math.abs(newRes[i] - res[i]) > eps) {
                    steady = false;
                    break;
                }
            }
            res = newRes;
        }
        return res;
    }

    public static void main(String[] args) {
        WeightedEdgeGraph g = new WeightedEdgeGraph(13);
        g.setWeight(0, 1, 1);
        g.setWeight(0, 2, 1);
        g.setWeight(0, 3, 1);
        g.setWeight(0, 4, 1);
        g.setWeight(1, 5, 1);
        g.setWeight(1, 6, 1);
        g.setWeight(2, 7, 1);
        g.setWeight(2, 8, 1);
        g.setWeight(3, 9, 1);
        g.setWeight(3, 10, 1);
        g.setWeight(4, 11, 1);
        g.setWeight(4, 12, 1);
        double[] r = g.computeNodeRank(0.85, 1, 0.01);
        for (int i = 0; i < r.length; i++) {
            System.out.println(r[i]);
        }
    }

}

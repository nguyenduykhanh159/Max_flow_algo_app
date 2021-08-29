package solver;

import entity.Edge;
import entity.Node;
import entity.Network;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class FordFulkersonSolver extends NetworkFlowSolver{
    /**
     * @param network
     */
    public FordFulkersonSolver(Network network) {
        super(network);
    }
    public int visitedToken = 1;
    public static final long INF = Long.MAX_VALUE;
    // Performs the Ford-Fulkerson method applying a depth first search as
    // a means of finding an augmenting path.
    @Override
    public void solve() {
        Node s = network.getS();
        // Find max flow by adding all augmenting path flows.
        for (long f = dfs(s, INF); f != 0; f = dfs(s, INF)) {
            visitedToken++;
            result.clear();
            network.setMaxFlow(f); // maxFlow += flow;
        }
    }

    public long dfs(Node node, long flow) {
        // At sink node, return augmented path flow.
        if (node.equals(network.getT())) return flow;

        // Mark the current node as visited.
        node.setVisited(visitedToken);

        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < network.getEdges().size(); i++) {
            Edge edge = network.getEdges().get(i);
            if (node.equals(edge.getSource())) {
                edges.add(edge);
            }
        }
        for (Edge edge : edges) {
            if (edge.remainingCapacity() > 0 && edge.getTarget().getVisited() != visitedToken) {
                long bottleNeck = dfs(edge.getTarget(), min(flow, edge.remainingCapacity()));
                if (bottleNeck > 0) {
                    result.add(edge);
                    edge.augment(bottleNeck);
                    return bottleNeck;
                }
            }
        }
        return 0;
    }
}

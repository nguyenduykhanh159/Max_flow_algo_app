package solver;

import entity.Edge;
import entity.Node;
import entity.Network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.min;

public class EdmondsKarpSolver extends NetworkFlowSolver{
    /**
     * Creates an instance of a flow network solver.Use the { #addEdge(int, int, int)} method
     to add edges to the graph.
     *
     * @param network
     */
    public EdmondsKarpSolver(Network network) {
        super(network);
    }
    public int visitedToken = 0;

    // Run Edmonds-Karp and compute the max flow from the source to the sink node.
    @Override
    public void solve() {
        long flow;
        do {
            visitedToken++;
            flow = bfs();
            network.setMaxFlow(flow); // maxFlow += flow
        } while (flow != 0);
    }

    public long bfs() {

        // Initialize BFS queue and add starting source node.
        int n = network.getN();
        Queue<Node> q = new ArrayDeque<>(n);
        Node s = network.getS();
        Node t = network.getT();
        s.setVisited(visitedToken);
        q.offer(s);
        // Perform BFS from source to sink
        List<Edge> prev = new ArrayList<>();

        while (!q.isEmpty()) {
            Node node = q.poll();
            if (node.equals(t)) break;

            List<Edge> edges = new ArrayList<>();
            for (int i = 0; i < this.network.getEdges().size(); i++) {
                Edge edge = this.network.getEdges().get(i);
                if (node.equals(edge.getSource())) {
                    edges.add(edge);
                }
            }

            edges.forEach(edge -> {
                long cap = edge.remainingCapacity();
                if (cap > 0 && edge.getTarget().getVisited() != visitedToken) {
                    edge.getTarget().setVisited(visitedToken);
                    prev.add(edge);
                    q.offer(edge.getTarget());
                }
            });
        }

        // Sink not reachable!
        int index = 0;
        int reach = 0;

        for (int i = 0; i < prev.size(); i++) {
            if (prev.get(i).getTarget().equals(t)) {
                reach = 1;
                index = i;
                break;
            }
        }
        if (reach == 0) return 0;

        long bottleNeck = Long.MAX_VALUE;
        int next = Integer.MAX_VALUE;

        for (Edge edge = prev.get(index); edge != null ; edge = prev.get(next)) {
            bottleNeck = min(bottleNeck, edge.remainingCapacity());
            result.add(edge);
            if (edge.getSource().equals(s)) {
                break;
            }
            Node node = edge.getSource();
            for (int i = prev.size()-1; i >= 0; i--) {
                if (node.equals(prev.get(i).getTarget())) {
                    next = i;
                    break;
                }
            }
        }

        next = Integer.MAX_VALUE;
        for (Edge edge = prev.get(index); edge != null ; edge = prev.get(next)) {
            edge.augment(bottleNeck);
            if (edge.getSource().equals(s)) {
                break;
            }
            Node node = edge.getSource();
            for (int i = prev.size() - 1; i >= 0; i--) {
                if (node.equals(prev.get(i).getTarget())) {
                    next = i;
                    break;
                }
            }
        }
        return bottleNeck;
    }
}

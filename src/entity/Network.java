package entity;

import entity.Edge;
import entity.Node;

import java.util.ArrayList;
import java.util.List;

public class Network {
    // Inputs: n = number of nodes, s = source, t = sink
    private Node s;
    private Node t;
    // The adjacency list representing the flow graph.
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    // The maximum flow. Calculated by calling the {@link #solve} method.
    private long maxFlow;

    // To avoid overflow, set infinity to a value less than Long.MAX_VALUE;
    static final long INF = Long.MAX_VALUE / 2;
    /**
     * Creates an instance of a flow network solver. Use the {@link #addEdge} method to add edges to
     * the graph.
     *
     */

    public Network() {
    }

    public Network(List<Node> nodes, Node s, Node t) {
        this.s = s;
        this.t = t;
        this.maxFlow = 0;
        this.nodes = nodes;
    }

    // Constructs an empty graph with n nodes including s and t

    /**
     * Adds a directed edge (and its residual edge) to the flow graph.
     *
     * @param from - The index of the node the directed edge starts at.
     * @param to - The index of the node the directed edge ends at.
     * @param capacity - The capacity of the edge
     */
    public void addEdge(Node from, Node to, long capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Forward edge capacity <= 0");
        Edge e1 = new Edge(from, to, capacity);
        Edge e2 = new Edge(to, from, 0);
        e1.setResidual(e2);
        e2.setResidual(e1);
        this.edges.add(e1);
        this.edges.add(e2);
    }

    public void setMaxFlow(long flow) {
        this.maxFlow += flow;
    }

    // Return number of nodes
    public int getN() {
        return this.nodes.size();
    }

    public Node getS() {
        return s;
    }

    public Node getT() {
        return t;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    // Returns the maximum flow from the source to the sink.
    public long getMaxFlow() {
        return maxFlow;
    }
}

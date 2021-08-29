/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import javafx.scene.control.Label;
import javafx.scene.shape.Shape;

/**
 *
 * @author sowme
 */
public class Edge {

    private Node source, target;
    private long capacity;
    public Shape line;
    public Label weightLabel;
    private Edge residual;
    private long flow;

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public Edge getResidual() {
        return residual;
    }

    public void setResidual(Edge residual) {
        this.residual = residual;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public Shape getLine() {
        return line;
    }

    public Edge(Node argSource, Node argTarget) {
        source = argSource;
        target = argTarget;
        capacity = 0;
    }

    public Edge(Node node1, Node node2, long capacity) {
        source = node1;
        target = node2;
        this.capacity = capacity;
    }

    public Edge(Node argSource, Node argTarget, long argCapacity, Shape argline, Label weiLabel) {
        source = argSource;
        target = argTarget;
        capacity = argCapacity;
        line = argline;
        this.weightLabel = weiLabel;
    }

    public Edge(Node source, Node target, long capacity, Shape line, Label weightLabel, Edge residual, long flow) {
        this.source = source;
        this.target = target;
        this.capacity = capacity;
        this.line = line;
        this.weightLabel = weightLabel;
        this.residual = residual;
        this.flow = flow;
    }
    public Edge copy() {
        Edge a = new Edge(this.source, this.target, this.capacity, this.line, this.weightLabel,this.residual,this.flow);
        return a;
    }

    public boolean isResidual() {
        return capacity == 0;
    }

    public long remainingCapacity() {
        return capacity - flow;
    }

    public void augment(long bottleNeck) {
        flow += bottleNeck;
        residual.flow -= bottleNeck;
    }
    public boolean equals(Edge e){
        return this.source.equals(e.source)&&this.target.equals(e.target);
    }

    @Override
    public String toString() {
        String u = source.getName();
        String v = target.getName();
//        String u = String.valueOf(from);
//        String v = String.valueOf(to);
        return String.format(
                "Edge %s -> %s | flow = %3d | capacity = %3d | is residual: %s",
                u, v, flow, capacity, isResidual());
    }
}

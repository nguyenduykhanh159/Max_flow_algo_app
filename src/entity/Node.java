/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import sample.MenuController;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sowme
 */
public class Node{

    private String name;
    private List<Edge> adjacents = new ArrayList<Edge>();
    private MenuController.NodeFX circle;
    private int visited;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edge> getAdjacents() {
        return adjacents;
    }

    public void setAdjacents(List<Edge> adjacents) {
        this.adjacents = adjacents;
    }

    public MenuController.NodeFX getCircle() {
        return circle;
    }

    public void setCircle(MenuController.NodeFX circle) {
        this.circle = circle;
    }

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public Node() {

    }
    public Node(String argName) {
        name = argName;
        visited = 0;
    }

    public Node(String argName, MenuController.NodeFX c) {
        name = argName;
        circle = c;
        visited = 0;
    }

    public Node(String name, List<Edge> adjacents, MenuController.NodeFX circle, int visited) {
        this.name = name;
        this.adjacents = adjacents;
        this.circle = circle;
        this.visited = visited;
    }

    public Node copy() {
        Node a = new Node(this.name, this.adjacents, this.circle, this.visited);
        return a;
    }
    public static List<Node> copyList(List<Node> a){
        List<Node> copy = new ArrayList<Node>();
        for(int i = 0; i < a.size(); i++)
        {
            copy.add(a.get(i).copy());
        }
        return copy;
    }
    public boolean equals(Node o)
    {
        return this.name == o.name;
    }
}

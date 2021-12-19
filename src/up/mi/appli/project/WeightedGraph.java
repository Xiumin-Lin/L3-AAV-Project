// Par Sylvain Lobry, pour le cours "IF05X040 Algorithmique avancée"
// de l'Université de Paris, 11/2020

package up.mi.appli.project;

import java.util.ArrayList;
import java.util.LinkedList;

// Classe définissant un graphe pondéré.
public class WeightedGraph {
    private WeightedGraph() {
    }

    // Sous-classe pour une arrête.
    static class Edge {
        int source;
        int destination;
        double weight;

        public Edge(int source, int destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    // Sous-classe pour un sommet.
    static class Vertex {
        char value;
        double indivTime;
        double timeFromSource;
        double heuristic;
        LinkedList<Edge> adjacencylist;
        int num;

        public Vertex(int num, char value) {
            this.indivTime = Double.POSITIVE_INFINITY;
            this.timeFromSource = Double.POSITIVE_INFINITY;
            this.heuristic = -1;
            this.adjacencylist = new LinkedList<>();
            this.num = num;
            this.value = value;
        }

        public void addNeighbor(Edge e) {
            this.adjacencylist.addFirst(e);
        }
    }

    //Sous-classe pour le graphe.
    static class Graph {
        ArrayList<Vertex> vertexlist;
        int numV = 0;

        Graph() {
            vertexlist = new ArrayList<>();
        }

        public void addVertex(double indivTime, char value) {
            Vertex v = new Vertex(numV, value);
            v.indivTime = indivTime;
            vertexlist.add(v);
            numV = numV + 1;
        }

        public void addEgde(int source, int destination, double weight) {
            Edge edge = new Edge(source, destination, weight);
            vertexlist.get(source).addNeighbor(edge);
        }
    }
}

// Par Sylvain Lobry, pour le cours "IF05X040 Algorithmique avancée"
// de l'Université de Paris, 11/2020

package up.mi.appli.td7and8;

import java.util.ArrayList;
import java.util.LinkedList;

// Classe définissant un graphe pondéré.
public class WeightedGraph {
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
        double indivTime;
        double timeFromSource;
        double heuristic;
        Vertex prev;
        LinkedList<Edge> adjacencylist;
        int num;

        public Vertex(int num) {
            this.indivTime = Double.POSITIVE_INFINITY;
            this.timeFromSource = Double.POSITIVE_INFINITY;
            this.heuristic = -1;
            this.prev = null;
            this.adjacencylist = new LinkedList<>();
            this.num = num;
        }

        public void addNeighbor(Edge e) {
            this.adjacencylist.addFirst(e);
        }
    }

    //Sous-classe pour le graphe.
    static class Graph {
        ArrayList<Vertex> vertexlist;
        int num_v = 0;

        Graph() {
            vertexlist = new ArrayList<>();
        }

        public void addVertex(double indivTime) {
            Vertex v = new Vertex(num_v);
            v.indivTime = indivTime;
            vertexlist.add(v);
            num_v = num_v + 1;
        }

        public void addEgde(int source, int destination, double weight) {
            Edge edge = new Edge(source, destination, weight);
            vertexlist.get(source).addNeighbor(edge);
        }

    }

    //Test de la classe.
    public static void main(String[] args) {
        int vertices = 6;
        Graph graph = new Graph();
        graph.addVertex(10);
        graph.addVertex(10);
        graph.addVertex(10);
        graph.addVertex(10);
        graph.addVertex(10);
        graph.addVertex(10);
        graph.addEgde(0, 1, 4);
        graph.addEgde(0, 2, 3);
        graph.addEgde(1, 3, 2);
        graph.addEgde(1, 2, 5);
        graph.addEgde(2, 3, 7);
        graph.addEgde(3, 4, 2);
        graph.addEgde(4, 0, 4);
        graph.addEgde(4, 1, 4);
        graph.addEgde(4, 5, 6);
        //graph.printGraph();
    }
}

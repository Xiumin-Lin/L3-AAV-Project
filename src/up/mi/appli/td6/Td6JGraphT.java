package up.mi.appli.td6;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;

public class Td6JGraphT {

    public static void main(String[] args) {
        // Exemple inspiré de la docs de JGraphT
        // Création d'un graph simple orienté avec boucle
        Graph<String, DefaultEdge> directedGraph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        // ajout des sommets
        directedGraph.addVertex("a");
        directedGraph.addVertex("b");
        directedGraph.addVertex("c");
        directedGraph.addVertex("d");
        // ajout des arêtes
        directedGraph.addEdge("a", "b"); //     "a" --> "b"
        directedGraph.addEdge("b", "d"); //      ^       |
        directedGraph.addEdge("d", "c"); //      |       v
        directedGraph.addEdge("c", "a"); //     "c" <-- "d"
        // affichage
        System.out.println("Ensemble des sommets : " + directedGraph.vertexSet());
        System.out.println("Ensemble des arêtes orienté (a-->b) : " + directedGraph.edgeSet());

        // à partir du 1er graph, on va venir ajouter de nouveaux elements de manière plus simple
        Graph<String, DefaultEdge> secondDirectedGraph = new GraphBuilder<>(directedGraph)
                .addVertex("e") // un nouveau sommet
                .addEdgeChain("e", "a", "d", "e") // nouveau arêtes : e-->a-->d-->e
                .build();
        System.out.println("Ensemble des sommets v2 : " + secondDirectedGraph.vertexSet());
        System.out.println("Ensemble des arêtes orienté (a-->b) v2 : " + secondDirectedGraph.edgeSet());
    }
}

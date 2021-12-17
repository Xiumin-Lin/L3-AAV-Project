package up.mi.appli.td;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;

public class Td6 {

    public static void main(String[] args) {
        // Exemple inspiré de la docs de JGRaphT
        Graph<String, DefaultEdge> directedGraph = new DefaultUndirectedGraph<>(DefaultEdge.class);
        directedGraph.addVertex("a");
        directedGraph.addVertex("b");
        directedGraph.addVertex("c");
        directedGraph.addVertex("d");
        directedGraph.addEdge("a", "b"); //     "a" --> "b"
        directedGraph.addEdge("b", "d"); //      ^       |
        directedGraph.addEdge("d", "c"); //      |       v
        directedGraph.addEdge("c", "a"); //     "c" <-- "d"
        System.out.println("Ensemble des sommets : " + directedGraph.vertexSet());
        System.out.println("Ensemble des arêtes orienté (a-->b) : " + directedGraph.edgeSet());

        // à partir du 1er graph, on va venir ajouter de nouveau elements
        Graph<String, DefaultEdge> secondDirectedGraph = new GraphBuilder<>(directedGraph)
                .addVertex("e") // un nouveau sommet
                .addEdgeChain("e", "a",  "d", "e") // nouveau arêtes : e-->a-->d-->e
                .build();
        System.out.println("Ensemble des sommets : " + secondDirectedGraph.vertexSet());
        System.out.println("Ensemble des arêtes orienté (a-->b) : " + secondDirectedGraph.edgeSet());
    }
}

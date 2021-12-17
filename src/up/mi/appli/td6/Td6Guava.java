package up.mi.appli.td6;

import com.google.common.graph.*;

public class Td6Guava {
    public static void main(String[] args) {
        // Exemple inspiré de la docs de Google Guava sur les graphs
        // Création de graphe orienté mutable
        MutableGraph<String> graph = GraphBuilder.directed().build();
        graph.addNode("a");
        graph.addNode("b");
        graph.putEdge("a", "b");
        graph.putEdge("d","c");     // si ajout d'arête avec des sommets inexistants
        graph.putEdge("b", "d");    // alors les sommets sont crées en même temps
        graph.putEdge("c", "a");
        System.out.println("Graphe simple : " + graph);

        // Création de graphe valué et non-orienté mutable
        MutableValueGraph<String, Integer> valueGraph = ValueGraphBuilder.undirected()
                .incidentEdgeOrder(ElementOrder.stable())
                .build();
        valueGraph.putEdgeValue("a", "b", 1);
        valueGraph.putEdgeValue("b", "d", 2);
        valueGraph.putEdgeValue("d", "c", 5);
        valueGraph.putEdgeValue("c", "a", 3);
        System.out.println("Graphe valué : " + valueGraph);

        // Création de graphe immuable
        ImmutableGraph<String> countryAdjacencyGraph = GraphBuilder.undirected()
                .<String>immutable()
                .putEdge("FRANCE", "GERMANY")
                .putEdge("FRANCE", "BELGIUM")
                .putEdge("GERMANY", "BELGIUM")
                .addNode("ICELAND")
                .build();
        System.out.println("Graph immuable : " + countryAdjacencyGraph);
    }
}

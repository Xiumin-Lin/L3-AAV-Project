package up.mi.appli.project;

import java.util.HashSet;
import java.util.LinkedList;
import static up.mi.appli.project.WeightedGraph.Graph;

public class Algorithm {

    /**
     * Methode A*
     *
     * @param graph           le graphe representant la carte
     * @param start           un entier representant la case de depart
     * @param end             un entier representant la case d'arrivee
     * @param ncols           le nombre de colonnes dans la carte
     * @param numberV         le nombre de cases dans la carte
     * @param heuristicWeight le poids de l'heuristique
     * @return une liste d'entiers correspondant au chemin.
     */
    public static LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, int heuristicWeight) {
        graph.vertexlist.get(start).timeFromSource = 0;
        int number_tries = 0;

        //TODO: mettre tous les noeuds du graphe dans la liste des noeuds à visiter:
        HashSet<Integer> to_visit = new HashSet<>();
        for(int i = 0; i < numberV; i++) {
            to_visit.add(i);
        }
        //TODO: Remplir l'attribut graph.vertexlist.get(v).heuristic pour tous les noeuds v du graphe:
        int endX = graph.vertexlist.get(end).num / ncols;
        int endY = graph.vertexlist.get(end).num % ncols;
        for(int i = 0; i < graph.numV; i++) {
            int nodeX = graph.vertexlist.get(i).num / ncols;
            int nodeY = graph.vertexlist.get(i).num % ncols;
            graph.vertexlist.get(i).heuristic = Math.sqrt(Math.pow((endX - nodeX), 2) + Math.pow((endY - nodeY), 2)) * heuristicWeight;
        }

        while(to_visit.contains(end)) {
            //TODO: trouver le noeud min_v parmis tous les noeuds v ayant la distance temporaire
            //      (graph.vertexlist.get(v).timeFromSource + heuristic) minimale.
            int min_v = -1;
            double minimal = Double.POSITIVE_INFINITY;
            for(int v : to_visit) {
                WeightedGraph.Vertex vertex = graph.vertexlist.get(v);
                double tmpTimeFromSrc = vertex.timeFromSource + vertex.heuristic;
                if(tmpTimeFromSrc < minimal) {
                    minimal = tmpTimeFromSrc;
                    min_v = v;
                }
            }
            if(min_v == -1) {
                System.out.println("Pas de solution !");
                return null;
            }
            //On l'enleve des noeuds à visiter
            to_visit.remove(min_v);
            number_tries += 1;

            //TODO: pour tous ses voisins, on vérifie si on est plus rapide en passant par ce noeud.
            for(int i = 0; i < graph.vertexlist.get(min_v).adjacencylist.size(); i++) {
                int to_try = graph.vertexlist.get(min_v).adjacencylist.get(i).destination;
                double weight = graph.vertexlist.get(min_v).adjacencylist.get(i).weight;
                double newTimeFromSource = graph.vertexlist.get(min_v).timeFromSource + weight;
                if(newTimeFromSource < graph.vertexlist.get(to_try).timeFromSource) {
                    graph.vertexlist.get(to_try).timeFromSource = newTimeFromSource;
                }
            }

        }

        System.out.println("Done! Using A*:");
        System.out.println("\tNumber of nodes explored: " + number_tries);
        System.out.println("\tTotal time of the path: " + graph.vertexlist.get(end).timeFromSource);
        LinkedList<Integer> path = new LinkedList<>();
        path.addFirst(end);
        // remplir la liste path avec le chemin
        int v = end;
        double minTime = graph.vertexlist.get(v).timeFromSource;
        while(v != start) {
            for(WeightedGraph.Edge e : graph.vertexlist.get(v).adjacencylist) {
                double neighBourTime = graph.vertexlist.get(e.destination).timeFromSource;
                if(neighBourTime < minTime) {
                    minTime = neighBourTime;
                    v = e.destination;
                }
            }
            path.addFirst(v);
        }
        return path;
    }
}

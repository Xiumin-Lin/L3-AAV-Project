package up.mi.appli.project;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static up.mi.appli.project.WeightedGraph.Graph;
import static up.mi.appli.project.WeightedGraph.Vertex;

public class Labyrinthe {
    // Les symboles qui representent le labyrinthe
    public static final char FREE = '.';
    public static final char WALL = '#';
    public static final char FIRE = 'F';
    public static final char NEXT_TO_BURN = 'A';
    public static final char PRISONER = 'D';
    public static final char PASSED = 'L';
    public static final char END = 'S';

    public static final char WIN = 'Y';
    public static final char LOSE = 'N';

    public static void main(String[] args) {
        // Le labyrinthe: s’échapper d’Ayutthaya
        double heuristicWeight = 1;
        System.out.print("Entrer le nom du fichier avec les entrees : ");
        try(Scanner sc = new Scanner(System.in); Scanner fileScanner = new Scanner(new File(sc.nextLine()))) {
            System.out.print("Entrer le poids de l'heuristique (Ex : 2.5 ou 2,5 selon votre OS) : ");
            heuristicWeight = sc.nextDouble();
            int t = fileScanner.nextInt(); // le nombre d'instance

            for(int numInstance = 0; numInstance < t; numInstance++) {
                System.out.println("Instance num : " + numInstance); // debug pour plus de clarté
                int n = fileScanner.nextInt(); // le nombre de ligne
                int m = fileScanner.nextInt(); // le nombre de symbole par ligne

                // creation & initialisation de la carte
                Graph map = new Graph();
                for(int row = 0; row < n; row++) {
                    String ligne = fileScanner.next();
                    for(int col = 0; col < ligne.length(); col++) {
                        // ajout des sommets et de leur temps pour etre parcouru
                        if(ligne.charAt(col) == FREE || ligne.charAt(col) == PRISONER || ligne.charAt(col) == END)
                            map.addVertex(1, ligne.charAt(col));
                        else map.addVertex(Double.POSITIVE_INFINITY, ligne.charAt(col));
                    }
                }
                showMap(map, m);
                // ajout des aretes
                for(int line = 0; line < n; line++) {
                    for(int col = 0; col < m; col++) {
                        int source = line * m + col;
                        int dest;
                        double weight;
                        double srcIndivTime = map.vertexlist.get(source).indivTime;
                        if(line > 0) {          // Si Non 1er ligne
                            dest = (line - 1) * m + col;
                            weight = (srcIndivTime + map.vertexlist.get(dest).indivTime) / 2;
                            map.addEgde(source, dest, weight);        // arete vertical haut : source -> dest
                            map.addEgde(dest, source, weight);        // vice versa
                        }
                        if(col < m - 1) {       // Si 1er ligne && Non dernier col
                            dest = source + 1;
                            weight = (srcIndivTime + map.vertexlist.get(dest).indivTime) / 2;
                            map.addEgde(source, dest, weight);        // arête horizontal droite : source -> dest
                            map.addEgde(dest, source, weight);        // vice versa
                        }
                    }
                }
                // lancement de la recherche de solution
                System.out.println(runInstance(map, n, m, heuristicWeight));
                System.out.println(); // debug pour ameliorer l'affichage en separant les instances
            }
        } catch(Exception e) {
            System.out.println("[Erreur] " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lance une resolution du labyrinthe donnee en parametre
     *
     * @param map             le labyrinthe a resoudre
     * @param n               le nombre de ligne du labyrinthe
     * @param m               le nombre de symbole par ligne du labyrinthe
     * @param heuristicWeight le poids de l'heuristique pour l'algo A*
     * @return la valeur de {@link #WIN} si le prisonnier arrive a la sortie,
     * sinon retourne la valeur de {@link #LOSE}
     */
    private static char runInstance(Graph map, int n, int m, double heuristicWeight) {
        int turn = 0;
        while(turn < n * m) {
            // on fait propager les flammes
            for(int v = 0; v < n * m; v++) {
                if(map.vertexlist.get(v).value == NEXT_TO_BURN) map.vertexlist.get(v).value = FIRE;
            }
            // les cases en feu marquent leurs cases voisines qui vont prendre feu au prochain tour
            for(int numV = 0; numV < n * m; numV++) {
                if(map.vertexlist.get(numV).value == FIRE && burnAround(numV, n, m, map)) return LOSE;
            }
            // au tour du prisonnier de se deplacer
            boolean isWin = movePrisonerWithAStar(n, m, map, heuristicWeight);
            if(isWin) return WIN;
            showMap(map, m); // debug afficher labyrinthe à chaque tour
            turn++;
        }
        return LOSE;
    }

    /**
     * Pour la case {@link #FIRE} donnée en param, les cases {@link #FREE} qui lui sont adjacents
     * par le haut, le bas, la gauche et la droite deviennent des cases {@link #NEXT_TO_BURN}.
     * Si une case adjacents represent la sortie ({@link #END}) ou bien le prisonnier ({@link #PRISONER}),
     * alors la partie est terminée et le prisonnier a perdu.
     *
     * @param numV le num de la case {@link #FIRE}
     * @param n    le nombre de ligne du labyrinthe
     * @param m    le nombre de symbole par ligne du labyrinthe
     * @param map  le labyrinthe
     * @return true si l
     */
    private static boolean burnAround(int numV, int n, int m, Graph map) {
        int row = map.vertexlist.get(numV).num / m;
        int col = map.vertexlist.get(numV).num % m;
        // Si la case actuel n'est pas sur la 1ere ligne,
        if(row > 0) { // alors sa case du haut va bruler au prochain tour s'il est libre
            int topVertex = (row - 1) * m + col;
            if(burn(map, topVertex)) return true;
        }
        // Si la case actuel n'est pas sur la dernire ligne
        if(row < n - 1) { // alors sa case du bas va bruler au prochain tour s'il est libre
            int bottomVertex = (row + 1) * m + col;
            if(burn(map, bottomVertex)) return true;
        }
        // Si la case actuel n'est pas sur la 1ere colonne
        if(col > 0) { // alors sa case de gauche va bruler au prochain tour s'il est libre
            int leftVertex = numV - 1;
            if(burn(map, leftVertex)) return true;
        }
        // Si la case actuel n'est pas sur la derniere colonne
        if(col < m - 1) { // alors sa case de droite va bruler au prochain tour s'il est libre
            int rightVertex = numV + 1;
            return burn(map, rightVertex);
        }
        return false;
    }

    /**
     * @param map       le labyrinthe
     * @param vertexNum le num du sommet a etre brulee
     * @return true si la prochaine case a brulee est la case sortie ou bien celle du prisonnier
     */
    private static boolean burn(Graph map, int vertexNum) {
        if(map.vertexlist.get(vertexNum).value == FREE) {
            map.vertexlist.get(vertexNum).value = NEXT_TO_BURN;
            // on met a jour le temps du sommet et le poids de ses aretes
            map.vertexlist.get(vertexNum).indivTime = Double.POSITIVE_INFINITY;
            // pour chaque arete du sommet brulee, on met a jour son poid
            for(int edge = 0; edge < map.vertexlist.get(vertexNum).adjacencylist.size(); edge++) {
                int dest = map.vertexlist.get(vertexNum).adjacencylist.get(edge).destination;
                map.vertexlist.get(vertexNum).adjacencylist.get(edge).weight = (map.vertexlist.get(vertexNum).indivTime + map.vertexlist.get(dest).indivTime) / 2;
            }
        } else return map.vertexlist.get(vertexNum).value == END || map.vertexlist.get(vertexNum).value == PRISONER;
        return false;
    }

    /**
     * Deplace le prisonnier sur une case adjacent de sa position si c'est possible.
     * Si une case adjacent est la sortie, retourne true sinon false
     *
     * @param n               le nombre de ligne du labyrinthe
     * @param m               le nombre de symbole par ligne du labyrinthe
     * @param map             le labyrinthe
     * @param heuristicWeight le poids de l'heuristique pour l'algo A*
     * @return true si le prisonnier a atteint la sortie
     */
    private static boolean movePrisonerWithAStar(int n, int m, Graph map, double heuristicWeight) {
        int prisonerVertexNum = -1;
        int exitVertexNum = -1;
        // on recupere les position du prisonnier et de la sortie
        for(int numV = 0; numV < n * m; numV++) {
            if(map.vertexlist.get(numV).value == END) {
                exitVertexNum = numV;
            } else if(map.vertexlist.get(numV).value == PRISONER) {
                prisonerVertexNum = numV;
            }
        }
        if(prisonerVertexNum >= 0 && exitVertexNum >= 0) {
            // on verifie si le prisonnier a cote de la sortie
            if(winMove(prisonerVertexNum, n, m, map)) return true;
            // sinon on utilise A star pour trouver un chemin
            List<Integer> path = Algorithm.AStar(map, prisonerVertexNum, exitVertexNum, m, n * m, heuristicWeight);
            if(path.size() > 1) {
                map.vertexlist.get(path.get(1)).value = PRISONER;
                map.vertexlist.get(prisonerVertexNum).value = FREE;
                System.out.println(path); // debug
            }
        }
        return false;
    }

    /**
     * Indique si son prochaine déplacement est gagnant (il peut atteindre la sortie indiqué par {@link #END}
     *
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si une case voisine à celle du prisonnier est la sortie du labyrinthe
     */
    private static boolean winMove(int pVertexNum, int n, int m, Graph map) {
        int pRow = pVertexNum / m;
        int pCol = pVertexNum % m;
        boolean top = pRow > 0 && map.vertexlist.get((pRow - 1) * m + pCol).value == END;
        boolean bottom = pRow < (n - 1) && map.vertexlist.get((pRow + 1) * m + pCol).value == END;
        boolean left = pCol > 0 && map.vertexlist.get(pRow * m + pCol - 1).value == END;
        boolean right = pCol < (m - 1) && map.vertexlist.get(pRow * m + pCol + 1).value == END;
        return top || left || right || bottom;
    }

    /**
     * Affiche le labyrinthe
     *
     * @param map le labyrinthe
     */
    private static void showMap(Graph map, int m) {
        int i = 0;
        for(Vertex vertex : map.vertexlist) {
            System.out.print(vertex.value);
            i++;
            if(i == m) {
                System.out.println();
                i = 0;
            }
        }
        System.out.println();
    }
}

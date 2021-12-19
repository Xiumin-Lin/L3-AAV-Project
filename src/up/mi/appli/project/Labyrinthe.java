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
        System.out.print("Entrer le nom du fichier avec les entrees : ");
        try(Scanner sc = new Scanner(System.in);
            Scanner fileScanner = new Scanner(new File(sc.nextLine()))) {
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
                System.out.println(runInstance(map, n, m, false));
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
     * @param map le labyrinthe a resoudre
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @return la valeur de {@link #WIN} si le prisonnier arrive a la sortie,
     * sinon retourne la valeur de {@link #LOSE}
     */
    private static char runInstance(Graph map, int n, int m, Boolean useAStar) {
        int turn = 0;
        while(turn < n * m) {
            // on fait propager les flammes
            for(int v = 0; v < n * m; v++) {
                if(map.vertexlist.get(v).value == NEXT_TO_BURN) map.vertexlist.get(v).value = FIRE;
            }
            // les cases en feu marquent leurs cases voisines qui vont prendre feu au prochain tour
            for(int numV = 0; numV < n * m; numV++) {
                if(map.vertexlist.get(numV).value == FIRE && burnAround(numV, n, m, map))
                    return LOSE;
            }
            // au tour du prisonnier de se deplacer
            boolean isWin = Boolean.TRUE.equals(useAStar) ? movePrisonerWithAStar(n, m, map) : movePrisonerNaive(n, m, map);
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
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si l
     */
    private static boolean burnAround(int numV, int n, int m, Graph map) {
        int row = map.vertexlist.get(numV).num / m;
        int col = map.vertexlist.get(numV).num % m;
        // Si la case actuel n'est pas sur la 1ere ligne,
        if(row > 0) { // alors sa case du haut va bruler au prochain tour s'il est libre
            int topVertex = (row - 1) * m + col;
            if(map.vertexlist.get(topVertex).value == FREE) map.vertexlist.get(topVertex).value = NEXT_TO_BURN;
            else if(map.vertexlist.get(topVertex).value == END || map.vertexlist.get(topVertex).value == PRISONER)
                return true;
        }
        // Si la case actuel n'est pas sur la dernire ligne
        if(row < n - 1) { // alors sa case du bas va bruler au prochain tour s'il est libre
            int bottomVertex = (row + 1) * m + col;
            if(map.vertexlist.get(bottomVertex).value == FREE) map.vertexlist.get(bottomVertex).value = NEXT_TO_BURN;
            else if(map.vertexlist.get(bottomVertex).value == END || map.vertexlist.get(bottomVertex).value == PRISONER)
                return true;
        }
        // Si la case actuel n'est pas sur la 1ere colonne
        if(col > 0) { // alors sa case de gauche va bruler au prochain tour s'il est libre
            int leftVertex = numV - 1;
            if(map.vertexlist.get(leftVertex).value == FREE) map.vertexlist.get(leftVertex).value = NEXT_TO_BURN;
            else if(map.vertexlist.get(leftVertex).value == END || map.vertexlist.get(leftVertex).value == PRISONER)
                return true;
        }
        // Si la case actuel n'est pas sur la derniere colonne
        if(col < m - 1) { // alors sa case de droite va bruler au prochain tour s'il est libre
            int rightVertex = numV + 1;
            if(map.vertexlist.get(rightVertex).value == FREE) map.vertexlist.get(rightVertex).value = NEXT_TO_BURN;
            else
                return map.vertexlist.get(rightVertex).value == END || map.vertexlist.get(rightVertex).value == PRISONER;
        }
        return false;
    }

    /**
     * // TODO
     * Deplace le prisonnier sur une case adjacent de sa position si c'est possible.
     * Si une case adjacent est la sortie, retourne true sinon false
     *
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si le prisonnier a atteint la sortie
     */
    private static boolean movePrisonerWithAStar(int n, int m, Graph map) {
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
        if(winMove(prisonerVertexNum, n, m, map)) return true;
        if(prisonerVertexNum < 0 && exitVertexNum < 0) return false;
        List<Integer> path = Algorithm.AStar(map, prisonerVertexNum, exitVertexNum, m, n * m, 1);
        System.out.println(path); // debug

        return false;
    }

    /**
     * // TODO
     * Deplace le prisonnier sur une case adjacent de sa position si c'est possible.
     * Si une case adjacent est la sortie, retourne true sinon false
     *
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si le prisonnier a atteint la sortie
     */
    private static boolean movePrisonerNaive(int n, int m, Graph map) {
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
        if(winMove(prisonerVertexNum, n, m, map)) return true;

        int nbMove = canMove(prisonerVertexNum, n, m, map);

        if(nbMove > 0) {
            boolean top = canMoveDir(prisonerVertexNum, Direction.TOP, n, m, map);
            boolean bottom = canMoveDir(prisonerVertexNum, Direction.BOT, n, m, map);
            boolean left = canMoveDir(prisonerVertexNum, Direction.LEFT, n, m, map);
            boolean right = canMoveDir(prisonerVertexNum, Direction.RIGHT, n, m, map);
            int pRow = prisonerVertexNum / m;
            int pCol = prisonerVertexNum % m;
            int topVertexNum = (pRow - 1) * m + pCol;
            int bottomVertexNum = (pRow + 1) * m + pCol;
            int leftVertexNum = pRow * m - pCol;
            int rightVertexNum = pRow * m + pCol;
            if(nbMove == 1) {
                map.vertexlist.get(pRow * m + pCol).value = PASSED; // Bloque la position actuelle pour ne plus y revenir
                if(top) map.vertexlist.get(topVertexNum).value = PRISONER;
                else if(bottom) map.vertexlist.get(bottomVertexNum).value = PRISONER;
                else if(right) map.vertexlist.get(leftVertexNum).value = PRISONER;
                else if(left) map.vertexlist.get(rightVertexNum).value = PRISONER;
            } else {
                int exitRow = exitVertexNum / m;
                int exitCol = exitVertexNum % m;
                int deltaRow = pRow - exitRow;
                int deltaCol = pCol - exitCol;
                if(deltaCol > 0 && left) map.vertexlist.get(leftVertexNum).value = PRISONER;
                else if(deltaCol < 0 && right) map.vertexlist.get(rightVertexNum).value = PRISONER;
                else if(deltaRow < 0 && bottom) map.vertexlist.get(bottomVertexNum).value = PRISONER;
                else if(deltaRow > 0 && top) map.vertexlist.get(topVertexNum).value = PRISONER;
                else if(top) map.vertexlist.get(topVertexNum).value = PRISONER;
                else if(bottom) map.vertexlist.get(bottomVertexNum).value = PRISONER;
                else if(right) map.vertexlist.get(rightVertexNum).value = PRISONER;
                else if(left) map.vertexlist.get(leftVertexNum).value = PRISONER;
                map.vertexlist.get(pRow * m + pCol).value = FREE;
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
     * Retourne le nombre de directions possibles que peut prendre le prisonier
     *
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return le nombre de directions possibles que peut prendre le prisonier
     */
    private static int canMove(int pVertexNum, int n, int m, Graph map) {
        int nbDir = 0;
        if(canMoveDir(pVertexNum, Direction.TOP, n, m, map)) nbDir++;
        if(canMoveDir(pVertexNum, Direction.BOT, n, m, map)) nbDir++;
        if(canMoveDir(pVertexNum, Direction.LEFT, n, m, map)) nbDir++;
        if(canMoveDir(pVertexNum, Direction.RIGHT, n, m, map)) nbDir++;
        return nbDir;
    }

    /**
     * Indique si le prisonnier peut se diriger dans la direction indiquée dans les params
     *
     * @param dir la direction a prendre
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si il peut passer par la direction donnee sinon return false
     */
    private static boolean canMoveDir(int pVertexNum, Direction dir, int n, int m, Graph map) {
        int pRow = pVertexNum / m;
        int pCol = pVertexNum % m;
        switch(dir) {
            case TOP:
                return pRow != 0 && map.vertexlist.get((pRow - 1) * m + pCol).value == FREE;
            case BOT:
                return pRow != (n - 1) && map.vertexlist.get((pRow + 1) * m + pCol).value == FREE;
            case LEFT:
                return pCol != 0 && map.vertexlist.get(pRow * m + pCol - 1).value == FREE;
            case RIGHT:
                return pCol != (m - 1) && map.vertexlist.get(pRow * m + pCol + 1).value == FREE;
            default:
                return false;
        }
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

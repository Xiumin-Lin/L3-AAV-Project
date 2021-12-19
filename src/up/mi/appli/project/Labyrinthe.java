package up.mi.appli.project;

import java.io.File;
import java.util.Scanner;

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

    public enum Direction {
        TOP, BOT, LEFT, RIGHT
    }

    public static void main(String[] args) {
        // Le labyrinthe: s’échapper d’Ayutthaya
        System.out.print("Entrer le nom du fichier avec les entrees : ");
        try(Scanner sc = new Scanner(System.in);
            Scanner fileScanner = new Scanner(new File(sc.nextLine()))) {
            int t = fileScanner.nextInt(); // le nombre d'instance

            for(int numInstance = 0; numInstance < t; numInstance++) {
                int n = fileScanner.nextInt(); // le nombre de ligne
                int m = fileScanner.nextInt(); // le nombre de symbole par ligne

                // creation & initialisation de la carte
                char[][] map = new char[n][m];
                for(int row = 0; row < n; row++) {
                    String ligne = fileScanner.next();
                    for(int col = 0; col < m; col++) {
                        map[row][col] = ligne.charAt(col);
                    }
                }
                showMap(map); //debug
                System.out.println(runInstance(map, n, m));
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
    private static char runInstance(char[][] map, int n, int m) {
        int turn = 0;
        while(turn < n * m) {
            // on fait propager les flammes
            for(int row = 0; row < n; row++) {
                for(int col = 0; col < m; col++) {
                    if(map[row][col] == NEXT_TO_BURN) map[row][col] = FIRE;
                }
            }
            // les cases en feu marquent leurs cases voisines qui vont prendre feu au prochain tour
            for(int row = 0; row < n; row++) {
                for(int col = 0; col < m; col++) {
                    if(map[row][col] == FIRE && burnAround(row, col, n, m, map))
                        return LOSE;
                }
            }
            showMap(map); //debug
            // au tour du prisonnier de se deplacer
            if(movePrisoner(n, m, map)) return WIN;
            showMap(map);
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
     * @param row la ligne de la case {@link #FIRE}
     * @param col la colonne de la case {@link #FIRE}
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si l
     */
    private static boolean burnAround(int row, int col, int n, int m, char[][] map) {
        // Si la case actuel n'est pas sur la 1ere ligne,
        if(row > 0) { // alors sa case du haut va bruler au prochain tour s'il est libre
            int prevRow = row - 1;
            if(map[prevRow][col] == FREE) map[prevRow][col] = NEXT_TO_BURN;
            else if(map[prevRow][col] == END || map[prevRow][col] == PRISONER) return true;
        }
        // Si la case actuel n'est pas sur la dernire ligne
        if(row < n - 1) { // alors sa case du bas va bruler au prochain tour s'il est libre
            int nextRow = row + 1;
            if(map[nextRow][col] == FREE) map[nextRow][col] = NEXT_TO_BURN;
            else if(map[nextRow][col] == END || map[nextRow][col] == PRISONER) return true;
        }
        // Si la case actuel n'est pas sur la 1ere colonne
        if(col > 0) { // alors sa case de gauche va bruler au prochain tour s'il est libre
            int prevCol = col - 1;
            if(map[row][prevCol] == FREE) map[row][prevCol] = NEXT_TO_BURN;
            else if(map[row][prevCol] == END || map[row][prevCol] == PRISONER) return true;
        }
        // Si la case actuel n'est pas sur la derniere colonne
        if(col < m - 1) { // alors sa case de droite va bruler au prochain tour s'il est libre
            int nextCol = col + 1;
            if(map[row][nextCol] == FREE) map[row][nextCol] = NEXT_TO_BURN;
            else return map[row][nextCol] == END || map[row][nextCol] == PRISONER;
        }
        return false;
    }

    /**
     * Deplace le prisonnier sur une case adjacent de sa position si c'est possible.
     * Si une case adjacent est la sortie, retourne true sinon false
     *
     * @param n   le nombre de ligne du labyrinthe
     * @param m   le nombre de symbole par ligne du labyrinthe
     * @param map le labyrinthe
     * @return true si le prisonnier a atteint la sortie
     */
    private static boolean movePrisoner(int n, int m, char[][] map) {
        int[] prisonerPosition = new int[2];
        int[] exitPosition = new int[2];
        // on recupere les position du prisonnier et de la sortie
        for(int row = 0; row < n; row++) {
            for(int col = 0; col < m; col++) {
                if(map[row][col] == END) {
                    exitPosition[0] = row;
                    exitPosition[1] = col;
                } else if(map[row][col] == PRISONER) {
                    prisonerPosition[0] = row;
                    prisonerPosition[1] = col;
                }
            }
        }
        if(winMove(prisonerPosition[0], prisonerPosition[1], n, m, map)) return true;

        int nbMove = canMove(prisonerPosition[0], prisonerPosition[1], n, m, map);
        System.out.println("nbMove : " + nbMove);
        if(nbMove > 0) {
            int pRow = prisonerPosition[0];
            int pCol = prisonerPosition[1];
            boolean top = canMoveDir(pRow, pCol, Direction.TOP, n, m, map);
            boolean bottom = canMoveDir(pRow, pCol, Direction.BOT, n, m, map);
            boolean left = canMoveDir(pRow, pCol, Direction.LEFT, n, m, map);
            boolean right = canMoveDir(pRow, pCol, Direction.RIGHT, n, m, map);

            if(nbMove == 1) {
                map[pRow][pCol] = PASSED; // Bloque la position actuelle pour ne plus y revenir
                if(top) map[pRow - 1][pCol] = PRISONER;
                else if(bottom) map[pRow + 1][pCol] = PRISONER;
                else if(right) map[pRow][pCol + 1] = PRISONER;
                else if(left) map[pRow][pCol - 1] = PRISONER;
            } else {
                int deltaRow = prisonerPosition[0] - exitPosition[0];
                int deltaCol = prisonerPosition[1] - exitPosition[1];
                if(deltaCol > 0 && left) map[pRow][pCol - 1] = PRISONER;
                else if(deltaCol < 0 && right) map[pRow][pCol + 1] = PRISONER;
                else if(deltaRow < 0 && bottom) map[pRow + 1][pCol] = PRISONER;
                else if(deltaRow > 0 && top) map[pRow - 1][pCol] = PRISONER;
                else if(top) map[pRow - 1][pCol] = PRISONER;
                else if(bottom) map[pRow + 1][pCol] = PRISONER;
                else if(right) map[pRow][pCol + 1] = PRISONER;
                else if(left) map[pRow][pCol - 1] = PRISONER;
                map[pRow][pCol] = FREE;
            }
        }
        return false;
    }

    /**
     * Indique si son prochaine déplacement est gagnant (il peut atteindre la sortie indiqué par {@link #END}
     *
     * @param pRow la ligne de la position du prisonnier
     * @param pCol la colonne de la position du prisonnier
     * @param n    le nombre de ligne du labyrinthe
     * @param m    le nombre de symbole par ligne du labyrinthe
     * @param map  le labyrinthe
     * @return true si une case voisine à celle du prisonnier est la sortie du labyrinthe
     */
    private static boolean winMove(int pRow, int pCol, int n, int m, char[][] map) {
        boolean top = pRow > 0 && map[pRow - 1][pCol] == END;
        boolean bottom = pRow < (n - 1) && map[pRow + 1][pCol] == END;
        boolean left = pCol > 0 && map[pRow][pCol - 1] == END;
        boolean right = pCol < (m - 1) && map[pRow][pCol + 1] == END;
        return top || left || right || bottom;
    }

    /**
     * Retourne le nombre de directions possibles que peut prendre le prisonier
     *
     * @param pRow la ligne de la position du prisonnier
     * @param pCol la colonne de la position du prisonnier
     * @param n    le nombre de ligne du labyrinthe
     * @param m    le nombre de symbole par ligne du labyrinthe
     * @param map  le labyrinthe
     * @return le nombre de directions possibles que peut prendre le prisonier
     */
    private static int canMove(int pRow, int pCol, int n, int m, char[][] map) {
        int nbDir = 0;
        if(canMoveDir(pRow, pCol, Direction.TOP, n, m, map)) nbDir++;
        if(canMoveDir(pRow, pCol, Direction.BOT, n, m, map)) nbDir++;
        if(canMoveDir(pRow, pCol, Direction.LEFT, n, m, map)) nbDir++;
        if(canMoveDir(pRow, pCol, Direction.RIGHT, n, m, map)) nbDir++;
        return nbDir;
    }

    /**
     * Indique si le prisonnier peut se diriger dans la direction indiquée dans les params
     *
     * @param pRow la ligne de la position du prisonnier
     * @param pCol la colonne de la position du prisonnier
     * @param dir  la direction a prendre
     * @param n    le nombre de ligne du labyrinthe
     * @param m    le nombre de symbole par ligne du labyrinthe
     * @param map  le labyrinthe
     * @return true si il peut passer par la direction donnee sinon return false
     */
    private static boolean canMoveDir(int pRow, int pCol, Direction dir, int n, int m, char[][] map) {
        switch(dir) {
            case TOP:
                return pRow != 0 && map[pRow - 1][pCol] == FREE;
            case BOT:
                return pRow != (n - 1) && map[pRow + 1][pCol] == FREE;
            case LEFT:
                return pCol != 0 && map[pRow][pCol - 1] == FREE;
            case RIGHT:
                return pCol != (m - 1) && map[pRow][pCol + 1] == FREE;
            default:
                return false;
        }
    }

    /**
     * Affiche le labyrinthe
     *
     * @param map le labyrinthe
     */
    private static void showMap(char[][] map) {
        for(char[] ligne : map) {
            for(char c : ligne) {
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }
}

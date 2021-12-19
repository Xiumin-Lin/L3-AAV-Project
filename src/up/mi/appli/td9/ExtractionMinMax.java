package up.mi.appli.td9;

import java.util.*;

public class ExtractionMinMax {
    public static void main(String[] args) {
        Random rand = new Random();
        final int LIMIT_SIZE = 550000;
        final int MAX_NUMBER = 100000;
        Map<Integer, Double[]> registre = new LinkedHashMap<>();

        // on compare des listes de taille 2^size jusqu'à atteindre une taille > 550000
        for(int size = 2; size < LIMIT_SIZE; size *= 2) {
            int[] listForQuicksort = new int[size];
            int[] listForPriorityQ = new int[size];
            int randNumber;
            for(int i = 0; i < size; i++) {
                randNumber = rand.nextInt(MAX_NUMBER);
                listForQuicksort[i] = randNumber;   // on cree 2 liste identiques
                listForPriorityQ[i] = randNumber;   // avec des entiers positif aleatoire
            }
            Double[] list = new Double[2];
            // pour comparait les extration de maximum
//            list[0] = extrationMinQuickSort(listForQuicksort);
//            list[1] = extrationMinPriorityQueue(listForPriorityQ);

            // pour comparait les extration de minimum
            list[0] = extrationMaxQuickSort(listForQuicksort);
            list[1] = extrationMaxPriorityQueue(listForPriorityQ);
            registre.put(size, list);
        }

        // affichage
        String separator = "\t\t\t\t";
        System.out.println("Extration de max :");
        System.out.println("Size" + separator + "QuickSort (ms)" + separator + "PriorityQueue (ms)");
        for(Map.Entry<Integer, Double[]> entry : registre.entrySet()) {
            System.out.println(entry.getKey() + separator + entry.getValue()[0]
                    + separator + entry.getValue()[1]);
        }
    }

    // Code pour le calcul du temps provient de StackOverFlow :
    // https://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java

    public static double extrationMinQuickSort(int[] list) {
        long startTime = System.nanoTime();
        Arrays.sort(list); // Quicksort de Java
        int extract = list[0];
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000.0;
    }

    public static double extrationMaxQuickSort(int[] list) {
        long startTime = System.nanoTime();
        Arrays.sort(list); // Quicksort de Java
        int extract = list[list.length - 1];
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000.0;
    }

    public static double extrationMinPriorityQueue(int[] list) {
        // Code inspiré de https://stackoverflow.com/questions/1098277/java-implementation-for-min-max-heap
        PriorityQueue<Integer> prq = new PriorityQueue<>();
        long startTime = System.nanoTime();
        for(int j : list) prq.add(j);
        int extract = prq.peek();
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000.0;
    }

    public static double extrationMaxPriorityQueue(int[] list) {
        PriorityQueue<Integer> prq = new PriorityQueue<>(Collections.reverseOrder());
        long startTime = System.nanoTime();
        for(int j : list) prq.add(j);
        int extract =  prq.peek();
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000.0;
    }
}

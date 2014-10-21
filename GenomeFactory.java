import java.util.*;
import java.util.concurrent.*;

public class GenomeFactory {
    public static double[][][][][] getRandomGenome(int players, int rows, int columns, int runLengths, int directions) {
        double[][][][][] genome = new double[players][rows][columns][runLengths][directions];

        for (int p = 0; p < players; p++)
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < columns; c++)
                    for (int l = 0; l < runLengths; l++)
                        for (int d = 0; d < directions; d++)
                            genome[p][r][c][l][d] = Math.random();

        return genome;
    }

    public static Set<HWPlayer> generateRandomPopulation(int size, Callable<HWPlayer> factory) throws Exception {
        Set<HWPlayer> population = new HashSet<>(size);
        while (population.size() < size)
            population.add(factory.call());

        return population;
    }

    public static Set<HWPlayer> generateGiftedPopulation(int size, Callable<HWPlayer> factory, Player competeAgainst) throws Exception {
        Set<HWPlayer> population = new HashSet<>(size);
        while (population.size() < size) {
            HWPlayer p1 = factory.call();
            ConnectFourGameLogic g = new ConnectFourGameLogic(6, 7, p1, competeAgainst);
            g.run();
            Player winner = g.getWinner();

            if (winner != null && winner.equals(p1))
                population.add(p1);
        }

        return population;
    }

    public static void breedToPopulationSize(List<Phenotype> population, int desiredPopulationSize) throws Exception {
        Random r = new Random();
        while (population.size() < desiredPopulationSize)
            population.add(new Phenotype(population.get(r.nextInt(population.size())).breedWith(population.get(r.nextInt(population.size())))));
    }

    public static void calculateFitness(List<Phenotype> population) throws Exception {
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (Phenotype p : population) {
            tasks.add(() -> {
                Integer fitness = p.calculateFitness(2);
                System.out.println(fitness);
                return fitness;
            });
        }

        System.out.println("\tExecuting " + tasks.size() + " tasks");
        long start = System.currentTimeMillis();
        int totalFitness = 0, totalItems = 0, maxFitness = Integer.MIN_VALUE, minFitness = Integer.MAX_VALUE;
//        ExecutorService s = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        List<Future<Integer>> futures =s.invokeAll(tasks);
//        s.shutdown();
//        for (Future<Integer> f : futures) {
        for (Callable<Integer> f : tasks) {
            int fitness = f.call();
            totalFitness += fitness;
            totalItems++;
            maxFitness = Math.max(maxFitness, fitness);
            minFitness = Math.min(minFitness, fitness);
        }

        System.out.println(String.format("Calculated %s total items in %s ms.\tavg: %s\tmin: %s\tmax: %s\t", totalItems, System.currentTimeMillis() - start, totalFitness / (double) totalItems, minFitness, maxFitness));
    }
}
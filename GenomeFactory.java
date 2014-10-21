import java.io.*;
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

	public static void breedToPopulationSize(List<Phenotype> population, int desiredPopulationSize) throws Exception {
		Random r = new Random();

		List<Phenotype> breedingStock = new ArrayList<>();
		for (Phenotype p : population)
			for (int i = 0; i < p.getFitness() * 100; i++)
				breedingStock.add(p);

		while (population.size() < desiredPopulationSize)
			population.add(new Phenotype(breedingStock.get(r.nextInt(breedingStock.size())).breedWith(breedingStock.get(r.nextInt(breedingStock.size())))));
	}

	public static void calculateFitness(List<Phenotype> population) throws Exception {
		List<Callable<Double>> tasks = new ArrayList<>();
		for (Phenotype p : population)
			tasks.add(p::calculateFitness);

		long start = System.currentTimeMillis();
		ExecutorService s = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 2);
		List<Future<Double>> futures = s.invokeAll(tasks);
		s.shutdown();
		for (Future<Double> f : futures) {}

		Generation g = new Generation(population);
//		String filename = String.format("C:\\Development\\TCSS435\\HW2\\generations\\%s_%s_%s_%s_%s.ser", System.currentTimeMillis(), g.getTotalItems(), g.getAverageFitness(), g.getMinFitness(), g.getMaxFitness());
//		g.save(filename);
		System.out.println(String.format("\tCalculated %s total items in %s ms.\tavg: %s\tmin: %s\tmax: %s\t", g.getTotalItems(), System.currentTimeMillis() - start, g.getAverageFitness(), g.getMinFitness(), g.getMaxFitness()));
	}

	public static void crossover(List<Phenotype> population, double percentSurvivors) throws Exception {
		int originalPopulationSize = population.size();
		int remainingPopulation = (int) (population.size() * percentSurvivors);

		Collections.sort(population, (o1, o2) -> (int) ((o2.getFitness() - o1.getFitness()) * 10000));
		while (population.size() > remainingPopulation)
			population.remove(remainingPopulation);

		breedToPopulationSize(population, originalPopulationSize);
	}
}
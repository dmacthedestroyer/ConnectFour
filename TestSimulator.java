import java.util.*;
import java.util.stream.Collectors;

public class TestSimulator {
	public static void main(String[] args) throws Exception {
		final int maxDepth = 4;
		final int populationSize = 100;

		List<Phenotype> population = GenomeFactory.generateRandomPopulation(populationSize, () -> new HWPlayer(maxDepth))
				.stream()
				.map(Phenotype::new)
				.collect(Collectors.toList());

		for (int i = 0; i < 100; i++) {
			System.out.print((i+1) + ": ");
			GenomeFactory.calculateFitness(population);

			GenomeFactory.crossover(population, 0.2);
		}
	}
}
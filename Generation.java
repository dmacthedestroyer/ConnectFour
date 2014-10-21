import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Generation {
	private List<Phenotype> population;
	private double totalFitness = 0, maxFitness = Integer.MIN_VALUE, minFitness = Integer.MAX_VALUE;

	public Generation(List<Phenotype> population) {
		this.population = population;
		calculateStatistics();
	}

	private void calculateStatistics() {
		for (Phenotype p : population) {
			double fitness = p.getFitness();
			totalFitness += fitness;
			maxFitness = Math.max(maxFitness, fitness);
			minFitness = Math.min(minFitness, fitness);
		}
	}

	public double getTotalFitness() {
		return totalFitness;
	}

	public double getTotalItems() {
		return population.size();
	}

	public double getAverageFitness() {
		return getTotalFitness() / getTotalItems();
	}

	public double getMaxFitness() {
		return maxFitness;
	}

	public double getMinFitness() {
		return minFitness;
	}

	public static Generation load(String filename) throws IOException, ClassNotFoundException {
		List<double[][][][][]> genomes;
		try (ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
			genomes = (List<double[][][][][]>) input.readObject();
		}
		return new Generation(genomes.stream().map(g -> new Phenotype(new HWPlayer(4, g))).collect(Collectors.toList()));
	}

	public void save(String filename) {
		try (ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
			output.writeObject(population.stream().map(p -> p.getPlayer().getGenome()).collect(Collectors.toList()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

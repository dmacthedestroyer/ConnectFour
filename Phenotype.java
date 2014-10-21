import java.util.Random;

public class Phenotype {

	public Phenotype(HWPlayer player) {
		this.player = player;
	}

	private HWPlayer player;

	public HWPlayer getPlayer() {
		return player;
	}

	private double fitness = 0;

	public double getFitness() {
		return fitness;
	}

	public double calculateFitness() {
		final int rounds = 20;
		Player opponent = new MysteryPlayer(4);
		fitness = 0;

		for (int i = 0; i < rounds; i++) {
			Player winner = playGame(player, opponent);
			if (winner != null && winner.equals(player)) fitness++;

			winner = playGame(opponent, player);
			if (winner == null || winner.equals(player)) fitness++;
		}

		return (fitness = fitness / ((double) rounds * 2));
	}

	private static Player playGame(Player p1, Player p2) {
		ConnectFourGameLogic g = new ConnectFourGameLogic(6, 7, p1, p2);
		g.run();
		return g.getWinner();
	}

	private double calculateCrossoverRatio(double mateFitness) {
		return 0.5;
	}

	public HWPlayer breedWith(Phenotype mate) {
		double[][][][][] p1 = player.getGenome();
		double[][][][][] p2 = mate.player.getGenome();
		double[][][][][] g = new double[p1.length][p1[0].length][p1[0][0].length][p1[0][0][0].length][p1[0][0][0][0].length];

		Random random = new Random();

		//the fitter of the two will have more of its chromosomes passed on. This is a form of elitist selection
		double crossoverRatio = calculateCrossoverRatio(mate.fitness);
		double mutationChance = 0.1;//4.0 / (g.length * g[0].length * g[0][0].length * g[0][0][0].length * g[0][0][0][0].length);

		for (int p = 0; p < g.length; p++)
			for (int r = 0; r < g[0].length; r++)
				for (int c = 0; c < g[0][0].length; c++)
					for (int l = 0; l < g[0][0][0].length; l++)
						for (int d = 0; d < g[0][0][0][0].length; d++) {
							g[p][r][c][l][d] = random.nextDouble() <= crossoverRatio ? p1[p][r][c][l][d] : p2[p][r][c][l][d];
							if (random.nextDouble() < mutationChance)
								g[p][r][c][l][d] += ((random.nextDouble() / 2) * (random.nextBoolean() ? -1.0 : 1.0));
						}

		return new HWPlayer(player.getMaxDepth(), p1);
	}
}
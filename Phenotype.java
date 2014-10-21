public class Phenotype {

    public Phenotype(HWPlayer player) {
        this.player = player;
    }

    private HWPlayer player;

    private int fitness = 0;

    public int calculateFitness(int rounds) {
        Player opponent = new MysteryPlayer(4);
        fitness = 0;

        for (int i = 0; i < rounds; i++) {
            ConnectFourGameLogic g = new ConnectFourGameLogic(6, 7, player, opponent);
            g.run();
            Player winner = g.getWinner();
            if (winner == null)
                fitness--; //lose one point for tying when you start
            else if (winner.equals(player))
                fitness++; //gain a point for winning
            else fitness -= 2; //lose two points for losing when you start
        }

        for (int i = 0; i < rounds; i++) {
            ConnectFourGameLogic g = new ConnectFourGameLogic(6, 7, opponent, player);
            g.run();
            Player winner = g.getWinner();
            if (winner == null)
                fitness++;
            else if (winner.equals(player))
                fitness += 2;
            else fitness--;
        }

        return fitness;
    }

    private double calculateFitnessRatio(int mateFitness) {
        if (fitness == 0 && mateFitness == 0)
            return 0.5;

        double delta = Math.abs(Math.min(fitness, mateFitness));//boost both scores to get a positive number
        return fitness + delta / (fitness + delta + mateFitness + delta);
    }

    public HWPlayer breedWith(Phenotype mate) {
        double[][][][][] p1 = player.getGenome();
        double[][][][][] p2 = mate.player.getGenome();
        double[][][][][] g = new double[p1.length][p1[0].length][p1[0][0].length][p1[0][0][0].length][p1[0][0][0][0].length];

        //the fitter of the two will have more of its chromosomes passed on. This is a form of elitist selection
        double fitnessRatio = calculateFitnessRatio(mate.fitness);
        //start with roughly 2 mutations per genome
        double mutationChance = 2.0 / (g.length * g[0].length * g[0][0].length * g[0][0][0].length * g[0][0][0][0].length);

        for (int p = 0; p < g.length; p++)
            for (int r = 0; r < g[0].length; r++)
                for (int c = 0; c < g[0][0].length; c++)
                    for (int l = 0; l < g[0][0][0].length; l++)
                        for (int d = 0; d < g[0][0][0][0].length; d++) {
                            g[p][r][c][l][d] = Math.random() <= fitnessRatio ? p1[p][r][c][l][d] : p2[p][r][c][l][d];
                            if (Math.random() < mutationChance)
                                g[p][r][c][l][d] += (Math.random() * (Math.random() <= 0.5 ? -1.0 : 1.0));
                        }

        return new HWPlayer(player.getMaxDepth(), p1);
    }
}
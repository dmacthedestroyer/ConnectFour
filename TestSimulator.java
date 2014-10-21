import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TestSimulator {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 100; i++) {
            ConnectFourGameLogic g = new ConnectFourGameLogic(6, 7, new HWPlayer(4), new HWPlayer(4));
            g.run();
            if(g.getWinner() == null)
                System.out.println("Tie");
            else System.out.println(g.getWinner());
        }
//        final int maxDepth = 4;
//        final int seedPopulationSize = 10;
//        final int populationSize = 10;
//
//        System.out.println("building base population");
//        List<Phenotype> population = GenomeFactory.generateRandomPopulation(seedPopulationSize, () -> new HWPlayer(maxDepth))//, new MysteryPlayer(maxDepth))
//                .stream()
//                .map(Phenotype::new)
//                .collect(Collectors.toList());
//
//        System.out.println("breeding to base population size");
//        GenomeFactory.breedToPopulationSize(population, populationSize);
//
//        System.out.println("calculating fitness of population");
//        GenomeFactory.calculateFitness(population);
//
//        System.out.println("Did it!");
    }

    private static void fillPopulation(Collection<Player> population, int size) {
        while (population.size() < size)
            population.add(new HWPlayer(7));
    }

    private static Player compete(Player p1, Player p2) {
        ConnectFourGameLogic g = new ConnectFourGameLogic(6, 7, p1, p2);
        g.run();
        return g.getWinner();
    }

    private static void competeCycle(final Collection<Player> population, Map<Player, Integer> winCounts) throws ExecutionException, InterruptedException {
        List<Player> players = new ArrayList<>(population);
        Collections.shuffle(players);

        ExecutorService s = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Player>> results = new ArrayList<>();
        for (int i = 0; i < players.size() - 1; i += 2) {
            final Player p1 = players.get(i);
            final Player p2 = players.get(i + 1);
            results.add(s.submit(new Callable<Player>() {
                @Override
                public Player call() throws Exception {
                    return compete(p1, p2);
                }
            }));
        }
        s.shutdown();

        for (Future<Player> f : results) {
            Player p = f.get();
            if (!winCounts.containsKey(p))
                winCounts.put(p, 1);
            else winCounts.put(p, winCounts.get(p) + 1);
        }
    }

    private static Map.Entry<Player, Integer> winningest(Map<Player, Integer> winCounts) {
        ArrayList<Map.Entry<Player, Integer>> entries = new ArrayList<>(winCounts.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Player, Integer>>() {
            @Override
            public int compare(Map.Entry<Player, Integer> o1, Map.Entry<Player, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        return entries.get(1);
    }
}
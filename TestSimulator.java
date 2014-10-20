import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class TestSimulator {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int totalRuns = 100;

        long allTimeStart = System.currentTimeMillis();

        List<Callable<String>> tasks = new ArrayList<>(1000);
        for (int i = 0; i < totalRuns; i++) {
            tasks.add(new Callable() {
                @Override
                public String call() throws Exception {
                    long start = System.currentTimeMillis();
                    Player ab = new AlphaBetaPlayer(7);
                    Player m = new MysteryPlayer(7);
                    ConnectFourGameLogic gl = new ConnectFourGameLogic(6, 7, ab, m);
                    gl.run();
                    if(gl.getBoard().isWinner(ab.myDisc))
                        return ab.getName();
                    return m.getName();
                }
            });
        }

        ExecutorService s = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<String>> results =  s.invokeAll(tasks);
        s.shutdown();
        long allTimeEnd = System.currentTimeMillis();

        System.out.println("Total time (ms): " + (allTimeEnd - allTimeStart));
        System.out.println(" Avg. time/task: " + ((allTimeEnd - allTimeStart)/totalRuns));


        Map<String, Integer> winCount = new HashMap<>();
        for(Future<String> f : results){
            String winnerName = f.get();
            if(!winCount.containsKey(winnerName))
                winCount.put(winnerName, 1);
            else winCount.put(winnerName, winCount.get(winnerName) + 1);
        }

        for (Map.Entry<String, Integer> e : winCount.entrySet()){
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }
}

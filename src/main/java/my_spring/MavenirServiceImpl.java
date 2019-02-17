package my_spring;

/**
 * @author Evgeny Borisov
 */

//@Benchmark
public class MavenirServiceImpl implements MavenirService {
    @Benchmark
    @Override
    public void doWork() {
        System.out.println("Working...");
    }

    @Override
    @Benchmark
    public void drinkBeer() {
        System.out.println("drinking beer");
    }
}

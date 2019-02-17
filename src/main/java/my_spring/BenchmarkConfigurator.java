package my_spring;

public interface BenchmarkConfigurator {
    <T> T handleBenchmark(Class<T> type, T t);
}

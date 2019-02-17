package my_spring;

import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class BenchmarkConfiguratorImpl implements BenchmarkConfigurator {
    @Override
    public <T> T handleBenchmark(Class<T> type, T t) {
        T result = t;
        if (type.isAnnotationPresent(Benchmark.class)) {
            result = (T) Proxy.newProxyInstance(
                    type.getClassLoader(),
                    type.getInterfaces(),
                    (proxy, method, args) -> invokeMethodAsProxy(method, args, t));
        } else if (typeHasBenchmarkMethod(t) == true) {
            result = (T) Proxy.newProxyInstance(
                    type.getClassLoader(),
                    type.getInterfaces(),
                    (proxy, proxyMethod, args) -> {
                if (isMethodBenchmarked(type, proxyMethod)){
                    return invokeMethodAsProxy(proxyMethod, args, t);
                } else {
                    return proxyMethod.invoke(t, args);
                }
            });
        }

        return result;
    }

    @SneakyThrows
    private <T> boolean isMethodBenchmarked(Class<T> type, Method proxyMethod){
        Method classMethod = type.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        boolean result = classMethod.isAnnotationPresent(Benchmark.class);
        return result;
    }

    private <T> boolean typeHasBenchmarkMethod(T t) {
        final Method[] allClassMethods = t.getClass().getMethods();
        final boolean result = Arrays.asList(allClassMethods).stream().anyMatch(curMethod -> curMethod.isAnnotationPresent(Benchmark.class));
        return result;
    }

    private <T> Object invokeMethodAsProxy(Method method, Object[] args, T t) throws IllegalAccessException, InvocationTargetException {
        System.out.println("********* BENCHMARK started for method " + method.getName() + " ***********");
        long start = System.nanoTime();
        Object retVal = method.invoke(t, args);
        long end = System.nanoTime();
        System.out.println(end - start);
        System.out.println("********* BENCHMARK ended for method " + method.getName() + " **************");
        return retVal;
    }
}

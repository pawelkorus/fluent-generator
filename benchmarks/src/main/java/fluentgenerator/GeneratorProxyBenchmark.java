package fluentgenerator;

import fluentgenerator.lib.core.Generator;
import fluentgenerator.lib.core.GeneratorFactory;
import fluentgenerator.lib.core.reflect.ReflectGeneratorProxyFactory;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class GeneratorProxyBenchmark {

    public static Supplier<Integer> randomInt = () -> ThreadLocalRandom.current().nextInt();
    public static String testValue = "some constant value";

    public static class BenchmarkModel {
        Integer v;

        public void setSomeValue(Integer someValue) { v = someValue; }
    }

    public static class VanillaGenerator {
        public BenchmarkModel build() {
            BenchmarkModel m = new BenchmarkModel();
            m.setSomeValue(randomInt.get());
            return m;
        }
    }

    public static interface ModelGenerator extends Generator<BenchmarkModel> {
        ModelGenerator someValue(Supplier<Integer> s);
        BenchmarkModel build();
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        public ModelGenerator jdkProxyGenerator;
        public VanillaGenerator vanillaGenerator;

        @Setup(Level.Trial)
        public void setup() {
            vanillaGenerator = new VanillaGenerator();

            Supplier<String> valueSupplier = () -> { return testValue; };

            GeneratorFactory factory = new ReflectGeneratorProxyFactory();
            jdkProxyGenerator = factory.generatorInstance(ModelGenerator.class);
            jdkProxyGenerator.someValue(randomInt);
        }
    }

    @Benchmark
    public void vanillaGeneratorBenchmark(BenchmarkState state) {
        state.vanillaGenerator.build();
    }

    @Benchmark
    public void jdkDynamicProxyGeneratorBenchmark(BenchmarkState state) {
        state.jdkProxyGenerator.build();
    }

}

import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;

public class Benchmarks {

	public static void main(String[] args) throws Exception {
		Options options = new OptionsBuilder()
			.include("fluentgenerator.GeneratorProxyBenchmark.*")
			.warmupIterations(7)
			.measurementIterations(4)
			.forks(3)
			.build();

		Collection<RunResult> results = new Runner(options).run();
		for (RunResult result : results) {
			Result r = result.getPrimaryResult();
			System.out.println("API replied benchmark score: "
				+ r.getScore() + " "
				+ r.getScoreUnit() + " over "
				+ r.getStatistics().getN() + " iterations");
		}
	}

}

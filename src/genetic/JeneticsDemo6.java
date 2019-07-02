package genetic;


import io.jenetics.DoubleGene;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.StochasticUniversalSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.DoubleRange;

/**
 *  https://github.com/jenetics/jenetics/blob/master/jenetics.prog/README.adoc#examples
 *  
 */
public class JeneticsDemo6 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final Codec<double[], DoubleGene> CODEC = Codecs
		        .ofVector(DoubleRange.of(0, 1), 3);

		

		final Engine<DoubleGene, Double> engine = Engine
				.builder(JeneticsDemo6::func, CODEC)
					.populationSize(800)
					.optimize(Optimize.MAXIMUM)
					.offspringSelector(new StochasticUniversalSelector<>())
					.alterers(new Mutator<>(0.01), new MeanAlterer<>(0.5))
					.build();

		final EvolutionStatistics<Double, DoubleMomentStatistics> statistics =
			     EvolutionStatistics.ofNumber();
		
		// Execute the GA (engine).
		final Phenotype<DoubleGene, Double> result = engine.stream()
				// Truncate the evolution stream if no better individual could
				// be found after 5 consecutive generations.
				.limit(Limits.bySteadyFitness(5))
				.peek(statistics)
				// Terminate the evolution after maximal 100 generations.
				.limit(500).collect(EvolutionResult.toBestPhenotype());
		
		System.out.println("Best GenoType: " + result);
		System.out.println(statistics);

	}
	

	public static double func(double [] params) {
		// f(a, b, c) = 3 * cos(a)^4 + 4 * cos(b)^3 + 2 sin(c)^2 * cos(c)^2 + 5	
		return 3 * Math.pow(Math.cos(params[0]), 4) + 4 * Math.pow(Math.cos(params[1]), 3) +
				2 * Math.pow(Math.sin(params[2]), 2) * Math.pow(Math.cos(params[2]), 2) + 5;
	}

}
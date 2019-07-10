package genetic;


import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

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
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/**
		 * Optimization problem without any constraint
		 * 
		 * final Codec<double[], DoubleGene> CODEC = Codecs
		        .ofVector(DoubleRange.of(0, 1), 3);
		 */
		
		// Optimization problem with constraints
		// a + b + c = 1
		// c <= 0.8
		final Codec<double[], DoubleGene> CODEC = Codecs
				.ofVector(DoubleRange.of(0, 1), 3).map(JeneticsDemo6::normalize);

		

		final Engine<DoubleGene, Double> engine = Engine
				.builder(JeneticsDemo6::func, CODEC)
					.populationSize(8000)
					.optimize(Optimize.MAXIMUM)
					.offspringSelector(new StochasticUniversalSelector<>())
					.alterers(new Mutator<>(0.01), new MeanAlterer<>(0.5))
					.build();

		final EvolutionStatistics<Double, DoubleMomentStatistics> statistics =
			     EvolutionStatistics.ofNumber();
		
		// Execute the GA (engine).
		final Phenotype<DoubleGene, Double> result = engine.stream()
				// Truncate the evolution stream if no better individual could
				// be found after 200 consecutive generations.
				.limit(Limits.bySteadyFitness(200))
				.peek(statistics)
				// Terminate the evolution after maximal 50000 generations.
				.limit(50000).collect(EvolutionResult.toBestPhenotype());
		
		double [] res = CODEC.decode(result.getGenotype());
		System.out.println("Best Phenotype: " + 
					Arrays.stream(res).mapToObj(p -> ff.format(p))
							.collect(Collectors.joining(", ")) + 
					" ==> " + ff.format(result.getFitness()));
		System.out.println(statistics);

	}
	
	public static double [] normalize(final double [] x) {
		
		// a + b + c = 1
		// c <= 0.8
		
		x[2] = x[2] * 0.8;
		x[1] = x[1] * (1.0 - x[2]);
		x[0] = 1.0 - x[2] - x[1];
		
		return x;
	}

	public static double func(double [] params) {
		// f(a, b, c) = 3 * cos(a)^4 + 4 * cos(b)^3 + 2 sin(c)^2 * cos(c)^2 + 5	
		return 3 * Math.pow(Math.cos(params[0]), 4) + 4 * Math.pow(Math.cos(params[1]), 3) +
				2 * Math.pow(Math.sin(params[2]), 2) * Math.pow(Math.cos(params[2]), 2) + 5;
	}

}
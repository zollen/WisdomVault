package genetic;


import io.jenetics.DoubleGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.util.DoubleRange;
import io.jenetics.util.ISeq;

/**
 *  https://github.com/jenetics/jenetics/blob/master/jenetics.prog/README.adoc#examples
 *  
 */
public class JeneticsDemo6 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final Codec<Double, DoubleGene> PARAM = Codecs.ofScalar(DoubleRange.of(1, Math.PI));
		
		final Codec<Double, DoubleGene> CODEC = Codec.of(
				ISeq.of(PARAM, PARAM, PARAM),
				params -> {
					final double param1 = (double) params[0];
					final double param2 = (double) params[1];
					final double param3 = (double) params[2];
					
					return 0.0;
				}
		);		
		
/*		
		final Engine<DoubleGene, Double> engine = Engine
				.builder(JeneticsDemo6::func, CODEC)
					.populationSize(500)
					.optimize(Optimize.MAXIMUM)
					.offspringSelector(new StochasticUniversalSelector<>())
					.alterers(new Mutator<>(0.03), new MeanAlterer<>(0.5))
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
*/
	}
	

	public static double func(double a, double b, double c) {
		// f(a, b, c) = 3 * cos(a)^4 + 4 * cos(b)^3 + 2 sin(c)^2 * cos(c)^2 + 5	
		return 3 * Math.pow(Math.cos(a), 4) + 4 * Math.pow(Math.cos(b), 3) +
				2 * Math.pow(Math.sin(c), 2) * Math.pow(Math.cos(c), 2) + 5;
	}

}
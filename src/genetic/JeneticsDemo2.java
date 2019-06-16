package genetic;

import io.jenetics.DoubleChromosome;
import io.jenetics.DoubleGene;
import io.jenetics.Genotype;
import io.jenetics.MeanAlterer;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;

public class JeneticsDemo2 {

	public static void main(String[] args) {
		// Create/configuring the engine via its builder.
		final Engine<DoubleGene, Double> engine = Engine
				.builder(JeneticsDemo2::eval, DoubleChromosome.of(0.0, 2.0 * Math.PI))
					.populationSize(500)
					.optimize(Optimize.MINIMUM)
					.alterers(new Mutator<>(0.03), new MeanAlterer<>(0.6))
					.build();

		// Execute the GA (engine).
		final Phenotype<DoubleGene, Double> result = engine.stream()
				// Truncate the evolution stream if no better individual could
				// be found after 5 consecutive generations.
				.limit(Limits.bySteadyFitness(5))
				// Terminate the evolution after maximal 100 generations.
				.limit(100).collect(EvolutionResult.toBestPhenotype());
		
		System.out.println("Best GenoType: " + result);
	}

	private static Double eval(final Genotype<DoubleGene> gt) {
		final double x = gt.getGene().doubleValue();
		return Math.cos(0.5 + Math.sin(x)) * Math.cos(x);
	}
}

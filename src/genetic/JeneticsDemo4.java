package genetic;

import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.Genotype;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.RouletteWheelSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.RandomRegistry;

/**
 * Building my own custom Gene
 * 
 * @author StephenK
 *
 */
public class JeneticsDemo4 {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final Codec<ABC, AnyGene<ABC>> CODEC = Codec.of(
								Genotype.of(AnyChromosome.of(JeneticsDemo4::random)),
								gt -> gt.gene().allele());
		
		final Engine<AnyGene<ABC>, Integer> engine = 
					Engine.builder(JeneticsDemo4::fitness, CODEC)
						.populationSize(500)
						.optimize(Optimize.MAXIMUM)
						.offspringSelector(new RouletteWheelSelector<>())
						.build();
		

		final EvolutionStatistics<Integer, DoubleMomentStatistics> statistics =
			     EvolutionStatistics.ofNumber();
		
		final Phenotype<AnyGene<ABC>, Integer> best = engine.stream()
											.limit(Limits.bySteadyFitness(10))
											.peek(statistics)
											.limit(100)
											.collect(EvolutionResult.toBestPhenotype());
		
		System.out.println(best);
		System.out.println(statistics);

	}
	
	private static ABC random() {
		return new ABC(RandomRegistry.random().nextInt(10000));
	}
	
	private static int fitness(final ABC abc) {
		return abc.get();
	}
	
	
	
	// The 1st method to create a custom gene
	private static class ABC {
		
		private int value;
		
		public ABC(int value) {
			this.value = value;
		}
		
		public int get() {
			return value;
		}
		
		@Override
		public String toString() {
			return String.valueOf(get());
		}
	}

}
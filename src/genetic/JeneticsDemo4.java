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
import io.jenetics.engine.Limits;
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
								gt -> gt.getGene().getAllele());
		
		final Engine<AnyGene<ABC>, Integer> engine = 
					Engine.builder(JeneticsDemo4::fitness, CODEC)
						.populationSize(500)
						.optimize(Optimize.MAXIMUM)
						.offspringSelector(new RouletteWheelSelector<>())
						.build();
		

		final Phenotype<AnyGene<ABC>, Integer> best = engine.stream()
											.limit(Limits.bySteadyFitness(10))
											.limit(100)
											.collect(EvolutionResult.toBestPhenotype());
		
		System.out.println(best);

	}
	
	private static ABC random() {
		return new ABC(RandomRegistry.getRandom().nextInt(10000));
	}
	
	private static int fitness(final ABC abc) {
		return abc.get();
	}
	
	
	
	// The 1st method to create a custom gene
	private static class ABC implements Comparable<ABC> {
		
		private int value;
		
		public ABC(int value) {
			this.value = value;
		}
		
		public int get() {
			return value;
		}

		@Override
		public int compareTo(ABC o) {
			// TODO Auto-generated method stub
			return Integer.valueOf(value).compareTo(o.value);
		}
		
		@Override
		public int hashCode() {
			return String.valueOf(value).hashCode();
		}
		
		@Override
		public String toString() {
			return String.valueOf(value);
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if (obj instanceof ABC) {
				
				ABC abc = (ABC) obj;
				
				return this.value == abc.value;				
			}		
			
			return false;
		}	
	}

}
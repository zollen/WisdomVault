package genetic.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.jenetics.AltererResult;
import io.jenetics.AnyChromosome;
import io.jenetics.AnyGene;
import io.jenetics.Genotype;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.StochasticUniversalSelector;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.ISeq;
import io.jenetics.util.Seq;

public class MazeAgent {
	
	private static final Random rand = new Random(23);
	
	private static final int MAX_GENERATIONS = 5;
	private static final int POPULATION_SIZE = 200;
	private static final int MAX_UNCHANGED_GENERATIONS = 1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final MazeLoader loader = new MazeLoader("data/mymaze.txt");
		
		final Codec<MazeGame, AnyGene<MazeGame>> CODEC = Codecs
				.ofScalar(() -> loader.create().random());
		
		final Engine<AnyGene<MazeGame>, Double> engine = Engine
				.builder(MazeAgent::score, CODEC)
					.populationSize(POPULATION_SIZE)
					.optimize(Optimize.MAXIMUM)
					.offspringSelector(new StochasticUniversalSelector<>())
					.alterers(new MazeMutator(0.1, loader))
					.build();
		
		final Phenotype<AnyGene<MazeGame>, Double> result = engine.stream()
				// Truncate the evolution stream if no better individual could
				// be found after 5 consecutive generations.
				.limit(Limits.bySteadyFitness(MAX_UNCHANGED_GENERATIONS))
				// Terminate the evolution after maximal 10 generations.
				.limit(MAX_GENERATIONS).collect(EvolutionResult.toBestPhenotype());
		
	
		MazeGame game = result.genotype().gene().allele();
		System.out.println(game);
	}
	
	public static double score(MazeGame game) {
		return game.score();
	}
	

	
	private static class MazeMutator extends Mutator<AnyGene<MazeGame>, Double> {
		
		private MazeLoader loader;
		
		public MazeMutator(double prob, MazeLoader loader) {
			super(prob);
			
			this.loader = loader;
		}
		
		@Override
		public AltererResult<AnyGene<MazeGame>, Double> alter(Seq<Phenotype<AnyGene<MazeGame>, Double>> population, long generation) {
				
			List<Phenotype<AnyGene<MazeGame>, Double>> games = new ArrayList<Phenotype<AnyGene<MazeGame>, Double>>();
			
			population.forEach(child -> {
				
				Phenotype<AnyGene<MazeGame>, Double> target = child;
				MazeGame game = child.genotype().gene().allele();
				
				if (rand.nextDouble() <= this._probability) {
					
					double score = game.score();
					
					final MazeGame gg = game.clone();
					
					if (score > 0)
						gg.optimize();
					else
						gg.mutate(loader);
					
					target = Phenotype.of(
								Genotype.of(
									AnyChromosome.of(() -> gg)), generation);
				}

				games.add(target);

			});
		
			return AltererResult.of(ISeq.of(games));
		}		
	}
}

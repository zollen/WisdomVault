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
	
	private static final int MAX_UNCHANGED_GENERATIONS = 10;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final Codec<MazeGame, AnyGene<MazeGame>> CODEC = Codecs
				.ofScalar(MazeAgent::newInstance);
		
		final Engine<AnyGene<MazeGame>, Integer> engine = Engine
				.builder(MazeAgent::score, CODEC)
					.populationSize(100)
					.optimize(Optimize.MAXIMUM)
					.offspringSelector(new StochasticUniversalSelector<>())
					.alterers(new MazeMutator(0.1))
					.build();
		
		final Phenotype<AnyGene<MazeGame>, Integer> result = engine.stream()
				// Truncate the evolution stream if no better individual could
				// be found after 5 consecutive generations.
				.limit(Limits.bySteadyFitness(MAX_UNCHANGED_GENERATIONS))
				// Terminate the evolution after maximal 10 generations.
				.limit(20).collect(EvolutionResult.toBestPhenotype());
		
	
		MazeGame game = result.getGenotype().getGene().getAllele();
		System.out.println(game);
		System.out.println(game.moves().size() + " moves");
	}
	
	public static int score(MazeGame game) {
		return game.score();
	}
	
	public static MazeGame newInstance() {
		MazeGame game = new MazeGame();
		
		game.random();
		
		return game;
	}
	
	
	private static class MazeMutator extends Mutator<AnyGene<MazeGame>, Integer> {
		
		public MazeMutator(double prob) {
			super(prob);
		}
		
		@Override
		public AltererResult<AnyGene<MazeGame>, Integer> alter(Seq<Phenotype<AnyGene<MazeGame>, Integer>> population, long generation) {
				
			List<Phenotype<AnyGene<MazeGame>, Integer>> games = new ArrayList<Phenotype<AnyGene<MazeGame>, Integer>>();
			
			population.forEach(child -> {
				
				Phenotype<AnyGene<MazeGame>, Integer> target = child;
				MazeGame game = child.getGenotype().getGene().getAllele();
				
				if (rand.nextDouble() <= this._probability) {
					
					int mutations = (int) (MAX_UNCHANGED_GENERATIONS > generation ? 
							Math.abs(MAX_UNCHANGED_GENERATIONS - generation) : 1); 
					
					final MazeGame gg = game.clone();
					gg.mutate(mutations);
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

package genetic;

import java.util.concurrent.atomic.AtomicInteger;

import io.jenetics.AnyGene;
import io.jenetics.EnumGene;
import io.jenetics.Mutator;
import io.jenetics.Optimize;
import io.jenetics.PermutationChromosome;
import io.jenetics.Phenotype;
import io.jenetics.TournamentSelector;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.engine.Limits;
import io.jenetics.stat.DoubleMomentStatistics;
import io.jenetics.util.ISeq;

public class JeneticsDemo5 {
	
	private static final ISeq<String> ALLELES = ISeq.of("one", "two", "three", "four", "five", 
			"six", "seven", "eight", "nine", "ten");
	
	private static final EnumGene<String> GENE = EnumGene.of(ALLELES);
	
	private static final PermutationChromosome<String> CHO = PermutationChromosome.of(ALLELES, 4);
	
	private static final PermutationChromosome<EnumGene<String>> CHF = PermutationChromosome.of(
			GENE.newInstance(), GENE.newInstance(), GENE.newInstance()
	);
	
	private static final ISeq<String> IDEAL = ISeq.of("four", "eight", "ten", "two");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 3; i++)
			System.out.println(GENE.newInstance());
		
		for (int i = 0; i < 3; i++)
			System.out.println(CHF.newInstance());
		
		for (int i = 0; i < 3; i++)
			System.out.println(CHO.newInstance());
		
		
		final Engine<AnyGene<ISeq<String>>, Double> engine = Engine
				.builder(JeneticsDemo5::fitness, Codecs.ofScalar(JeneticsDemo5::random))
					.populationSize(1000)
					.optimize(Optimize.MINIMUM)
					.survivorsSelector(new TournamentSelector<>(500))
					.offspringSelector(new TournamentSelector<>(100))
					.alterers(new Mutator<>())
					.build();
		
		final EvolutionStatistics<Double, DoubleMomentStatistics> statistics =
			     EvolutionStatistics.ofNumber();
				
		final Phenotype<AnyGene<ISeq<String>>, Double> result = engine.stream()
				// Truncate the evolution stream if no better individual could
				// be found after 5 consecutive generations.
				.limit(Limits.bySteadyFitness(20))
				.peek(statistics)
				// Terminate the evolution after maximal 100 generations.
				.limit(100).collect(EvolutionResult.toBestPhenotype());
		
		System.out.println();
		System.out.println("Ideal PhenoType: " + IDEAL);
		System.out.println("Best PhenoType: " + result);
		
		System.out.println(statistics);
	}
	
	public static ISeq<String> random() {
		
		PermutationChromosome<String> cho = CHO.newInstance();
		return ISeq.of(cho.getGene(0).getAllele(), 
					cho.getGene(1).getAllele(), 
					cho.getGene(2).getAllele(),
					cho.getGene(3).getAllele());
	}
	
	public static double fitness(ISeq<String> numbers) {
	
		AtomicInteger index = new AtomicInteger(0);
		return numbers.stream().mapToDouble(p -> {
			return score(p, IDEAL.get(index.getAndIncrement()));
		}).sum();
	}
	
	public static double score(String left, String right) {
		
		int ind1 = ALLELES.indexOf(left);
		int ind2 = ALLELES.indexOf(right);
			
		return Math.abs(ind1 - ind2);
	}

}
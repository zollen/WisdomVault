package genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticBasic {
	
	public static final int SEED = 83;
	public static final int GENERATIONS = 13;
	public static final int POPULATION_SIZE = 100;
	public static final int SELECTION_SIZE = 10;
	public static final int MATING_PARTNERS = 2;
	public static final int GENE_LENGTH = 65;
	public static final double CROSSOVER_RATE = 0.5;
	public static final double MUTATION_RATE = 0.01;
	
	public static final Random rand = new Random(SEED);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<Individual> individuals = new ArrayList<Individual>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			individuals.add(new CharacterString());
		}
		
		Population population = new Population(rand, individuals);
		
		
		CharacterString ideal = new CharacterString("10110001000001000100001000001001110010000001000001000000000011110");
	
		
		SimpleGeneticAlgorthim genetic = new SimpleGeneticAlgorthim(rand, ideal, 
				GENERATIONS, SELECTION_SIZE, MATING_PARTNERS);
		Individual best = genetic.begin(population);
		
		System.out.println("TARGET: " + ideal);
		System.out.println("BEST  : " + best);
	}
	
	private static class CharacterString implements Individual {
		
		private String gene = null;
		
		public CharacterString() {
			this.gene = random();
		}
		
		public CharacterString(String gene) {
			this.gene = gene;
		}

		@Override
		public Double score(Individual ideal) {
			// TODO Auto-generated method stub
			double score = 0;
			
			for (int i = 0; i < GENE_LENGTH; i++) {
				if (this.gene.charAt(i) == ((CharacterString)ideal).gene.charAt(i)) {
					score++;
				}
			}
			
			return Double.valueOf(score / GENE_LENGTH);
		}

		@Override
		public Individual mate(List<Individual> individuals) {
			// TODO Auto-generated method stub
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < GENE_LENGTH; i++) {
				
				if (individuals.size() < 2) {
					if (rand.nextDouble() < CROSSOVER_RATE) {
						builder.append(this.gene.charAt(i));
					}
					else {
						builder.append(((CharacterString)individuals.get(0)).gene.charAt(i));
					}
				}
				else {	
					
					List<Individual> subjects = new ArrayList<Individual>(individuals);
					
					subjects.add(this);
					
					builder.append(((CharacterString) subjects
							.get(rand.nextInt(subjects.size()))).gene.charAt(i));
				}
			}
			
			return new CharacterString(builder.toString()).mutation(MUTATION_RATE);
		}

		@Override
		public Individual variation(double param) {
			// TODO Auto-generated method stub
			return new CharacterString(random());
		}

		@Override
		public Individual mutation(double param) {
			// TODO Auto-generated method stub
			StringBuilder builder = new StringBuilder(gene);
			
			for (int i = 0; i < GENE_LENGTH; i++) {
				
				if (rand.nextDouble() <= param) {
					builder.setCharAt(i, builder.charAt(i) == '0' ? '1' : '0');
				}
			}
			
			return new CharacterString(builder.toString());
		}
		
		@Override
		public String toString() {
			return gene;
		}
		
		private String random() {
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < GENE_LENGTH; i++) {
				builder.append(rand.nextBoolean() ? "1" : "0");
			}
			
			return builder.toString();
		}	
	}

}

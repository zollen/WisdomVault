package genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GeneticBasic {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	public static final int SEED = 83;
	
	public static final int GENERATIONS = 13;
	public static final double MUTATION_RATE = 0.01;
	public static final int POPULATION_SIZE = 100;
	public static final int SELECTION_SIZE = 10;
	public static final int GENE_LENGTH = 65;
	public static final double UNIFORM_RATE = 0.5;
	
	
	public static final Random rand = new Random(SEED);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Population parents = new Population();
		Individual target = new Individual("10110001000001000100001000001001110010000001000001000000000011110");
		
		
		SimpleGenetic genetic = new SimpleGenetic(target);
		genetic.begin(parents, GENERATIONS);

	}
	
	public static class SimpleGenetic {
			
		private Individual target = null;
		
		public SimpleGenetic(Individual target) {
			this.target = target;
		}
		
		public void begin(Population population, int gens) {
			
			System.out.println("Generation #1: " + ff.format(population.score(target)));
			
			Population parents = population;
			
			for (int i = 2; i <= gens; i++) {
				Population children = evolve(parents);
				System.out.println("Generation #" + i + ": " + ff.format(children.score(target)));
				parents = children;
			}	
			
			sort(parents.people());
			
			System.out.println("TARGET: " + target.gene());
			System.out.println("BEST  : " + parents.people().get(0).gene());
		}
		
		public Population evolve(Population parents) {
			
			List<Individual> children = new ArrayList<Individual>();
			
			for (int i = 0; i < POPULATION_SIZE; i++) {
				
				Individual ind1 = selection(parents);
				Individual ind2 = selection(parents);
				
				Individual child = mate(ind1, ind2);
				children.add(child);
			}
			
			return new Population(children);
		}
		
		public Individual selection(Population population) {
			
			List<Individual> subjects = new ArrayList<Individual>();
			
			for (int i = 0; i < SELECTION_SIZE; i++) {
				subjects.add(population.people().get(rand.nextInt(POPULATION_SIZE)));
			}
		
			
			sort(subjects);
			
			return subjects.get(0);
		}
		
		public Individual mate(Individual ind1, Individual ind2) {
			
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < GENE_LENGTH; i++) {
				
				if (rand.nextDouble() < UNIFORM_RATE) {
					builder.append(ind1.gene().charAt(i));
				}
				else {
					builder.append(ind2.gene().charAt(i));
				}
			}
			
			return mutation(new Individual(builder.toString()));
		}
		
		public Individual mutation(Individual individual) {
	
			StringBuilder builder = new StringBuilder(individual.gene());
			
			for (int i = 0; i < GENE_LENGTH; i++) {
				
				if (rand.nextDouble() <= MUTATION_RATE) {
					builder.setCharAt(i, builder.charAt(i) == '0' ? '1' : '0');
				}
			}
			
			return new Individual(builder.toString());
		}
		
		public void sort(List<Individual> individuals) {
			
			Collections.sort(individuals, new Comparator<Individual>() {

				@Override
				public int compare(Individual o1, Individual o2) {
					// TODO Auto-generated method stub
					return o1.score(target).compareTo(o2.score(target)) * -1;
				}	
			});
		}
	}
	
	public static class Individual {
			
		private String gene = null;
		
		public Individual() {
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < GENE_LENGTH; i++) {
				builder.append(rand.nextBoolean() ? "1" : "0");
			}
			
			this.gene = builder.toString();
		}
		
		public Individual(String gene) {
			this.gene = gene;
		}
		
		public Double score(Individual target) {
			
			double score = 0;
			
			for (int i = 0; i < GENE_LENGTH; i++) {
				if (this.gene.charAt(i) == target.gene().charAt(i)) {
					score++;
				}
			}
			
			return Double.valueOf(score / GENE_LENGTH);
		}
		
		public String gene() {
			return gene;
		}
	}
	
	public static class Population {
		
		List<Individual> individuals = new ArrayList<Individual>();
		
		public Population() {
			
			for (int i = 0; i < POPULATION_SIZE; i++)
				individuals.add(new Individual());
		}
		
		public Population(List<Individual> fittest) {
			this.individuals = fittest;
		}
			
		public List<Individual> people() {
			return individuals;
		}
		
		public double score(Individual target) {
			
			double score = 0;
			for (Individual person : individuals) {			
				score += person.score(target);
			}
			
			return (double) score / POPULATION_SIZE;
		}
	}

}
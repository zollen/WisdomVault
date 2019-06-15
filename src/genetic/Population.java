package genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Population {
	
	List<Individual> population = new ArrayList<Individual>();
	
	private Random rand = null;
	
	public Population(Random rand, List<Individual> individuals) {
		this.population = individuals;
		this.rand = rand;
	}
		
	public List<Individual> people() {
		return population;
	}
	
	public double score(Individual ideal) {
		
		double score = 0;
		for (Individual person : population) {			
			score += person.score(ideal);
		}
		
		return (double) score / population.size();
	}
	
	public List<Individual> chosen(int size) {
		
		List<Individual> subjects = new ArrayList<Individual>();
		
		for (int i = 0; i < size; i++) {
			subjects.add(population.get(rand.nextInt(population.size())));
		}	
			
		return subjects;
	}
	
	public int size() {
		return population.size();
	}
	
	public List<Individual> fittness(Individual ideal, int size) {
		
		List<Individual> subjects = new ArrayList<Individual>(population);
		
		Collections.sort(subjects, new Comparator<Individual>() {

			@Override
			public int compare(Individual o1, Individual o2) {
					// TODO Auto-generated method stub
					return o1.score(ideal).compareTo(o2.score(ideal)) * -1;
			}	
		});
		
		return subjects.stream().limit(size).collect(Collectors.toList());
	}
	
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		population.stream().forEach(p -> builder.append(p + "\n"));
		
		return builder.toString();
	}
}

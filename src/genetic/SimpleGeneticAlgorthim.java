package genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleGeneticAlgorthim {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private Individual target = null;
	private Random rand = null;
	
	private int generations = 0;
	private int selectionSize = 0;
	private int matingPartners = 0;
	private double crossOverRate = 0.0;
	private double mutationRate = 0.0;
	
	public SimpleGeneticAlgorthim(Random rand, Individual target, int generations,
			int selectionSize, int matingPartners, double crossOverRate, double mutationRate) {
		this.target = target;
		this.rand = rand;
		this.selectionSize = selectionSize;
		this.generations = generations;
		this.matingPartners = matingPartners;
		this.crossOverRate = crossOverRate;
		this.mutationRate = mutationRate;
	}
	
	public Individual begin(Population population) {
		
		System.out.println("Generation #1: " + ff.format(population.score(target)));
		
		Population parents = population;
		
		for (int i = 2; i <= generations; i++) {
			Population children = evolve(parents);
			System.out.println("Generation #" + i + ": " + ff.format(children.score(target)));
			parents = children;
		}	
		
		return parents.fittness(target, 1).get(0);
	}
	
	public Population evolve(Population population) {
		
		List<Individual> children = new ArrayList<Individual>();
		
		for (int i = 0; i < population.size(); i++) {
			
			List<Individual> parents = new Population(rand, population.chosen(selectionSize))
											.fittness(target, matingPartners);
			
			Individual child = parents.remove(0).mate(crossOverRate, mutationRate, parents);
			children.add(child);
		}
		
		return new Population(rand, children);
	}
}

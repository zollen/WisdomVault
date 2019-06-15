package genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.nd4j.linalg.primitives.Pair;

public class PolynomialApprox {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	
	public static final int GENERATIONS = 50;
	public static final int POPULATION_SIZE = 100;
	public static final int SELECTION_SIZE = 20;
	public static final int MATING_PARTNERS = 10;
	public static final double CROSSOVER_RATE = 0.5;
	public static final double MUTATION_RATE = 0.01;
	
	
	private static final Random rand = new Random(83);
	
	// 3 x^3 - 2 x^2 + 4 x + 5
	private static final PolynomialFunc IDEAL = new PolynomialFunc(new double[] { 5.0, 1.0, -2.0, 3.0 });

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		List<Individual> individuals = new ArrayList<Individual>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			individuals.add(new PolynomialFunc());
		}
		Population population = new Population(rand, individuals);
		
		
		SimpleGeneticAlgorthim genetic = new SimpleGeneticAlgorthim(rand, IDEAL, 
				GENERATIONS, SELECTION_SIZE, MATING_PARTNERS, CROSSOVER_RATE, MUTATION_RATE);
		PolynomialFunc best = (PolynomialFunc) genetic.begin(population);
		
		
		System.out.println("TARGET: " + IDEAL);
		eval(IDEAL);
		
		System.out.println("BEST  : " + best);	
		eval(best);
	}
	
	public static void eval(PolynomialFunc func) {
		
		double [] params = { 2.1, 2.7, 3.2, 3.5, 5.4, 8.1, 10.871 };
		
		List<String> eq = new ArrayList<String>();
		
		for (double param : params) {
			double res = func.value(param);
			eq.add("f(" + ff.format(param) + ") = " + ff.format(res));
		}
		
		System.out.println(eq.stream().collect(Collectors.joining(", ")));
	}
	
	
	
	private static class PolynomialFunc implements Individual {
		
		private static final int MAX_NUM_COFFICIENTS = 4;
		private static final int MAX_COEFFICIENT_NUMBER = 200;
		private static final int MAX_VARIANCE = 100000;
		private static final double MUTATION_RATE = 0.1;
		private static final double TORLANCE = 0.0001;
		
		private double [] coeffs = null;
		private PolynomialFunction func = null;
		
		public PolynomialFunc() {		
			this.coeffs = random();
			this.func = new PolynomialFunction(this.coeffs);
		}
		
		public PolynomialFunc(double [] coeffs) {
			this.coeffs = coeffs;
			this.func = new PolynomialFunction(coeffs);
		}

		@Override
		public Double score(Individual ideal) {
			// TODO Auto-generated method stub
			double [] actuals = new double[2000];
			double [] expected = new double[2000];
			double param = -500;
			
			double variance = 0.0;
			
			for (int i = 0; i < 2000; i++) {
				
				expected[i] = ((PolynomialFunc) ideal).value(param);
				actuals[i] = func.value(param);
				
				double diff = Math.abs(expected[i] - actuals[i]);
				variance += (diff < TORLANCE) ? 0 : Math.log(diff);
				
				param += 0.5;
			}
					
			return (double) (MAX_VARIANCE - variance) / MAX_VARIANCE;
		}

		@Override
		public Individual mate(double mutationRate, List<Individual> individuals) {
			// TODO Auto-generated method stub
			double [] newcoeffs = new double[MAX_NUM_COFFICIENTS];
			
			List<Individual> subjects = new ArrayList<Individual>(individuals);			
			subjects.add(this);
			
			final DoubleAdder total = new DoubleAdder();
			List<Pair<Double, Individual>> list1 = new ArrayList<Pair<Double, Individual>>();
			subjects.forEach(p -> {
				double val = ((PolynomialFunc)p).score(IDEAL);
				list1.add(new Pair<Double, Individual>(val, p));
				total.add(val);
			});
			
			
			List<Pair<Double, Individual>> list2 = new ArrayList<Pair<Double, Individual>>();	
			list1.stream().forEach(p -> {
				list2.add(new Pair<Double, Individual>(
						p.getFirst() / total.doubleValue(), p.getSecond()));
			});
			
			List<Pair<Double, Individual>> list3 = new ArrayList<Pair<Double, Individual>>();
			final DoubleAdder percent = new DoubleAdder();
			list2.stream().forEach(p -> {
				percent.add(p.getFirst());
				list3.add(new Pair<Double, Individual>(percent.doubleValue(), p.getSecond()));
			});
			
	
			for (int i = 0; i < MAX_NUM_COFFICIENTS; i++) {	
				
				
				
				newcoeffs[i] = ((PolynomialFunc) 
						subjects.get(rand.nextInt(subjects.size()))).coeffs()[i];
			}
			
			return new PolynomialFunc(newcoeffs).mutation(MUTATION_RATE);
		}

		@Override
		public Individual variation(double param) {
			// TODO Auto-generated method stub
			return new PolynomialFunc();
		}

		@Override
		public Individual mutation(double param) {
			// TODO Auto-generated method stub
			for (int i = 0; i < MAX_NUM_COFFICIENTS; i++) {
				if (rand.nextDouble() <= param)
					coeffs[i] = coeffs[i] + rand.nextInt(2);
			}
			
			return new PolynomialFunc(coeffs);
		}
		
		@Override
		public String toString() {
			return func.toString();
		}
		
		public double value(double param) {
			return func.value(param);
		}
		
		public double [] coeffs() {
			return coeffs;
		}
		
		private double [] random() {
			
			double [] coeffs = new double[MAX_NUM_COFFICIENTS];
			
			for (int i = 0; i < MAX_NUM_COFFICIENTS; i++) {
				double coeff = rand.nextInt(MAX_COEFFICIENT_NUMBER);
				if (rand.nextDouble() <= 0.5)
					coeff *= -1;
				
				coeffs[i] = coeff;
			}
			
			return coeffs;
		}
	}

}

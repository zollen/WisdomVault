package statistics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

public class ProbabilityDistribution {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Pair<String, Double>> pmf = new ArrayList<Pair<String, Double>>();
		pmf.add(new Pair<String, Double>("0.33A", 0.64));
		pmf.add(new Pair<String, Double>("0.33B", 0.73));
		pmf.add(new Pair<String, Double>("0.33C", 0.76));
		
		EnumeratedDistribution<String> dist = new EnumeratedDistribution<String>(pmf);
		
		double aa = 0;
		double bb = 0;
		double cc = 0;
		double all = 1000;
		for (int i = 0; i < all; i++) {
			String tmp = dist.sample();
			if (tmp.endsWith("A"))
				aa++;
			else
			if (tmp.endsWith("B"))
				bb++;
			else
			if (tmp.endsWith("C"))
				cc++;
		}
		
		System.out.println("A: " + aa / all + " B: " + bb / all + " C: " + cc / all);
	}

}

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;


public class KMeansClustering1 {
	
	private static final Vector2D [] POINTS = {
			new Vector2D(0, 4),
			new Vector2D(3, -5),
			new Vector2D(-2, -2),
			new Vector2D(-4, -8),
			new Vector2D(-2, 5),
			new Vector2D(1, 7),
	};
	
	private static final Random rand = new Random(0);

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		clustering(POINTS, 2);
	}
	
	public static double variance (Set<Vector2D> points, int k) {
		
		Vector2D center = centerofMass(points);
		
		double sumX = 0d;
		double sumY = 0d;
		for (Vector2D pt : points) {
			
			sumX += Math.pow(pt.getX() - center.getX(), 2);	
			sumY += Math.pow(pt.getY() - center.getY(), 2);
		}
		
		if (points.size() <= 0)
			return 0d;
		
		return (sumX + sumY) / points.size() * POINTS.length / points.size() * 1 / (k * k);
	}
	
	public static Vector2D centerofMass(Set<Vector2D> points) {
		
		double sumX = 0d;
		double sumY = 0d;
		
		for (Vector2D pt : points) {
			sumX += pt.getX();
			sumY += pt.getY();
		}
		
		if (points.size() <= 0)
			return null;
		
		return new Vector2D(sumX / points.size(), sumY / points.size());
	}
	
	public static Map<Vector2D, Set<Vector2D>> _clustering(Set<Vector2D> centers, Vector2D [] points, int k, int i) {
		
		Map<Vector2D, Set<Vector2D>> map = new HashMap<Vector2D, Set<Vector2D>>();
		
		centers.stream().forEach(p -> map.put(p, new HashSet<Vector2D>()));
		
		Arrays.stream(points).forEach(p -> {
			
			Vector2D center = centers.stream().min(Comparator.comparing(c -> c.distance(p))).orElse(null);
			if (center != null)
				map.get(center).add(p);			
		});

		Set<Vector2D> next = new HashSet<Vector2D>();
		
		map.entrySet().stream().forEach(p -> { 
						Vector2D center = centerofMass(p.getValue());
						if (center != null)
							next.add(center);
						});
		
		if (i >= 3)
			return map;
		else	
			return _clustering(next, points, k, i + 1);
	}
	
	public static void clustering(Vector2D [] points, int k) {
		
		Map<Double, Map<Vector2D, Set<Vector2D>>> library = new TreeMap<Double, Map<Vector2D, Set<Vector2D>>>(
				new Comparator<Double>() {

					@Override
					public int compare(Double o1, Double o2) {
						// TODO Auto-generated method stub
						return o1.compareTo(o2) * -1;
					}
				});
		
		for (int i = 0; i < 10; i++) {
			
			Set<Vector2D> centers = random(points, k);
			
			Map<Vector2D, Set<Vector2D>> map = _clustering(centers, points, k, 0);
			
			DoubleAdder d = new DoubleAdder();
			
			map.entrySet().stream().forEach(p -> {
				
				d.add(variance(p.getValue(), k));
			});
			
			
			if (map.size() == k)
				library.put(d.doubleValue(), map);
		}
		
		library.entrySet().stream().forEach(p -> {
			
			print(p.getValue(), k);
		});
	}
	
	
	public static Set<Vector2D> random(Vector2D [] points, int k) {
		
		Set<Vector2D> clusters = new HashSet<Vector2D>();
		
		double maxX = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		
		for (Vector2D point : points) {
			
			if (maxX < point.getX()) {
				maxX = point.getX();
			}
			
			if (minX > point.getX()) {
				minX = point.getX();
			}
			
			if (maxY < point.getY()) {
				maxY = point.getY();
			}
			
			if (minY > point.getY()) {
				minY = point.getY();
			}
		}
		
		
		for (int i = 0; i < k; i++) {
			int xx = rand.nextInt((int) (maxX - minX)); 
			int yy = rand.nextInt((int) (maxY - minY));
			
			xx = xx - (int) Math.abs(minX) - 1;
			yy = yy - (int) Math.abs(minY) - 1;
			
			clusters.add(new Vector2D(xx, yy));			
		}
			
		return clusters;
	}
	
	public static void print(Map<Vector2D, Set<Vector2D>> map, int k) {
		
		DoubleAdder d = new DoubleAdder();
		
		map.entrySet().stream().forEach(p -> {
			
			d.add(variance(p.getValue(), k));
		});
		
		System.out.println("****** k = " + k + ", Variance = " + d.doubleValue());
		
		print(map);
	}
	
	public static void print(Map<Vector2D, Set<Vector2D>> map) {
		
		map.entrySet().stream().forEach(p -> {

			System.out.println( p.getKey() + " ==> " +
					p.getValue().stream().map( Vector2D::toString ).collect(Collectors.joining(", ")));
		});
	}
}

package machinelearning;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;


public class KMeansClustering1 {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	
	private static final Vector2D [] POINTS = {
			new Vector2D(1, 0),
			new Vector2D(10, 12),
			new Vector2D(8, 13),
			new Vector2D(9, 10),
			new Vector2D(12, 11),
			new Vector2D(8, 10),
			new Vector2D(1, 1),
			new Vector2D(-1, -3),
			new Vector2D(-2, -2),
			new Vector2D(5, -2),
			new Vector2D(-10, 8),
			new Vector2D(-12, 10),
			new Vector2D(-8, 14),
			new Vector2D(-12, 12),
			new Vector2D(-9, 11)
	};
	
	private static final Vector2D [] TESTS = {
			new Vector2D(8, 8),
			new Vector2D(-4, -4),
			new Vector2D(-10, 10)
	};
	
	private static final Random rand = new Random(0);

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		// k = 1, kvar = 0
		// k = 2, kvar = 900
		// k = 3, kvar = 1469.6  <-- Optimal k where anything beyond have sudden drop of slope
		// k = 4, kvar = 1492.9
		// k = 5, kvar = 1510.1
		// k = 6, kvar = 1525.0
		// k = 7, lvar = 1533.5
		Map<Vector2D, Set<Vector2D>> clusters = clustering(3);
		
		System.out.println("======= k = 3, VarBetweenClusters: " + ff.format(kvariance(clusters)) + " =======");
		print(clusters);
		
	
		for (Vector2D pt : TESTS) {
			Vector2D clus = classify(clusters, pt);
			System.out.println("Test: " + pt + " ======> Cluster: " + clus);
		}	
	}
	
	public static Vector2D classify(Map<Vector2D, Set<Vector2D>> map, Vector2D target) {
		
		Set<Vector2D> pts = new HashSet<Vector2D>();
		
		map.entrySet().stream().forEach(p -> p.getValue().stream().forEach(q -> pts.add(q)));
		
		Vector2D [] points = pts.toArray(new Vector2D[pts.size()]);
		
		List<Vector2D> clostest = knnsearch(points, target, 3);
		
		Map<Vector2D, Integer> counter = new HashMap<Vector2D, Integer>();
		
		Set<Vector2D> chosen = new HashSet<Vector2D>();
		AtomicInteger max = new AtomicInteger(-1);
		map.entrySet().stream().forEach(
								p -> { 
									
									clostest.stream().forEach(q -> {
										
										if (p.getValue().contains(q)) {
											
											Integer curr = counter.get(p.getKey());
											if (curr == null)
												curr = Integer.valueOf(1);
											else
												curr = Integer.valueOf(curr + 1);
										
											counter.put(p.getKey(), curr);
											if (curr > max.intValue()) {
												max.set(curr);
												chosen.clear();
												chosen.add(p.getKey());
											}
										}
									});
								});
		
		
		return chosen.iterator().next();
	}
	
	public static List<Vector2D> knnsearch(Vector2D [] points, Vector2D target, int k) {
		
		Map<Double, List<Vector2D>> map = new TreeMap<Double, List<Vector2D>>();
		
		for (Vector2D point : points) {
			
			double dist = target.distance(point);
			
			List<Vector2D> list = map.get(dist);
			if (list == null) {
				list = new ArrayList<Vector2D>();
				map.put(dist, list);
			}
			
			list.add(point);
		}
		
		List<Vector2D> ll = new ArrayList<Vector2D>();
		
		map.entrySet().stream().forEach(p -> p.getValue().stream().forEach(q -> ll.add(q)));
		
		return ll.subList(0,  k);
	}
	

	public static double kvariance (Map<Vector2D, Set<Vector2D>> map) {
				
		Set<Vector2D> pts = new HashSet<Vector2D>();
		pts.addAll(Arrays.asList(POINTS));
		Vector2D aCenter = centerofMass(new HashSet<Vector2D>(pts));
		
		DoubleAdder adder = new DoubleAdder();
		
		map.entrySet().stream().forEach(p -> {
			
			adder.add(Math.pow(p.getKey().distance(aCenter), 2) * p.getValue().size());
		});
		
						
		return adder.doubleValue();
	}
	
	public static double variance (Vector2D center, Set<Vector2D> points) {
		
		double sum = 0d;
		for (Vector2D pt : points) {
			
			pt.distance(center);
			sum += Math.pow(pt.distance(center), 2);	
		}
		
		if (points.size() <= 0)
			return 0d;
		
		return sum;
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
	
	public static Map<Vector2D, Set<Vector2D>> _clustering(Set<Vector2D> centers, int k, int i) {
		
		Map<Vector2D, Set<Vector2D>> map = new HashMap<Vector2D, Set<Vector2D>>();
		
		centers.stream().forEach(p -> map.put(p, new HashSet<Vector2D>()));
		
		Arrays.stream(POINTS).forEach(p -> {
			
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
		
		if (i >= 10)
			return map;
		else	
			return _clustering(next, k, i + 1);
	}
	
	public static Map<Vector2D, Set<Vector2D>> clustering(int k) {
		
		Map<Double, Map<Vector2D, Set<Vector2D>>> library = new TreeMap<Double, Map<Vector2D, Set<Vector2D>>>(
				new Comparator<Double>() {

					@Override
					public int compare(Double arg0, Double arg1) {
						// TODO Auto-generated method stub
						return arg0.compareTo(arg1) * -1;
					}
					
				});
		
		for (int i = 0; i < 1000; i++) {
			
			Set<Vector2D> centers = random(POINTS, k);
			
			Map<Vector2D, Set<Vector2D>> map = _clustering(centers, k, 0);
			
			if (map.size() == k) {
				library.put(kvariance(map), map);
			}
		}
		
		// just get the first entry (smallest variance)
		
		if (library.size() > 0) {
			return library.entrySet().stream().findFirst().get().getValue();
		}
		
		return null;
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
	
	public static void print(Map<Vector2D, Set<Vector2D>> map) {
	
		map.entrySet().stream().forEach(p -> {

			System.out.println(p.getKey() + " ==> " +
					p.getValue().stream().map( Vector2D::toString ).collect(Collectors.joining(", ")) +
					" ==> VarInCluster: " + ff.format(variance(p.getKey(), p.getValue())));
		});
	}
}

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class KMeansClustering1 {
	
	private static final Random rand = new Random(0);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vector2D [] points = {
				new Vector2D(0, 4),
				new Vector2D(3, -5),
				new Vector2D(-2, -2),
				new Vector2D(-4, -8),
				new Vector2D(-2, 5),
				new Vector2D(1, 7),
		};
		
		clustering(points, 2);
	}
	
	public static void _clustering(Set<Vector2D> centers, Vector2D [] points, int k) {
		
		Map<Vector2D, Set<Vector2D>> map = new HashMap<Vector2D, Set<Vector2D>>();
		
		if (centers == null)
			centers = random(points, k);
		
		for (Vector2D center : centers) {
			map.put(center, new HashSet<Vector2D>());
		}
		
			
		for (Vector2D pt : points) {
			
			double min = Double.MAX_VALUE;
			Vector2D clostest = null;
			for (Vector2D center : centers) {
				
				double dist = pt.distance(center);
				if (dist < min) {
					min = dist;
					clostest = center;
				}
			}
			
			map.get(clostest).add(pt);
		}

		System.out.println("=============================");
		map.entrySet().stream().forEach(p -> {

			System.out.println( p.getKey() + " ==> " +
			p.getValue().stream().map( Vector2D::toString ).collect(Collectors.joining(", ")));
		});
		
	}
	
	public static void clustering(Vector2D [] points, int k) {
	
		for (int i = 0; i < 3; i++)
			_clustering(null, points, k);
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
	
	public static void print(Set<Set<Vector2D>> sets) {
		
		System.out.println("============================");
		
		sets.stream().forEach(p -> {
			
			System.out.println ("[" + 
			p.stream().map( Vector2D::toString ).collect(Collectors.joining(", ")) + "]");
		});
	}	
	
	public static double distance(Set<Vector2D> lefts, Set<Vector2D> rights) {
		
		double sum = 0d;
		int num = 0;
		
		for (Vector2D left : lefts) {
			
			for (Vector2D right : rights) {
				
					sum += left.distance(right);
					num++;
			}
		}
		
		return (double) sum / num;
	}
}

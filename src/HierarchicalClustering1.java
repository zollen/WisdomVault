import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class HierarchicalClustering1 {

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
	
	public static void clustering(Vector2D [] points, int clusters) {
		
		Set<Set<Vector2D>> sets = new HashSet<Set<Vector2D>>();
		
		for (int i = 0; i < points.length; i++) {	
			HashSet<Vector2D> set = new HashSet<Vector2D>();
			set.add(points[i]);
			sets.add(set);
		}
		
		_clustering(sets, clusters);
		
		sets.stream().forEach(p -> {
			
			System.out.println ("[" + 
			p.stream().map( Vector2D::toString ).collect(Collectors.joining(", ")) + "]");
		});
	}
	
	public static void _clustering(Set<Set<Vector2D>> sets, int clusters) {
		
		Set<Vector2D> mleft = null;
		Set<Vector2D> mright = null;
		double min = Double.MAX_VALUE;
		
		if (sets.size() <= clusters)
			return;
		
		for (Set<Vector2D> right : sets) {
			
			for (Set<Vector2D> left : sets) {
				
				if (right != left) {
					
					double dist = distance(left, right);
					
					if (dist < min) {
						mleft = left;
						mright = right;
						min = dist;
					}	
				}
			}	
		}
		
		
		sets.remove(mleft);
		sets.remove(mright);
		mleft.addAll(mright);
		
		sets.add(mleft);
		
		_clustering(sets, clusters);
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

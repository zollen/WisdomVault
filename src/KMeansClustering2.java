import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;


public class KMeansClustering2 {
	
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
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		List<Point> points = new ArrayList<Point>();
		
		for (Vector2D point : POINTS)
			points.add(new Point(point));
		
		// initialize a new clustering algorithm. 
		// we use KMeans++ with 10 clusters and 10000 iterations maximum.
		// we did not specify a distance measure; the default (euclidean distance) is used.
		KMeansPlusPlusClusterer<Point> clusterer = new KMeansPlusPlusClusterer<Point>(3, 10000);
		List<CentroidCluster<Point>> clusterResults = clusterer.cluster(points);

		// output the clusters
		for (int i = 0; i < clusterResults.size(); i++) {
		    String tmp = clusterResults.get(i).getPoints().stream().map( Point::getLocation ).collect(Collectors.joining(", "));
		    System.out.println(clusterResults.get(i).getCenter() + " ===> " + tmp);
		    System.out.println("============================================================");
		}
	}
	
	public static class Point implements Clusterable {
	    private double[] points;
	    private Vector2D location;

	    public Point(Vector2D location) {
	        this.location = location;
	        this.points = new double[] { location.getX(), location.getY() };
	    }

	    public String getLocation() {
	        return location.toString();
	    }

	    public double[] getPoint() {
	        return points;
	    }
	}
}

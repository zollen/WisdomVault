package statistics;
import java.text.DecimalFormat;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.FastMath;

public class CommonStatistic {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	
	private static final double [] POINTS = {
			15, 20, 5, 8, 7, 40, 11, 9, 1, 3, 5, 4, 26, 31, 10, 6, 2, 13
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Review TestUtils for chi-square test, t-test, g-test, ANOVA test, KS-test
		System.err.println("Review TestUtils for chi-square test, t-test, g-test, ANOVA test, KS-test");
		
		// for small to medium size dataset for in-memory processing
		DescriptiveStatistics dstats = new DescriptiveStatistics();
		
		// for very large size dataset for filesystem processing
		SummaryStatistics sstats = new SummaryStatistics();

		// Add the data from the array
		for( int i = 0; i < POINTS.length; i++) {
		        dstats.addValue(POINTS[i]);
		        sstats.addValue(POINTS[i]);
		}

		{
			// Compute some statistics
			double mean = dstats.getMean();
			double std = dstats.getStandardDeviation();
			double median = dstats.getPercentile(50);
			System.out.println("mean: " + ff.format(mean) + ", sd: " + ff.format(std)
							+ ", " + ff.format(median));
		}
		
		{
			// Compute some statistics
			double mean = sstats.getMean();
			double std = sstats.getStandardDeviation();
			double median = dstats.getPercentile(50);
			System.out.println("mean: " + ff.format(mean) + ", sd: " + ff.format(std)
						+ ", " + ff.format(median));
		}
		
		{
			double mean = StatUtils.mean(POINTS);
			double std = FastMath.sqrt(StatUtils.variance(POINTS));
			double median = StatUtils.percentile(POINTS, 50);
			
			System.out.println("mean: " + ff.format(mean) + ", sd: " + ff.format(std)
					+ ", " + ff.format(median));

			// Compute the mean of the first three values in the array
			mean = StatUtils.mean(POINTS, 0, 3);
			System.out.println("mean(first 3): " + ff.format(mean));
		}
		
	}

}

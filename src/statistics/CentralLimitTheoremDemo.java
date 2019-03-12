package statistics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


/**
 * A demo showing the use of log axes.
 *
 */
public class CentralLimitTheoremDemo extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int TOTAL_DATA = 100;
	private static final int TOTAL_SAMPLES = 100000;
	private static final int SAMPLE_SIZE = 20;
	private static final int UPPER = 10;

	/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public CentralLimitTheoremDemo(final String title, Map<Double, Integer> distributions) {

        super(title);

        final XYSeries data1 = new XYSeries("Samples Distribution of the sample mean");
      
        
        Iterator<Double> itr = distributions.keySet().iterator();
        	
        while (itr.hasNext()) {
        	Double avg = itr.next();
        	Integer count = distributions.get(avg);
        	
        	data1.add(avg, count);
        }

        

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data1);
        
     

        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Central Limit Theorem",      		// chart title
            "X",              		 	// domain axis label
            "Y",                  	   // range axis label
            dataset,                  		// data
            PlotOrientation.VERTICAL,
            true,                     		// include legend
            true,
            false
        );

        final XYPlot plot = chart.getXYPlot();
        final NumberAxis domainAxis = new NumberAxis("X");
        final NumberAxis rangeAxis = new NumberAxis("Y");
    
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
        setContentPane(chartPanel);

    }

    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

    	List<Integer> samples = generateSamples();
    	
    	System.out.println("Mean(data): " + (double) samples.stream().mapToInt(p -> p).sum() / samples.size());
    	
    	Map<Double, Integer> distributions = generateDistribution(samples);
    	
    	System.out.println("Mean(dist): " + (double) distributions.keySet().stream().mapToDouble(p -> p).sum() / distributions.size());
    	
        final CentralLimitTheoremDemo demo = new CentralLimitTheoremDemo("Central Limit Theorem", distributions);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
    
    private static List<Integer> generateSamples() {
    	
    	Random rand = new Random(System.nanoTime());
    	
    	List<Integer> list = new ArrayList<Integer>();
    	
    	for (int i = 0; i < TOTAL_DATA; i++) {
    		int val = Math.abs(rand.nextInt() * 857) % UPPER;
    		if (val == 0)
    			val = 1;
    		
    		list.add(val);
    	}
    	
    	return list;
    }
    
    @SuppressWarnings("deprecation")
	private static Map<Double, Integer> generateDistribution(List<Integer> samples) {
    	
    	Random rand = new Random(System.nanoTime());
    	
    	Map<Double, Integer> map = new HashMap<Double, Integer>();
    	
    	for (int i = 0; i < TOTAL_SAMPLES; i++) {
    		
    		List<Integer> sample = new ArrayList<Integer>();
    		for (int j = 0; j < SAMPLE_SIZE; j++) {
    			sample.add(samples.get(Math.abs(rand.nextInt() * 709) % TOTAL_DATA));
    		}
    		
    		double avg = (double) sample.stream().mapToInt(p -> p).sum() / SAMPLE_SIZE;
    		
    		Integer count = map.get(avg);
    		if (count == null) {
    			count = new Integer(0);
    		}
    		
    		count = count + 1;
    		map.put(avg, count);
    	}
    	
    	return map;
    	
    }

}

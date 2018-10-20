
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
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
public class ZipfDemo1 extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final double EULER_MASCHERONI_CONSTANT = 0.5772156649d;
	
	private static final DecimalFormat formatter = new DecimalFormat("0.0000");

	/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public ZipfDemo1(final String title, long wordsCount, List<ZipfExercise1.Word> words) {

        super(title);

        final XYSeries data1 = new XYSeries("USA.txt Words Analysis");
        final XYSeries data2 = new XYSeries("Zipf Distribution Prediction");
       
       
        double total = 0d;
        for (int i = 1; i < words.size(); i++) {
        	total += (double) 1 / Math.pow(i, EULER_MASCHERONI_CONSTANT);
        }
   
        System.out.println("Rank            Word    Frequency      Freq(#N)/Freq(#1)  Probability          K             Average(K)          Deviation(K)");
        System.out.println("=============================================================================================================================");
        List<Double> allKs = new ArrayList<Double>();
        for (int i = 0; i < words.size(); i++) {
        	
        	ZipfExercise1.Word word = words.get(i);
        	
        	data1.add(i + 1, word.getCount());
        	data2.add(i + 1, 30000 * (double)(1 / Math.pow((i + 1), EULER_MASCHERONI_CONSTANT) / total));
        	
        	double prob = (double) word.getCount() / wordsCount;
        	double k = (double) prob * (i + 1);
        	allKs.add(k);
        	double avg = (double) allKs.stream().collect(Collectors.summingDouble(p -> p)) / allKs.size();
        	
        	System.out.println(String.format("%3d [%15s]   %4d           %5s             %5s               %5s        %5s             %7s", 
        			(i + 1), 
        			(word.getWord().substring(0, word.getWord().length() < 15 ? word.getWord().length() : 15)),
        			word.getCount(),
        			formatter.format((double) word.getCount() / words.get(0).getCount()),
        			formatter.format(prob),
        			formatter.format(k),
        			formatter.format(avg),
        			formatter.format(k - avg)
        			));

        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data1);
        dataset.addSeries(data2);
       
     

        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Zipf Distribution",      		// chart title
            "Rank",              		 	// domain axis label
            "Frequency",                  	// range axis label
            dataset,                  		// data
            PlotOrientation.VERTICAL,
            true,                     		// include legend
            true,
            false
        );

        final XYPlot plot = chart.getXYPlot();
   //     final NumberAxis domainAxis = new NumberAxis("Rank");
        final NumberAxis domainAxis = new LogarithmicAxis("Rank");
        final NumberAxis rangeAxis = new LogarithmicAxis("Frequency");
    
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

    	ZipfExercise1 zipf = new ZipfExercise1("data/book.txt");
    	
    	
        final ZipfDemo1 demo = new ZipfDemo1("Zipf Distribution", zipf.getTotal(), zipf.getWords());
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}

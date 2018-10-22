
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.List;

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
	
	private static final DecimalFormat formatter = new DecimalFormat("0.0000");

	/**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public ZipfDemo1(final String title, long wordsCount, List<ZipfDistribution.Word> words) {

        super(title);

        final XYSeries data1 = new XYSeries("book.txt Words Analysis");
        final XYSeries data2 = new XYSeries("Standard Zipf Distribution");
      
       
        double total = 0d;
        for (int i = 1; i < wordsCount; i++) {
        	total += (double) 1 / i;
        }
   
        System.out.println("Total Words: " + wordsCount);
        System.out.println("Rank            Word    Frequency      Freq(#N)/Freq(#1)  Freq(#N)/Total     Zipf-Freq(#Rank)");
        System.out.println("=============================================================================================");
        
        for (int i = 0; i < words.size(); i++) {
        	
        	ZipfDistribution.Word word = words.get(i);
        	
        	double prob = (double) word.getCount() / wordsCount;
        	double zipf = (double) 1 / total / (i + 1);
   
       	
        	System.out.println(String.format("%3d [%15s]   %4d          |%5s            |%5s            |%5s", 
        			(i + 1), 
        			(word.getWord().substring(0, word.getWord().length() < 15 ? word.getWord().length() : 15)),
        			word.getCount(),
        			formatter.format((double) word.getCount() / words.get(0).getCount()),
        			formatter.format(prob),
        			formatter.format(zipf)
        			));
        	
        	data1.add(i + 1, prob);
        	data2.add(i + 1, zipf);

        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data1);
        dataset.addSeries(data2);
        
     

        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Zipf and Novel Comparison",      		// chart title
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

    	ZipfDistribution zipf = new ZipfDistribution("data/book.txt");
    	
    	
        final ZipfDemo1 demo = new ZipfDemo1("Zipf Comparsion", zipf.getTotal(), zipf.getWords());
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}

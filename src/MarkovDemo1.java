import java.awt.Color;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
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


public class MarkovDemo1 extends ApplicationFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new demo.
	 *
	 * @param title
	 *            the frame title.
	 */
	public MarkovDemo1(final String title) {

		super(title);

		final XYSeries data1 = new XYSeries("Markov Distriubtion N = 50");

		
		// N = 50
		final int N = 50;
		Equation eq = new Equation();
		DMatrixRMaj A = new DMatrixRMaj((2 * N) + 1, (2 * N) + 1);
		{
			for (int j = 0; j < (2 * N + 1); j++) {

				for (int i = 0; i < (2 * N + 1); i++) {

					if (i == 0 && j == 0) {
						A.set(i, j, 1);
					} else if (i == 2 * N && j == 2 * N) {
						A.set(i, j, 1);
					} else if (j > 0 && j < 2 * N && (i == j - 1 || i == j + 1)) {
						A.set(i, j, 0.5);
					}
				}
			}

			eq.alias(A, "A");

			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < 50; i++) {
				buf.append("A * ");
			}

			DMatrixRMaj X = new DMatrixRMaj(2 * N + 1, 1);
			X.set(N, 0, 1);

			eq.alias(X, "X");

			eq.process("K = " + buf.toString() + " X");

			DMatrixRMaj D = new DMatrixRMaj(2 * N + 1, 1);
			for (int i = 0; i < 2 * N + 1; i++) {
				D.set(i, 0, i - N);
			}

			eq.alias(D, "D");

			eq.process("KK = [ D, K ]");
		}
		
		DMatrixRMaj KK = eq.lookupDDRM("KK");
		System.out.println(KK);
		

		for (int i = 0; i < KK.numRows; i++) {

			int state = (int) KK.get(i, 0);
			double prob = (double) KK.get(i, 1);

	
			data1.add(state, prob);
		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(data1);

		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Markov Distriubtion N = 50", // chart title
				"States", // domain axis label
				"Probablities", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, false);

		final XYPlot plot = chart.getXYPlot();
	
		final NumberAxis domainAxis = new NumberAxis("States");
		final NumberAxis rangeAxis = new NumberAxis("Probabilities");

		plot.setDomainAxis(domainAxis);
		plot.setRangeAxis(rangeAxis);
		chart.setBackgroundPaint(Color.white);
		plot.setOutlinePaint(Color.black);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
		setContentPane(chartPanel);

	}


	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(final String[] args) {

		final MarkovDemo1 demo = new MarkovDemo1("Markov Distriubtion N = 50");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);

	}

}

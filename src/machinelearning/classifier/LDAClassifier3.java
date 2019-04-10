package machinelearning.classifier;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class LDAClassifier3 extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

//	private static final DecimalFormat ff = new DecimalFormat("0.000");

	private static final String VALUE_FLOWER_SETOSA = "Iris-setosa";
	private static final String VALUE_QUALITY_VERCOLOR = "Iris-versicolor";
	private static final String VALUE_QUALITY_VIGRINICA = "Iris-virginica";

	public LDAClassifier3(final String title, DMatrixRMaj setosa, DMatrixRMaj vcolor, DMatrixRMaj virgin) {

		super(title);

		final XYSeries setosaData = new XYSeries("Setosa");
		final XYSeries vcolorData = new XYSeries("Versicolor");
		final XYSeries virginData = new XYSeries("Virginica");

		for (int i = 0; i < setosa.numCols; i++) {
			setosaData.add(setosa.get(0, i), setosa.get(1, i));
		}

		for (int i = 0; i < vcolor.numCols; i++) {
			vcolorData.add(vcolor.get(0, i), vcolor.get(1, i));
		}

		for (int i = 0; i < virgin.numCols; i++) {
			virginData.add(virgin.get(0, i), virgin.get(1, i));
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(setosaData);
		dataset.addSeries(vcolorData);
		dataset.addSeries(virginData);

		final JFreeChart chart = ChartFactory.createXYLineChart("Linear Discriminant Analysis", // chart title
				"LDA1", // domain axis label
				"LDA2", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, false);

		final XYPlot plot = chart.getXYPlot();
		final NumberAxis lda1 = new NumberAxis("LDA1");
		final NumberAxis lda2 = new NumberAxis("LDA2");
		
		XYDotRenderer render = new XYDotRenderer();
		render.setDotHeight(5);
		render.setDotWidth(5);
		plot.setRenderer(render);


		plot.setDomainAxis(lda1);
		plot.setRangeAxis(lda2);
		chart.setBackgroundPaint(Color.white);
		plot.setOutlinePaint(Color.black);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
		setContentPane(chartPanel);
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// LDA Algorithm
		// 1. Compute the d-dimensional mean vectors for the different classes from the
		// dataset.
		// 2. Compute the scatter matrices (in-between-class and within-class scatter
		// matrix).
		// 3. Compute the eigenvectors (ee1,ee2,...,eed) and corresponding eigenvalues
		// (λλ1,λλ2,...,λλd) for the scatter matrices.
		// 4. Sort the eigenvectors by decreasing eigenvalues and choose k eigenvectors
		// with the largest eigenvalues to
		// form a d×k dimensional matrix WW (where every column represents an
		// eigenvector).
		// 5. Use this d×k eigenvector matrix to transform the samples onto the new
		// subspace. This can be summarized by
		// the matrix multiplication: YY=XX×WW (where XX is a n×d-dimensional matrix
		// representing the n samples, and yy are
		// the transformed n×k-dimensional samples in the new subspace).

		Instances training = generateTrainingData("data/iris.arff.txt");
		Attribute cls = training.classAttribute();

		DMatrixRMaj all = get(cls, null, training);
		DMatrixRMaj setosa = get(cls, VALUE_FLOWER_SETOSA, training);
		DMatrixRMaj vcolor = get(cls, VALUE_QUALITY_VERCOLOR, training);
		DMatrixRMaj virgin = get(cls, VALUE_QUALITY_VIGRINICA, training);

		DMatrixRMaj overalMean = avg(all);
		DMatrixRMaj setosaMean = avg(setosa);
		DMatrixRMaj vcolorMean = avg(vcolor);
		DMatrixRMaj virginMean = avg(virgin);

		List<DMatrixRMaj> means = new ArrayList<DMatrixRMaj>();
		means.add(setosaMean);
		means.add(vcolorMean);
		means.add(virginMean);

		List<DMatrixRMaj> world = new ArrayList<DMatrixRMaj>();
		world.add(setosa);
		world.add(vcolor);
		world.add(virgin);

		DMatrixRMaj within = scatter(world, means);
		DMatrixRMaj between = scatter(world, means, overalMean);
		
		DMatrixRMaj covar = new DMatrixRMaj(4, 4);
		CommonOps_DDRM.invert(within);
		CommonOps_DDRM.mult(within, between, covar);
		
	
//		List<DMatrixRMaj> list = svd(covar);
	
		List<DMatrixRMaj> list = eigen(covar);
	
		DMatrixRMaj proj = combine(list);

		DMatrixRMaj setosa2 = new DMatrixRMaj(2, setosa.numRows);
		DMatrixRMaj vcolor2 = new DMatrixRMaj(2, vcolor.numRows);
		DMatrixRMaj virgin2 = new DMatrixRMaj(2, virgin.numRows);

		CommonOps_DDRM.multTransAB(proj, setosa, setosa2);
		CommonOps_DDRM.multTransAB(proj, vcolor, vcolor2);
		CommonOps_DDRM.multTransAB(proj, virgin, virgin2);

		LDAClassifier3 classifier = new LDAClassifier3("Linear Discriminant Analysis", setosa2, vcolor2, virgin2);
		
		classifier.pack();
        RefineryUtilities.centerFrameOnScreen(classifier);
        classifier.setVisible(true);
	}
	
	public static List<DMatrixRMaj> svd(DMatrixRMaj covar) {
		
		SingularValueDecomposition<DMatrixRMaj> svd = DecompositionFactory_DDRM.svd(covar.numRows, covar.numCols, true, false, false);
		svd.decompose(covar);
		DMatrixRMaj U = svd.getU(null, false);
		
		List<DMatrixRMaj> list = new ArrayList<DMatrixRMaj>();
		for (int col = 0; col < U.numCols; col++) {
			
			DMatrixRMaj sv = new DMatrixRMaj(U.numRows, 1);
			
			for (int row = 0; row < U.numRows; row++) {			
				sv.set(row, 0, U.get(row, col));	
			}
			if (col < 2)
				list.add(sv);
		}
		
		return list;
	}
	
	public static List<DMatrixRMaj> eigen(DMatrixRMaj covar) {
		
		EigenDecomposition_F64<DMatrixRMaj> eigen = DecompositionFactory_DDRM.eig(covar.numCols, true);
		eigen.decompose(covar);
		
		Map<Double, DMatrixRMaj> mat = new TreeMap<Double, DMatrixRMaj>(new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2) * -1;
			}
			
		});
		
		
		for (int i = 0; i < eigen.getNumberOfEigenvalues(); i++) {
			mat.put(eigen.getEigenvalue(i).getReal(), eigen.getEigenVector(i));
		}
		
		return mat.entrySet().stream().limit(2).map(p -> p.getValue()).collect(Collectors.toList());
	}
	
	public static DMatrixRMaj scatter(List<DMatrixRMaj> data, List<DMatrixRMaj> means, DMatrixRMaj overall) {

		int size = overall.numRows;

		DMatrixRMaj S = new DMatrixRMaj(size, size);
		CommonOps_DDRM.fill(S, 0.0);

		for (int index = 0; index < means.size(); index++) {

			DMatrixRMaj c = new DMatrixRMaj(size, size);
			DMatrixRMaj diff = new DMatrixRMaj(size, 1);

			DMatrixRMaj A = data.get(index);
			DMatrixRMaj mean = means.get(index);

			CommonOps_DDRM.subtract(mean, overall, diff);

			CommonOps_DDRM.multOuter(diff, c);

			CommonOps_DDRM.scale(A.numRows, c);
			
			CommonOps_DDRM.addEquals(S, c);
		}

		return S;
	}

	public static DMatrixRMaj scatter(List<DMatrixRMaj> data, List<DMatrixRMaj> means) {

		int size = means.get(0).numRows;

		DMatrixRMaj S = new DMatrixRMaj(size, size);
		CommonOps_DDRM.fill(S, 0.0);

		for (int index = 0; index < data.size(); index++) {

			DMatrixRMaj c = new DMatrixRMaj(size, size);

			DMatrixRMaj A = data.get(index);
			DMatrixRMaj mean = means.get(index);

			DMatrixRMaj e = meanError(A, mean);

			CommonOps_DDRM.multInner(e, c);

			CommonOps_DDRM.addEquals(S, c);
		}

		return S;
	}

	public static DMatrixRMaj meanError(DMatrixRMaj A, DMatrixRMaj mean) {

		DMatrixRMaj E = new DMatrixRMaj(A.numRows, A.numCols);

		for (int col = 0; col < A.numCols; col++) {

			for (int row = 0; row < A.numRows; row++) {
				E.set(row, col, A.get(row, col) - mean.get(col, 0));
			}
		}

		return E;
	}

	public static DMatrixRMaj avg(DMatrixRMaj A) {

		DMatrixRMaj data = new DMatrixRMaj(A.numCols, 1);

		for (int col = 0; col < A.numCols; col++) {

			double sum = 0.0;

			for (int row = 0; row < A.numRows; row++) {

				sum += A.get(row, col);
			}

			data.set(col, 0, (double) sum / A.numRows);
		}

		return data;
	}

	public static Instances generateTrainingData(String fileName) {

		Instances training = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			training = arff.getData();
			training.setClassIndex(training.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return training;
	}

	public static DMatrixRMaj get(Attribute attr, String val, Instances instances) {

		List<Instance> list = null;

		if (val != null)
			list = instances.stream().filter(p -> val.equals(p.stringValue(attr))).collect(Collectors.toList());
		else
			list = instances;

		List<Attribute> attrs = get(instances);

		DMatrixRMaj data = new DMatrixRMaj(list.size(), attrs.size());

		for (int row = 0; row < list.size(); row++) {

			Instance instance = list.get(row);

			for (int col = 0; col < attrs.size(); col++) {
				data.set(row, col, instance.value(attrs.get(col)));
			}
		}

		return data;
	}

	public static DMatrixRMaj get(List<Attribute> attrs, Instances instances) {

		DMatrixRMaj data = new DMatrixRMaj(instances.size(), attrs.size());
		DMatrixRMaj output = new DMatrixRMaj(attrs.size(), attrs.size());

		int row = 0;
		for (Instance instance : instances) {

			List<Double> vals = new ArrayList<Double>();
			for (Attribute attr : attrs) {
				vals.add(instance.value(attr));
			}

			for (int col = 0; col < attrs.size(); col++) {
				data.set(row, col, vals.get(col));
			}

			row++;
		}

		CommonOps_DDRM.multOuter(data, output);

		return output;
	}

	public static List<Attribute> get(Instances instances) {

		List<Attribute> attrs = new ArrayList<Attribute>();

		Enumeration<Attribute> list = instances.enumerateAttributes();

		while (list != null && list.hasMoreElements()) {
			attrs.add(list.nextElement());
		}

		return attrs;
	}

	public static DMatrixRMaj combine(List<DMatrixRMaj> data) {

		DMatrixRMaj output = new DMatrixRMaj(data.get(0).numRows, data.size());

		for (int col = 0; col < data.size(); col++) {

			DMatrixRMaj vector = data.get(col);

			for (int row = 0; row < data.get(0).numRows; row++) {

				output.set(row, col, vector.get(row, 0));
			}
		}

		return output;

	}
}

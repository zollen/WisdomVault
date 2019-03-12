package linearalgebra;
import java.text.DecimalFormat;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.ejml.data.DMatrixRMaj;

class EigenVector implements Comparable<EigenVector> {

	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	private double value = 0d;

	private DMatrixRMaj vector = null;

	public EigenVector() {
	}

	public EigenVector(double value, RealVector vector) {
		convert(value, vector);
	}

	public boolean isPrimary() {
		return isPrimary(value);
	}

	public double getValue() {
		return value;
	}

	public DMatrixRMaj getVector() {
		return vector;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(ff.format(value) + " ===> ");
		for (int k = 0; k < vector.numRows; k++) {
			if (k > 0)
				builder.append(", ");
			builder.append(ff.format(vector.get(k, 0)));
		}

		return builder.toString();
	}

	private boolean isPrimary(double val) {
		return ((double) Math.abs(val - 1d) < 0.000001d);
	}

	private void convert(double val, RealVector vec) {

		if (isPrimary(val)) {
			val = 1;
		}

		double[] data = new double[vec.getDimension()];
		for (int k = 0; k < data.length; k++) {

			if (val == 1) {
				data[k] = Math.abs(vec.getEntry(k));
			} else {
				data[k] = vec.getEntry(k);
			}
		}
		RealMatrix tmp = MatrixUtils.createColumnRealMatrix(data);

		this.value = Math.abs(val) < 0.0000001d ? 0 : val;
		this.vector = new DMatrixRMaj(tmp.getData());
	}

	@Override
	public int compareTo(EigenVector o) {
		// TODO Auto-generated method stub
		if (this.value < o.value)
			return 1;
		else
		if (this.value > o.value)
			return -1;
		
		return 0;
	}
}
package linearalgebra;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.ejml.data.DMatrixRMaj;

class EigenMatrix {

	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	private List<EigenVector> vectors = new ArrayList<EigenVector>();

	private DMatrixRMaj matrix = null;
	
	private boolean markov = false;

	private EigenDecomposition eigen = null;
	
	public EigenMatrix(EigenDecomposition eigen) {
		this.eigen = eigen;
		construct();
	}

	public EigenMatrix(EigenDecomposition eigen, boolean markov) {
		this.eigen = eigen;
		this.markov = markov;
		construct();
	}

	public List<EigenVector> getVectors() {
		return vectors;
	}

	public DMatrixRMaj getMatrix() {
		return matrix;
	}

	public int getSize() {
		return vectors.size();
	}
	
	public boolean hasComplex() {
		return eigen.hasComplexEigenvalues();
	}

	public String getValues() {

		StringBuilder builder = new StringBuilder();

		for (EigenVector vector : vectors) {

			if (builder.length() > 0)
				builder.append(", ");

			builder.append(ff.format(vector.getValue()));
		}

		return builder.toString();
	}

	public String toString() {
		return matrix.toString();
	}

	private void construct() {

		double[] vals = eigen.getRealEigenvalues();
		for (int i = 0; i < vals.length; i++) {

			EigenVector vector = new EigenVector(eigen.getRealEigenvalue(i), eigen.getEigenvector(i));

			if (vector.isPrimary())
				vectors.add(0, vector);
			else
				vectors.add(vector);
		}
		
		if (!markov)
			Collections.sort(vectors);

		int size = eigen.getRealEigenvalues().length;
		DMatrixRMaj mat = new DMatrixRMaj(size, size);
		int col = 0;
		for (EigenVector vector : vectors) {

			for (int row = 0; row < size; row++) {
				mat.set(row, col, vector.getVector().get(row, 0));
			}

			col++;
		}

		this.matrix = mat;
	}

}
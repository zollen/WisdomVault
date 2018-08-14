import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.decomposition.QRDecomposition;

public class GramSchmidtExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("a = [ 1; 1; 1; 1 ]");
		eq.process("b = [ 3; 3; -1; -1 ]");
		eq.process("c = [ 2; -1; 5; 0 ]");
	
		
		eq.process("A = a");	
		eq.process("B = b - ((A' * b) * inv( A' * A ) * A')'");
		eq.process("C = c - ((A' * c) * inv( A' * A ) * A')'");
		eq.process("C = C - ((B' * c) * inv( B' * B ) * B')'");
		
		
		
		eq.process("AA = [ a, b, c ]");
		DMatrixRMaj AA = eq.lookupMatrix("AA");
		System.out.println("<=== A ====>");
		System.out.println(AA);
		
		eq.process("A = A / normF(A)");
		eq.process("B = B / normF(B)");
		eq.process("C = C / normF(C)");
		eq.process("Q = [ A, B, C ]");
		DMatrixRMaj Q = eq.lookupMatrix("Q");
		System.out.println("<=== Q ====>");
		System.out.println(Q);
		
		System.out.println("Orthogonal: " + MatrixFeatures.isOrthogonal(Q, 0) + "\n");
				
		
		
		
		QRDecomposition<DMatrixRMaj> qr =  DecompositionFactory_DDRM.qr(AA.numRows, AA.numCols);
		qr.decompose(AA);
	
		System.out.println("<=== Q ====>");
		System.out.println(qr.getQ(null, true));
		System.out.println("<=== R ====>");
		System.out.println(qr.getR(null, true));	

	}

}

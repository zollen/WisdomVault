import org.ejml.data.ComplexPolar_F64;
import org.ejml.data.Complex_F64;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.ZMatrixRMaj;
import org.ejml.dense.row.CommonOps_ZDRM;
import org.ejml.dense.row.MatrixFeatures_ZDRM;
import org.ejml.ops.ComplexMath_F64;

public class ComplexMatrixExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Complex_F64 a = new Complex_F64(1,2);
        Complex_F64 b = new Complex_F64(-1,-0.6);
        Complex_F64 c = new Complex_F64();
        ComplexPolar_F64 polarC = new ComplexPolar_F64();

        System.out.println("a = "+a);
        System.out.println("b = "+b);
        System.out.println("------------------");

        ComplexMath_F64.plus(a, b, c);
        System.out.println("a + b = "+c);
        ComplexMath_F64.minus(a, b, c);
        System.out.println("a - b = "+c);
        ComplexMath_F64.multiply(a, b, c);
        System.out.println("a * b = "+c);
        ComplexMath_F64.divide(a, b, c);
        System.out.println("a / b = "+c);

        System.out.println("------------------");
        ComplexPolar_F64 polarA = new ComplexPolar_F64();
        ComplexMath_F64.convert(a, polarA);
        System.out.println("polar notation of a = "+polarA);
        ComplexMath_F64.pow(polarA, 3, polarC);
        System.out.println("a ** 3 = "+polarC);
        ComplexMath_F64.convert(polarC, c);
        System.out.println("a ** 3 = "+c);
        
        Complex_F64 result = new Complex_F64();
        ComplexMath_F64.plus(a, b, result);
        System.out.println(result);
        
        ZMatrixRMaj A = new ZMatrixRMaj(2, 2);
        A.set(0, 0, 1, 0);
        A.set(1, 1, 1, 0);
        A.set(0, 1, 1, 3);
        A.set(1, 0, 1, -3);
        
        A.print();
        System.out.println("Sym(A): " + MatrixFeatures_ZDRM.isHermitian(A, 0.0000001d));
        System.out.println("Det(A): " + CommonOps_ZDRM.det(A));
        
        DMatrixRMaj mat = new DMatrixRMaj(2, 2);
        CommonOps_ZDRM.magnitude(A, mat);  
        System.out.println(mat);
        
   
        ZMatrixRMaj Q = new ZMatrixRMaj(4, 4);
        Q.set(0, 0, 1, 0); Q.set(0, 1, 1, 0);  Q.set(0, 2, 1, 0);  Q.set(0, 3, 1, 0);
        Q.set(1, 0, 1, 0); Q.set(1, 1, 0, 1);  Q.set(1, 2, -1, 0); Q.set(1, 3, 0, -1);
        Q.set(2, 0, 1, 0); Q.set(2, 1, -1, 0); Q.set(2, 2, 1, 0);  Q.set(2, 3, -1, 0);
        Q.set(3, 0, 1, 0); Q.set(3, 1, 0, -1); Q.set(3, 2, -1, 0); Q.set(3, 3, 0, 1);
        

        CommonOps_ZDRM.scale(0.5, 0, Q);
      
        Q.print();
        
        System.out.println("Unitary(Q): " + MatrixFeatures_ZDRM.isUnitary(Q, 0.0000001d));
        
	}
	


}

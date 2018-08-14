import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class TestMe12 {
	
	private static final StringBuilder command1 = new StringBuilder();
	private static final StringBuilder command2 = new StringBuilder();
	
	private static final DecimalFormat formatter = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("Rz = [" +
							" cos(pi/3), -sin(pi/3), 		0;" +
							" sin(pi/3),  cos(pi/3), 		0;" +
							" 		  0,		  0, 		1 " +
						"]");
		
		eq.process("Ry = [" +
							" cos(pi/3), 		  0,  sin(pi/3);" +
							" 		  0,  		  1,   		  0;" +
							"-sin(pi/3),		  0,  cos(pi/3) " +
						"]");
		
		eq.process("Rx =[" +
							"		  1,          0,          0;"	+
							"         0,  cos(pi/3), -sin(pi/3);" +
							"         0,  sin(pi/3),  cos(pi/3) " +
					"]");
		
		eq.process("M = Rz * Ry * Rx");
		
		eq.process("X1 = [" +
							" 0, 0, 0, 0;" +
							" 0, 0, 1, 1;" +
							" 0, 1, 1, 0 " +
						"]");
		eq.process("X2 = [" +
							" 1, 1, 1, 1;" +
							" 0, 1, 1, 0;" +
							" 0, 0, 1, 1 " +
						"]");
		eq.process("Y1 = [" +
							" 0, 1, 1, 0;" +
							" 0, 0, 0, 0;" +
							" 0, 0, 1, 1 " +
						"]");
		eq.process("Y2 = [" +
							" 0, 0, 1, 1;" +
							" 1, 1, 1, 1;" +
							" 0, 1, 1, 0 " +
						"]");
		eq.process("Z1 = [" +
							" 0, 0, 1, 1;" +
							" 0, 1, 1, 0;" +
							" 0, 0, 0, 0 " +
						"]");
		eq.process("Z2 = [" +
							" 0, 1, 1, 0;" +
							" 0, 0, 1, 1;" +
							" 1, 1, 1, 1 " +
						"]");
		
		eq.process("P = [ X1; X2; Y1; Y2; Z1; Z2 ]");
		
		
		DMatrixRMaj M = eq.lookupMatrix("M");
		DMatrixRMaj P = eq.lookupMatrix("P");
		
		drawRotatedPolyhedron(M, P);
		
		System.out.println(command1.toString());
		System.out.println("===============================================================");
		System.out.println(command2.toString());
	}
	
	public static void drawRotatedPolyhedron(DMatrixRMaj M, DMatrixRMaj P) {
		
		int rows = P.numRows;
		int cols = P.numCols;
		
		List<DMatrixRMaj> faces = new ArrayList<DMatrixRMaj>();
		for (int i = 0; i < rows; i+=3) {	
			DMatrixRMaj tmp = CommonOps_DDRM.extract(P, i, i + 3, 0, cols);
			DMatrixRMaj transformed = new DMatrixRMaj(tmp.numRows, tmp.numCols);
			CommonOps_DDRM.mult(M, tmp, transformed);
			faces.add(transformed);
		}
		
		for (DMatrixRMaj p : faces) {
			
			if (!isFacingFromUnderneath(p))
				continue;
			
			
			DMatrixRMaj pp = CommonOps_DDRM.extract(p, 0, 2, 0, p.numCols);
			DMatrixRMaj circle = drawCentralCircle(pp);
			
			appendCommand(command1, pp);
			appendCommand(command2, circle);
		}
	}
	
	public static DMatrixRMaj circumscribeTringle(DMatrixRMaj A, DMatrixRMaj B,
													DMatrixRMaj C) {
		
		Equation eq = new Equation();
		eq.alias(A, "A");
		eq.alias(B, "B");
		eq.alias(C, "C");
		
		eq.process("U = A - B");
		eq.process("V = A - C");
		
		
		// midpoints of U and V
		eq.process("UM = 1./2 * U + B");
		eq.process("VM = 1./2 * V + C");
		
	
		// Perpendicular line of U at midpoint(U)
		// Perpendicular line of V at midpoint(V)
		eq.process("L = [ -1 * U(1,0); U(0,0) ]");
		eq.process("M = [ -1 * V(1,0); V(0,0) ]");
		
		
		eq.process("X = UM");
		eq.process("K = X + L");
		eq.process("W = VM");
		eq.process("Z = W + M");
		
		DMatrixRMaj X = eq.lookupMatrix("X");
		DMatrixRMaj Y = eq.lookupMatrix("K");
		DMatrixRMaj W = eq.lookupMatrix("W");
		DMatrixRMaj Z = eq.lookupMatrix("Z");
		
		return quadIntersect(X, Y, W, Z);
	}
	
	public static DMatrixRMaj quadIntersect(DMatrixRMaj A, DMatrixRMaj B,
										DMatrixRMaj C, DMatrixRMaj D) {
		Equation eq = new Equation();
		
		eq.alias(A.copy(), "A");
		eq.alias(B.copy(), "B");
		eq.alias(C.copy(), "C");
		eq.alias(D.copy(), "D");
		
		eq.process("U = B - A");
		eq.process("V = D - C");
		
		
		eq.process("K = [ " +
							" U(0,0), -1 * V(0,0),  C(0,0) - A(0,0);" +
							" U(1,0), -1 * V(1,0),  C(1,0) - A(1,0) " +
						"]");
		
		DMatrixRMaj K = eq.lookupMatrix("K");
		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);
		eq.alias(K, "K");
		
	
		eq.process("M1 = A + K(0,2) * U");
		eq.process("M2 = C + K(1,2) * V");
		
		if (MatrixFeatures.isEquals(eq.lookupMatrix("M1"), 
									eq.lookupMatrix("M2"), 
									0.00000001d))
			return eq.lookupMatrix("M1");
		
		return null;
	}
	
	public static DMatrixRMaj drawCentralCircle(DMatrixRMaj p) {
		
		DMatrixRMaj [] pts = new DMatrixRMaj[p.numCols];
		
		for (int i = 0; i < p.numCols; i++) {
			pts[i] = CommonOps_DDRM.extract(p, 0, 2, i, i + 1);
		}
		
		
		DMatrixRMaj o = null;
		if (p.numCols == 3)
			o = circumscribeTringle(pts[0], pts[1], pts[2]);
		else
			o = quadIntersect(pts[0], pts[2], pts[1], pts[3]);
		
		System.out.println("[P]: " + p);
		System.out.println("[O]: " + o);
		
		
		
		
		// Vector of each edge
		DMatrixRMaj u = new DMatrixRMaj(2, 1);
		DMatrixRMaj v = new DMatrixRMaj(2, 1);
		DMatrixRMaj w = new DMatrixRMaj(2, 1);
		DMatrixRMaj z = new DMatrixRMaj(2, 1);
		if (p.numCols == 3) {
			CommonOps_DDRM.subtract(pts[1], pts[0], u);
			CommonOps_DDRM.subtract(pts[2], pts[1], v);
			CommonOps_DDRM.subtract(pts[0], pts[2], w);
		}
		else {
			CommonOps_DDRM.subtract(pts[1], pts[0], u);
			CommonOps_DDRM.subtract(pts[2], pts[1], v);
			CommonOps_DDRM.subtract(pts[3], pts[2], w);
			CommonOps_DDRM.subtract(pts[0], pts[3], z);
		}
		
		
		
		// vectors of each vertex from the center circle
		DMatrixRMaj aoo = new DMatrixRMaj(2, 1);
		DMatrixRMaj boo = new DMatrixRMaj(2, 1);
		DMatrixRMaj coo = new DMatrixRMaj(2, 1);
		DMatrixRMaj doo = new DMatrixRMaj(2, 1);
		if (p.numCols == 3) {
			CommonOps_DDRM.subtract(o, pts[0], aoo);
			CommonOps_DDRM.subtract(o, pts[1], boo);
			CommonOps_DDRM.subtract(o, pts[2], coo);
		}
		else {
			CommonOps_DDRM.subtract(o, pts[0], aoo);
			CommonOps_DDRM.subtract(o, pts[1], boo);
			CommonOps_DDRM.subtract(o, pts[2], coo);
			CommonOps_DDRM.subtract(o, pts[3], doo);
		}

		
		// Projection of above every vector on each edge
		double len1 = CommonOps_DDRM.dot(aoo, u) / CommonOps.normF(u) * CommonOps.normF(u);
		double len2 = CommonOps_DDRM.dot(boo, v) / CommonOps.normF(v) * CommonOps.normF(v);
		double len3 = CommonOps_DDRM.dot(coo, w) / CommonOps.normF(w) * CommonOps.normF(w);
		double len4 = CommonOps_DDRM.dot(doo, z) / CommonOps.normF(z) * CommonOps.normF(z);
		
		
		DMatrixRMaj u1 = new DMatrixRMaj(2, 1);
		DMatrixRMaj v1 = new DMatrixRMaj(2, 1);
		DMatrixRMaj w1 = new DMatrixRMaj(2, 1);
		DMatrixRMaj z1 = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.scale(len1, u, u1);
		CommonOps_DDRM.scale(len2, v, v1);
		CommonOps_DDRM.scale(len3, w, w1);
		CommonOps_DDRM.scale(len4, z, z1);
		
		// Calculating the orthgonal vectors
		DMatrixRMaj d1 = new DMatrixRMaj(2, 1);
		DMatrixRMaj d2 = new DMatrixRMaj(2, 1);
		DMatrixRMaj d3 = new DMatrixRMaj(2, 1);
		DMatrixRMaj d4 = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.subtract(aoo, u1, d1);
		CommonOps_DDRM.subtract(boo, v1, d2);
		CommonOps_DDRM.subtract(coo, w1, d3);
		CommonOps_DDRM.subtract(doo, z1, d4);
		
		// Looking for the nearest edge
		double min = 9999999;
		double lenPa = CommonOps.normF(d1);
		double lenPb = CommonOps.normF(d2);
		double lenPc = CommonOps.normF(d3);
		double lenPd = CommonOps.normF(d4);
		
		if (min > lenPa && len1 > 0 && lenPa > 0)
			min = lenPa;
		if (min > lenPb && len2 > 0 && lenPb > 0)
			min = lenPb;
		if (min > lenPc && len3 > 0 && lenPc > 0)
			min = lenPc;
		if (min > lenPd && len4 > 0 && lenPd > 0)
			min = lenPd;
		
		double r = min / 2;
		System.out.println("Radius(R): " + r);
		
		
		// calculating the orthgonal basis with vector u
		DMatrixRMaj qu = u;
		DMatrixRMaj qv = new DMatrixRMaj(2, qu.numCols);
		
		qv.set(0, 0, -1 * u.get(1, 0));
		qv.set(1, 0, u.get(0, 0));
		
		// Computing points on the circle in 3D as:
		// o + r cos(2 pi t / N) u + r sin(2 pi t / N) v
					
		List<DMatrixRMaj> points = new ArrayList<DMatrixRMaj>();
		for (int t = 0; t < 10; t++) {
						
			DMatrixRMaj cosTerm = new DMatrixRMaj(2, 1);
			DMatrixRMaj sinTerm = new DMatrixRMaj(2, 1);
			DMatrixRMaj finalTerm = new DMatrixRMaj(2, 1);
						
			CommonOps_DDRM.scale(r * Math.cos(2 * Math.PI * t / 10), qu, cosTerm);
			CommonOps_DDRM.scale(r * Math.sin(2 * Math.PI * t / 10), qv, sinTerm);
			CommonOps_DDRM.add(cosTerm, sinTerm, finalTerm);
			CommonOps_DDRM.add(o, finalTerm, finalTerm);
						
			points.add(finalTerm);
		}
		
		
		DMatrixRMaj enlarged = new DMatrixRMaj(2, pts.length + points.size());
		
		for (int i = 0; i < pts.length; i++) {
			enlarged.set(0, i, p.get(0, 0));
			enlarged.set(1, i, p.get(1, 0));
		}
		
		for (int i = 0; i < points.size(); i++) {
			DMatrixRMaj tmp = points.get(i);
			enlarged.set(0, pts.length + i, tmp.get(0, 0));
			enlarged.set(1, pts.length + i, tmp.get(1, 0));
		}
		
	
		return enlarged;
	}
	
	public static boolean isFacingFromUnderneath(DMatrixRMaj p) {
		
		DMatrixRMaj [] pts = new DMatrixRMaj[p.numCols];
		for (int i = 0; i < p.numCols; i++) {
			pts[i] = CommonOps_DDRM.extract(p, 0, p.numRows, i, i + 1);
		}
		
		
		DMatrixRMaj u = new DMatrixRMaj(p.numRows, 1); 
		DMatrixRMaj v = new DMatrixRMaj(p.numRows, 1);
		if (p.numCols == 3) {
			CommonOps_DDRM.subtract(pts[1], pts[0], u);
			CommonOps_DDRM.subtract(pts[2], pts[0], v);
		}
		else {
			CommonOps_DDRM.subtract(pts[2], pts[0], u);
			CommonOps_DDRM.subtract(pts[3], pts[1], v);
		}
		DMatrixRMaj w = CommonOps.crossProduct(u, v);
		

		// check z-component is pointing upward or positive value
		if (w.get(2, 0) > 0) {
			return true;
		}
		
		return false;
	}
	
	
	public static void appendCommand(StringBuilder cmd, DMatrixRMaj a) {
		
		cmd.append("Execute[{");
		
		for (int i = 0; i < a.numCols; i++) {
			
			if (i > 0)
				cmd.append(",");
			
			cmd.append("\"(");
			
			for (int j = 0; j < a.numRows; j++) {
				if (j > 0)
					cmd.append(",");
				
				cmd.append(formatter.format(a.get(j, i)));
			}
			
			cmd.append(")\"");
		}
		
		cmd.append("}]\n");
	}
	
	public static void appendCommand(StringBuilder cmd, List<DMatrixRMaj> list) {
		
		DMatrixRMaj m = new DMatrixRMaj(list.get(0).numRows, list.size());
		
		
		for (int i = 0; i < list.size(); i++) {
			
			DMatrixRMaj item = list.get(i);
			
			for (int j = 0; j < item.numRows; j++) {
				
				m.set(j, i, item.get(j, 0));
			}
		}
		
		appendCommand(cmd, m);	
	}
	
	

}

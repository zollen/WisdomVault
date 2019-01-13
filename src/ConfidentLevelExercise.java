import java.text.DecimalFormat;

public class ConfidentLevelExercise {
	
	private static final DecimalFormat formatter = new DecimalFormat("0.########");
	
	private static final double Z_SCORE_99 = 2.055d;
	private static final double Z_SCORE_999 = 2.88d;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		confident(0.2, 400, Z_SCORE_99);
		confident(0.2, 10000, Z_SCORE_99);
		confident(0.2, 1000000, Z_SCORE_99);
		System.out.println();
		
		confident(0.2, 400, Z_SCORE_999);
		confident(0.2, 10000, Z_SCORE_999);
		confident(0.2, 1000000, Z_SCORE_999);
		System.out.println();
		
		confident(0.5, 400, Z_SCORE_99);
		confident(0.5, 10000, Z_SCORE_99);
		confident(0.5, 1000000, Z_SCORE_99);
		System.out.println();
		
		confident(0.5, 400, Z_SCORE_999);
		confident(0.5, 10000, Z_SCORE_999);
		confident(0.5, 1000000, Z_SCORE_999);
		System.out.println();
	}
	
	private static void confident(double ff, int N, double z) {
		
		double sd = Math.sqrt(ff * (1 - ff) / N);
		System.out.println(formatter.format((ff - sd * z)) + "  " + 
					formatter.format((ff + sd * z)));	
	}

}

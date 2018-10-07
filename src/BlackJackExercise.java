import java.text.DecimalFormat;

public class BlackJackExercise {

	private static DecimalFormat formatter = new DecimalFormat("0.000");

	private static final int LIMIT = 21;
	
	private static final boolean[][] PLAY = new boolean[LIMIT + 1][LIMIT + 1];
	private static final double[][] PROB = new double[LIMIT + 1][LIMIT + 1];

	private static final int NCARD = 10;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		blackJack();
		
		System.out.println("TOTAL PROB_NEXT_X(5, 5): " + formatter.format(nextMove(5, 5)));
		System.out.println("TOTAL PROB_NEXT_X(3, 2): " + formatter.format(nextMove(3, 2)));
		System.out.println("TOTAL PROB_NEXT_X(4, 5): " + formatter.format(nextMove(4, 5)));
		System.out.println("TOTAL PROB_NEXT_X(5, 4): " + formatter.format(nextMove(5, 4)));
		System.out.println("TOTAL PROB_NEXT_X(5, 4): " + formatter.format(nextMove(10, 8)));

		printInfo();
	}
	
	public static double nextMove(int X, int Y) {
		
		double total = 0d;
		
		for (int i = 1; i <= NCARD; i++) {
			
			if (X + i <= LIMIT) {
				total += PROB[X + i][Y];
			}
		}
		
		return total;	
	}

	public static void blackJack() {

		for (int XT = LIMIT; XT >= 0; XT--) {
			for (int YT = LIMIT; YT >= 0; YT--) {

				double dealt = decideDraw(XT, YT);
				double brust = decidePass(XT, YT);
				
				if (XT < YT || (XT == 0 && YT == 0))
					PLAY[XT][YT] = true;
				else if (Math.abs(XT - YT) >= NCARD && brust > 0)
					PLAY[XT][YT] = false;
				else if (dealt > brust)
					PLAY[XT][YT] = true;
				else
					PLAY[XT][YT] = false;

				PROB[XT][YT] = dealt;
			}
		}
	}

	private static double decideDraw(int X, int Y) {
		double probX = (double) X / NCARD;
		double probY = (double) Y / NCARD;

		if (probX == 0 && probY == 0)
			return 0d;

		if (probX == 0)
			return (double) 1 - (Y < NCARD ? probY : 1d);

		if (probY == 0)
			return (double) X < NCARD ? probX : 1d;

		return (double) probX / (probX + probY);
	}

	private static double decidePass(int X, int Y) {

		int brust = (LIMIT - (X + NCARD)) <= 0 ? Math.abs(LIMIT - (X + NCARD)) : 0;

		return (double) brust / NCARD;
	}

	private static void printInfo() {

		StringBuilder info = new StringBuilder();

		for (int XT = 0; XT <= LIMIT; XT++) {

			StringBuilder builder = new StringBuilder();

			for (int YT = 0; YT <= LIMIT; YT++) {
				if (builder.length() > 0)
					builder.append(", ");

				builder.append(formatter.format(PROB[XT][YT]) + " " + (PLAY[XT][YT] == true ? "T" : "F"));
			}

			builder.append("\n");

			info.append(builder.toString());
		}

		System.out.println(info.toString());
	}

}

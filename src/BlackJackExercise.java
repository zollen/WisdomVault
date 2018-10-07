import java.text.DecimalFormat;

public class BlackJackExercise {

	private static DecimalFormat formatter = new DecimalFormat("0.000");

	private static final int LTARGET = 21;
	private static final int UTARGET = LTARGET;

	private static final boolean[][] PLAY = new boolean[LTARGET][LTARGET];
	private static final double[][] PROB = new double[LTARGET][LTARGET];

	private static final int NCARD = 10;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		blackJack();

		printInfo();
	}

	public static void blackJack() {

		for (int XT = LTARGET - 1; XT >= 0; XT--) {
			for (int YT = LTARGET - 1; YT >= 0; YT--) {

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

		int brust = (LTARGET - (X + NCARD)) <= 0 ? Math.abs(LTARGET - (X + NCARD)) : 0;

		return (double) brust / NCARD;
	}

	private static void printInfo() {

		StringBuilder info = new StringBuilder();

		for (int XT = 0; XT < LTARGET; XT++) {

			StringBuilder builder = new StringBuilder();

			for (int YT = 0; YT < LTARGET; YT++) {
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

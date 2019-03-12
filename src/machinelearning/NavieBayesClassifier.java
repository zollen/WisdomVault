package machinelearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NavieBayesClassifier {
	
	private static final double PROB_NOTSPAM_CHEAP = 0.66;
	private static final double PROB_NOTSPAM_WORK = 0.82;
	private static final double PROB_NOTSPAM_FREE = 0.71;
	private static final double PROB_SPAM_CHEAP = 0.89;
	private static final double PROB_SPAM_WORK = 0.52;
	private static final double PROB_SPAM_FREE = 0.72;
	private static final double PROB_SPAM = 0.05;
	
	private static final List<Letter> spams = new ArrayList<Letter>();
	private static final List<Letter> notspams = new ArrayList<Letter>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		generateTrainingSpams();

		count("Not Spam:", notspams);
		count("Spam:", spams);
		
		// NavieBayes class we assume the probabilty of the present of each word is independent
		// from each other in order to simplify the math. In reality, this is not always the case
		// of course. This is why this method is called Navie Bayes Classifer
		
		System.out.print("Test Data#1: [Cheap: true, Work: false, Free: true] ");
		System.out.print("Probability by counting: " + 4.0 / 100.0);		
		System.out.println(", Probabilty by bayes law: " + (13.0 / 100.0) * (31.0 / 100.0));
		System.out.println("Given [Cheap: true, Work: false, Free: true]: What is the probability of spam");
		System.out.println("P(S|TFT) = P(S and TFT) / P(TFT) = " );
		System.out.println();
	
		System.out.print("Test Data#2: [Cheap: false, Work: true, Free: true] ");
		System.out.print("Probability by counting: " + 14.0 / 100.0);		
		System.out.println(", Probabilty by bayes law: " + (32.0 / 100.0) * (31.0 / 100.0));
		
		System.out.print("Test Data#3: [Cheap: true, Work: true, Free: true] ");
		System.out.print("Probability by counting: " + 2.0 / 100.0);		
		System.out.println(", Probabilty by bayes law: " + (13.0 / 100.0) * (32.0 / 100.0) * (31.0 / 100.0));
	}
	
	public static void count(String prefix, List<Letter> letters) {
		
		int numCheap = 0;
		int numWork = 0;
		int numFree = 0;
		int numCheapAndWork = 0;
		int numWorkAndFree = 0;
		int numFreeAndCheap = 0;
		int numCheapAndWorkAndFree = 0;
		
		for (Letter letter : letters) {
			
			boolean hasCheap = false;
			boolean hasWork = false;
			boolean hasFree = false;
			
			if (letter.isHasCheap()) {
				numCheap++;
				hasCheap = true;
			}
			if (letter.isHasWork()) {
				numWork++;
				hasWork = true;
			}
			if (letter.isHasFree()) {
				numFree++;
				hasFree = true;
			}
			if (hasCheap && hasWork) {
				numCheapAndWork++;
			}
			if (hasWork && hasFree) {
				numWorkAndFree++;
			}
			if (hasFree && hasCheap) {
				numFreeAndCheap++;
			}
			if (hasFree && hasCheap && hasWork) {
				numCheapAndWorkAndFree++;
			}
		}
		
		System.out.println(prefix + " number of Cheap: " + numCheap);
		System.out.println(prefix + " number of Work: " + numWork);
		System.out.println(prefix + " number of Free: " + numFree);
		System.out.println(prefix + " number of Cheap And Work: " + numCheapAndWork);
		System.out.println(prefix + " number of Work And Free: " + numWorkAndFree);
		System.out.println(prefix + " number of Free And Cheap: " + numFreeAndCheap);
		System.out.println(prefix + " number of Cheap And Free And Work: " + numCheapAndWorkAndFree);
		System.out.println("===========================================================");
	}

	public static void generateTrainingSpams() {
		Random rand = new Random(0);
	
		for (int i = 0; i < 1000; i++) {

			boolean isSpam = rand.nextDouble() > PROB_SPAM ? true : false;
			double isCheap = PROB_NOTSPAM_CHEAP;
			double isWork = PROB_NOTSPAM_WORK;
			double isFree = PROB_NOTSPAM_FREE;
			if (isSpam) {
				isCheap = PROB_SPAM_CHEAP;
				isWork = PROB_SPAM_WORK;
				isFree = PROB_SPAM_FREE;
			}
			
			
			boolean hasCheap = rand.nextDouble() > isCheap ? true : false;
			boolean hasWork = rand.nextDouble() > isWork ? true : false;
			boolean hasFree = rand.nextDouble() > isFree ? true : false;
			
			if (isSpam)
				spams.add(new Letter(hasCheap, hasWork, hasFree));
			else
				notspams.add(new Letter(hasCheap, hasWork, hasFree));
		}
	}

	public static class Letter {

		private boolean hasCheap = false;
		private boolean hasWork = false;
		private boolean hasFree = false;
		
		public Letter() {
		}

		public Letter(boolean hasCheap, boolean hasWork, boolean hasFree) {
			this.hasCheap = hasCheap;
			this.hasWork = hasWork;
			this.hasFree = hasFree;
		}

		public boolean isHasCheap() {
			return hasCheap;
		}

		public void setHasCheap(boolean hasCheap) {
			this.hasCheap = hasCheap;
		}

		public boolean isHasWork() {
			return hasWork;
		}

		public void setHasWork(boolean hasWork) {
			this.hasWork = hasWork;
		}

		public boolean isHasFree() {
			return hasFree;
		}

		public void setHasFree(boolean hasFree) {
			this.hasFree = hasFree;
		}

		@Override
		public String toString() {
			return "Spam [hasCheap=" + hasCheap + ", hasWork=" + hasWork + ", hasFree=" + hasFree + "]";
		}

	}

}

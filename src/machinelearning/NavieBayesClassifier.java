package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class NavieBayesClassifier {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final int TOTAL_POPULATION = 1000;
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
		
		Predicate<Letter> condCheap = p -> p.hasCheap == true;
		Predicate<Letter> condCheapOnly = p -> p.hasCheap == true && p.hasFree == false && p.hasWork == false;
		Predicate<Letter> condWork = p -> p.hasWork == true;
		Predicate<Letter> condWorkOnly = p -> p.hasCheap == false && p.hasFree == false && p.hasWork == true;
		Predicate<Letter> condFree = p -> p.hasFree == true;
		Predicate<Letter> condFreeOnly = p -> p.hasCheap == false && p.hasFree == true && p.hasWork == false;
		Predicate<Letter> condWorkAndFreeOnly = p -> p.hasCheap == false && p.hasFree == true && p.hasWork == true;
		Predicate<Letter> condAll = p -> p.hasCheap == true && p.hasFree == true && p.hasWork == true;
		
		System.out.println("Not Spam: " + notspams.size());
		System.out.println("	Number of Cheap: " + count(notspams, condCheap));
		System.out.println("	Number of Cheap Only: " + count(notspams, condCheapOnly));
		System.out.println("	Number of Work: " + count(notspams, condWork));
		System.out.println("	Number of Work Only: " + count(notspams, condWorkOnly));
		System.out.println("	Number of Free: " + count(notspams, condFree));
		System.out.println("	Number of Free Only: " + count(notspams, condFreeOnly));
		System.out.println("	Number of Work And Free Only: " + count(notspams, condWorkAndFreeOnly));
		System.out.println("	Number of All Only: " + count(notspams, condAll));
	
		System.out.println("Spam:     " + spams.size());
		System.out.println("	Number of Cheap: " + count(spams, condCheap));
		System.out.println("	Number of Cheap Only: " + count(spams, condCheapOnly));
		System.out.println("	Number of Work: " + count(spams, condWork));
		System.out.println("	Number of Work Only: " + count(spams, condWorkOnly));
		System.out.println("	Number of Free: " + count(spams, condFree));
		System.out.println("	Number of Free Only: " + count(spams, condFreeOnly));
		System.out.println("	Number of Work And Free Only: " + count(notspams, condWorkAndFreeOnly));
		System.out.println("	Number of All Only: " + count(spams, condAll));
		
		
		// NavieBayes classifier assumes when any one of the above count is 0, the classifier
		// assumes the probabilities of each word is "independent" from each other. Let say
		// Count(Spam|c=T,w=F,f=F) = 0, then we assume P(c=T), P(w=T) and P(f=T) are independent
		// we "guess" the probability by using P(c=T) * P(w=T) * P(f=T) 
		System.out.println();
		
		System.out.println("Test Data#1: P(SPAM|[Cheap=true, Work=false, Free=false]) = " + 
				ff.format((double) count(spams, condCheapOnly) / (count(spams, condCheapOnly) + 
						count(notspams, condCheapOnly))));
			
	
		System.out.println("Test Data#2: P(SPAM|[Cheap: false, Work: true, Free: true]) = " +
				ff.format((double) count(spams, condWorkAndFreeOnly) / 
						(count(spams, condWorkAndFreeOnly) + count(notspams, condWorkAndFreeOnly))));
		
		
		System.out.println("Test Data#3: P(SPAM|[Cheap: true, Work: true, Free: true]) = " +
				ff.format((double) count(spams, condAll) / 
						(count(spams, condAll) + count(notspams, condAll))) + " {{ OBVIOUSLY WRONG }}");
		
		System.err.println("Approximate: P(SPAM|[Cheap: true, Work: true, Free: true]) = " +
					ff.format((double) count(spams, condCheap) / spams.size() *
							count(spams, condWork) / spams.size() *
							count(spams, condFree) / spams.size()));	
	}
	
	public static long count(List<Letter> letters, Predicate<Letter> condition) {
		
		return letters.stream().filter(condition).count();
	}

	public static void generateTrainingSpams() {
		Random rand = new Random(0);
	
		for (int i = 0; i < TOTAL_POPULATION; i++) {

			boolean isSpam = rand.nextDouble() > PROB_SPAM ? false : true;
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
			
			if (hasCheap && hasWork && hasFree)
				isSpam = true;
			
			if (isSpam)
				spams.add(new Letter(hasCheap, hasWork, hasFree));
			else
				notspams.add(new Letter(hasCheap, hasWork, hasFree));
		}
	}
	
	public static class Stats {
		
		public int numOfCheap;
		public int numOfCheapOnly;
		public int numOfWork;
		public int numOfWorkOnly;
		public int numOfFree;
		public int numOfFreeOnly;
		public int numOfCheapAndWork;
		public int numOfWorkAndFree;
		public int numOfFreeAndCheap;
		public int numOfCheapAndFreeAndWork;
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

package machinelearning.classifier;

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
		Predicate<Letter> condWork = p -> p.hasWork == true;
		Predicate<Letter> condFree = p -> p.hasFree == true;
		
		
		System.out.println("Not Spam: " + notspams.size());
		System.out.println("	Number of Cheap: " + count(notspams, condCheap) + 
				", P(!Spam|Cheap) = " + ff.format((double) count(notspams, condCheap) / (count(notspams, condCheap) + count(spams, condCheap))));
		System.out.println("	Number of Work: " + count(notspams, condWork) +
				", P(!Spam|Work) = " + ff.format((double) count(notspams, condWork) / (count(notspams, condWork) + count(spams, condWork))));
		System.out.println("	Number of Free: " + count(notspams, condFree) +
				", P(!Spam|Free) = " + ff.format((double) count(notspams, condFree) / (count(notspams, condFree) + count(spams, condFree))));
		
	
		
		// formula: P(Spam|cheap) = P(cheap|Spam)P(Spam) / P(cheap)
		//             = P(cheap|Spam)P(Spam) / P(cheap and Spam) + P(cheap and !Spam)
		//             = P(cheap|Spam)P(Spam) / P(cheap|Spam)P(spam) + P(cheap|!Spam)P(!Spam)
					
		System.out.println("Spam:    " + spams.size());
		double pSpamCheap = (double) count(spams, condCheap) / (count(notspams, condCheap) + count(spams, condCheap));
		double pSpamWork = (double) count(spams, condWork) / (count(notspams, condWork) + count(spams, condWork));
		double pSpamFree = (double) count(spams, condFree) / (count(notspams, condFree) + count(spams, condFree));
		System.out.println("	Number of Cheap: " + count(spams, condCheap) + ", P(Spam|Cheap) = " + ff.format(pSpamCheap));
		System.out.println("	Number of Work: " + count(spams, condWork) + ", P(Spam|Work) = " + ff.format(pSpamWork));
		System.out.println("	Number of Free: " + count(spams, condFree) + ", P(Spam|Free) = " + ff.format(pSpamFree));
		
		
		System.out.println();
		// NavieBayes classifier assumes when any one of the above count is 0, the classifier
		// assumes the probabilities of each event is "independent" from each other. Let say
		// Count(Spam|c=T,w=F,f=F) = 0, then we assume P(Spam|c=T), P(Spam|w=T) and P(Spam|f=T) 
		// are independent. We "guess" the probability by using P(c=T) * P(w=T) * P(f=T) 
		
		// Any 0 count (sample size too small) must be "approximate using Navie Bayes law. 
		// Because 0 count could lead to probability of 0 or 1 <-- impossible!
		
	
		// Using Navie Bayes (assume all events are independent!!
		System.err.println("Test Data#1: P(SPAM|[Cheap=true, Work=false, Free=false]) = " + 
				ff.format((double) pSpamCheap * (1 - pSpamWork) * (1 - pSpamFree)));
			
	
		System.err.println("Test Data#2: P(SPAM|[Cheap=false, Work=true, Free=true]) = " +
				ff.format((double) (1 - pSpamCheap) * pSpamWork * pSpamFree));
		
		
		System.err.println("Test Data#3: P(SPAM|[Cheap=true, Work=true, Free=true]) = " +
				ff.format((double) pSpamCheap * pSpamWork * pSpamFree));
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

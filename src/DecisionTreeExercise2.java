public class DecisionTreeExercise2 {

	/**
	 * A book is either a success or failure. If the book is a success, the
	 * publisher will gain $50,000. If the book is a failure, the publisher will
	 * lose $10,000.
	 * 
	 * The probability that a manuscript will succeed is 0.2, and the
	 * probability that it will fail is 0.8.
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		{
			DecisionTree.Node<ExpectValue> successOrFailure = DecisionTree
					.createChanceNode(new ExpectValue("Success or Failure"));
			DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(successOrFailure);

			DecisionTree.Node<ExpectValue> gain500000 = DecisionTree.createUtilityNode(new ExpectValue(50000d));
			DecisionTree.Node<ExpectValue> lose100000 = DecisionTree.createUtilityNode(new ExpectValue(-10000d));

			tree.add(0.20, gain500000);
			tree.add(0.80, lose100000);

			System.out.println(tree);

			System.out.println("Expect Net Profit: " + tree.analysis());
		}
		 */
		/**
		 * 
		 * If the infallible reviewer approve it, then publish it.
		 * 
		 */
		/*
		{

			DecisionTree.Node<ExpectValue> successOrFailure = DecisionTree
					.createChanceNode(new ExpectValue("Success or Failure"));
			DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(successOrFailure);

			DecisionTree.Node<ExpectValue> gain500000 = DecisionTree.createUtilityNode(new ExpectValue(50000d));
			DecisionTree.Node<ExpectValue> lose0 = DecisionTree.createUtilityNode(new ExpectValue(0d));

			tree.add(0.20, gain500000);
			tree.add(0.80, lose0);

			System.out.println(tree);

			System.out.println("Expect Net Profit: " + tree.analysis());
		}
		*/
		/**
		 * 
		 * Reviewer has the following conditional probabilitiy:
		 * 
		 * Let R be a random variable of reviewer's opinion
		 * Let S be a random variable of book would be a success
		 * 
		 * P(S=T) = 0.2
		 * P(S=F) = 0.8
		 * 
		 * P(R=T|S=T) = 0.7, P(R=T and S=T)/P(S=T) = 0.7, P(R=T and S=T) = 0.14
		 * P(R=T|S=F) = 0.4, P(R=T and S=F)/P(S=F) = 0.4, P(R=T and S=F) = 0.32
		 * P(R=T) = P(R=T and S=T) + P(R=T and S=F) = P(R=T) = 0.46
		 * P(R=F) = 1 - P(R=T) = 0.56
		 * 
		 * Based on P(R=T|S=T) = 0.7
		 * P(S=T|R=T)P(R=T)/P(S=T) = 0.7
		 * P(S=T|R=T) = 0.7 * P(S=T) / P(R=T) = 0.7 * 0.2 / 0.46 = 0.3043 = 0.30
		 * 
		 * Based on P(R=T|S=F) = 0.4
		 * P(S=F|R=T)P(R=T)/P(S=F) = 0.4
		 * P(S=F|R=T) = 0.4 * P(S=F) / P(R=T) = 0.4 * 0.8 / 0.46 = 0.6956 = 0.70
		 * 
		 * Let A1 be the above strategy
		 * Exp(Outcome(A1)) = Exp(Outcome(A1)|R=T)P(R=T) + Exp(Outcome(A1)|R=F)P(R=F)
		 * 
		 * Since Exp(OutCome(A1)|R=F)P(R=F) = 0
		 * Exp(Outcome(A1)) = Exp(Outcome(A1)|R=T)P(R=T)
		 * Exp(Outcome(A1)) = $50,000 * P(S=T|R=T)P(R=T) + (-10,000) * P(S=F|R=T)P(R=T)
		 * Exp(Outcome(A1)) = $50,000 * P(R=T|S=T)P(S=T) + (-10,000) * P(R=T|S=F)P(S=F)
		 * Exp(Outcome(A1)) = $50,000 * 0.7 * 0.2 + (-10,000) * 0.4 * 0.8
		 * Exp(Outcome(A1)) = $3,800
		 * 
		 */
		{
			DecisionTree.Node<ExpectValue> reviewerDecision = DecisionTree
					.createDecisionNode(new ExpectValue("Decisions"));
			DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(reviewerDecision);

			DecisionTree.Node<ExpectValue> publishedAny = DecisionTree.createChanceNode(new ExpectValue("Published"));
			DecisionTree.Node<ExpectValue> rejectedAny = DecisionTree.createChanceNode(new ExpectValue("Rejected"));
			DecisionTree.Node<ExpectValue> consulted = DecisionTree.createChanceNode(new ExpectValue("Consulted"));

			tree.add("Published", publishedAny);
			tree.add("Rejected", rejectedAny);
			tree.add("Consulted", consulted);

			DecisionTree.Node<ExpectValue> success1 = DecisionTree.createUtilityNode(new ExpectValue(50000d));
			DecisionTree.Node<ExpectValue> failure1 = DecisionTree.createUtilityNode(new ExpectValue(-10000d));

			publishedAny.add(0.2, success1);
			publishedAny.add(0.8, failure1);

			DecisionTree.Node<ExpectValue> lose0 = DecisionTree.createUtilityNode(new ExpectValue(0d));
			rejectedAny.add(1.0, lose0);

			DecisionTree.Node<ExpectValue> published = DecisionTree
					.createDecisionNode(new ExpectValue("Published After Reviewed"));
			DecisionTree.Node<ExpectValue> rejected = DecisionTree
					.createDecisionNode(new ExpectValue("Rejected After Reviewed"));

			consulted.add(0.46, published);
			consulted.add(0.54, rejected);

			DecisionTree.Node<ExpectValue> goAhead = DecisionTree.createChanceNode(new ExpectValue("Go Ahead"));
			published.add(1.0, goAhead);

			DecisionTree.Node<ExpectValue> loseFee = DecisionTree.createUtilityNode(new ExpectValue(-500d));
			rejected.add(1.0, loseFee);

			DecisionTree.Node<ExpectValue> success2 = DecisionTree.createUtilityNode(new ExpectValue(49500d));
			DecisionTree.Node<ExpectValue> failure2 = DecisionTree.createUtilityNode(new ExpectValue(-10500d));

			goAhead.add(0.30, success2);
			goAhead.add(0.70, failure2);

			System.out.println(tree);

			System.out.println("Expect Net Profit: " + tree.analysis());

		}
	}

	private static class ExpectValue implements DecisionTree.Computable {

		private Double value = null;
		private String label = null;

		public ExpectValue(double val) {
			this.value = val;
		}

		public ExpectValue(String label) {
			this.label = label;
		}

		@Override
		public double value() {
			// TODO Auto-generated method stub
			if (value != null)
				return value;

			return 0;
		}

		@Override
		public String toString() {
			return (label != null ? label.toString() : "") + (value != null ? String.valueOf(value) : "");
		}

	}

}

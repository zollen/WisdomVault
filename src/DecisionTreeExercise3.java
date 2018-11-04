public class DecisionTreeExercise3 {

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
		{
			DecisionTree.Node<ExpectValue> reviewerDecision = DecisionTree
					.createDecisionNode(new ExpectValue("Decisions"));
			DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(reviewerDecision);

			DecisionTree.Node<ExpectValue> publishedAny = DecisionTree.createChanceNode(new ExpectValue("Published"));
			DecisionTree.Node<ExpectValue> rejectedAny = DecisionTree.createChanceNode(new ExpectValue("Rejected"));
			DecisionTree.Node<ExpectValue> consulted1 = DecisionTree.createChanceNode(new ExpectValue("Consulted #1"));
			DecisionTree.Node<ExpectValue> consulted2 = DecisionTree.createChanceNode(new ExpectValue("Consulted #2"));

			
			tree.add("Published", publishedAny);
			tree.add("Rejected", rejectedAny);
			tree.add("Consulted #1", consulted1);

			DecisionTree.Node<ExpectValue> success1 = DecisionTree.createUtilityNode(new ExpectValue(50000d));
			DecisionTree.Node<ExpectValue> failure1 = DecisionTree.createUtilityNode(new ExpectValue(-10000d));

			publishedAny.add(0.2, success1);
			publishedAny.add(0.8, failure1);

			DecisionTree.Node<ExpectValue> lose0 = DecisionTree.createUtilityNode(new ExpectValue(0d));
			rejectedAny.add(1.0, lose0);

			DecisionTree.Node<ExpectValue> published1 = DecisionTree
					.createDecisionNode(new ExpectValue("Published After Reviewed"));
			DecisionTree.Node<ExpectValue> rejected1 = DecisionTree
					.createDecisionNode(new ExpectValue("Rejected After Reviewed"));
			
			consulted1.add(0.46, published1);
			consulted1.add(0.54, rejected1);
			
			DecisionTree.Node<ExpectValue> published2 = DecisionTree
					.createDecisionNode(new ExpectValue("Published After Reviewed"));
			DecisionTree.Node<ExpectValue> rejected2 = DecisionTree
					.createDecisionNode(new ExpectValue("Rejected After Reviewed"));

			published1.add(1.0, consulted2);
			
			consulted2.add(0.46, published2);
			consulted2.add(0.54, rejected2);

			DecisionTree.Node<ExpectValue> loseFee1 = DecisionTree.createUtilityNode(new ExpectValue(-500d));
			rejected1.add(1.0, loseFee1);
			
			DecisionTree.Node<ExpectValue> loseFee2 = DecisionTree.createUtilityNode(new ExpectValue(-1000d));
			rejected2.add(1.0, loseFee2);

			DecisionTree.Node<ExpectValue> success2 = DecisionTree.createUtilityNode(new ExpectValue(49000d));
			DecisionTree.Node<ExpectValue> failure2 = DecisionTree.createUtilityNode(new ExpectValue(-11000d));

			DecisionTree.Node<ExpectValue> goAhead = DecisionTree.createChanceNode(new ExpectValue("Go Ahead"));
			
			published2.add(1.0, goAhead);
			
			/**
			 * P(S=T|R1=T,R2=T) = P(S=T,R1=T,R2=T)/P(R1=T,R2=T)
			 * 					= P(R1=T|R2=T,S=T)P(R2=T,S=T)/P(R1=T,R2=T)
			 * 					= P(R1=T|S=T)P(R2=T|S=T)P(S=T)/(P(R1=T)P(R2=T))
			 * 					= 0.7 * 0.7 * 0.2 / (0.46 * 0.46)
			 * 					= 0.463138
			 * 
			 * P(S=F|R1=T,R2=T) = P(S=F,R1=T,R2=T)/P(P1=T,R2=T)
			 * 					= P(R1=T|R2=T,S=F)P(R2=T,S=F)/P(R1=T,R2=T)
			 * 					= P(R1=T|S=F)P(R2=T|S=F)P(S=F)/(P(R1=T)P(R2=T))
			 * 					= 0.4 * 0.4 * 0.8 / (0.46 * 0.46)
			 * 					= 0.60491493
			 */
			
			goAhead.add(0.463138d, success2);
			goAhead.add(0.60491493d, failure2);

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

public class DecisionTreeExercise4 {

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
			DecisionTree.Node<ExpectValue> consulted = DecisionTree.createChanceNode(new ExpectValue("Consulting"));
			
			tree.add("Published", publishedAny);
			tree.add("Rejected", rejectedAny);
			tree.add("Consulted", consulted);
			
			DecisionTree.Node<ExpectValue> success1 = DecisionTree.createUtilityNode(new ExpectValue(50000d));
			DecisionTree.Node<ExpectValue> failure1 = DecisionTree.createUtilityNode(new ExpectValue(-10000d));

			publishedAny.add(0.2, success1);
			publishedAny.add(0.8, failure1);

			DecisionTree.Node<ExpectValue> lose0 = DecisionTree.createUtilityNode(new ExpectValue(0d));
			rejectedAny.add(1.0, lose0);
			
			DecisionTree.Node<ExpectValue> bothApproved = DecisionTree.createChanceNode(new ExpectValue("Both Approved: 0.46 * 0.46"));
			DecisionTree.Node<ExpectValue> bothRejected = DecisionTree.createChanceNode(new ExpectValue("Both Rejected: 0.54 * 0.54"));
			DecisionTree.Node<ExpectValue> approved1 = DecisionTree.createChanceNode(new ExpectValue("#1 Approved, #2 rejected: 0.46 * 0.54"));
			DecisionTree.Node<ExpectValue> approved2 = DecisionTree.createChanceNode(new ExpectValue("#1 rejected, #2 Approved: 0.54 * 0.46"));
			
			consulted.add(0.46 * 0.46, bothApproved);
			consulted.add(0.54 * 0.54, bothRejected);
			consulted.add(0.46 * 0.54, approved1);
			consulted.add(0.54 * 0.46, approved2);
			
			DecisionTree.Node<ExpectValue> goAhead2 = DecisionTree.createDecisionNode(new ExpectValue("Go Ahead"));
			DecisionTree.Node<ExpectValue> success2 = DecisionTree.createUtilityNode(new ExpectValue(49000d));
			DecisionTree.Node<ExpectValue> failure2 = DecisionTree.createUtilityNode(new ExpectValue(-11000d));
			
			/**
			 * P(S=T|R1=T,R2=T) = P(S=T,R1=T,R2=T)/P(R1=T,R2=T)
			 * 					= P(R1=T|R2=T,S=T)P(R2=T,S=T)/P(R1=T,R2=T)
			 * 					= P(R1=T|S=T)P(R2=T|S=T)P(S=T)/(P(R1=T)P(R2=T))
			 * 					= 0.7 * 0.7 * 0.2 / (0.46 * 0.46)
			 * 					= 0.463138
			 * 
			 * P(S=F|R1=T,R2=T) = P(S=F,R1=T,R2=T)/P(R1=T,R2=T)
			 * 					= P(R1=T|R2=T,S=F)P(R2=T,S=F)/P(R1=T,R2=T)
			 * 					= P(R1=T|S=F)P(R2=T|S=F)P(S=F)/(P(R1=T)P(R2=T))
			 * 					= 0.4 * 0.4 * 0.8 / (0.46 * 0.46)
			 * 					= 0.60491493
			 * 
			 * P(S=T|R1=T,R2=F) = P(S=T,R1=T,R2=F)/P(R1=T,R2=F)
			 * 					= P(R1=T|R2=F,S=T)P(R2=F,S=T)/P(R1=T,R2=F)
			 * 					= P(R1=T|S=T)P(R2=F|S=T)P(S=T)/(P(R1=T)P(R2=F))
			 * 					= 0.7 * 0.3 * 0.2 / (0.46 * 0.54)
			 * 					= 0.16908213
			 * 
			 * P(S=F|R1=T,R2=F) = 1 - P(S=T|R1=T,R2=F) = 0.83091787
			 */
			
			goAhead2.add(0.463138d, success2);
			goAhead2.add(0.60391493d, failure2);
			
			bothApproved.add(1.0d, goAhead2);
			
			
			DecisionTree.Node<ExpectValue> goAhead3 = DecisionTree.createDecisionNode(new ExpectValue("Go Ahead"));
			DecisionTree.Node<ExpectValue> success3 = DecisionTree.createUtilityNode(new ExpectValue(49000d));
			DecisionTree.Node<ExpectValue> failure3 = DecisionTree.createUtilityNode(new ExpectValue(-11000d));
			
			goAhead3.add(0.16908213d, success3);
			goAhead3.add(0.83091787d, failure3);
			
			approved1.add(1.0, goAhead3);
			approved2.add(1.0, goAhead3);
			
			
			DecisionTree.Node<ExpectValue> loseFee = DecisionTree.createUtilityNode(new ExpectValue(-1000d));
			bothRejected.add(1.0, loseFee);
			
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

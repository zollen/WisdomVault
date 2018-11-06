public class DecisionTreeExercise6 {

	/**
	 * A book is either a success or failure. If the book is a success, the
	 * publisher will gain $50,000. If the book is a failure, the publisher will
	 * lose $10,000.
	 * 
	 * The probability that a manuscript will succeed is 0.2, and the
	 * probability that it will fail is 0.8.
	 * 
	 * Consult reviewer A first. If the opinion is unfavorable, then consult reviewer B.
	 * If either is favorable, then publish.
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
			
			DecisionTree.Node<ExpectValue> successAll = DecisionTree.createUtilityNode(new ExpectValue(50000d));
			DecisionTree.Node<ExpectValue> failureAll = DecisionTree.createUtilityNode(new ExpectValue(-10000d));

			publishedAny.add(0.2, successAll);
			publishedAny.add(0.8, failureAll);
			
			DecisionTree.Node<ExpectValue> lose0 = DecisionTree.createUtilityNode(new ExpectValue(0d));
			rejectedAny.add(1.0, lose0);
			
			DecisionTree.Node<ExpectValue> approved1 = DecisionTree
					.createChanceNode(new ExpectValue("Published After Reviewed"));
			DecisionTree.Node<ExpectValue> rejected1 = DecisionTree
					.createChanceNode(new ExpectValue("Rejected After Reviewed"));
			DecisionTree.Node<ExpectValue> goAhead1 = DecisionTree.createChanceNode(new ExpectValue("Go Ahead"));
			DecisionTree.Node<ExpectValue> success1 = DecisionTree.createUtilityNode(new ExpectValue(49500d));
			DecisionTree.Node<ExpectValue> failure1 = DecisionTree.createUtilityNode(new ExpectValue(-10500d));
			DecisionTree.Node<ExpectValue> loseFee1 = DecisionTree.createUtilityNode(new ExpectValue(-500d));
		
			consulted.add(0.46d, approved1);
			consulted.add(0.56d, rejected1);
			
			approved1.add(1.0, goAhead1);
			goAhead1.add(0.30434784d, success1);
			goAhead1.add(0.69565217d, failure1);
			rejected1.add(1.0, loseFee1);
		
			
			DecisionTree.Node<ExpectValue> approved2 = DecisionTree
					.createChanceNode(new ExpectValue("Published After Reviewed"));
			DecisionTree.Node<ExpectValue> rejected2 = DecisionTree
					.createChanceNode(new ExpectValue("Rejected After Reviewed"));
			DecisionTree.Node<ExpectValue> goAhead2 = DecisionTree.createChanceNode(new ExpectValue("Go Ahead"));
			DecisionTree.Node<ExpectValue> success2 = DecisionTree.createUtilityNode(new ExpectValue(49000d));
			DecisionTree.Node<ExpectValue> failure2 = DecisionTree.createUtilityNode(new ExpectValue(-11000d));
			DecisionTree.Node<ExpectValue> loseFee2 = DecisionTree.createUtilityNode(new ExpectValue(-1000d));
			
			
			approved1.add(0.46, approved2);
			approved1.add(0.54, rejected2);	
			approved2.add(1.0, goAhead2);
			goAhead2.add(0.16908213d, success2);
			goAhead2.add(0.83091787d, failure2);			
			rejected2.add(1.0, loseFee2);
			
		
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

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
		
		
		/**
		 * 
		 * If the infallible reviewer approve it, then publish it.
		 * 
		 */
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
		
		
		/**
		 * 
		 * Reviewer has the following conditional probabilitiy:
		 * 
		 * P(R=T|S=T) = 0.7
		 * P(R=T|S=F) = 0.4
		 * 
		 */
		{
			DecisionTree.Node<ExpectValue> approveOrReject = DecisionTree
					.createChanceNode(new ExpectValue("Reviewer Inspection"));
			DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(approveOrReject);

			DecisionTree.Node<ExpectValue> approved = DecisionTree.createChanceNode(new ExpectValue("Approved"));
			DecisionTree.Node<ExpectValue> rejected = DecisionTree.createChanceNode(new ExpectValue("Rejected"));

			DecisionTree.Node<ExpectValue> zeroGain1 = DecisionTree.createUtilityNode(new ExpectValue(0.0d));
			
			tree.add(0.46, approved);
			tree.add(0.54, rejected);
			
			rejected.add(1.0, zeroGain1);
			
			DecisionTree.Node<ExpectValue> gain500000 = DecisionTree.createUtilityNode(new ExpectValue(500000d));
			DecisionTree.Node<ExpectValue> lose100000 = DecisionTree.createUtilityNode(new ExpectValue(-10000d));

			approved.add(0.20, gain500000);
			approved.add(0.80, lose100000);

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

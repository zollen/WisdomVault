public class DecisionTreeGeneral {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Expected Value: " + pubValue(50000, 10000, 500, 0.2, 0.7, 0.4, 0));
	}
	
	public static double pubValue(double profit, double loss, double fee, 
			double probSuccess, double probForSuccess, double probForFail, int n) {
		
		double probFail = (double) 1 - probSuccess;
		// P(R=T) = P(R=T|S=T)P(S=T) + P(R=T|S=F)P(S=F)
		double probFor = probForSuccess * probSuccess + probForFail * (1 - probSuccess);
		// P(S=T|R=T) = P(R=T|S=T)P(S=T) / P(R=T)
		double probSuccessFor = Math.pow(probForSuccess, n) * probSuccess / Math.pow(probFor, n);
		// P(S=F|R=T) = P(R=T|S=F)P(S=F) / P(R=T)
		double probFailFor = Math.pow(probForFail, n) * (1 - probSuccess) / Math.pow(probFor, n);

		
		DecisionTree.Node<ExpectValue> reviewerDecision = DecisionTree
				.createDecisionNode(new ExpectValue("Decisions"));
		DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(reviewerDecision);

		DecisionTree.Node<ExpectValue> publishedAny = DecisionTree.createChanceNode(new ExpectValue("Published"));
		DecisionTree.Node<ExpectValue> rejectedAny = DecisionTree.createChanceNode(new ExpectValue("Rejected"));		
		DecisionTree.Node<ExpectValue> consulted = DecisionTree.createChanceNode(new ExpectValue("Consulting"));
		
		tree.add("Published", publishedAny);
		tree.add("Rejected", rejectedAny);
		tree.add("Consulted", consulted);
		
		DecisionTree.Node<ExpectValue> success1 = DecisionTree.createUtilityNode(new ExpectValue(profit));
		DecisionTree.Node<ExpectValue> failure1 = DecisionTree.createUtilityNode(new ExpectValue(-1 * loss));

		publishedAny.add(probSuccess, success1);
		publishedAny.add(probFail, failure1);

		DecisionTree.Node<ExpectValue> lose0 = DecisionTree.createUtilityNode(new ExpectValue(0d));
		rejectedAny.add(1.0, lose0);
		
		DecisionTree.Node<ExpectValue> allApproved = DecisionTree.createChanceNode(new ExpectValue("All Approved: 0.46^" + n));
		DecisionTree.Node<ExpectValue> allElse = DecisionTree.createChanceNode(new ExpectValue("Anything Else"));
		
		consulted.add(Math.pow(probFor, n), allApproved);
		consulted.add(1 - Math.pow(probFor, n), allElse);
		
		DecisionTree.Node<ExpectValue> goAhead = DecisionTree.createChanceNode(new ExpectValue("Go Ahead"));
		DecisionTree.Node<ExpectValue> success = DecisionTree.createUtilityNode(new ExpectValue(profit - (fee * n)));
		DecisionTree.Node<ExpectValue> failure = DecisionTree.createUtilityNode(new ExpectValue(-1 * loss - (fee * n)));
		
		allApproved.add(1.0d, goAhead);
		
		goAhead.add(probSuccessFor, success);
		goAhead.add(probFailFor, failure);
		
		DecisionTree.Node<ExpectValue> loseFee = DecisionTree.createUtilityNode(new ExpectValue(-1 * (fee * n)));
		allElse.add(1.0, loseFee);
		
		System.out.println(tree);
		
		return tree.analysis();
	
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

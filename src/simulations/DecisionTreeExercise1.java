package simulations;
public class DecisionTreeExercise1 {

	/**
	 * Imagine a customer attempt to bargain with a store owner.
	 *  1. If the customer decides to ask for a discount, there is a 0.25 chance the discount is 80%, 0.3 chance the discount is 75% and 0.45 chance the discount is 90%
	 *  2. If the customer not to ask for a discount, then the owner has 0.2 chance to let him tries again or 0.8 chance to gives up.
	 *  3. If he tries again, he may have 0.1 to get another bargain.
	 *  4. If he gives up, he yells at the store owner and the store owner has 0.1 chance to offer 90% final.
	 *  5. If he gets another bargain, the store owner would have 0.6 chance to offer a 5% discount or 0.4 chance to refuses him.
	 *  
	 *  What is the Exp(discount)?
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DecisionTree.Node<ExpectValue> askorNot = DecisionTree.createDecisionNode(new ExpectValue("root"));
		
		DecisionTree.Node<ExpectValue> ask = DecisionTree.createChanceNode(new ExpectValue("Ask"));
		DecisionTree.Node<ExpectValue> discount80 = DecisionTree.createUtilityNode(new ExpectValue(20d));
		DecisionTree.Node<ExpectValue> discount75 = DecisionTree.createUtilityNode(new ExpectValue(25d));
		DecisionTree.Node<ExpectValue> discount90 = DecisionTree.createUtilityNode(new ExpectValue(10d));
		
		DecisionTree.Node<ExpectValue> notAsk = DecisionTree.createChanceNode(new ExpectValue("Not Ask"));
		DecisionTree.Node<ExpectValue> tryAgain = DecisionTree.createChanceNode(new ExpectValue("Try Again"));
		DecisionTree.Node<ExpectValue> giveUp = DecisionTree.createChanceNode(new ExpectValue("Give Up")); 
		
		DecisionTree.Node<ExpectValue> nodiscount0 = DecisionTree.createUtilityNode(new ExpectValue(0d));
		DecisionTree.Node<ExpectValue> dis90 = DecisionTree.createUtilityNode(new ExpectValue(10d));
		
		DecisionTree.Node<ExpectValue> nodiscount1 = DecisionTree.createUtilityNode(new ExpectValue(0d));
		DecisionTree.Node<ExpectValue> lessBargin = DecisionTree.createChanceNode(new ExpectValue("Lesser"));
		
		DecisionTree.Node<ExpectValue> discount95 = DecisionTree.createUtilityNode(new ExpectValue(5d));
		
		DecisionTree.Node<ExpectValue> nodiscount2 = DecisionTree.createUtilityNode(new ExpectValue(0d));
		
		DecisionTree<ExpectValue> tree = new DecisionTree<ExpectValue>(askorNot);
		tree.add("ask", ask);
		ask.add(0.25, discount80);
		ask.add(0.30, discount75);
		ask.add(0.45, discount90);
		
		tree.add("not ask", notAsk);
		notAsk.add(0.20, tryAgain);
		notAsk.add(0.80, giveUp);
		
		giveUp.add(0.90, nodiscount0);
		giveUp.add(0.10, dis90);
		
		
		tryAgain.add(0.10, lessBargin);
		tryAgain.add(0.90, nodiscount1);
		
		lessBargin.add(0.60, discount95);
		lessBargin.add(0.40, nodiscount2);
		
		System.out.println(tree);
		
		System.out.println("Expect Discount: " + tree.analysis());
		
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

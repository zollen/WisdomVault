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
		DecisionTree.INode<Double> askorNot = DecisionTree.createDecisionNode(1d);
		
		DecisionTree.INode<Double> ask = DecisionTree.createChanceNode(1d);
		DecisionTree.INode<Double> discount80 = DecisionTree.createUtilityNode(0.80d);
		DecisionTree.INode<Double> discount75 = DecisionTree.createUtilityNode(0.75d);
		DecisionTree.INode<Double> discount90 = DecisionTree.createUtilityNode(0.90d);
		
		DecisionTree.INode<Double> notAsk = DecisionTree.createChanceNode(1d);
		DecisionTree.INode<Double> tryAgain = DecisionTree.createChanceNode(1d);
		DecisionTree.INode<Double> giveUp = DecisionTree.createChanceNode(1d); 
		
		DecisionTree.INode<Double> nodiscount0 = DecisionTree.createUtilityNode(0d);
		DecisionTree.INode<Double> dis90 = DecisionTree.createUtilityNode(0.9d);
		
		DecisionTree.INode<Double> nodiscount1 = DecisionTree.createUtilityNode(0d);
		DecisionTree.INode<Double> lessBargin = DecisionTree.createChanceNode(1d);
		
		DecisionTree.INode<Double> discount95 = DecisionTree.createUtilityNode(0.95d);
		
		DecisionTree.INode<Double> nodiscount2 = DecisionTree.createUtilityNode(0d);
		
		DecisionTree<Double> tree = new DecisionTree<Double>(askorNot);
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
		
		tree.analysis();
		
	}

}

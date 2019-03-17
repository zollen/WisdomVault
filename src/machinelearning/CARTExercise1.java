package machinelearning;

import java.util.ArrayList;
import java.util.List;

import machinelearning.ClassificationPrototype.Node;
import weka.core.Instance;

public class CARTExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Instance> instances = new ArrayList<Instance>();
		
		GoodCirculationStats g = new GoodCirculationStats(instances);
		
		GoodCirculationStats left = new GoodCirculationStats(g.getYes());
		GoodCirculationStats right = new GoodCirculationStats(g.getNo());
		
		g.setLeft(left);
		g.setRight(right);
	}

	public static abstract class Stats {

		protected List<Instance> yes = new ArrayList<Instance>();
		protected List<Instance> no = new ArrayList<Instance>();
		protected double gini = 0.0;
		protected Stats left = null;
		protected Stats right = null;
		
		public Stats(List<Instance> instances) {
			yes = filterYes(instances);
			no = filterNo(instances);
		}
		
		public List<Instance> getYes() {
			return yes;
		}
		
		public List<Instance> getNo() {
			return no;
		}
		
		public void setLeft(Stats left) {
			this.left = left;
		}
		
		public void setRight(Stats right) {
			this.right = right;
		}
		
		public String toString() {
			return null;
		}

		protected double gini(double val1, double val2) {
			// gini impurities
			return (1 - Math.pow(val1 / (val1 + val2), 2)) - Math.pow(val2 / (val1 + val2), 2);
		}

		protected double gini(Node node1, Node node2) {
			// gini impurities
			double total = 0d;
			double totalLeft = 0d;
			double totalRight = 0d;
			double giniLeft = 0d;
			double giniRight = 0d;
			double result = 0d;

			if (node1 != null) {
				total += node1.getYes() + node1.getNo();
				totalLeft = node1.getYes() + node1.getNo();
				giniLeft = node1.getGini();
			}

			if (node2 != null) {
				total += node2.getYes() + node2.getNo();
				totalRight = node2.getYes() + node2.getNo();
				giniRight = node2.getGini();
			}

			if (total <= 0) {
				result = 0;
			} else {
				result = (totalLeft / total) * giniLeft + (totalRight / total) * giniRight;
			}

			return result;
		}

		protected abstract String getLabel();
		
		protected abstract List<Instance> filterYes(List<Instance> instances);
		
		protected abstract List<Instance> filterNo(List<Instance> instances);
	}

	public static class GoodCirculationStats extends Stats {

		public GoodCirculationStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class ChestPainStats extends Stats {

		public ChestPainStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class BlockedArteriesStats extends Stats {

		public BlockedArteriesStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}

package machinelearning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.core.Instance;

public class CARTExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Set<Integer> pool = new HashSet<Integer>();
		pool.add(StatsFactory.BLOCKED_ARTERIES);
		pool.add(StatsFactory.CHEST_PAIN);
		pool.add(StatsFactory.GOOD_CIRCULATION);
		
		
	}
	
	
	public static class StatsFactory {
		
		public static final int GOOD_CIRCULATION = 1;
		public static final int CHEST_PAIN = 2;
		public static final int BLOCKED_ARTERIES = 3;
		
		public static Stats create(int flag, List<Instance> instances) {
			
			Stats stats = null;
			Stats left = null;
			Stats right = null;
			
			switch(flag) {
			
			case GOOD_CIRCULATION:
				stats = new GoodCirculationStats(instances);
				left = new GoodCirculationStats(stats.getYes());
				right = new GoodCirculationStats(stats.getNo());
				stats.setLeft(left);
				stats.setRight(right);
				break;
			case CHEST_PAIN:
				stats = new ChestPainStats(instances);
				left = new ChestPainStats(stats.getYes());
				right = new ChestPainStats(stats.getNo());
				stats.setLeft(left);
				stats.setRight(right);
				break;
			case BLOCKED_ARTERIES:
				stats = new BlockedArteriesStats(instances);
				left = new BlockedArteriesStats(stats.getYes());
				right = new BlockedArteriesStats(stats.getNo());
				stats.setLeft(left);
				stats.setRight(right);
				break;		
			}
			
			return stats;
		}
	}
	

	public static abstract class Stats {

		protected List<Instance> yes = null;
		protected List<Instance> no = null;
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
		
		public double getGini() {
			
			if (left == null && right == null) {
				return gini(yes.size(), no.size());
			}
			else {	
				return gini(left, right);
			}
		}
		
		public void setLeft(Stats left) {
			this.left = left;
		}
		
		public void setRight(Stats right) {
			this.right = right;
		}
		
		public String toString() {
			return getLabel();
		}

		protected double gini(double val1, double val2) {
			// gini impurities
			return (1 - Math.pow(val1 / (val1 + val2), 2)) - Math.pow(val2 / (val1 + val2), 2);
		}

		protected double gini(Stats node1, Stats node2) {
			// gini impurities
			double total = 0d;
			double totalLeft = 0d;
			double totalRight = 0d;
			double giniLeft = 0d;
			double giniRight = 0d;
			double result = 0d;

			if (node1 != null) {
				total += node1.getYes().size() + node1.getNo().size();
				totalLeft = node1.getYes().size() + node1.getNo().size();
				giniLeft = node1.getGini();
			}

			if (node2 != null) {
				total += node2.getYes().size() + node2.getNo().size();
				totalRight = node2.getYes().size() + node2.getNo().size();
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
			return "Good Blood Circulation";
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
			return "Chest Pain";
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
			return "Blocked Arteries";
		}
	}

}

package machinelearning.hmm;

import java.util.Map;

public class Robot {
	
	private int seed;
	private Sensor sensor;
	
	public Robot(int seed) {
		this.seed = seed;
		this.sensor = new Sensor();
	}
	
	public Sensor.Reading sense(Map map, int row, int col) {
		return sensor.sense(map, row, col);
	}
	
	public void moveTop() {
		
	}
	
	public void moveBottom() {
		
	}
	
	public void moveLeft() {
		
	}
	
	public void moveRight() {
		
	}
	
	
	
	public static class Sensor {
		
		public Sensor() {}
		
		public Reading sense(Map map, int row, int col) {
			return null;
		}
		
		
		public static class Reading {
			
			int top;
			int bottom;
			int left;
			int right;
			
			public Reading(int top, int bottom, int left, int right) {
				this.top = top;
				this.bottom = bottom;
				this.left = left;
				this.right = right;
			}
			
			public int getTop() {
				return top;
			}

			public void setTop(int top) {
				this.top = top;
			}

			public int getBottom() {
				return bottom;
			}

			public void setBottom(int bottom) {
				this.bottom = bottom;
			}

			public int getLeft() {
				return left;
			}

			public void setLeft(int left) {
				this.left = left;
			}

			public int getRight() {
				return right;
			}

			public void setRight(int right) {
				this.right = right;
			}
			
			@Override
			public String toString() {
				return null;
			}

		}
		
	}
	
	

}

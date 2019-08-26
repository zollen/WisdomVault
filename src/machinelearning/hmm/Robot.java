package machinelearning.hmm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Robot {
	
	public static final int TOP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public static final Random rand = new Random(83);
	public static final int ROUND = 100;
	public static final int HEIGHT = 3;
	public static final int WIDTH = 4;
	public static final char [][] map = new char[HEIGHT][WIDTH];
	public static final Map<String, Set<Integer>> allowed = new HashMap<String, Set<Integer>>();
	public static int x = 0;
	public static int y = 0;
	
	/**
	 * ******
	 * *S   *
	 * ** *E*
	 * *S   *
	 * ******
	 */
	
	static {
			
		map[1][0] = map[1][2] = '*';
		
		Set<Integer> m00 = new HashSet<Integer>();
		m00.add(RIGHT); 
		allowed.put("00", m00);
		
		Set<Integer> m01 = new HashSet<Integer>();
		m01.add(RIGHT); m01.add(LEFT); m01.add(DOWN);
		allowed.put("01", m01);
		
		Set<Integer> m02 = new HashSet<Integer>();
		m02.add(RIGHT); m02.add(LEFT); 
		allowed.put("02", m02);
		
		Set<Integer> m03 = new HashSet<Integer>();
		m03.add(LEFT); m03.add(DOWN);
		allowed.put("03", m03);
		
		Set<Integer> m11 = new HashSet<Integer>();
		m11.add(TOP); m11.add(DOWN);
		allowed.put("11", m11);
		
		Set<Integer> m13 = new HashSet<Integer>();
		m13.add(TOP); m13.add(DOWN);
		allowed.put("13", m13);
		
		Set<Integer> m20 = new HashSet<Integer>();
		m20.add(RIGHT);
		allowed.put("20", m20);
		
		Set<Integer> m21 = new HashSet<Integer>();
		m21.add(LEFT); m21.add(RIGHT); m21.add(TOP);
		allowed.put("21", m21);
		
		Set<Integer> m22 = new HashSet<Integer>();
		m22.add(LEFT); m22.add(RIGHT); 
		allowed.put("22", m22);
		
		Set<Integer> m23 = new HashSet<Integer>();
		m23.add(LEFT); m23.add(TOP); 
		allowed.put("23", m23);
		
		
	}
	
	public static void main(String ...args) {
		
		Robot robot = new Robot();
		
		for (int i = 0; i < ROUND; i++) {
			
			Set<Integer> possiblities = allowed.get(String.valueOf(y) + String.valueOf(x));
			
			System.out.println(robot.sense(possiblities));
			System.out.println(robot);
			
			robot.move(next(possiblities));
		}
	}
	
	public static Integer next(Set<Integer> moves) {
		
		int chosen = rand.nextInt(moves.size());
		
		int next = 0;
		int index = 0;
		for (Integer move : moves) {
			
			if (index == chosen)
				next = move;
			
			index++;
		}
		
		return next;
	}
	
	private Sensor sensor;
	
	public Robot() {
		this.sensor = new Sensor();
	}
	
	public Sensor.Reading sense(Set<Integer> surrounding) {
		return sensor.sense(surrounding);
	}
	
	public void move(int action) {
		
		switch (action) {
		case TOP:
			top();
		break;
		case DOWN:
			down();
		break;
		case RIGHT:
			right();
		break;
		case LEFT:
			left();
		break;
		}
	}
	
	public void top() {
		
		if (y - 1 >= 0 && allowed.get(String.valueOf(y) + String.valueOf(x)).contains(TOP)) {		
			y -= 1;
		}
	}
	
	public void down() {
		
		if (y + 1 < HEIGHT && allowed.get(String.valueOf(y) + String.valueOf(x)).contains(DOWN)) {		
			y += 1;
		}
	}
	
	public void left() {
		if (x - 1 >= 0 && allowed.get(String.valueOf(y) + String.valueOf(x)).contains(LEFT)) {
			x -= 1;
		}	
	}
	
	public void right() {
		
		if (x + 1 < WIDTH && allowed.get(String.valueOf(y) + String.valueOf(x)).contains(RIGHT)) {			
			x += 1;
		}
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < WIDTH + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		for (int i = 0; i < HEIGHT; i++) {
			
			builder.append("|");
			for (int j = 0; j < WIDTH; j++) {
				if (i == y && j == x) {
					builder.append("X");
				}
				else {
					builder.append(map[i][j]);
				}
			}
			builder.append("|\n");
		}
		
		for (int i = 0; i < WIDTH + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		return builder.toString();
	}
	
	
	
	public static class Sensor {
		
		public Sensor() {}
		
		public Reading sense(Set<Integer> surrounding) {
			return new Reading(surrounding);
		}
		
		
		public static class Reading {
			
			public static final int RIGHT_ONLY = 50;
			public static final int RIGHT_LEFT_DOWN = 63;
			public static final int RIGHT_LEFT = 60;
			public static final int LEFT_DOWN = 13;
			public static final int TOP_DOWN = 4;
			public static final int RIGHT_LEFT_TOP = 61;
			public static final int LEFT_TOP = 11;
			
			private static final Map<Integer, String> desc = new HashMap<Integer, String>();
			
			static {
				desc.put(RIGHT_ONLY, "{RIGHT}");
				desc.put(RIGHT_LEFT_DOWN, "{RIGHT, LEFT, DOWN}");
				desc.put(RIGHT_LEFT, "{RIGHT, LEFT}");
				desc.put(LEFT_DOWN, "{LEFT, DOWN}");
				desc.put(TOP_DOWN, "{TOP DOWN}");
				desc.put(RIGHT_LEFT_TOP, "{RIGHT, LEFT, TOP}");
				desc.put(LEFT_TOP, "{LEFT, TOP}");				
			}
			
			
			int emissionState;
			
			public Reading(Set<Integer> surrounding) {
				
				int score = 0;
				
				if (surrounding.contains(TOP))
					score += 1;
				
				if (surrounding.contains(DOWN))
					score += 3;
				
				if (surrounding.contains(LEFT))
					score += 10;
				
				if (surrounding.contains(RIGHT))
					score += 50;
				
				this.emissionState = score;
				
			}
			
			public int get() {
				return emissionState;
			}
				
			@Override
			public String toString() {
				return desc.get(emissionState);
			}

		}
		
	}
	
	

}

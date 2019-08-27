package machinelearning.hmm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.nd4j.linalg.primitives.Pair;

import machinelearning.hmm.ForwardBackward.HMMResult;
import machinelearning.hmm.Robot.Sensor.Reading;

public class Robot {
	
	public static final int TOP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	private static final DecimalFormat ff = new DecimalFormat("0.0000");
	public static final int ROUND = 1000;
	public static final int HEIGHT = 3;
	public static final int WIDTH = 4;
	public static final char [][] map = new char[HEIGHT][WIDTH];
	public static final Map<String, Set<Integer>> environment = new HashMap<String, Set<Integer>>();
	public static final Map<String, Integer> positions = new HashMap<String, Integer>();

	
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
		environment.put("00", m00);
		positions.put("00", 0);
		
		Set<Integer> m01 = new HashSet<Integer>();
		m01.add(RIGHT); m01.add(LEFT); m01.add(DOWN);
		environment.put("01", m01);
		positions.put("01", 1);
		
		Set<Integer> m02 = new HashSet<Integer>();
		m02.add(RIGHT); m02.add(LEFT); 
		environment.put("02", m02);
		positions.put("02", 2);
		
		Set<Integer> m03 = new HashSet<Integer>();
		m03.add(LEFT); m03.add(DOWN);
		environment.put("03", m03);
		positions.put("03", 3);
		
		Set<Integer> m11 = new HashSet<Integer>();
		m11.add(TOP); m11.add(DOWN);
		environment.put("11", m11);
		positions.put("11", 4);
		
		Set<Integer> m13 = new HashSet<Integer>();
		m13.add(TOP); m13.add(DOWN);
		environment.put("13", m13);
		positions.put("13", 5);
		
		Set<Integer> m20 = new HashSet<Integer>();
		m20.add(RIGHT);
		environment.put("20", m20);
		positions.put("20", 6);
		
		Set<Integer> m21 = new HashSet<Integer>();
		m21.add(LEFT); m21.add(RIGHT); m21.add(TOP);
		environment.put("21", m21);
		positions.put("21", 7);
		
		Set<Integer> m22 = new HashSet<Integer>();
		m22.add(LEFT); m22.add(RIGHT); 
		environment.put("22", m22);
		positions.put("22", 8);
		
		Set<Integer> m23 = new HashSet<Integer>();
		m23.add(LEFT); m23.add(TOP); 
		environment.put("23", m23);
		positions.put("23", 9);
		
	}
	
	public static void main(String ...args) {
		
		Random rand = new Random(83);
		
		DMatrixRMaj S = new DMatrixRMaj(HEIGHT * WIDTH - 2, 1);
		DMatrixRMaj T = new DMatrixRMaj(HEIGHT * WIDTH - 2, HEIGHT * WIDTH - 2);
		DMatrixRMaj E = new DMatrixRMaj(HEIGHT * WIDTH - 2, Sensor.Reading.emissions.size());
				
		CommonOps_DDRM.fill(S, 0.0);
		CommonOps_DDRM.fill(T, 0.0);
		CommonOps_DDRM.fill(E, 0.0);
		
		S.set(positions.get("00"), 0, 0.5);
		S.set(positions.get("20"), 0, 0.5);
			
		{
			// stochastic training robot
			Robot robot = new Robot(rand);

			for (int i = 0; i < ROUND; i++) {
				
				Robot.Sensor.Reading reading = robot.sense(environment);
				
				int from = positions.get(String.valueOf(robot.row) + String.valueOf(robot.col));
				
				E.set(from, Sensor.Reading.emissions.get(reading.get()),
						E.get(from, Sensor.Reading.emissions.get(reading.get())) + 1);

				robot.random(environment);

				int to = positions.get(String.valueOf(robot.row) + String.valueOf(robot.col));

				T.set(from, to, T.get(from, to) + 1);
			}

			CommonOps_DDRM.divideRows(CommonOps_DDRM.transpose(CommonOps_DDRM.sumRows(T, null), null).data, T);
			CommonOps_DDRM.divideRows(CommonOps_DDRM.transpose(CommonOps_DDRM.sumRows(E, null), null).data, E);
			
		}
		

		for (int epoch = 0; epoch < 10; epoch++)
		{
			// testing robots
			Robot robot = new Robot(rand);
			
			for (int i = 0; i < 6; i++) {
				
				robot.record(robot.sense(environment));
						
				robot.random(environment);
			}
			
			ForwardBackward fb = new ForwardBackward.Builder().build();
			
			HMMResult h = fb.fit(robot.get(), S, T, E);
			
			Printer p = new Printer(ff);
			String [] states = { "0:0", "0:1", "0:2", "0:3", "1:1", "1:3", "2:0", "2:1", "2:2", "2:3" };
			String [] emissions = { "R", "RLW", "RL", "LD", "TD", "RLT", "LT" };
			
			System.out.println("Robot #" + (epoch + 1));
			System.out.println("==========");
			System.out.println(robot);
			System.out.println("Viterbi    : " + p.display(states, h.vlist()) + "   => P: " + ff.format(h.viterbi().probability(h.vlist())));
			System.out.println("Forward    : " + p.display(emissions, h.flist()) + "   => P: " + ff.format(h.forward().probability(h.flist())));
			System.out.println("Backward   : " + p.display(emissions, h.blist()));
			System.out.println("FB         : " + p.display(emissions, h.fblist()));
			System.out.println("Posterior  : " + p.display(emissions, h.plist()));
			System.out.println();
			
		}
		
	}
	

	
	
	
	private Random rand;
	private int row;
	private int col;
	private Sensor sensor;
	private List<Pair<String, Sensor.Reading>> readings = new ArrayList<Pair<String, Sensor.Reading>>();
	
	public Robot(Random rand) {
		this.rand = rand;
		this.sensor = new Sensor();
		if (rand.nextDouble() <= 0.5) {
			row = 0;
			col = 0;
		}
		else {
			row = 2;
			col = 0;
		}
			
	}
	
	public Sensor.Reading sense(Map<String, Set<Integer>> surrounding) {
		Set<Integer> info = environment.get(String.valueOf(row) + String.valueOf(col));
		return sensor.sense(info);
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
	
	public void random(Map<String, Set<Integer>> surrounding) {
		
		Set<Integer> info = surrounding.get(String.valueOf(row) + String.valueOf(col));
		
		int chosen = rand.nextInt(info.size());
		
		int next = 0;
		int index = 0;
		for (Integer move : info) {
			
			if (index == chosen)
				next = move;
			
			index++;
		}
		
		move(next);		
	}
	
	public void record(Reading reading) {
		readings.add(new Pair<>(String.valueOf(row) + ":" + String.valueOf(col), reading));
	}
	
	public int [] get() {
		
		int [] results = new int[readings.size()];
		
		for (int i = 0; i < readings.size(); i++) {
			
			Pair<String, Reading> reading = readings.get(i);
			
			results[i] = Sensor.Reading.emissions.get(reading.getSecond().get());
		}
		
		return results;
	}
	
	public void top() {
				
		row -= 1;
	}
	
	public void down() {
		
		row += 1;
	}
	
	public void left() {
		col -= 1;
	}
	
	public void right() {
		
		col += 1;
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
				if (i == row && j == col) {
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
		
		builder.append(readings.stream().map(p -> "{" + p.getFirst() + "}:" + p.getSecond())
								.collect(Collectors.joining(", ")));
		
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
			private static final Map<Integer, Integer> emissions = new HashMap<Integer, Integer>();
			
			static {
				desc.put(RIGHT_ONLY, "[RIGHT]");
				desc.put(RIGHT_LEFT_DOWN, "[RIGHT,LEFT,DOWN]");
				desc.put(RIGHT_LEFT, "[RIGHT,LEFT]");
				desc.put(LEFT_DOWN, "[LEFT,DOWN]");
				desc.put(TOP_DOWN, "[TOP,DOWN]");
				desc.put(RIGHT_LEFT_TOP, "[RIGHT,LEFT,TOP]");
				desc.put(LEFT_TOP, "[LEFT,TOP]");		
				
				emissions.put(RIGHT_ONLY, 0);
				emissions.put(RIGHT_LEFT_DOWN, 1);
				emissions.put(RIGHT_LEFT, 2);
				emissions.put(LEFT_DOWN, 3);
				emissions.put(TOP_DOWN, 4);
				emissions.put(RIGHT_LEFT_TOP, 5);
				emissions.put(LEFT_TOP, 6);
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

package machinelearning.hmm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.nd4j.linalg.primitives.Pair;

import genetic.maze.Maze;
import genetic.maze.MazeGame;
import genetic.maze.MazeGame.Move;
import genetic.maze.MazeLoader;
import machinelearning.hmm.HMMComposite.HMMResult;
import machinelearning.hmm.Robot.Sensor.Reading;

public class Robot {
	
	public static final int TOP = MazeGame.UP;
	public static final int DOWN = MazeGame.DOWN;
	public static final int LEFT = MazeGame.LEFT;
	public static final int RIGHT = MazeGame.RIGHT;
	
	private static final DecimalFormat ff = new DecimalFormat("0.0000");
	public static final int ROUND = 1000;

	
	/**
	 * ******
	 * *S   *
	 * ** *E*
	 * *S   *
	 * ******
	 */
	
	public static void main(String ...args) {
		
		Random rand = new Random(83);
		
		MazeLoader loader = new MazeLoader("data/toymaze.txt");
		
		Maze maze = loader.build();
		System.out.println(maze);
		System.out.println("---------------------------------------------------");
		
		DMatrixRMaj S = new DMatrixRMaj(maze.getSpaces(), 1);
		DMatrixRMaj T = new DMatrixRMaj(maze.getSpaces(), maze.getSpaces());
		DMatrixRMaj E = new DMatrixRMaj(maze.getSpaces(), Sensor.Reading.emissions.size());
				
		CommonOps_DDRM.fill(S, Double.MIN_VALUE);
		CommonOps_DDRM.fill(T, Double.MIN_VALUE);
		CommonOps_DDRM.fill(E, Double.MIN_VALUE);
		
		S.set(maze.getPosition(0, 0), 0, 0.5);
		S.set(maze.getPosition(0, 0), 0, 0.5);
			
		{
			// stochastic training robot
			Robot robot = new Robot(rand);

			for (int i = 0; i < ROUND; i++) {
				
				Robot.Sensor.Reading reading = robot.sense(maze);
				
				int from = maze.getPosition(robot.row, robot.col);
				
				E.set(from, Sensor.Reading.emissions.get(reading.get()),
						E.get(from, Sensor.Reading.emissions.get(reading.get())) + 1);

				robot.random(maze);

				int to = maze.getPosition(robot.row, robot.col);

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
				
				robot.record(robot.sense(maze));
						
				robot.random(maze);
			}
			
			HMMComposite fb = new HMMComposite.Builder().build();
			
			HMMResult h = fb.fit(robot.get(), S, T, E);
			
			Printer p = new Printer(ff);
			String [] states = { "0:0", "0:1", "0:2", "0:3", "1:1", "1:3", "2:0", "2:1", "2:2", "2:3" };
			String [] emissions = Reading.desc.values().toArray(new String[0]);
			
			System.out.println("Robot #" + (epoch + 1));
			System.out.println("==========");
			System.out.println(robot.summary(maze));
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
	
	public Sensor.Reading sense(Maze maze) {
		return sensor.sense(maze.getFreedom(row, col));
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
	
	public void random(Maze maze) {
		
		Set<Integer> info = maze.getFreedom(row, col);
		
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
	
	public String summary(Maze maze) {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < maze.getWidth() + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		for (int i = 0; i < maze.getHeight(); i++) {
			
			builder.append("|");
			for (int j = 0; j < maze.getWidth(); j++) {
				if (i == row && j == col) {
					builder.append("X");
				}
				else {
					builder.append(maze.getMap()[i][j]);
				}
			}
			builder.append("|\n");
		}
		
		for (int i = 0; i < maze.getWidth() + 2; i++)
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
			
			
			public static final Map<Integer, String> desc = new LinkedHashMap<Integer, String>();
			public static final Map<Integer, Integer> emissions = new LinkedHashMap<Integer, Integer>();
			
			static {
				desc.put(RIGHT_ONLY, Move.ACTIONS[RIGHT]);
				desc.put(RIGHT_LEFT_DOWN, Move.ACTIONS[LEFT] + Move.ACTIONS[DOWN] + Move.ACTIONS[RIGHT]);
				desc.put(RIGHT_LEFT, Move.ACTIONS[LEFT] + Move.ACTIONS[RIGHT]);
				desc.put(LEFT_DOWN, Move.ACTIONS[LEFT] + Move.ACTIONS[DOWN]);
				desc.put(TOP_DOWN, Move.ACTIONS[TOP] + Move.ACTIONS[DOWN]);
				desc.put(RIGHT_LEFT_TOP, Move.ACTIONS[LEFT] + Move.ACTIONS[TOP] + Move.ACTIONS[RIGHT]);
				desc.put(LEFT_TOP, Move.ACTIONS[TOP] + Move.ACTIONS[LEFT]);		
				
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

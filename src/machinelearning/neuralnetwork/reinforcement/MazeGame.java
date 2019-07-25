package machinelearning.neuralnetwork.reinforcement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MazeGame {
	
	private static final Random rand = new Random(83);
	private static final int WIDTH = 7;
	private static final int HEIGHT = 3;
	
	public static final int MAX_ROUNDS = 100;
	public static final int TOTAL_SLOTS = 17;
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	public static final int END_ROW = 2;
	public static final int END_COLUMN = 6;
	

	private static final Map<String, Set<Integer>> moves = new HashMap<String, Set<Integer>>();
	private static final Map<String, Integer> positions = new HashMap<String, Integer>();
	
	static {
		setup();
	}
	
	/**
	 * ---------
	 * |S      |
	 * | ** ** |
	 * |      E|
	 * ---------
	 */
	
	private char [][] map = new char[HEIGHT][WIDTH];
	private int row;
	private int col;
	private List<Move> actions = new ArrayList<Move>();
	
	public MazeGame() {
		init();
	}
	
	private void init() {
		for (int i = 0; i < HEIGHT; i++) 
			for (int j = 0; j < WIDTH; j++)
				map[i][j] = ' ';
		
		row = col = 0;
		map[row][col] = 'X';
		map[1][1] = map[1][2] = map[1][4] = map[1][5] = '*'; 
	}
	
	public void reset() {
		
		init();
		
		actions.clear();
	}
	
	public void up() {
		
		Set<Integer> possibilities = moves(row, col);	
		if (!possibilities.contains(UP))
			throw new RuntimeException("UP not allowed for [" + row + "][" + col + "]");
		
		map[row][col] = ' ';
		
		row--;
		
		map[row][col] = 'X';
		
		actions.add(new Move("UP", row, col));
	}
	
	public void down() {
		
		Set<Integer> possibilities = moves(row, col);	
		if (!possibilities.contains(DOWN))
			throw new RuntimeException("DOWN not allowed for [" + row + "][" + col + "]");
		
		map[row][col] = ' ';
		
		row++;
		
		map[row][col] = 'X';	
		
		actions.add(new Move("DOWN", row, col));
	}
	
	public void left() {
		
		Set<Integer> possibilities = moves(row, col);	
		if (!possibilities.contains(LEFT))
			throw new RuntimeException("LEFT not allowed for [" + row + "][" + col + "]");
		
		map[row][col] = ' ';
		
		col--;
		
		map[row][col] = 'X';	
		
		actions.add(new Move("LEFT", row, col));
	}
	
	public void right() {
		
		Set<Integer> possibilities = moves(row, col);	
		if (!possibilities.contains(RIGHT))
			throw new RuntimeException("RIGHT not allowed for [" + row + "][" + col + "]");
		
		map[row][col] = ' ';
		
		col++;
		
		map[row][col] = 'X';	
		
		actions.add(new Move("RIGHT", row, col));
	}
	
	public Set<Integer> moves(int row, int col) {
		return moves.get(String.valueOf(row) + String.valueOf(col));
	}
	
	public List<Move> moves() {
		return actions;
	}
	
	public int row() {
		return row;
	}
	
	public int col() {
		return col;
	}
	
	public int current() {
		return positions.get(String.valueOf(row()) + String.valueOf(col()));
	}
	
	public void run() {
		
		for (int turn = 1; turn <= MAX_ROUNDS; turn++) {

			int move = random(moves(row(), col()));

			switch (move) {
			case UP:
				up();
			break;
			case DOWN:
				down();
			break;
			case LEFT:
				left();
			break;
			case RIGHT:
				right();
			break;
			default:
			}

			if (row() >= HEIGHT - 1 && col() >= WIDTH - 1)
				break;
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
				builder.append(map[i][j]);
			}
			
			builder.append("|\n");
		}
		
		for (int i = 0; i < WIDTH + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		builder.append("Number of Moves: " + actions.size());
		builder.append("\n");
		
		builder.append(actions.stream().map(p -> p.toString())
				.collect(Collectors.joining(", ")));
		
		builder.append("\n");
				
		return builder.toString();
	}
	
	private static void setup() {
		
		Set<Integer> p00 = new HashSet<Integer>();
		p00.add(DOWN); p00.add(RIGHT);
		moves.put("00", p00);
		positions.put("00", 0);
		
		Set<Integer> p01 = new HashSet<Integer>();
		p01.add(LEFT); p01.add(RIGHT);
		moves.put("01", p01);
		positions.put("01", 1);
		
		Set<Integer> p02 = new HashSet<Integer>();
		p02.add(LEFT); p02.add(RIGHT);
		moves.put("02", p02);
		positions.put("02", 2);
		
		Set<Integer> p03 = new HashSet<Integer>();
		p03.add(LEFT); p03.add(RIGHT); p03.add(DOWN);
		moves.put("03", p03);
		positions.put("03", 3);
		
		Set<Integer> p04 = new HashSet<Integer>();
		p04.add(LEFT); p04.add(RIGHT);
		moves.put("04", p04);
		positions.put("04", 4);
		
		Set<Integer> p05 = new HashSet<Integer>();
		p05.add(LEFT); p05.add(RIGHT);
		moves.put("05", p05);
		positions.put("05", 5);
		
		Set<Integer> p06 = new HashSet<Integer>();
		p06.add(LEFT); p06.add(DOWN);
		moves.put("06", p06);
		positions.put("06", 6);
		
		Set<Integer> p10 = new HashSet<Integer>();
		p10.add(UP); p10.add(DOWN);
		moves.put("10", p10);
		positions.put("10", 7);
		
		Set<Integer> p13 = new HashSet<Integer>();
		p13.add(UP); p13.add(DOWN);
		moves.put("13", p13);
		positions.put("13", 8);
		
		Set<Integer> p16 = new HashSet<Integer>();
		p16.add(UP); p16.add(DOWN);
		moves.put("16", p16);
		positions.put("16", 9);
		
		Set<Integer> p20 = new HashSet<Integer>();
		p20.add(UP); p20.add(RIGHT);
		moves.put("20", p20);
		positions.put("20", 10);
		
		Set<Integer> p21 = new HashSet<Integer>();
		p21.add(LEFT); p21.add(RIGHT);
		moves.put("21", p21);
		positions.put("21", 11);
		
		Set<Integer> p22 = new HashSet<Integer>();
		p22.add(LEFT); p22.add(RIGHT);
		moves.put("22", p22);
		positions.put("22", 12);
		
		Set<Integer> p23 = new HashSet<Integer>();
		p23.add(LEFT); p23.add(RIGHT); p23.add(UP);
		moves.put("23", p23);
		positions.put("23", 13);
		
		Set<Integer> p24 = new HashSet<Integer>();
		p24.add(LEFT); p24.add(RIGHT);
		moves.put("24", p24);
		positions.put("24", 14);
		
		Set<Integer> p25 = new HashSet<Integer>();
		p25.add(LEFT); p25.add(RIGHT);
		moves.put("25", p25);
		positions.put("25", 15);
		
		Set<Integer> p26 = new HashSet<Integer>();
		p26.add(RIGHT); p26.add(UP);
		moves.put("26", p26);
		positions.put("26", 16);
	}

	private static int random(Set<Integer> moves) {
		int target = rand.nextInt(moves.size());
		
		int count = 0;
		for (Integer move : moves) {
			
			if (count == target) {
				return move;
			}
			
			count++;
		}
		
		return -1;
	}
	
	public static class Move {
		
		private String name;
		private int row;
		private int col;
		
		public Move(String name, int row, int col) {
			this.name = name;
			this.row = row;
			this.col = col;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getRow() {
			return row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public int getCol() {
			return col;
		}

		public void setCol(int col) {
			this.col = col;
		}
		
		@Override
		public String toString() {
			return "[" + name + "]: " + row + "|" + col;
		}

	}

}

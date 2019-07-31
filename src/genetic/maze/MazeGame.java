package genetic.maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MazeGame {
	
	private static final int WIDTH = 7;
	private static final int HEIGHT = 3;	
	private static final int SEED = 83;
	
	
	private static final int MAX_TURNS = 100;
	private static final int END_STATE = 15;
	
	
	
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	
	private static Random rand = new Random(SEED);
	
	private static final Map<String, Set<Integer>> allowed = new HashMap<String, Set<Integer>>();
	private static final Map<String, Integer> positions = new HashMap<String, Integer>();

	private char [][] map;
	private int row;
	private int col;
	private List<Move> moves;
	
	static {
		setup();
	}
	
	public MazeGame() {

		this.map = new char[HEIGHT][WIDTH];
		this.row = 0;
		this.col = 0;
		this.moves = new ArrayList<Move>();
		this.map[row][col] = 'X';
		
		this.map[1][1] = this.map[1][2] = 
				this.map[1][4] = this.map[1][5] = this.map[2][3] = '*';
	}
	
	public void mutate(int start) {
		
		map[row][col] = ' ';
		
		if (start >= moves.size() - 1) {
			if (moves.size() - 5 > 0)
				start = moves.size() - 5;
			else
				start = 1;
		}
		
		int last = moves.size() - 1;
		for (int j = last; j > last - start; j--) {
			moves.remove(j);
		}
		
		Move move = moves.get(moves.size() - 1);
		
		row = move.getRow();
		col = move.getCol();
		
		random();
	}
	
	public int score() {
		return this.current() == END_STATE ? (MAX_TURNS + 10) - this.moves.size() : -1 * (END_STATE - this.current());
	}
	
	public void random() {
		
		for (int turn = 0; turn < MAX_TURNS; turn++) {
			
			List<Integer> mvs = new ArrayList<Integer>(allowed());
			
			int next = mvs.get(rand.nextInt(mvs.size()));
			
			move(next);
			
			if (this.current() == END_STATE)
				break;
		}
	}
	
	private void move(int action) {
		
		switch (action) {
		case UP:
			this.up();
		break;
		case DOWN:
			this.down();
		break;
		case LEFT:
			this.left();
		break;
		case RIGHT:
			this.right();
		break;
		default:
		}	
	}
	
	public Set<Integer> allowed() {
		return allowed.get(String.valueOf(row) + String.valueOf(col));
	}
	
	public int current() {
		return positions.get(String.valueOf(row) + String.valueOf(col));
	}
	
	public List<Move> moves() {
		return moves;
	}
	
	public void up() {
		
		allowed(UP);
		
		map[row][col] = ' ';
		
		row--;
		
		map[row][col] = 'X';
		
		moves.add(new Move(UP, row, col));
	}
	
	public void down() {
		
		allowed(DOWN);
		
		map[row][col] = ' ';
		
		row++;
		
		map[row][col] = 'X';
		
		moves.add(new Move(DOWN, row, col));
	}
	
	public void left() {
		
		allowed(LEFT);
		
		map[row][col] = ' ';
		
		col--;
		
		map[row][col] = 'X';
		
		moves.add(new Move(LEFT, row, col));
	}
	
	public void right() {
		
		allowed(RIGHT);
		
		map[row][col] = ' ';
		
		col++;
		
		map[row][col] = 'X';
		
		moves.add(new Move(RIGHT, row, col));
	}
	
	public void reset() {
		
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				map[j][i] = ' ';
			}
		}
		
		this.map[1][1] = this.map[1][2] = 
				this.map[1][4] = this.map[1][5] = this.map[2][3] = '*';
		
		row = col = 0;
		map[row][col] = 'X';
		
		moves = new ArrayList<Move>();
	}
	
	@Override
	public MazeGame clone() {
		
		MazeGame game = new MazeGame();
		
		for (int row = 0; row < HEIGHT; row++) {
			for (int col = 0; col < WIDTH; col++) {
				game.map[row][col] = this.map[row][col];
			}
		}
		
		game.row = this.row;
		game.col = this.col;
		game.moves = new ArrayList<Move>(this.moves);
		
		return game;
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
		
		builder.append(moves.stream().map(p -> p.toString()).collect(Collectors.joining(", ")));
		
		return builder.toString();
	}
	
	private void allowed(int action) {
		
		if (!allowed.get(String.valueOf(row) + String.valueOf(col)).contains(action))
			throw new RuntimeException(Move.ACTIONS[action] + " not allowed for (" + row + ", " + col + ")");
	}
	
	private static void setup() {
		
		Set<Integer> a00 = new HashSet<Integer>();
		a00.add(DOWN); a00.add(RIGHT);
		allowed.put("00", a00);
		positions.put("00", 0);
		
		Set<Integer> a01 = new HashSet<Integer>();
		a01.add(LEFT); a01.add(RIGHT);
		allowed.put("01", a01);
		positions.put("01", 1);
		
		Set<Integer> a02 = new HashSet<Integer>();
		a02.add(LEFT); a02.add(RIGHT);
		allowed.put("02", a02);
		positions.put("02", 2);
		
		Set<Integer> a03 = new HashSet<Integer>();
		a03.add(DOWN); a03.add(LEFT); a03.add(RIGHT);
		allowed.put("03", a03);
		positions.put("03", 3);
		
		Set<Integer> a04 = new HashSet<Integer>();
		a04.add(LEFT); a04.add(RIGHT);
		allowed.put("04", a04);
		positions.put("04", 4);
		
		Set<Integer> a05 = new HashSet<Integer>();
		a05.add(LEFT); a05.add(RIGHT);
		allowed.put("05", a05);
		positions.put("05", 5);
		
		Set<Integer> a06 = new HashSet<Integer>();
		a06.add(DOWN); a06.add(LEFT);
		allowed.put("06", a06);
		positions.put("06", 6);
		
		Set<Integer> a10 = new HashSet<Integer>();
		a10.add(UP); a10.add(DOWN);
		allowed.put("10", a10);
		positions.put("10", 7);
		
		Set<Integer> a13 = new HashSet<Integer>();
		a13.add(UP);
		allowed.put("13", a13);
		positions.put("13", 8);
		
		Set<Integer> a16 = new HashSet<Integer>();
		a16.add(UP); a16.add(DOWN);
		allowed.put("16", a16);
		positions.put("16", 9);
		
		Set<Integer> a20 = new HashSet<Integer>();
		a20.add(UP); a20.add(RIGHT);
		allowed.put("20", a20);
		positions.put("20", 10);
		
		Set<Integer> a21 = new HashSet<Integer>();
		a21.add(LEFT); a21.add(RIGHT);
		allowed.put("21", a21);
		positions.put("21", 11);
		
		Set<Integer> a22 = new HashSet<Integer>();
		a22.add(LEFT); 
		allowed.put("22", a22);
		positions.put("22", 12);
		
		Set<Integer> a24 = new HashSet<Integer>();
		a24.add(RIGHT);
		allowed.put("24", a24);
		positions.put("24", 13);
		
		Set<Integer> a25 = new HashSet<Integer>();
		a25.add(LEFT); a25.add(RIGHT);
		allowed.put("25", a25);
		positions.put("25", 14);
		
		Set<Integer> a26 = new HashSet<Integer>();
		a26.add(UP); a26.add(LEFT);
		allowed.put("26", a26);
		positions.put("26", 15);		
	}
	
	public static class Move {
		
		private static final String [] ACTIONS = {
				"\u2191", "\u2193", "\u2190", "\u2192"
		};
		
		private int action;
		private int row;
		private int col;
		
		public Move(int action, int row, int col) {
			this.action = action;
			this.row = row;
			this.col = col;
		}
		
		public int getAction() {
			return action;
		}

		public void setAction(int action) {
			this.action = action;
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
			return "[" + ACTIONS[action] + "]" + row + ":" + col;
		}
	}

}

package genetic.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MazeGame {

	private static final int SEED = 83;
	
	private static final int MAX_TURNS = 20;
	
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	private static Random rand = new Random(SEED);
	
	private Map<String, Set<Integer>> allowed;
	private Map<String, Integer> positions;

	private char [][] map;
	private int width;
	private int height;	
	private int end;
	private int row;
	private int col;
	private List<Move> moves;
	

	public MazeGame(int width, int height, int col, int row, 
			int end, char [][] map, Map<String, Set<Integer>> allowed, Map<String, Integer> positions) {

		this.end = end;
		this.width = width;
		this.height = height;
		this.allowed = allowed;
		this.positions = positions;
		this.map = map;
		this.row = row;
		this.col = col;
		this.moves = new ArrayList<Move>();
		this.map[row][col] = 'X';
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
		return this.current() == end ? (MAX_TURNS + 10) - this.moves.size() : -1 * (end - this.current());
	}
	
	public MazeGame random() {
		
		for (int turn = 0; turn < MAX_TURNS; turn++) {
			
			List<Integer> mvs = new ArrayList<Integer>(allowed());
			
			int next = mvs.get(rand.nextInt(mvs.size()));
			
			move(next);
			
			if (this.current() == end)
				break;
		}
		
		return this;
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
	
	@Override
	public MazeGame clone() {
		
		MazeGame game = new MazeGame(this.width, this.height, this.col, this.row, this.end,
				new char[height][width], this.allowed, this.positions);
						
		
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
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
		
		for (int i = 0; i < width + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		for (int i = 0; i < height; i++) {
			
			builder.append("|");
			for (int j = 0; j < width; j++) {
				builder.append(map[i][j]);
			}
			builder.append("|\n");
		}
		
		for (int i = 0; i < width + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		builder.append(moves.stream().map(p -> p.toString()).collect(Collectors.joining(", ")));
		
		builder.append("\n");
		
		builder.append(moves.size() + " moves");
		
		return builder.toString();
	}
	
	private void allowed(int action) {
		
		if (!allowed.get(String.valueOf(row) + String.valueOf(col)).contains(action))
			throw new RuntimeException(Move.ACTIONS[action] + " not allowed for (" + row + ", " + col + ")");
	}
	
	
	public static class Move {
		
		public static final String [] ACTIONS = {
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

package genetic.maze;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MazeGame {

	private static final int SEED = 83;
	
	private static final int MAX_TURNS = 100;
	
	
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int NONE = 4;
	
	private static Random rand = new Random(SEED);
	
	private Map<String, Set<Integer>> allowed;
	private char [][] map;
	private int width;
	private int height;	
	private int row;
	private int col;
	private Move end;
	
	private List<Move> moves;
	

	private MazeGame() {
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	public void setEnd(Move end) {
		this.end = end;
	}
	
	public void setMap(char [][] map) {
		this.map = map;
	}
	
	public void setAllowed(Map<String, Set<Integer>> allowed) {
		this.allowed = allowed;
	}
	
	public void setMove(List<Move> moves) {
		this.moves = moves;
	}
	
	public static class Builder {
		
		private Map<String, Set<Integer>> allowed;
		private char [][] map;
		private int width;
		private int height;	
		private int row;
		private int col;
		private Move end;
		private List<Move> moves;
		
		public Builder() {
			moves = new ArrayList<Move>();
		}
		
		public Builder setWidth(int width) {
			this.width = width;
			return this;
		}
		
		public Builder setHeight(int height) {
			this.height = height;
			return this;
		}
		
		public Builder setRow(int row) {
			this.row = row;
			return this;
		}
		
		public Builder setCol(int col) {
			this.col = col;
			return this;
		}
		
		public Builder setEnd(Move end) {
			this.end = end;
			return this;
		}
		
		public Builder setMap(char [][] map) {
			this.map = map;
			return this;
		}
		
		public Builder setAllowed(Map<String, Set<Integer>> allowed) {
			this.allowed = allowed;
			return this;
		}
		
		public Builder setMoves(List<Move> moves) {
			this.moves = moves;
			return this;
		}
		
		public MazeGame build() {
		
			MazeGame game = new MazeGame();
			
			game.setWidth(width);
			game.setHeight(height);
			game.setRow(row);
			game.setCol(col);
			game.setEnd(end);
			game.setAllowed(allowed);
			game.setMove(new ArrayList<Move>(moves));
	
			char [][] mmap = new char[height][width];
			
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					mmap[j][i] = map[j][i];
				}
			}
			
			mmap[row][col] = 'X';
			
			game.setMap(mmap);
			
			return game;
		}
		
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
	
	public double score() {
		return this.current().same(end) ? 
				(MAX_TURNS + 10) - this.moves.size() : 
				-100 *  this.current().distance(end);
	}
	
	public MazeGame random() {
		
		for (int turn = 0; turn < MAX_TURNS; turn++) {
			
			List<Integer> mvs = new ArrayList<Integer>(allowed());
			
			int next = mvs.get(rand.nextInt(mvs.size()));
			
			move(next);
			
			if (this.current().same(end))
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
	
	public Move current() {
		
		if (moves == null || moves.size() <= 0)
			return new Move(NONE, 0, 0);
		
		return moves.get(moves.size() - 1);
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
		
		return new MazeGame.Builder()
			.setWidth(width)
			.setHeight(height)
			.setRow(row)
			.setCol(col)
			.setEnd(end)
			.setMap(this.map)
			.setAllowed(allowed)
			.setMoves(moves)
			.build();
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
				"\u2191", "\u2193", "\u2190", "\u2192", "N"
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
		
		public boolean same(Move position) {
			
			if (this.row == position.row && this.col == position.col)
				return true;
			
			return false;
		}
		
		public double distance(Move position) {		
			return Math.sqrt(Math.pow(this.row - position.row, 2) + 
					Math.pow(this.col - position.col, 2));
		}
		
		@Override
		public String toString() {
			return "[" + ACTIONS[action] + "]" + row + ":" + col;
		}
	}

}

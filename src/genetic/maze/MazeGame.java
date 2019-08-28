package genetic.maze;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	
	private Maze maze;

	private int row;
	private int col;
	private Move end;
	
	private List<Move> moves;
	

	private MazeGame() {
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
	
	public void setMaze(Maze maze) {
		this.maze = maze;
	}
	
	public void setMove(List<Move> moves) {
		this.moves = moves;
	}
	
	public static class Builder {
	
		private Maze maze;
		private int row;
		private int col;
		private Move end;
		private List<Move> moves;
	
		
		
		public Builder() {
			moves = new ArrayList<Move>();
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
		
		public Builder setMoves(List<Move> moves) {
			this.moves = moves;
			return this;
		}
		
		public Builder setMaze(Maze maze) {
			this.maze = maze;
			return this;
		}
		
		public MazeGame build() {
		
			MazeGame game = new MazeGame();
			
			game.setMaze(maze.clone());
			game.setRow(row);
			game.setCol(col);
			game.setEnd(end);
			game.setMove(new ArrayList<Move>(moves));
			
			return game;
		}
		
	}
	
	public void mutate(MazeLoader loader) {
		
		MazeGame [] game = new MazeGame[100];
		
		MazeGame target = null;
		double max = Integer.MIN_VALUE;
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			
			game[i] = loader.create().random();
			
			if (i > 0)
				builder.append(", " );
			builder.append(game[i].score());
			
	
			if (game[i].score() > max) {
				max = game[i].score();
				target = game[i];
			}
		}
		
		if (target.score() > this.score()) {	
			this.maze = target.maze;
			this.row = target.row;
			this.col = target.col;
			this.moves = target.moves();
		}
	}
	
	public void optimize() {
		
		Map<Move, Move> optimized = new LinkedHashMap<Move, Move>();
		
		Move start = new Move(NONE, 0, 0);
			
		Move prev = start;
		
		for (Move move : moves) {
				
			Move curr = optimized.get(move);
						
			if (curr == null) {
				optimized.put(prev, move);
			}
			else {
				Move purged = optimized.keySet().toArray(new Move[0])[optimized.size() - 1];
			
				while (optimized.size() > 0 && !purged.same(move)) {
					
					optimized.remove(purged);
					if (optimized.size() > 0)
						purged = optimized.keySet().toArray(new Move[0])[optimized.size() - 1];
				}	
			}
			
			prev = move;
		}
		
		this.moves = new ArrayList<Move>(optimized.values());
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
		return maze.getFreedom(row, col);
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
		
		maze.getMap()[row][col] = ' ';
		
		row--;
		
		maze.getMap()[row][col] = 'X';
		
		moves.add(new Move(UP, row, col));
	}
	
	public void down() {
		
		allowed(DOWN);
		
		maze.getMap()[row][col] = ' ';
		
		row++;
		
		maze.getMap()[row][col] = 'X';
		
		moves.add(new Move(DOWN, row, col));
	}
	
	public void left() {
		
		allowed(LEFT);
		
		maze.getMap()[row][col] = ' ';
		
		col--;
		
		maze.getMap()[row][col] = 'X';
		
		moves.add(new Move(LEFT, row, col));
	}
	
	public void right() {
		
		allowed(RIGHT);
		
		maze.getMap()[row][col] = ' ';
		
		col++;
		
		maze.getMap()[row][col] = 'X';
		
		moves.add(new Move(RIGHT, row, col));
	}
	
	@Override
	public MazeGame clone() {
		
		return new MazeGame.Builder()
			.setRow(row)
			.setCol(col)
			.setEnd(end)
			.setMaze(this.maze.clone())
			.setMoves(moves)
			.build();
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < maze.getWidth() + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		for (int i = 0; i < maze.getHeight(); i++) {
			
			builder.append("|");
			for (int j = 0; j < maze.getWidth(); j++) {
				builder.append(maze.getMap()[i][j]);
			}
			builder.append("|\n");
		}
		
		for (int i = 0; i < maze.getWidth() + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		builder.append(moves.stream().map(p -> p.toString()).collect(Collectors.joining(", ")));
		
		builder.append("\n");
		
		builder.append(moves.size() + " moves");
		
		return builder.toString();
	}
	
	private void allowed(int action) {
		
		if (!maze.getFreedom(row, col).contains(action))
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
		
		@Override
		public boolean equals(Object position) {
			return same((Move) position);
		}
	
		@Override
		public int hashCode() {
			return this.row * 100 + this.col;
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

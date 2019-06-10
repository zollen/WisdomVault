package simulations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.nd4j.linalg.primitives.Pair;

public class TicTacTocExercise {
	

	private static final char PLAYER_ONE = '\u2660';
	private static final char PLAYER_TWO = '\u2665';
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TicTacToc game = new TicTacToc(PLAYER_ONE, PLAYER_TWO);
		
		char player = PLAYER_ONE;
		
		while (!game.over()) {
	
			game.eval(player);
			System.out.println(game.toString(true));
			player = game.other(player);
		}
	
	}
	


	public static class Move extends Position {

		private static final long serialVersionUID = 1L;

		public Move(char player, Integer x, Integer y) {
			super(player, x, y);
			// TODO Auto-generated constructor stub
		}	
		
		public String toString() {
			return "Player[" + player + "] applied move(" + 
							this.getX() + ", " + this.getY() + ")"; 
		}
	}

	public static class Position extends Pair<Integer, Integer> {
	
		private static final long serialVersionUID = 1L;
		
		protected char player;

		public Position (char player, Integer x, Integer y) {
			super(x, y);
			this.player = player;
		}
		
		public char getPlayer() {
			return player;
		}
		
		public int getX() {
			return getFirst();
		}
		
		public int getY() {
			return getSecond();
		}
		
		public String toString() {
			return "Player [" + player + "] position at (" + 
							this.getX() + ", " + this.getY() + ")"; 
		}
	}
	
	public static class TicTacToc {
		
		private static final int WIDTH = 3;
		private static final int HEIGHT = 3;
		private static final int MAX_DEPTH = 3;
		private static final int WIN_CONDITION = 3;
		private static final int WIN_THRESHOLD = (int) Math.pow(WIN_CONDITION, WIN_CONDITION);
		
		
		
		private char [][] board = null;
		private char [] players = null;
		private List<Move> history = null;
		
		public TicTacToc(char player1, char player2) {
			this.board = new char[WIDTH][HEIGHT];
			this.history = new ArrayList<Move>();
			
			this.players = new char[2];
			
			this.players[0] = player1;
			this.players[1] = player2;
		}
		
		public TicTacToc(TicTacToc original) {
			this.board = new char[WIDTH][HEIGHT];
			this.history = new ArrayList<Move>();
			
			for (int i = 0; i < WIDTH; i++) {
				for (int j = 0; j < HEIGHT; j++) {
					this.board[i][j] = original.board[i][j];
				}
			}
			
			this.players = original.players;
		}
		
		public char other(char player) {
			
			if (player == this.players[0])
				return this.players[1];
			else
				return this.players[0];
		}
		
		public void apply(Move move) {
			board[move.getX()][move.getY()] = move.getPlayer();
			history.add(move);
		}
		
		public boolean over() {
						
			int count = 0;
			
			for (int i = 0; i < WIDTH; i++) {
				
				for (int j = 0; j < HEIGHT; j++) {
					
					if (board[i][j] == 0)
						count++;
					
					boolean won1 = newReferee(players[0], i, j).check().parallelStream().anyMatch(p -> p.won());
					boolean won2 = newReferee(players[1], i, j).check().parallelStream().anyMatch(p -> p.won());
							
					if (won1 || won2)
						return true;
				}
			}
			
			if (count > 0)
				return false;
			
			return true;
		}
		
		public int eval(char player) {
			return eval(player, 0);
		}
		
		public int eval(char player, int depth) {
			
			if (depth >= MAX_DEPTH) {				
				return 0;
			}
			
			
			int maxScore = Integer.MIN_VALUE;
			Move bestMove = null;
			
			for (int i = 0; i < WIDTH; i++) {	
				
				for (int j = 0; j < HEIGHT; j++) {
					
					if (board[i][j] == 0) {
						
						TicTacToc cloned = new TicTacToc(this);
						
						Move nextMove = new Move(player, i, j);
						
						cloned.apply(nextMove);
							
						int score1 = cloned.newReferee(player, i, j).check()
										.stream().mapToInt(p -> p.score()).sum();
						
						int score2 = 0;
						
						if (score1 < WIN_THRESHOLD)
							score2 = cloned.eval(other(player), depth + 1) * -1;
						
						
						int score = score1 + score2;
					
						
						if (score >= maxScore) {
							maxScore = score;
							bestMove = nextMove;
						}
					}
				}
			}
			
			
			if (bestMove != null) {
				
				apply(bestMove);
				
				return maxScore;
			}
			
			return 0;
		}
		
		public String toString() {
			return toString(false);
		}
		
		public Referee newReferee(char player, int i, int j) {
			return new Referee(player, i, j);
		}
		
		public String toString(boolean withMoves) {
			
			StringBuilder builder = new StringBuilder();
			
			StringBuilder boarder = new StringBuilder();
			for (int i = 0; i < WIDTH; i++)
				boarder.append("----");
			boarder.append("-\n");
			
			for (int j = 0; j < HEIGHT; j++) {
				
				builder.append(boarder.toString());
				
				for (int i = 0; i < WIDTH; i++) {
					
					builder.append("|  " + board[i][j]);			
				}
				
				builder.append("|\n");				
			}
			
			builder.append(boarder.toString());
			builder.append("\n");
			
			AtomicInteger index = new AtomicInteger(1);
			if (withMoves)
				history.stream().forEach(p -> builder.append(index.getAndIncrement() + ". " + p + "\n")); 
			
			return builder.toString();
		}
		
		public class Range extends HashSet<Position> {
			
			private static final long serialVersionUID = 1L;
			
			private char player;
			
			public Range(char player) {
				this.player = player;
			}
			
			public int score() {
				
				int winning = 0;
				
				for (Position position : this) {
					if (board[position.getX()][position.getY()] == player) {
						winning++;
					}
				}
								
				return (int) Math.pow(winning, winning);
			}
			
			public boolean won() {
				
				AtomicInteger count = new AtomicInteger(0);
				
				this.stream().forEach(p -> {
					
					if (board[p.getX()][p.getY()] == player)
						count.incrementAndGet();
				});
				
				if (count.get() == WIN_CONDITION)
					return true;
				
				return false;
			}
		}
		
		public class Referee {
			
			private char player;
			private int x;
			private int y;
			
			public Referee(char player, int x, int y) {
				this.player = player;
				this.x = x;
				this.y = y;
			}
			
			public Set<Range> check() {
				
				Set<Range> bag = new HashSet<Range>();
				
				int width = board.length;
				int height = board[0].length;
				
				if (x - 2 >= 0) {
					Range range = new Range(player);
					range.add(new Position(player, x - 2, y));
					range.add(new Position(player, x - 1, y));
					range.add(new Position(player, x, y));
					bag.add(range);
				}
				
				if (x - 1 >= 0 && x + 1 < width) {
					Range range = new Range(player);
					range.add(new Position(player, x - 1, y));
					range.add(new Position(player, x, y));
					range.add(new Position(player, x + 1, y));
					bag.add(range);
					
				}
				
				if (x + 2 < width) {
					Range range = new Range(player);
					range.add(new Position(player, x, y));
					range.add(new Position(player, x + 1, y));
					range.add(new Position(player, x + 2, y));
					bag.add(range);
				}
				
				if (y - 2 >= 0) {
					Range range = new Range(player);
					range.add(new Position(player, x, y - 2));
					range.add(new Position(player, x, y - 1));
					range.add(new Position(player, x, y));
					bag.add(range);
				}
				
				if (y - 1 >= 0 && y + 1 < height) {
					Range range = new Range(player);
					range.add(new Position(player, x, y - 1));
					range.add(new Position(player, x, y));
					range.add(new Position(player, x, y + 1));
					bag.add(range);
				}
				
				if (y + 2 < height) {
					Range range = new Range(player);
					range.add(new Position(player, x, y));
					range.add(new Position(player, x, y + 1));
					range.add(new Position(player, x, y + 2));
					bag.add(range);
				}
				
				if (x - 2 >= 0 && y - 2 >= 0) {
					Range range = new Range(player);
					range.add(new Position(player, x - 2, y - 2));
					range.add(new Position(player, x - 1, y - 1));
					range.add(new Position(player, x, y));
					bag.add(range);
				}
				
				if (x - 1 >= 0 && y - 1 >= 0 &&
						x + 1 < width && y + 1 < height) {
					Range range = new Range(player);
					range.add(new Position(player, x - 1, y - 1));
					range.add(new Position(player, x, y + 1));
					range.add(new Position(player, x + 1, y + 1));
					bag.add(range);
				}
				
				if (x + 2 < width && y + 2 < height) {
					Range range = new Range(player);
					range.add(new Position(player, x, y));
					range.add(new Position(player, x + 1, y + 1));
					range.add(new Position(player, x + 2, y + 2));
					bag.add(range);
				}
				
				if (x + 2 < width && y - 2 >= 0) {
					Range range = new Range(player);
					range.add(new Position(player, x + 2, y - 2));
					range.add(new Position(player, x + 1, y - 1));
					range.add(new Position(player, x, y));
					bag.add(range);
				}
				
				if (x + 1 < width && y - 1 >= 0 &&
						x - 1 >= 0 && y + 1 < height) {
					Range range = new Range(player);
					range.add(new Position(player, x + 1, y - 1));
					range.add(new Position(player, x, y));
					range.add(new Position(player, x - 1, y + 1));
					bag.add(range);
				}
				
				if (x + 2 < width && y - 2 >= 0) {
					Range range = new Range(player);
					range.add(new Position(player, x + 2, y - 2));
					range.add(new Position(player, x + 1, y - 1));
					range.add(new Position(player, x, y));
					bag.add(range);
				}
								
				return bag;
			}
		}
		
	}
	
}

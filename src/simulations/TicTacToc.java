package simulations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.nd4j.linalg.primitives.Pair;

public class TicTacToc {
	
	private static final char PLAYER_ONE = '\u2660';
	private static final char PLAYER_TWO = '\u2665';
	
	private static final List<Move> bestMoves = new ArrayList<Move>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Board tictactoc = new Board();
	
		make(tictactoc, new Move(PLAYER_ONE, 1, 1));
		
		make(tictactoc, new Move(PLAYER_TWO, 0, 0));
		
		make(tictactoc, new Move(PLAYER_ONE, 2, 0));
		
		make(tictactoc, new Move(PLAYER_TWO, 0, 2));
		
		make(tictactoc, new Move(PLAYER_ONE, 0, 1));
				
		for (int i = bestMoves.size(); i < 9; i++) {
			
			char player;
			if (i % 2 == 0)
				player = PLAYER_ONE;
			else
				player = PLAYER_TWO;
			
			tictactoc.eval(player, 0);
			tictactoc.apply(bestMoves.get(i));
			System.out.println(tictactoc.toString(true));
		}
	
	}
	
	public static void make(Board board, Move move) {
		
		bestMoves.add(move);
		board.apply(bestMoves.get(bestMoves.size() - 1));
		System.out.println(board.toString(true));
	}

	

	public static class Move extends Pair<Integer, Integer> {
	
		private static final long serialVersionUID = 1L;
		
		private char player;

		public Move (char player, Integer x, Integer y) {
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
			return "Player[" + player + "] applied move(" + 
							this.getX() + ", " + this.getY() + ")"; 
		}
	}
	
	public static class Board {
		
		private static final int DEPTH_LIMIT = 3;
		
		private char [][] board = null;
		private List<Move> history = null;
		
		public Board() {
			this.board = new char[3][3];
			this.history = new ArrayList<Move>();
		}
		
		public Board(Board original) {
			this.board = new char[3][3];
			this.history = new ArrayList<Move>();
			
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					this.board[i][j] = original.board[i][j];
				}
			}
		}
		
		public void apply(Move move) {
			board[move.getX()][move.getY()] = move.getPlayer();
			history.add(move);
		}
		
		public int maximizer(char player, int depth) {
			
			Collection<Move> moves = availables(player);
			Move bestMove = null;
			int bestScore = Integer.MIN_VALUE;
			
			for (Move move : moves) {
				
				Board cloned = new Board(this);
				
				cloned.apply(move);
				
				int score = cloned.eval(player, depth + 1);
				if (score >= bestScore) {
					bestMove = move;
					bestScore = score;
				}
			}
			
			if (depth == 0 && bestMove != null) {
				bestMoves.add(bestMove);
			}
				
			return bestScore;
		}
		
		public int mimimizer(char player, int depth) {
			
			Collection<Move> moves = availables(player);
			Move bestMove = null;
			int bestScore = Integer.MAX_VALUE;
			
			for (Move move : moves) {
				
				Board cloned = new Board(this);
				
				cloned.apply(move);
				
				int score = cloned.eval(player, depth + 1);
				if (score <= bestScore) {
					bestMove = move;
					bestScore = score;
				}
			}
			
			if (depth == 0 && bestMove != null) {
				bestMoves.add(bestMove);
			}
				
			return bestScore;
		}
		
		public int eval(char player, int depth) {
			
			if (depth >= DEPTH_LIMIT) 
				return heuristic(player);
			
			if (depth % 2 != 0) {
				
				if (player == PLAYER_ONE)
					return mimimizer(PLAYER_TWO, depth);
				else
					return maximizer(PLAYER_ONE, depth);
			}
			else {
				
				if (player == PLAYER_ONE)
					return maximizer(PLAYER_ONE, depth);
				else	
					return mimimizer(PLAYER_TWO, depth);
			}
		}
		
		public int heuristic(char player) {
			
			for (int i = 0; i < 3; i++) {
				if (board[i][0] != 0 && board[i][0] == board[i][1] && 
						board[i][1] == board[i][2]) {
					if (player == board[0][0])
						return 100;
					else
						return -100;
				}
			}
			
			for (int j = 0; j < 3; j++) {
				if (board[0][j] != 0 && board[0][j] == board[1][j] && 
						board[1][j] == board[2][j]) {
					if (player == board[0][0])
						return 100;
					else
						return -100;
				}
			}
			
			if (board[0][0] != 0 && board[0][0] == board[1][1] && 
					board[1][1] == board[2][2]) {
				if (player == board[0][0])
					return 100;
				else
					return -100;
			}
			
			if (board[0][2] != 0 && board[0][2] == board[1][1] && 
					board[1][1] == board[2][0]) {
				if (player == board[0][2])
					return 100;
				else
					return -100;
			}
			
			return 0;
		}
		
		public Collection<Move> availables(char player) {
			
			Set<Move> all = new HashSet<Move>();
			Set<Move> losses = new HashSet<Move>();
			Set<Move> wins = new HashSet<Move>();
			
			for (int i = 0; i < 3; i++) {
				
				for (int j = 0; j < 3; j++) {
					
					if (board[i][j] == 0) {
						
						all.add(new Move(player, i, j));
						
						if (wins(player, i, j))
							wins.add(new Move(player, i, j));
						
						if (loss(player, i, j))
							losses.add(new Move(player, i, j));
					}
				}
			}
			
			if (wins.size() > 0)
				return wins;
			
			if (losses.size() > 0)
				return losses;
			
			
			return all;
		}
		
		public boolean wins(char player, int x, int y) {
			
			{
				int win = 0;
				for (int i = 0; i < 3; i++) {
					
					if (board[x][i] == player) {
						win++;
					}
				}
	
				if (win == 2)
					return true;
			}
			
			{
				int win = 0;
				for (int i = 0; i < 3; i++) {
					
					if (board[i][y] == player) {
						win++;
					}
				}
				
				if (win == 2)
					return true;
			}
			
			{
				int win = 0;
				if (x == y) {
					for (int i = 0; i < 3; i++) {
						if (board[i][i] == player) {
							win++;
						}
					}
				}
				
				if (win == 2)
					return true;
			}
			
			{
				int win = 0;
				if ((x == 1 && y == 1) ||
						(x == 0 && y == 2) ||
						(x == 2 && y == 0)) {
					for (int i = 0; i < 3; i++) {
						if (board[2 - i][i] == player) {
							win++;
						}
					}
				}
				
				if (win == 2)
					return true;
			}
			
			return false;
		}
		
		public boolean loss(char player, int x, int y) {
			
			{
				int loss = 0;	
				for (int i = 0; i < 3; i++) {
					
					if (board[x][i] != 0 && board[x][i] != player) {
						loss++;
					}
				}
				
				if (loss == 2)
					return true;
			}
			
			{
				int loss = 0;
				for (int i = 0; i < 3; i++) {
										
					if (board[i][y] != 0 && board[i][y] != player) {
						loss++;
					}
				}
				
				if (loss == 2)
					return true;
			}
			
			{
				int loss = 0;
				if (x == y) {
					for (int i = 0; i < 3; i++) {
						if (board[i][i] != 0 && board[i][i] != player) {
							loss++;
						}
					}
				}
				
				if (loss == 2)
					return true;
			}
			
			{
				int loss = 0;
				if ((x == 1 && y == 1) ||
						(x == 0 && y == 2) ||
						(x == 2 && y == 0)) {
					for (int i = 0; i < 3; i++) {
						if (board[2 - i][i] != 0 && board[2 - i][i] != player) {
							loss++;
						}
					}
				}
				
				if (loss == 2)
					return true;
			}
			
			return false;
		}
		
		public String toString() {
			return toString(false);
		}
		
		public String toString(boolean withMoves) {
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < 3; i++) {
				
				builder.append("-----------\n");
				
				for (int j = 0; j < 3; j++) {
					
					if (j > 0)
						builder.append("| ");
					
					builder.append(" " + board[i][j]);
				}

				builder.append("|\n");
			}
			
			builder.append("-----------\n");
			
			AtomicInteger index = new AtomicInteger(1);
			if (withMoves)
				history.stream().forEach(p -> builder.append(index.getAndIncrement() + ". " + p + "\n")); 
			
			return builder.toString();
		}
		
	}

}

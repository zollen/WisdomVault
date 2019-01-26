import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.ejml.data.DMatrixRMaj;

public class AcesUpPoker2 {
	
	public static final int CARD_2 = 2;
	public static final int CARD_3 = 3;
	public static final int CARD_4 = 4;
	public static final int CARD_5 = 5;
	public static final int CARD_6 = 6;
	public static final int CARD_7 = 7;
	public static final int CARD_8 = 8;
	public static final int CARD_9 = 9;
	public static final int CARD_10 = 10;
	public static final int CARD_J = 11;
	public static final int CARD_Q = 12;
	public static final int CARD_K = 13;
	public static final int CARD_A = 14;
	
	public static final int CARD_SPADE = 1;
	public static final int CARD_HEART = 2;
	public static final int CARD_DIAMOND = 3;
	public static final int CARD_CLUB = 4;
	
	private List<Integer> deck = new ArrayList<Integer>();
	private int turn = 1;
	private int discards = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int count = 0;
		double total = 0d;
		for (int i = 0; i < 1000; i++) {
			
			AcesUpPoker2 poker = new AcesUpPoker2();
		
			DMatrixRMaj M = new DMatrixRMaj(20, 4);
		
			poker.begin(M);
			
			if (poker.win(M))
				count++;
			
			total += poker.discards();
		}
		
		System.out.println("TOTAL WINS: " + count + " +/- Error games out of 1000 games");
		System.out.println("AVERAGE DISCARDS PER GAME: " + (double) total / 1000 + "+/- Error");
	}
	
	public AcesUpPoker2() {
		prepare();
	}
	
	public void begin(DMatrixRMaj M) {
		
		while (deck.size() > 0 && !win(M)) {
			
			draw(M);
			
			System.out.println("= [Turn " + (turn) + " Draw] =======");
			
			print(M);
			
			while (move(M));
		
			System.out.println("= [Turn " + (turn) + " Move] =======");
			
			print(M);
			
			turn++;
		}
		
		System.out.println("GAME OVER");
	}
	
	public void prepare() {
		
		Random rand = new Random(System.nanoTime());
		
		for (int i = 1; i <= 4; i++) {
			for (int j = 2; j <= 14; j++) {
				deck.add(i * 100 + j);
			}
		}
		
		for (int i = 0; i < 52; i++) {
			int pos1 = rand.nextInt(52);
			int pos2 = rand.nextInt(52);
			
			Collections.swap(deck, pos1, pos2);
		}
	}
	
	public void draw(DMatrixRMaj M) {
		
		for (int i = 0; i < M.numCols; i++) {
			if (deck.size() > 0) {
				push(M, i, deck.remove(0));
			}
		}
	}
	
	public boolean move(DMatrixRMaj M) {
		
		boolean moved = false;
		
		
		DMatrixRMaj N = _move(M);
		
		if (count(M) > count(N)) {
			copy(N, M);
			moved = true;
		}
		
		return moved;	
	}
	
	public void print(DMatrixRMaj M) {
		
		StringBuilder builder = new StringBuilder();
		
		for (int row = 0; row < M.numRows; row++) {	
			
			boolean empty = true;
			for (int col = 0; col < M.numCols; col++) {
				if (M.get(row, col) != 0) {
					empty = false;
					break;
				}
			}
			
			if (!empty) {
				builder.append("|  ");
			}
			
            for (int col = 0; !empty && col < M.numCols; col++) {     
            	int val = (int) M.get(row, col);
                builder.append(val != 0 ? card(val) : "     ");
            }
            
            if (!empty) {
            	builder.append("|");
            	builder.append("\n");
            }
        }
		
		System.out.println(builder.toString());
	}
	
	public void push(DMatrixRMaj M, int column, int val) {
		
		for (int i = 0; i < M.numRows; i++) {
			
			if (M.get(i, column) == 0) {
				M.set(i, column, val);
				break;
			}	
		}
	}
	
	public int pop(DMatrixRMaj M, int column) {
		
		int row = M.numRows - 1;
		while (row >= 0 && M.get(row, column) == 0) {
			row--;
		}
		
		if (row >= 0) {
			int val = (int) M.get(row, column);
			M.set(row, column, 0d);		
			return val;
		}
		
		return 0;
		
	}
	
	public boolean discard(DMatrixRMaj M) {
		
		boolean removed = false;
		
		for (int i = 0; i < M.numCols; i++) {
			
			int right = top(M, i);
			
			for (int j = 0; j < M.numCols; j++) {
				
				if (i != j) {
						
					int left = top(M, j);
						
					if (suit(left, right) && right > left) {
						pop(M, j);
						discards++;
						removed = true;
					}				
				}
			}
		}
		
		return removed;
	}
	
	public boolean win(DMatrixRMaj M) {
		
		for (int i = 0;i < M.numCols; i++) {
			if (count(M, i) != 1 || num((int) M.get(0, i)) != CARD_A) {
				return false;
			}
		}
		
		return true;
	}
	
	public int discards() {
		return discards;
	}
	
	private DMatrixRMaj _move(DMatrixRMaj M) {
		
		List<Integer> empties = new ArrayList<Integer>();
		
		while(discard(M));
		
		for (int i = 0; i < M.numCols; i++) {
			if (empty(M, i)) {
				empties.add(i);
			}
		}
		
		if (empties.size() <= 0)
			return M;
		
		List<DMatrixRMaj> list = new ArrayList<DMatrixRMaj>();
		for (int i = 0; i < M.numCols; i++) {
			if (count(M, i) >= 2) {
				DMatrixRMaj N = M.copy();
				int val = pop(N, i);
				push(N, empties.get(0), val);
				list.add(_move(N));
			}
		}
		
		int max = 9999;
		DMatrixRMaj chosen = null;
		for (DMatrixRMaj N : list) {
			int weight = count(N);
			if (weight < max) {
				max = weight;
				chosen = N;
			}
		}
		
		if (chosen != null) {
			return chosen;
		}
		
		return M;
	}
	
	private void copy(DMatrixRMaj src, DMatrixRMaj dest) {
		
		for (int i = 0; i < src.numRows; i++) {
			for (int j = 0; j < src.numCols; j++) {
				dest.set(i, j, src.get(i, j));
			}
		}
	}
	
	private int count(DMatrixRMaj M, int column) {
		
		int count = 0;
		
		for (int i = 0; i < M.numRows; i++) {
			
			if (M.get(i, column) != 0)
				count++;
		}
		
		return count;
	}
	
	private int count(DMatrixRMaj M) {
		
		int count = 0;
		for (int i = 0; i < M.numRows; i++) {
			for (int j = 0; j < M.numCols; j++) {
				if (M.get(i, j) != 0) {
					count++;
				}
			}
		}
		
		return count;
	}
	
	private boolean empty(DMatrixRMaj M, int column) {
		
		for (int i = 0; i < M.numRows; i++) {
			if (M.get(i, column) != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	private String card(int n) {
		
		final String [] NUMBER = { " 2  ", " 3  ", " 4  ", " 5  ", " 6  ", " 7  ", " 8  ", " 9  ",
							"10  ", " J  ", " Q  ", " K  ", " A  " };
		
		int suit = (int) Math.floor(n / 100);
		int num = n % 100;
		
		
		return String.format("%4s", suit(suit) + NUMBER[(num - 2)]);
	}
	
	private String suit(int i) {

		StringBuilder builder = new StringBuilder();

		switch (i) {
		case CARD_SPADE:
			builder.append((char) '\u2660');
			break;
		case CARD_DIAMOND:
			builder.append((char) '\u2666');
			break;
		case CARD_CLUB:
			builder.append((char) '\u2663');
			break;
		case CARD_HEART:
			builder.append((char) '\u2665');
			break;
		}

		return builder.toString();

	}
	
	private int top(DMatrixRMaj M, int column) {
		
		int row = M.numRows - 1;
		while (row >= 0 && M.get(row, column) == 0) {
			row--;
		}
		
		if (row >= 0)
			return (int) M.get(row, column);
		
		return 0;
	}
	
	private boolean suit(int left, int right) {
		
		if (Math.floor(left / 100) == Math.floor(right / 100))
			return true;
		
		return false;
	}
	
	private int num(int card) {
		
		return card % 100;
	}

}

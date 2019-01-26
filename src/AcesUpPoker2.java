import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DMatrixRMaj M = new DMatrixRMaj(20, 4);
		
		M.set(0, 0, card(CARD_HEART, CARD_7)); 
		M.set(0, 1, card(CARD_SPADE, CARD_8)); 
		M.set(0, 2, card(CARD_CLUB, CARD_A));
		M.set(0, 3, card(CARD_DIAMOND, CARD_K));
		
		M.set(1, 0, card(CARD_SPADE, CARD_10)); 
		M.set(1, 1, card(CARD_SPADE, CARD_6)); 
		M.set(1, 2, card(CARD_HEART, CARD_4));
		M.set(1, 3, card(CARD_CLUB, CARD_Q));
	
		move(M);
		
		print(M);
	}
	
	public static boolean move(DMatrixRMaj M) {
		
		boolean moved = false;
		
		
		DMatrixRMaj N = _move(M);
		
		if (count(M) > count(N)) {
			copy(N, M);
			moved = true;
		}
		
		return moved;	
	}
	
	public static DMatrixRMaj _move(DMatrixRMaj M) {
		
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
	
	public static void copy(DMatrixRMaj src, DMatrixRMaj dest) {
		
		for (int i = 0; i < src.numRows; i++) {
			for (int j = 0; j < src.numCols; j++) {
				dest.set(i, j, src.get(i, j));
			}
		}
	}
	
	public static int count(DMatrixRMaj M, int column) {
		
		int count = 0;
		
		for (int i = 0; i < M.numRows; i++) {
			
			if (M.get(i, column) != 0)
				count++;
		}
		
		return count;
	}
	
	public static int count(DMatrixRMaj M) {
		
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
	
	public static boolean empty(DMatrixRMaj M, int column) {
		
		for (int i = 0; i < M.numRows; i++) {
			if (M.get(i, column) != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void print(DMatrixRMaj M) {
		
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
	
	public static int card(int suit, int num) {
		return suit * 100 + num;
	}
	
	public static String card(int n) {
			
		final String [] NUMBER = { " 2  ", " 3  ", " 4  ", " 5  ", " 6  ", " 7  ", " 8  ", " 9  ",
							"10  ", " J  ", " Q  ", " K  ", " A  " };
		
		int suit = (int) Math.floor(n / 100);
		int num = n % 100;
		
		
		return String.format("%4s", suit(suit) + NUMBER[(num - 2)]);
	}
	
	public static String suit(int i) {

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
	
	public static int top(DMatrixRMaj M, int column) {
		
		int row = M.numRows - 1;
		while (row >= 0 && M.get(row, column) == 0) {
			row--;
		}
		
		if (row >= 0)
			return (int) M.get(row, column);
		
		return 0;
	}
	
	public static boolean suit(int left, int right) {
		
		if (Math.floor(left / 100) == Math.floor(right / 100))
			return true;
		
		return false;
	}
	
	public static void push(DMatrixRMaj M, int column, int val) {
		
		for (int i = 0; i < M.numRows; i++) {
			
			if (M.get(i, column) == 0) {
				M.set(i, column, val);
				break;
			}	
		}
	}
	
	public static int pop(DMatrixRMaj M, int column) {
		
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
	
	public static boolean discard(DMatrixRMaj M) {
		
		boolean removed = false;
		
		for (int i = 0; i < M.numCols; i++) {
			
			int right = top(M, i);
			
			for (int j = 0; j < M.numCols; j++) {
				
				if (i != j) {
						
					int left = top(M, j);
						
					if (suit(left, right) && right > left) {
						pop(M, j);
						removed = true;
					}				
				}
			}
		}
		
		return removed;
	}

}

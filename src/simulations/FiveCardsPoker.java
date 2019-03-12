package simulations;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.ejml.data.DMatrixRMaj;

public class FiveCardsPoker {
	
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int won = 0;
		for (int i = 1; i <= 1000; i++) {
			
			System.out.println("= Game " + i + " =============");
			
			FiveCardsPoker poker = new FiveCardsPoker();
		
			DMatrixRMaj players = poker.setup(4, 3);
		
			poker.print(players);
		
			poker.begin(players);
		
			boolean res = poker.win(players);
			if (res) {
				System.out.println("I WON!!!!!!!!!!!\n");
				won++;
			}
			else {
				System.out.println("I LOST!!!!!!!!!!\n");
			}
		}

		System.out.println("TOTAL WINS: " + won + "+/-error games out of 1000 games");
		
	}
	
	public FiveCardsPoker() {
		prepare();
	}
	
	public DMatrixRMaj setup(int N, int K) {
		
		DMatrixRMaj players = new DMatrixRMaj(5, N);
		
		for (int player = 0; player < players.numCols; player++) {
			
			int cards = K;
			if (player == 0)
				cards = 5;
			
			draw(players, player, cards);
		}
		
		return players;
	}
	
	public void begin(DMatrixRMaj players) {
		
		
		for (int player = 0; player < players.numCols; player++) {
			int cards = count(players, player);
			if (cards < 5) {
				draw(players, player, 5 - cards);
			}
		}
			
		System.out.println("= [Draw] =======");
			
		print(players);
		
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
	
	public void draw(DMatrixRMaj M, int player, int numOfCards) {
		
		for (int j = 0; j < numOfCards && deck.size() > 0; j++) {
			push(M, player, deck.remove(0));
		}
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
	
	public boolean win(DMatrixRMaj M) {
		
		int max = -1;
		int winner = -1;
		for (int i = 0; i < M.numCols; i++) {
			
			int score1 = this.pairs(M, i);
			int score2 = this.straight(M, i);
			int score3 = this.flush(M, i);
			
			int score = score1 + score2 + score3;
			
			String name = "STEPHEN";
			if (i > 0)
				name = "PLAYER" + (i + 1);
			
			if (score == 0)
				score = this.highCard(M, i);
			
			System.out.println(name + " ====> PAIRS: " + score1 + ", STRAIGHT: " + score2 + 
					", FLUSH: " + score3 + ", TOTAL: " + score);
			
			if (max < score) {
				max = score;
				winner = i;
			}
		}
		
		if (winner == 0)
			return true;
		
		return false;
	}
		
	private int count(DMatrixRMaj M, int column) {
		
		int count = 0;
		
		for (int i = 0; i < M.numRows; i++) {
			
			if (M.get(i, column) != 0)
				count++;
		}
		
		return count;
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
	
	private int highCard(DMatrixRMaj M, int player) {
		
		int max = -1;
		for (int i = 0; i < M.numRows; i++) {
			if (max < M.get(i, player) % 100) {
				max = (int) M.get(i, player) % 100;
			}
		}
		
		return max;
	}
	
	private int pairs(DMatrixRMaj M, int player) {
		
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < M.numRows; i++) {
			
			int key = (int) M.get(i, player) % 100;
			Integer val = map.get(key);
			if (val == null) {
				map.put(key, 1);
			}
			else {
				map.put(key,  val + 1);
			}
		}
		
		int score = 0;
		
		Iterator<Integer> itr = map.keySet().iterator();
		while (itr.hasNext()) {
			
			Integer key = itr.next();
			Integer count = map.get(key);
			
			if (count == 2) {
				score += key + 500;
			}
			else 
			if (count == 3) {
				score += key + 1500;
			}
			else 
			if (count == 4) {
				score += key + 10000;
			}	
		}
		
		return score;
	}
	
	private int straight(DMatrixRMaj M, int player) {
		
		List<Integer> list = new ArrayList<Integer>();
		
		for (int i = 0; i < M.numRows; i++) {
			list.add((int) M.get(i,  player) % 100);
		}
		
		Collections.sort(list);
		
		int last = -1;
		int score = 0;
		boolean test = true;
		for (Integer card : list) {
			int num = card % 100;
			score += num;
			if (num - 1 == last || last == -1)
				last = num;
			else {
				test = false;
			}
		}
		
		if (!test)
			return 0;
		
		return score + 3000;
	}
	
	private int flush(DMatrixRMaj M, int player) {
		
		int suit = (int) Math.floor(M.get(0, player) / 100);
		int score = 0;
		boolean test = true;
		
		for (int i = 1; i < M.numRows; i++) {
			int s = (int) Math.floor(M.get(i, player) / 100);
			int num = (int) M.get(i, player) % 100;
			score += num;
			if (suit != s) {
				test = false;
			}	
		}
		
		if (!test)
			return 0;
		
		return score + 4000;
	}
}

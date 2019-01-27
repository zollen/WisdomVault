import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AcesUpPoker {
	
	
	private final List<Card> deck = new ArrayList<Card>();
	
	private final List<List<Card>> pies = new ArrayList<List<Card>>();	
	
	private String name = null;
	
	private int turn = 1;
	
	private int discards = 0;
	
	public static void main(String [] args) {
		
		int count = 0;
		double total = 0d;
		for (int i = 1; i <= 1000; i++) {
		
			AcesUpPoker poker = new AcesUpPoker(String.valueOf(i));
		
			poker.begin();
			
			if (poker.win())
				count++;
			
			total += poker.discards();
		}
		
		System.out.println("TOTAL WINS: " + count + "+/-error games out of 1000 games");
		System.out.println("AVERAGE DISCARDS PER GAME: " + (double) total / 1000 + "+/-error");
	}
	
	public AcesUpPoker(String name) {
		
		this.name = name;
		
		prepare();
		
		shuffle();
		
		setup();
	}
	
	public void debug(List<List<Card>> samples) {
		
		int index = 0;
		for (List<Card> sample : samples) {
			
			List<Card> pie = pies.get(index++);
			
			pie.clear();
			
			pie.addAll(sample);
		}
		
		
		print("Initial");
		
		while (discard() == true);
		
		print("Dicard");
		
		move();
		
		print("Move");
		
	}
	
	public void begin() {
		
		System.out.println("GAME [" + name + "] BEGIN");

		while (deck.size() > 0 && !win()) {
			
			draw();
			
			print("Draw four cards");
			
			while (discard() == true || move() == true);
			
			print("Discard and Move");
			
			turn++;
		}
		
		System.out.println("GAME [" + name + "] OVER");
		
	}
	
	public boolean move() {
		
		//     suit,  weight
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		boolean moved = false;
		
		int lindex = 0;
		for (List<Card> left : pies) {
			
			map.put(lindex, 0);
			
			if (left.size() >= 2) {
				Card primary = left.get(left.size() - 1);
				Card second = left.get(left.size() - 2);
				
				map.put(lindex, primary.getNum());
				
				int rindex = 0;
				for (List<Card> right : pies) {
					if (right.size() > 0) {
						
						Card candiate = null;
						if (lindex != rindex)
							candiate = right.get(right.size() - 1);
						else
							candiate = primary;
						
						if (candiate.getSuit() == second.getSuit()) {
							Integer weight = map.get(lindex);
							if (candiate.getNum() >= second.getNum()) {
								map.put(lindex, weight + candiate.getSuit() * 100);
							}
							else {
								map.put(lindex, weight + second.getSuit() * 100);
							}
						}
					}
					
					rindex++;
				}				
			}
			
			lindex++;
		}
		
		for (List<Card> pie : pies) {
			
			if (pie.size() <= 0) {
				
				int max = 0;
				int indd = -1;
				
				for (Integer ind : map.keySet()) {
					Integer num = map.get(ind);
					if (num > max) {
						max = num;
						indd = ind;
					}
				}
				
				
				if (indd >= 0 && pies.get(indd).size() >= 2) {
					Card last = pies.get(indd).remove(pies.get(indd).size() - 1);
					pie.add(last);
					
					moved = true;
				}
			}	
		}
		
		return moved;
	}
	
	public void print(String title) {
		
		System.out.println("=============== " + title + "[" + turn + "] =================");
		
		for (int i = 0;i < pies.size(); i++) {
			
			List<Card> pie = pies.get(i);
			
			for (Card card : pie) {
				System.out.println("Game[" + name + "] Turn[" + turn + "]:    Pie[" + 
							(i + 1) + "] ==> " + card);
			}
			
			if (pie.size() <= 0) {
				System.out.println("Game[" + name + "] Turn[" + turn + "]:    Pie[" + 
							(i + 1) + "] ==> EMPTY");
			}
		}	
	}
	
	public boolean discard() {
		Map<Integer, Integer> biggest = new HashMap<Integer, Integer>();
		boolean removed = false;
		
		// look for the biggest number of each suit in all available pies
		for (List<Card> pie : pies) {
			
			if (pie.size() > 0) {
				Card card = pie.get(pie.size() - 1);
				Integer big = biggest.get(card.getSuit());
				if (big == null) {
					biggest.put(card.getSuit(), card.getNum());
				}
				else {
					if (card.compareTo(big) > 0) {
						biggest.put(card.getSuit(), card.getNum());
					}
				}
			}
		}
		
		// put only the biggest number card in each suit back to the pies
		for (List<Card> pie : pies) {
			
			if (pie.size() > 0) {		
				Card card = pie.get(pie.size() - 1);
				Integer big = biggest.get(card.getSuit());
				if (big != null && card.getNum() < big) {
					pie.remove(card);
					discards++;
					removed = true;
				}
			}
		}
		
		return removed;
	}
	
	public int discards() {
		return discards;
	}
	
	public void draw() {
		
		for (List<Card> pie : pies) {
			
			if (deck.size() > 0)
				pie.add(deck.remove(0));
			
		}
	}
	
	public void setup() {
		
		List<Card> pie1 = new ArrayList<Card>();
		List<Card> pie2 = new ArrayList<Card>();
		List<Card> pie3 = new ArrayList<Card>();
		List<Card> pie4 = new ArrayList<Card>();
		
		pies.add(pie1); pies.add(pie2); pies.add(pie3); pies.add(pie4);
	}
	
	public void prepare() {
		
		for (int i = 0; i < Card.SUITS.length; i++) {
			
			for (int j = 0; j < Card.NUMBER.length; j++) {
				
				deck.add(new Card(i, j));
			}
		}
	}
	
	public void shuffle() {
	
		Random rand = new Random(System.nanoTime());
		
		for (int i = 0; i < Card.NUMBER.length * Card.SUITS.length; i++) {
			int pos1 = rand.nextInt(Card.NUMBER.length * Card.SUITS.length);
			int pos2 = rand.nextInt(Card.NUMBER.length * Card.SUITS.length);
			
			Collections.swap(deck, pos1, pos2);
		}
	}
	
	public boolean win() {
		
		for (List<Card> pie : pies) {
			if (pie.size() != 1 || pie.get(0).getNum() != Card.CARD_A) {
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("unused")
	private static class Card {
		
		public static final int CARD_2 = 0;
		public static final int CARD_3 = 1;
		public static final int CARD_4 = 2;
		public static final int CARD_5 = 3;
		public static final int CARD_6 = 4;
		public static final int CARD_7 = 5;
		public static final int CARD_8 = 6;
		public static final int CARD_9 = 7;
		public static final int CARD_10 = 8;
		public static final int CARD_J = 9;
		public static final int CARD_Q = 10;
		public static final int CARD_K = 11;
		public static final int CARD_A = 12;
		public static final int CARD_SPADE = 0;
		public static final int CARD_HEART = 1;
		public static final int CARD_DIAMOND = 2;
		public static final int CARD_CLUB = 3;
		
		public static final String [] SUITS = { "SPADE", "HEART", "DIAMOND", "CLUB"};
		
		public static final String [] NUMBER = { "2", "3", "4", "5", "6", "7", "8", "9",
							"10", "J", "Q", "K", "A" };
		
		private int suit;
		private int num;
		
		public Card(int suit, int num) {
			this.suit = suit;
			this.num = num;
		}
		
		public int getSuit() {
			return suit;
		}

		public void setSuit(int suit) {
			this.suit = suit;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}
		
		public int compareTo(Integer num) {
			return Integer.valueOf(this.getNum()).compareTo(num);
		}
		
		public int compareTo(Card card) {
			return Integer.valueOf(this.getNum()).compareTo(card.getNum());
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + num;
			result = prime * result + suit;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Card other = (Card) obj;
			if (num != other.num)
				return false;
			if (suit != other.suit)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "Card [suit=" + SUITS[suit] + ", num=" + NUMBER[num] + "]";
		}
		
	}

}

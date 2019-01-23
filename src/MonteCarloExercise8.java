import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MonteCarloExercise8 {
	
	
	private static final List<Card> deck = new ArrayList<Card>();
	
	private static final List<Set<Card>> pies = new ArrayList<Set<Card>>();	
	
	public static void main(String [] args) {
		
		prepareDeck();
		shuffleDeck();
		
		setupPies();
		
		pies.get(0).add(deck.remove(0));
		pies.get(1).add(deck.remove(0));
		pies.get(2).add(deck.remove(0));
		pies.get(3).add(deck.remove(0));
		
		printPies();
		
		discardLowerCards();
		
		System.out.println("Pie[1] has Spaces: " + hasPieSpace(pies.get(0)));
		System.out.println("Pie[2] has Spaces: " + hasPieSpace(pies.get(1)));
		System.out.println("Pie[3] has Spaces: " + hasPieSpace(pies.get(2)));
		System.out.println("Pie[4] has Spaces: " + hasPieSpace(pies.get(3)));
		
		printPies();
		
		System.out.println("GAME OVER");
		
	}
	
	public static void printPies() {
		
		for (int i = 0;i < pies.size(); i++) {
			
			Set<Card> pie = pies.get(i);
			
			for (Card card : pie) {
				System.out.println("Pie[" + (i + 1) + "] ==> " + card);
			}
			
			if (pie.size() <= 0) {
				System.out.println("Pie[" + (i + 1) + "] ==> EMPTY");
			}
		}
		
		System.out.println("================================");
		
	}
	
	public static void discardLowerCards() {
		Map<Integer, Integer> biggest = new HashMap<Integer, Integer>();
		
		// look for the biggest number of each suit in all available pies
		for (Set<Card> pie : pies) {
			
			for (Card card : pie) {
				
				Integer num = biggest.get(card.getSuit());
				if (num == null) {
					biggest.put(card.getSuit(), card.getNum());
				}
				else {
					if (card.compareTo(num) > 0) {
						biggest.put(card.getSuit(), card.getNum());
					}
				}
			}		
		}
		
		// put only the biggest number card in each suit back to the pies
		for (int i = 0; i < pies.size(); i++) {
			
			Set<Card> buffer = new LinkedHashSet<Card>();
			Set<Card> pie = pies.set(i, buffer);
			
			for (Card card : pie) {
				
				Integer num = biggest.get(card.getSuit());
				
				if (card.getNum() == num) {
					buffer.add(card);
				}
			}
		}
	}
	
	public static boolean hasPieSpace(Set<Card> pie) {
		
		return pie.size() <= 0 ? true : false;
	}
	
	public static List<Card> draw4Cards() {
		
		List<Card> cards = new ArrayList<Card>();
		
		for (int i = 0; i < 4 && deck.size() > 0; i++)
			cards.add(deck.remove(0));
		
		return cards;
	}
	
	public static void setupPies() {
		
		Set<Card> pie1 = new LinkedHashSet<Card>();
		Set<Card> pie2 = new LinkedHashSet<Card>();
		Set<Card> pie3 = new LinkedHashSet<Card>();
		Set<Card> pie4 = new LinkedHashSet<Card>();
		
		pies.add(pie1); pies.add(pie2); pies.add(pie3); pies.add(pie4);
	}
	
	public static void prepareDeck() {
		
		for (int i = 0; i < Card.SUITS.length; i++) {
			
			for (int j = 0; j < Card.NUMBER.length; j++) {
				
				deck.add(new Card(i, j));
			}
		}
	}
	
	public static void shuffleDeck() {
	
		Random rand = new Random(System.nanoTime());
		
		for (int i = 0; i < Card.NUMBER.length * Card.SUITS.length; i++) {
			int pos1 = rand.nextInt(Card.NUMBER.length * Card.SUITS.length);
			int pos2 = rand.nextInt(Card.NUMBER.length * Card.SUITS.length);
			
			Collections.swap(deck, pos1, pos2);
		}
	}
	
	public static class Card {
		
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
			return new Integer(this.getNum()).compareTo(num);
		}
		
		public int compareTo(Card card) {
			return new Integer(this.getNum()).compareTo(card.getNum());
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

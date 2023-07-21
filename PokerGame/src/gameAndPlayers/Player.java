package gameAndPlayers;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

public class Player {
	int chips;
	ArrayList<Pair<Integer,Integer>> cards;
	
	public Player(int chips, ArrayList<Pair<Integer,Integer>> cards) {
		this.chips = chips;
		this.cards = cards;
	}
	
	public void betChips(int amount) {
		chips -= amount;
	}
	
	public void hold() {
		
	}
	
	public void loseChips(int amount) {
		chips -= amount;
	}
	
	public void winChips(int amount) {
		chips += amount;
	}
	
	public int getChips() {
		return chips;
	}
	
	public ArrayList<Pair<Integer,Integer>> getCards() {
		return cards;
	}
	
	public void drawCards(ArrayList<Pair<Integer,Integer>> newCards) {
		cards = newCards;
	}
	
	// Used for determining moves as a robot player makes a move.
	public int decide(Pair<Integer,Integer> bestHand, int totalCards) {
		int left = bestHand.getLeft(); 		// Hand value
		int right = bestHand.getRight();	// High card in hand

		// DRAW
		if(totalCards == 2) {
			if(left == 1 && right >= 10) {
				// High card >= 10
				return randomPlay(50,50) ? 50 : -1;
			} 
			if(left == 2) {
				if(right < 10) {
					// Pair < 10
					return randomPlay(60,40) ? randomAmount(50,100) : -1;
				} else {
					// Pair >= 10
					return randomPlay(80,20) ? randomAmount(50,200) : -1;
				}
			}
		}
		
		// FLOP
		if(totalCards == 5) {
			if(left == 2 && right >= 10) {
				// Pair >= 10
				return randomPlay(80,20) ? randomAmount(50,100) : -1;
			}
			if(left == 3 || left == 4) {
				// 2 Pair or 3 of a kind
				return randomPlay(90,10) ? randomAmount(50,300) : -1;
			}
			if(left >= 4) {
				// Straight or better
				return randomPlay(50,50) ? randomAmount(50,500) : -1;
			}
		}
		
		
		// TURN
		if(totalCards == 6) {
			if(left == 2) {
				// Any pair
				return randomPlay(50,50) ? randomAmount(50,100) : -1;
			}
			if(left == 3 || left == 4) {
				// 2 Pair or 3 of a kind
				return randomPlay(50,50) ? randomAmount(100,200) : -1;
			}
			if(left >= 4) {
				// Straight or higher
				return randomPlay(80,20) ? randomAmount(200,400) : -1;
			}
		}
		
		// RIVER
		if(totalCards == 7) {
			// Any pair
			if(left == 2) {
				return randomPlay(30,70) ? randomAmount(50,500) : -1;
			}
			// 2 pair or 3 of a kind
			if(left == 3 || left == 4) {
				return randomPlay(50,50) ? randomAmount(50,500) : -1;
			}
			// Straight and up
			if(left >= 4) {
				return randomPlay(10,90) ? getChips() : randomAmount(50,400); 
			}
		}
		
		return -1;
	}
	
	// If another player calls all in. If straight or greater 70% chance of all int, Pair/2Pair/3ofKind have 30% chance of all in.
	public boolean allInCall(Pair<Integer,Integer> bestHand) {
		if(bestHand.getLeft() >= 2) {
			if(bestHand.getLeft() >= 4) {
				return randomPlay(70,30) ? true : false;
			}
			return randomPlay(30,70) ? true: false;
			
		} else return false;
	}

	public int randomAmount(int low, int high) {
        Random random = new Random();
        int randomNumber = random.nextInt(high - low + 1) + low;
        return randomNumber;
	}
	
	public boolean randomPlay(int trueOdds, int falseOdds) {
        Random random = new Random();
        int randomNumber = random.nextInt(100);
        return randomNumber < trueOdds;
	}
}


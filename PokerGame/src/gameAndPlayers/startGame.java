package gameAndPlayers;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import cardLogic.*; 

public class startGame {
	private int numberOfPlayers;
	private ArrayList<Player> players;
	private Stack<Pair<Integer,Integer>> deck;
	private ArrayList<Pair<Integer, Integer>> cardsInPlay;
	private NewDeck deckRef;
	private int potSize;
	private int roundPotSize;
	private int turnIndex;
	
	public startGame(int numberOfPlayers, ArrayList<Player> players) {
		this.numberOfPlayers = numberOfPlayers;
		this.players = players;
		this.potSize = 0;
		this.roundPotSize = 0;
		this.turnIndex = 0;
		cardsInPlay = new ArrayList<>(7);
	}

	public boolean start() {
		cardsInPlay = new ArrayList<>(7);
		Scanner stdin = new Scanner(System.in);
		deckRef = new NewDeck();
		deck = deckRef.create();
		
		for(Player p: players) p.setInOrOut(true);					// Make everyone in.
		for(Player p: players) p.setCards(deck.pop(), deck.pop()); 	// Give cards
		cardsInPlay.set(0,Pair.of(1,1));							// Initialize cardsInPlay with dummy cards
		cardsInPlay.set(1,Pair.of(1,1));							
		
		
		
		while(players.get(0).getChips() >= 0 && numberOfPlayers != 1) {	
			// Begin Round - New Deal - Until one person remains.
			// Each iteration of the loop should change whoIsIn or influence pot size.
			while(whoIsIn() > 1) {	
				// No cards dealt.
				if(cardsInPlay.get(2).getLeft() == 0) { 
					goAround();
					cardsInPlay.set(2, deck.pop());
					cardsInPlay.set(3, deck.pop());
					cardsInPlay.set(4, deck.pop());
					
				// 3 cards flipped.
				} else if(cardsInPlay.get(5).getLeft() == 0) { 
					goAround();
					cardsInPlay.set(5, deck.pop());
					
				// 4 cards flipped.
				} else if(cardsInPlay.get(6).getLeft() == 0) { 
					goAround();
					cardsInPlay.set(6, deck.pop());
					
				// 5 cards flipped.
				} else if(cardsInPlay.get(6).getLeft() != 0) { 
					goAround();
					
					// Should exit loop since no more cards will be dealt, therefore no need for setting cards in cardsInPlay array.
				} else {
				    System.err.println("Issue Drawing Cards.");
				    throw new RuntimeException("Unexpected state reached! Check the conditions.");
				}
				/*System.out.println("eliminate all?");
				String s = stdin.next();
				for(int i = 0; i < players.size()-1; i++) {
					players.get(i).setInOrOut(false);
				}*/
				
			}
			// End Round - Reset
			// New start with incremented turnIndex.
			turnIndex = (turnIndex == players.size()-1) ? 0 : turnIndex++;
			// Payout winner.
			for(Player p: players) if(p.inOrOut == true) p.winChips(potSize);
			// Remove players out.
			for(Player p: players) {
				if(p.getChips() <= 0) players.remove(p);
			}
			// Determine if game needs to continue based off main player chip count and players left
			if(players.get(0).getChips() <= 0) return false;
			if(players.size() == 1) return true;
			// Reset Pot
			potSize = 0;
			// Everyone back in.
			for(Player p: players) p.setInOrOut(true);
			// Collect cards to shuffle (cardsInPlay (0,0) )
			for (int i = 2; i < cardsInPlay.size(); i++) {
			    cardsInPlay.set(i, Pair.of(0, 0));
			}
			// Shuffle deck.
			deck = deckRef.create();
			// Re-deal cards
			for(Player p: players) {
				p.setCards(deck.pop(), deck.pop());
			}
			
			System.out.println("next round?");
			String continueGame = stdin.next();
		}
	
		return players.get(0).getChips() > 0 ? true : false;															
	}
	
	
	// Changes inOrOut or changes pot.
	public void goAround() {
		EvaluateHands calc = new EvaluateHands();
		// Go around once. 
		for(int i = 0; i < players.size(); i++) {
			if(turnIndex == 0) { // Main player turn.
				//PLAYER MOVE
				turnIndex = (turnIndex == players.size()-1) ? 0 : turnIndex++;
				
			} else { // Robot turn , Infer turn may be on someone that is out for the round, proceed to skip.
				// Get Robot Cards
				ArrayList<Pair<Integer,Integer>> botCards = players.get(turnIndex).getCards();
				cardsInPlay.set(0, botCards.get(0));
				cardsInPlay.set(1, botCards.get(1));
				
				// Find best hand and number of cards used.
				Pair<Integer,Integer> bestHand = calc.findHand(cardsInPlay);
				int numberOfCardsInPlay = 0;
				for(Pair<Integer,Integer> p : cardsInPlay) if(p.getLeft() != 0) numberOfCardsInPlay++;
				
				// Input found information to get chip amount decicion.
				int decision = players.get(turnIndex).decide(bestHand, numberOfCardsInPlay);
				
				// Fold
				if(decision == -1 || decision < roundPotSize || players.get(i).getChips() < roundPotSize) { //Redundancy
					players.get(i).setInOrOut(false);
				// Bet
				} else {
					roundPotSize = decision;
					players.get(i).betChips(decision);
				}
				turnIndex = (turnIndex == players.size()-1) ? 0 : turnIndex++;
			}
		}
	}
	
	
	
	public int whoIsIn() {
		int playerCnt = 0;
		for(Player p : players) if(p.inOrOut) playerCnt++;	
		return playerCnt;
	}
	
	public void printCards() {
		String set1 = "You have an ";
		String set2 = "The cards dealth are an ";
		ArrayList<Pair<Integer,Integer>> cardSet = players.get(0).getCards();
		cardsInPlay.set(0, cardSet.get(0));
		cardsInPlay.set(1, cardSet.get(1));
		

		for(int j = 0; j < cardsInPlay.size(); j++) {
			set1 += valuesOfNumbers(cardsInPlay.get(j).getLeft(), cardsInPlay.get(j).getRight());
		}

		for(int j = 0; j < cardsInPlay.size(); j++) {
			set2 += valuesOfNumbers(cardsInPlay.get(j).getLeft(), cardsInPlay.get(j).getRight());
		}

		if(cardsInPlay.get(2).getLeft() == 0) {
			System.out.println("No other cards have been dealt yet");
		} else {
			System.out.println(set2);
		}
		System.out.println(set1);
	}
	
	public String valuesOfNumbers(int num, int suit) {
		String set = "";
		if(num < 11) {
			set += num;
		} else {
			switch(num) {
			case 11: set += "Jack";
				break;
			case 12: set += "Queen";
				break;
			case 13: set += "King";
				break;
			case 14: set += "Ace";
				break;
			}
		}
		
		switch(suit) {
			case 1: set += " of Clubs";
				break;
			case 2: set += " of Diamonds";
				break;
			case 3: set += " of Hearts";
				break;
			case 4: set += " of Spades";
		}
		return set;
	}
}

package gameAndPlayers;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import cardLogic.*; 

public class startGame {
	private int numberOfPlayers;
	private ArrayList<Player> players;
	private int gameStep; 
	private Stack<Pair<Integer,Integer>> deck;
	private ArrayList<Pair<Integer, Integer>> cardsInPlay;
	private NewDeck deckRef;
	private int turnIndex;
	private int potSize;
	private int roundBet;
	
	public startGame(int numberOfPlayers, ArrayList<Player> players) {
		this.numberOfPlayers = numberOfPlayers;
		this.players = players;
		this.gameStep = 0;
		this.turnIndex = 0;
		this.potSize = 0;
		this.roundBet = 0;
		cardsInPlay = new ArrayList<>(7);
	}

	public boolean start() {
		cardsInPlay = new ArrayList<>(7);
		EvaluateHands calc = new EvaluateHands();
		Scanner stdin = new Scanner(System.in);
		deckRef = new NewDeck();	
		for(int i = 0; i < 7; i++) {															// Fill cards with blanks
	        cardsInPlay.add(Pair.of(0,0));
		}
		return true; 																			// Win
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
	// Add pot chips to winner and remove players with no chips left
	public void endRound(Player winner) {
		winner.winChips(potSize);
		for(Player p: players) if(p.getChips() <= 0) players.remove(p);
	}
	
	public int getGameStep(int gameStep) { 
		// DETERMINE GAME STEP, (DRAW -> FLOP -> TURN -> RIVER) or (0 -> 1 -> 2 -> 3)
		if(gameStep == 0) {
			Pair<Integer,Integer> zeros = Pair.of(0,0);			// Remove last round cards
			for(int i = 2; i < 7; i++) cardsInPlay.add(i,zeros);
			deck = deckRef.create();							// Shuffle Cards
			
			for(Player p: players) {
				p.setCards(deck.pop(),deck.pop());				// Give two cards to each player
			}
			gameStep++;
			
		} else if(gameStep == 1) {
			cardsInPlay.add(2, deck.pop());						//Draw 3 cards for the flop
			cardsInPlay.add(3, deck.pop());
			cardsInPlay.add(4, deck.pop());
			gameStep++;
			
		} else if(gameStep == 2) {
			cardsInPlay.add(5, deck.pop());						// Draw 1 card for the turn
			gameStep++;
			
		} else if(gameStep == 3) {	
			cardsInPlay.add(6, deck.pop());						// Draw 1 card for the river
			gameStep = 0;
		}
		
		return gameStep;
	}
	
	public int howManyCardsInPlay(ArrayList<Pair<Integer, Integer>> cardsInPlay) {
		int cardCnt = 0;
		for(Pair<Integer,Integer> card: cardsInPlay) {
			if(card.getLeft() == 0) {
				cardCnt++;
			}
		}
		return cardCnt;
	}
}

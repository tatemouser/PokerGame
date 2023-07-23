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
		for(int i = 0; i < 7; i++) {					// Fill cards with blanks
	        cardsInPlay.add(Pair.of(0,0));
		}
		
		// Once a player runs out, will stay false 
		boolean[] inOrOut = new boolean[players.size()];
		for(boolean i : inOrOut) i = true;
		
		while(players.size() > 1) {						// Til one player left.
			gameStep = getGameStep(gameStep);			// Determine cards in play for round around cirlce
			
			
			for(int i = 0; i < players.size(); i++) {	// Go around the circle
				if(inOrOut[i] == true) {				// False if player is out of chips or is out for round
					if(turnIndex != 0) {				// Robot move
						ArrayList<Pair<Integer,Integer>> temp = players.get(turnIndex).getCards();
						cardsInPlay.set(0, temp.get(0));
						cardsInPlay.set(1, temp.get(1));
						int cnt = 0;
						for(Pair<Integer,Integer> j : cardsInPlay) {
							if(j.getLeft() == 0) cnt++;
						}
						int robotBet = players.get(i).decide(calc.findHand(cardsInPlay),cnt); // What the robot is comfortable betting.
						
						if(roundBet <= robotBet) {
							roundBet += robotBet;
							potSize += robotBet;
						} else {
							inOrOut[i] = false;			// Robot folded
						}
					} else {
						int chipsLeft = players.get(0).getChips();
						System.out.println("Type fold or how much you would like to bet. You have " + chipsLeft + " chips left.");
						System.out.println("If you want to bet, you must bet, " + roundBet + " or more.");
						String choice = stdin.next();
						if(choice.equals("fold")) {
							inOrOut[0] = false;
						} else {
							roundBet += Integer.parseInt(choice);
							potSize += Integer.parseInt(choice);
						}
					}
				} 

				}
			}
			
		}
		
		
		
		
		return true; // Win
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

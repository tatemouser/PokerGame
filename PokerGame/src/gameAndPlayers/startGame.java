package gameAndPlayers;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

import cardLogic.*; 
// DO NOT ALLOW LOWER BETS THAN POT SIZE / MAIN PLAYER AND ROBOT
// ADD CHECKING CONSOLE INPUTS
// PRINT WIN/LOSE FOR ROUND
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
		EvaluateHands calc = new EvaluateHands();
		cardsInPlay = new ArrayList<>(7);
		Scanner stdin = new Scanner(System.in);
		deckRef = new NewDeck();
		deck = deckRef.create();
		
		for(Player p: players) p.setInOrOut(true);					// Make everyone in.
		for(Player p: players) p.setCards(deck.pop(), deck.pop()); 	// Give cards
		cardsInPlay.add(0,Pair.of(1,1));							// Initialize cardsInPlay with dummy cards
		cardsInPlay.add(1,Pair.of(1,1));							
		for(int i = 2; i < 7; i++) cardsInPlay.add(Pair.of(0,0));

		while(players.get(0).getChips() >= 0 && numberOfPlayers != 1) {	
			// Begin Round - New Deal - Until one person remains.
			// Each iteration of the loop should change whoIsIn or influence pot size.
			while(whoIsIn() > 1) {	
				// ADD ASKING TO MATCH OR FOLD.

				// No cards dealt.
				if(cardsInPlay.get(2).getLeft() == 0) { 
					goAround();
					players.get(0).setRoundBet(0);
					cardsInPlay.set(2, deck.pop());
					cardsInPlay.set(3, deck.pop());
					cardsInPlay.set(4, deck.pop());
					
				// 3 cards flipped.
				} else if(cardsInPlay.get(5).getLeft() == 0) { 
					goAround();
					players.get(0).setRoundBet(0);
					cardsInPlay.set(5, deck.pop());
					
				// 4 cards flipped.
				} else if(cardsInPlay.get(6).getLeft() == 0) { 
					goAround();
					players.get(0).setRoundBet(0);
					cardsInPlay.set(6, deck.pop());
					
				// 5 cards flipped.
				} else if(cardsInPlay.get(6).getLeft() != 0) { 
					goAround();
					players.get(0).setRoundBet(0);
					// Find winning hand since no more betting can be done.
					int winnerIndex = 0;
					Pair<Integer, Integer> winningHand = Pair.of(0,0);
					for(int j = 0; j < players.size(); j++) {
						ArrayList<Pair<Integer,Integer>> temp = players.get(j).getCards();
						cardsInPlay.set(0, temp.get(0));
						cardsInPlay.set(1, temp.get(1));
						Pair<Integer,Integer> playerHand = calc.findHand(cardsInPlay);
						if(playerHand.getLeft() > winningHand.getLeft()) {
							winningHand = playerHand;
							winnerIndex = j;
						} else if(playerHand.getLeft() == winningHand.getLeft() && playerHand.getRight() > winningHand.getRight()) {
							winningHand = playerHand;
							winnerIndex = j;
						}
					}
					// Make only winner still in.
					for(int j = 0; j < players.size(); j++) {
						if(j != winnerIndex) {
							players.get(j).setInOrOut(false);
						}
					}
				// Should exit loop since no more cards will be dealt, therefore no need for setting cards in cardsInPlay array.
				} else {
				    System.err.println("Issue Drawing Cards.");
				    throw new RuntimeException("Unexpected state reached! Check the conditions.");
				}
				
				// Get count of players still in, this is used to multiply pot size since players must have met wager to still be in.
				System.out.println("jsdnakjsad");
				int cnt = 0;
				for(Player p: players) if(p.inOrOut()) cnt++;
				potSize += roundPotSize * cnt;
				roundPotSize = 0;
				/*System.out.println("eliminate all?");
				String s = stdin.next();
				for(int i = 0; i < players.size()-1; i++) {
					players.get(i).setInOrOut(false);
				}*/
				
			}
			// End Round - Reset
			// New start with incremented turnIndex.
			if(turnIndex == players.size()-1) {
				turnIndex = 0;
			} else turnIndex++;
			// Payout winner.
			for(Player p: players) if(p.inOrOut == true) { 
				p.winChips(potSize);
			}
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
		
		stdin.close();
		return players.get(0).getChips() > 0 ? true : false;	
	}
	
	
	// Changes inOrOut or changes pot.
	public void goAround() {
		Scanner stdin = new Scanner(System.in);
		EvaluateHands calc = new EvaluateHands();
		// Go around once. 
		for(int i = 0; i < players.size(); i++) {
			gameDevCardView();
			if(turnIndex == 0) { // Main player turn.
				printCards();
				// ADD CHECK USER INPUT FOR VALID INPUT
				String decision = "";
				System.out.println("The pot right now is " + roundPotSize + ". You have " + players.get(i).getChips() + " chips left.");
				System.out.println("Type \"Fold\" or an amount you would like to bet.");
				decision = stdin.nextLine();
				
				if(decision.equals("Fold") || decision.equals("fold")) {
					players.get(i).setInOrOut(false);
					System.out.println("Nice Fold.");
				} else {
					int bet = Integer.parseInt(decision);
					if(players.get(i).getChips() >= bet) {
						players.get(i).betChips(bet);
						players.get(i).setRoundBet(bet);
						roundPotSize = bet;
						System.out.println("Nice Bet of " + bet + ".");
					}
				}
				
				if(turnIndex == players.size()-1) {
					turnIndex = 0;
				} else turnIndex++;
				
			} else if(players.get(turnIndex).inOrOut){ // Robot turn , Infer turn may be on someone that is out for the round, proceed to skip.
				
				System.out.println("Player " + (i+1) +" Turn");
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
				if((roundPotSize != 0) &&  decision == -1 || decision < roundPotSize || players.get(i).getChips() < roundPotSize) { //Redundancy
					players.get(i).setInOrOut(false);
					System.out.println("Player " + (i+1) + " has folded.");
				// Bet
				} else {
					roundPotSize = decision;
					// Check to see if other players would like to match the larger bet before continuing
					if(roundPotSize != 0) {
						players.get(i).betChips(decision);
						System.out.println("Player " + (i+1) + " has wagered " + decision + " chips. Setting the pot size to " + roundPotSize + " chips.");
						
						boolean correctInput = false;
						while(!correctInput) {
							System.out.println("To stay in, you must bet atleast " + decision + " Type yes to continue or no to fold.");
							String answer = stdin.nextLine();
							
							if(answer.equals("yes") || answer.equals("Yes")) {
								players.get(0).betChips(decision);
								players.get(0).winChips(players.get(0).getRoundBet());
								correctInput = true;
							} else if(answer.equals("no") || answer.equals("No")) {
								players.get(0).setInOrOut(false);
								correctInput = true;
							} 
						}
						// Initial bet, no need to ask.
					} else {
						roundPotSize = decision;
						players.get(i).betChips(decision);
						System.out.println("Player " + (i+1) + " has wagered " + decision + " chips. Setting the pot size to " + roundPotSize + " chips.");
					}
				}
				if(turnIndex == players.size()-1) {
					turnIndex = 0;
				} else turnIndex++;		
				
			} else {
				System.out.println("Skip player " + turnIndex);
				if(turnIndex == players.size()-1) {
					turnIndex = 0;
				} else turnIndex++;	
			}
			// Start and stop going around
			System.out.println("Type continue to go to the next player.");
			String val = stdin.nextLine();
		}
	}
	
	
	
	public int whoIsIn() {
		int playerCnt = 0;
		for(Player p : players) if(p.inOrOut) playerCnt++;	
		return playerCnt;
	}
	
	public void printCards() {
		String set1 = "You have an ";
		String set2 = "The cards dealt are a ";
		ArrayList<Pair<Integer,Integer>> cardSet = players.get(0).getCards();
		cardsInPlay.set(0, cardSet.get(0));
		cardsInPlay.set(1, cardSet.get(1));
		
		set1 += valuesOfNumbers(cardsInPlay.get(0).getLeft(), cardsInPlay.get(0).getRight());
		set1 += " and a " + valuesOfNumbers(cardsInPlay.get(1).getLeft(), cardsInPlay.get(1).getRight());
		
		
		for(int j = 2; j < cardsInPlay.size(); j++) {
			if(cardsInPlay.get(j).getLeft() != 0) {
				set2 += "(" + valuesOfNumbers(cardsInPlay.get(j).getLeft(), cardsInPlay.get(j).getRight()) + ")" + " ";
			}
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
	
	
	// Prints all game information
	public void gameDevCardView() {
        System.out.println("\n");
		
		// Print who is in or out.
		String playersIn = "";
		String playersInRound = "";
		String playersOutRound = "";
		for(int i = 0; i < players.size(); i++) playersIn += i + " ";
		for(int i = 0; i < players.size(); i++) {
			 if(players.get(i).inOrOut()) {
				 playersInRound += i + " ";
			 } else playersOutRound += i + " ";
		}
		
		System.out.println("Players in Game: " + playersIn + "\t" + "Player in for Round: " + playersInRound + "\t" + "Player out for Round: " + playersOutRound);
		
		// Print game status and cards dealt.
		if(cardsInPlay.get(2).getLeft() == 0) { 
			System.out.println("No cards have been dealt to the middle.");
			
		} else if(cardsInPlay.get(5).getLeft() == 0) { 
			String flop = "3 Cards have been dealt: ";
			for(int i = 2; i < cardsInPlay.size()-2; i++) flop += valuesOfNumbers(cardsInPlay.get(i).getLeft(), cardsInPlay.get(i).getRight()) + " ";
			
		} else if(cardsInPlay.get(6).getLeft() == 0) { 
			String turn = "4 Cards have been dealt: ";
			for(int i = 2; i < cardsInPlay.size()-1; i++) turn += valuesOfNumbers(cardsInPlay.get(i).getLeft(), cardsInPlay.get(i).getRight()) + " ";
			
		} else if(cardsInPlay.get(6).getLeft() != 0) {
			String river = "All 5 Cards have been dealt: ";
			for(int i = 2; i < cardsInPlay.size(); i++) river += valuesOfNumbers(cardsInPlay.get(i).getLeft(), cardsInPlay.get(i).getRight()) + " ";
			
		}
		
		// Print pot and round chips.
		System.out.println("Overall Pot: " + potSize + "\t" + "Round Pot: " + roundPotSize);
		
		// Print player information
		for(int i = 0; i < players.size(); i++) {
			String playerCards = "";
			if(players.get(i).inOrOut()) {
				
				playerCards += "Player " + i + ": CARDS - ";
				ArrayList<Pair<Integer,Integer>> temp = players.get(i).getCards();
				playerCards += valuesOfNumbers(temp.get(0).getLeft(), temp.get(0).getRight());
				playerCards += " and a " + valuesOfNumbers(temp.get(1).getLeft(), temp.get(1).getRight()) + "\t";
				
				playerCards += "CHIPS - " + players.get(i).getChips();
				System.out.println(playerCards);
			}
		}
		
        System.out.println("\n");

	}
}

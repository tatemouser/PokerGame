package main;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;

import gameAndPlayers.*;
import cardLogic.*;

public class Index {
	public static ArrayList<Player> players;
	
	public static void main(String[] args) {
		
		// Get number of players and create an ArrayList to hold the Player object's
		Scanner stdin = new Scanner(System.in);
		System.out.println("Please enter many people will be playing?");
		int numberOfPlayers = stdin.nextInt();
		
		players = new ArrayList<Player>(numberOfPlayers);
		
		// Assign and object for each player in the game.
		for(int i = 0; i < numberOfPlayers; i++) {
	        ArrayList<Pair<Integer, Integer>> empty = new ArrayList<>(List.of(Pair.of(0, 0), Pair.of(0, 0)));
			Player person = new Player(1000, empty);
			players.add(person);
		}
	
		startGame game = new startGame(numberOfPlayers, players);
		System.out.println(game.start() ? "Thanks for playing! Nice Win!" : "Better luck next time!");
		stdin.close();
		
		EvaluateHands cards = new EvaluateHands();
        Pair<Integer, Integer>[] card = new Pair[2];
        card[0] = Pair.of(2,1);
        card[1] = Pair.of(10,1);
        /*card[2] = Pair.of(1,2);
        card[3] = Pair.of(1,2);
        card[4] = Pair.of(7,3);
        card[5] = Pair.of(4,3);
        card[6] = Pair.of(13,3);*/
        
        
        /* 	cards.findHand() takes in 7 Pair<>'s. The first two being the players two cards and the other 5 being the drawn cards. 
        	It returns the highest hand value as the first int and the highest card number in the hand being the second.
         	High card 5 == (1,5)		Straight 6 7 8 9 10 == (5,10) */
        
		//System.out.println(cards.findHand(card)); 
	}
}

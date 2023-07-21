package main;

import java.util.AbstractMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import cardLogic.*;

public class Index {
	public static void main(String[] args) {
		// Creates 52 cards with two integers set as Pair<>. 2-14 representing 2-Ace and 1-4 representing clubs/diamonds/hearts/spades.
		NewDeck deck = new NewDeck();
		deck.create();
		
		
		
		EvaluateHands cards = new EvaluateHands();
        Pair<Integer, Integer>[] card = new Pair[7];
        card[0] = Pair.of(2,1);
        card[1] = Pair.of(2,1);
        card[2] = Pair.of(1,2);
        card[3] = Pair.of(1,2);
        card[4] = Pair.of(7,3);
        card[5] = Pair.of(4,3);
        card[6] = Pair.of(13,3);
        
        
        /* 	cards.findHand() takes in 7 Pair<>'s. The first two being the players two cards and the other 5 being the drawn cards. 
        	It returns the highest hand value as the first int and the highest card number in the hand being the second.
         	High card 5 == (1,5)		Straight 6 7 8 9 10 == (5,10) */
        
		System.out.println(cards.findHand(card)); 
		
	}
}

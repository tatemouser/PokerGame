package cardLogic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

// A card is represented as two numbers, 2-14 for duce through ace and 1-4 representing clubs/diamonds/hearts/spades
public class NewDeck {	
	Stack<Pair<Integer,Integer>> stack;
	
	public Stack<Pair<Integer,Integer>> create() {
		stack = new Stack<>();
		add();
		
		// Swap and shuffle cards
		List<Pair<Integer,Integer>> list = stack;
		Collections.shuffle(list);
		
		stack = new Stack<>();
		stack.addAll(list);
		
		return stack;
	}
	
	/*
	public ArrayList<Pair<Integer,Integer>> drawHand() {
		ArrayList<Pair<Integer,Integer>> cards = new ArrayList<Pair<Integer,Integer>>(); 
		Pair<Integer,Integer> pair1 = stack.pop();
		Pair<Integer,Integer> pair2 = stack.pop();
		cards.add(pair1);
		cards.add(pair2);
		return cards;
	}	
	
	public Pair<Integer,Integer> drawOne() {
		Pair<Integer,Integer> card = stack.pop();
		return card;
	}	
	*/
	
	public void add() {
		for(int i = 1; i < 5; i++) {
			for(int j = 2; j < 15; j++) {
				stack.push(Pair.of(j,i));
			}
		}
	}
}



package cardLogic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;

//Pair data type

public class NewDeck {	
	Stack<Pair<Integer,Integer>> stack;
	
	public void create() {
		stack = new Stack<>();
		add();
		
		// Swap and shuffle cards
		List<Pair<Integer,Integer>> list = stack;
		Collections.shuffle(list);
		
		stack = new Stack<>();
		stack.addAll(list);
		
		for(int i = 0; i < 50; i++) {
			Pair<Integer,Integer> val = stack.get(i);
			System.out.println(val.getLeft() + " " + val.getRight());
		}
	}
	
	public int draw() {
		return -1;
	}	
	
	public void add() {
		for(int i = 1; i < 5; i++) {
			for(int j = 2; j < 15; j++) {
				stack.push(Pair.of(j,i));
			}
		}
	}
}



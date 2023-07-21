package cardLogic;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.Pair;

public class EvaluateHands {
	
	// Returns points of hand and number of hand. Ace High Card = (1,14) or Straight Top Card 10 = (5,10) 
	// First two Pair<>'s are players cards, helpful for determining high card.
	public Pair<Integer,Integer> findHand(Pair<Integer, Integer>[] p) {
		
		ArrayList<Integer> allNumbers = getNumbers(p);
		ArrayList<Integer> allSuits = getSuits(p);
		// The String holds hand name. If two players had a pair. The Integer parameter would be used to see which is the higher pair.
		ArrayList<Pair<String,Integer>> hands = new ArrayList<Pair<String,Integer>>();
		
		
		// Check for each type of hand, add any and all to "hands" array list. This will be cleaned and sorted later.
		
		// 4 OF A KIND / 3 OF A KIND / PAIR
		for(int num: allNumbers) {
			if(countOccurrences(allNumbers, num) == 4){ 
				hands.add(Pair.of("4 of a kind", num));
			} else if(countOccurrences(allNumbers, num) == 3) {
				hands.add(Pair.of("3 of a kind", num));
			} else if(countOccurrences(allNumbers, num) == 2) {
				hands.add(Pair.of("pair", num));
			}
		}
		// FLUSH
		for(int suit: allSuits) {
			if(countOccurrences(allSuits, suit) == 5) {
				int[] nums = new int[p.length];
				// Find highest card number within flush
				for(int i = 0; i < p.length; i++) {
					if(p[i].getRight() == suit) {
						nums[i] = p[i].getLeft();
					}
				}
				int largest = 0;
				for(int i: nums) {
					largest = i > largest ? i : largest;
				}
				hands.add(Pair.of("flush", largest)); 
			}
		}
		// STRAIGHT
		for(int num: allNumbers) {
			int cnt = 0;
			for(int i = num; i < num+5; i++) {
				if(allNumbers.contains(i)) {
					cnt++;
				}
			}
			if(cnt == 5) {
				hands.add(Pair.of("straight", num+4));
			}
		}
		// 2 PAIR
		if(countStringOccurrences(hands, "pair") >= 4) {
			hands.add(Pair.of("2 pair",findLargestPairNumber(hands,"pair")));
		}
		// FULL HOUSE
		if(countStringOccurrences(hands, "pair") == 2 && countStringOccurrences(hands, "3 of a kind") == 3) {
			hands.add(Pair.of("full house",findLargestPairNumber(hands,"3 of a kind")));
		}
		// STRAIGHT FLUSH
		if(findWord(hands, "straight") && findWord(hands, "flush")) {
			hands.add(Pair.of("straight flush", 0));
		}
		// ROYAL FLUSH
		if(findWord(hands, "straight flush") && allNumbers.contains(14) && allNumbers.contains(13) && allNumbers.contains(12)) {
			hands.add(Pair.of("royal flush",0));
		}
		
		
		
		// If there is a pair, this would trigger for both cards in the pair as it scans left to right. This removes the extra additions. 
		removeDuplicates(hands);

		// HIGH CARD
		if(hands.size() == 0) {
			Pair<String,Integer> val = allNumbers.get(0) > allNumbers.get(1) ? Pair.of("high card", allNumbers.get(0)) : Pair.of("high card", allNumbers.get(1)); 
			hands.add(val);
		}
		
		/* Prints all 7 cards and best hand
		for(Pair<String,Integer> i: hands) {
			System.out.println(i.getLeft() + " " + i.getRight());
		} 
		System.out.println(greatestHandPointValue(hands)); */

		// Now we have the best hand String along with the highest card. This converts the string name to a point value to be compared.
		return greatestHandPointValue(hands);
	}
	
	
	
	
    // Finds all numbers for 7 cards entered as parameter
	public ArrayList<Integer> getNumbers(Pair<Integer,Integer>[] p) {
		ArrayList<Integer> nums = new ArrayList<>();
		for(Pair<Integer,Integer> num: p) nums.add(num.getLeft());
		return nums;
	}
    // Finds all numbers for 7 suit's entered as parameter
	public ArrayList<Integer> getSuits(Pair<Integer,Integer>[] p) {
		ArrayList<Integer> nums = new ArrayList<>();
		for(Pair<Integer,Integer> num: p) nums.add(num.getRight());
		return nums;	
	}
	
	
	// Convert hand String to hand value
	public Pair<Integer,Integer> greatestHandPointValue(ArrayList<Pair<String,Integer>> hands) {
		// Stores point values for all hands gathered
		int[] handValues = new int[hands.size()];
		
		for(int i = 0; i < hands.size(); i++) { //Most amount of hands in a poker set? 
			switch(hands.get(i).getLeft()) {
				case "high card": handValues[i] = 1;
					break;
				case "pair": handValues[i] = 2;
					break;
				case "2 pair": handValues[i] = 3;
					break;
				case "3 of a kind": handValues[i] = 4;
					break;
				case "straight": handValues[i] = 5;
					break;
				case "flush": handValues[i] = 6;
					break;
				case "full house": handValues[i] = 7;
					break;
				case "4 of a kind": handValues[i] = 8;
					break;
				case "straight flush": handValues[i] = 9;
					break;
				case "royal flush": handValues[i] = 10;
					break;
			}
		}
		// Find the highest point for hand
		int handPointValue = 0;
		for(int num: handValues) handPointValue = num > handPointValue ? num : handPointValue;
		// Find the highest card number for said high hand. (This would be 10 in the straight 6 7 8 9 10)
		int cardNumber = 0;
		for(int i = 0; i < handValues.length; i++) {
			if(handValues[i] == handPointValue) { 
				cardNumber = hands.get(i).getRight() > cardNumber ? hands.get(i).getRight() : cardNumber;
			}
		}
				
		return Pair.of(handPointValue, cardNumber);
	}
	
	
	// Used for finding pairs / repeats in number list.
    public static int countOccurrences(ArrayList<Integer> list, int target) {
        int cnt = 0;
        for (int num : list) {
            if (num == target) {
                cnt++;
            }
        }
        return cnt;
    }  
    // Used for combining two hands / if two pairs were added to hand list, this would also add "two pair" to the list.
    public static int countStringOccurrences(ArrayList<Pair<String, Integer>> list, String target) {
        int cnt = 0;
        for (Pair<String, Integer> name : list) {
            if (name.getLeft() == target) {
                cnt++;
            }
        }
        return cnt;
    }
	
  
    // 7 cards may have "two pair", "pair", "pair", "straight". This would remove one of the "pair"s from the hands list.
    public static <T> void removeDuplicates(ArrayList<T> list) {
        HashSet<T> set = new HashSet<>();

        for (int i = 0; i < list.size(); i++) set.add(list.get(i));
        
        list.clear();
        list.addAll(set);
    }
    
    
    // A player may have 2,2,6,6. This would detemine that 6's should be returned for comparing to opponent.
    public static int findLargestPairNumber(ArrayList<Pair<String, Integer>> list, String word) {
    	int[] matches = new int[4];
    	
        for (int i = 0; i < 4; i++) {
        	Pair<String, Integer> pair = list.get(i);
            if (pair.getKey().equals(word)) {
                matches[i] = pair.getValue(); // Return the associated Integer
            }
        }
        
        int greatest = 0;
        for(int i : matches) {
        	greatest = i > greatest ? i : greatest;
        }
        return greatest;
    }
    
    
    // A player may have a "straight" and "flush" this would add "straight flush" to the hands list.
    public boolean findWord(ArrayList<Pair<String,Integer>> words, String target) {
    	for(Pair<String,Integer> word: words) {
    		if(word.getLeft().equals(target)) {
    			return true;
    		}
    	}
    	return false;
    }
}

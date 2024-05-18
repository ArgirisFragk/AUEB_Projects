package Missionaries_and_Cannibals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Stack;

class A_Star_Search {
	
	ArrayList<State> front;
	HashSet<State> closedSet;
	
	
	A_Star_Search() {
		this.front = new ArrayList<>();
		this.closedSet = new HashSet<>();
	}
	
	State A_Star(State initialState) { // h euretikh xrhsimopoieitai mesa sthn compare to gia kathe state
		if(initialState.isFinal()) return initialState;
		
		front.add(initialState);
		
		while(front.size() > 0) {
			
			State currentState = front.remove(0);
			
			if(currentState.getK() == 0) return currentState;
			
			if(currentState.isFinal()) return currentState;
			
			if(!closedSet.contains(currentState)) {
				
				closedSet.add(currentState);
				front.addAll(currentState.generateChilds());
				Collections.sort(front);
			}
			
		}
		return null;
	}
	
	void printPath(State finalState) {
		State currentState = finalState;
		Stack<State> printList = new Stack<>();
		printList.add(currentState);
		while(currentState.getFatherState() != null) {
			printList.add(currentState.getFatherState());
			currentState = currentState.getFatherState();
		}
		
		while(!printList.isEmpty()) {
			currentState = printList.pop();
			System.out.println(currentState);
		}
	}
}

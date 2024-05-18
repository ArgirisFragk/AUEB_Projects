package N_Queens_Puzzle;

import java.util.ArrayList;
import java.util.Collections;

class HillClimbing {
	
	private int boardDimension;
	
	HillClimbing(int boardDimension) {
		this.boardDimension = boardDimension;
	}
	
	Board HillClimbingRR(int n)
    {
        ArrayList<Board> bestStates = new ArrayList<>(); // list with solutions of every run
        for(int i = 0; i < n; i++)
        {
            Board initialState = new Board(this.boardDimension); // different initial
            Board s = this.HillClimbingSearch(initialState);
            bestStates.add(s);
        }
        return Collections.min(bestStates); // best of bests
    }
	
	Board HillClimbingSearch(Board initialState)
    {
        // step 1: make initial state be the current one.
        Board currentState = initialState;
        while(true)
        {
            // step 2: evaluate the children of the current
        	ArrayList<Board> children = new ArrayList<>();
        	for(int i = 0; i < this.boardDimension; i++) {
        		children.addAll(currentState.generateSuccesors(i));
        	}
            Board bestChild = Collections.min(children); // neighbor with best score
            // step 3: if we do not have a child that has a better score, return the current state
            if(bestChild.CountThreats() >= currentState.CountThreats()) // if we have worse score, return
            {
                return currentState;
            }
            // step 4: make the best scored child be the current state
            currentState = bestChild;
            // continue...
        }
    }
}

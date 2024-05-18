package Missionaries_and_Cannibals;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("Wrong Number of Arguments");
			return;
		}
		
		int N = Integer.parseInt(args[0]);
		int M = Integer.parseInt(args[1]);
		int K = Integer.parseInt(args[2]);//use it for g(n)
		
		State initialState = new State(N,N,0,0,true,M,K,null);
		initialState.initializeCost(K);
		A_Star_Search search = new A_Star_Search();
		long start = System.currentTimeMillis();
		search.printPath(search.A_Star(initialState));
		long end = System.currentTimeMillis();
		long timePassed = end - start;
		System.out.println("\nTime For A* To Execute: " + timePassed + " ms.");
	}

}

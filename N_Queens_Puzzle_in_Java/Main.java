package N_Queens_Puzzle;



public class Main {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Wrong Number of Arguments");
			return;
		}
		
		int N = Integer.parseInt(args[0]);
		
		Board b = new Board(N);
		
		HillClimbing search = new HillClimbing(b.getDimension());
		long start = System.currentTimeMillis();
		System.out.println(search.HillClimbingRR(10));
		long end = System.currentTimeMillis();
		long timePassed = end - start;
		System.out.println("\nTime For HillClimbingRR To Execute: " + timePassed + " ms.");
		
	}
}

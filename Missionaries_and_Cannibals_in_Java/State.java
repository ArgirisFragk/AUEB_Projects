package Missionaries_and_Cannibals;
import java.util.ArrayList;

class State implements Comparable<State>{
	
	private int LeftMissionaries; // iearapostoloi sthn aristerh oxthi
	private int RightMissionaries;// iearapostoloi sthn deksia oxthi
	private int LeftCannibals;// kanivaloi sthn aristerh oxthi
	private int RightCannibals;//kanivaloi sthn deksia oxthi
	private int MaximumBoatCapacity; // posa atoma xwrane sthn varka
	private int OnBoat;
	private boolean BoatLocation;//true = left, false = right
	private int K;
	private int Cost; //xrhsimopoieitai gia ton megisto arithmo forwn pou mporoume na perasoume sthn apenanti oxthi
	private State fatherState;
	private ArrayList<State> childStates;
	
	State() {
		
	}
	
	
	State(int LeftM, int LeftC, int RightM, int RightC, boolean BoatLoc, int BoatCap,int K,State fatherState) {
		setLeftMissionaries(LeftM);
		setLeftCannibals(LeftC);
		setRightMissionaries(RightM);
		setRightCannibals(RightC);
		setBoatLocation(BoatLoc);
		setMaximumBoatCapacity(BoatCap);
		setK(K);
		setFatherState(fatherState);
		childStates = new ArrayList<>();
	}

	void initializeCost(int Cost) {
		this.Cost = Cost;
	}


	int getLeftMissionaries() {
		return LeftMissionaries;
	}



	void setLeftMissionaries(int leftMissionaries) {
		LeftMissionaries = leftMissionaries;
	}



	int getRightMissionaries() {
		return RightMissionaries;
	}



	void setRightMissionaries(int rightMissionaries) {
		RightMissionaries = rightMissionaries;
	}



	int getLeftCannibals() {
		return LeftCannibals;
	}



	void setLeftCannibals(int leftCannibals) {
		LeftCannibals = leftCannibals;
	}



	int getRightCannibals() {
		return RightCannibals;
	}



	void setRightCannibals(int rightCannibals) {
		RightCannibals = rightCannibals;
	}



	int getMaximumBoatCapacity() {
		return MaximumBoatCapacity;
	}



	void setMaximumBoatCapacity(int maximumBoatCapacity) {
		MaximumBoatCapacity = maximumBoatCapacity;
	}



	int getOnBoat() {
		return OnBoat;
	}



	void setOnBoat(int onBoat) {
		OnBoat = onBoat;
	}



	int getK() {
		return K;
	}



	void setK(int k) {
		K = k;
	}
	
	boolean isFinal() {
		if (getLeftCannibals() == 0 && getLeftMissionaries() == 0) {
			return true; //profanws otan den yparxoun oute kannivaloi oute ierapostoloi sthn aristerh oxthi, tha einai oloi deksia ara 
		}					// ftasame se telikh katastash
		return false;
	}
	
	boolean isValid() {
		
		if(getLeftMissionaries() == 0 ||(getLeftCannibals() <= getLeftMissionaries() 
				&& getRightCannibals() <= getRightMissionaries()) || getRightMissionaries() == 0) {
			return true; //ginetai elenxos gia to an mia katastash einai egkyrh h oxi
							//diladi koitame an oi kannivaloi einai kathe fora ligoteroi se kathe oxthi
		}
		return false;
	}
	
	void TestTheState(State test,ArrayList<State> childStates) {
		if (test.isValid()) {			//an h katastash einai egkyrh thn prosthetoume sto arraylist twn katastasewn paidiwn
			childStates.add(test);
		}
	}
	
	ArrayList<State> generateChilds() { //dhmiourgoume tis katastaseis-paidia prwta apo thn aristerh oxthi kai meta apo thn deksia
		if (isBoatLocation()) {
			for (int i = 0; i <= getLeftMissionaries(); i++) {
				for (int j = 0; j <= getLeftCannibals(); j++) {
					if(i == 0 && j == 0) continue;
					if(i + j <= getMaximumBoatCapacity()) {
						TestTheState(new State(getLeftMissionaries()-i,getLeftCannibals() - j,
								getRightMissionaries() + i,getRightCannibals() + j
								,false,getMaximumBoatCapacity(),getK() - 1,new State(getLeftMissionaries(),getLeftCannibals(),//to K einai o arithmos pou meiwnoume gia na metrame to poses fores exoun menei gia diasxish
										getRightMissionaries(),getRightCannibals(),isBoatLocation(),
										getMaximumBoatCapacity(),getK(),getFatherState())),this.childStates);
					}
				}
			}
		}
		else {
			for (int i = 0; i <= getRightMissionaries(); i++) {
				for (int j = 0; j <= getRightCannibals(); j++) {
					if(i == 0 && j == 0) continue;
					if(i + j <= getMaximumBoatCapacity()) {
						TestTheState(new State(getLeftMissionaries()+i,getLeftCannibals() + j,
								getRightMissionaries() - i,getRightCannibals() - j
								,true,getMaximumBoatCapacity(),getK() - 1,new State(getLeftMissionaries(),getLeftCannibals(),
										getRightMissionaries(),getRightCannibals(),isBoatLocation(),
										getMaximumBoatCapacity(),getK(),getFatherState())),this.childStates);
					}
				}
			}
		}
		return this.childStates;
	}



	boolean isBoatLocation() {
		return BoatLocation;
	}



	void setBoatLocation(boolean boatLocation) {
		BoatLocation = boatLocation;
	}
	
	@Override
	public String toString() {
		if (isBoatLocation()) {
            return "(" + getLeftCannibals() + "," + getLeftMissionaries() + ",L,"
                    + getRightCannibals() + "," + getRightMissionaries() + ")";
        } else {
            return "(" + getLeftCannibals() + "," + getLeftMissionaries() + ",R,"
                    + getRightCannibals() + "," + getRightMissionaries() + ")";
        }
	}
	
	int Heuristic() { //euretikh sunarthsh an sta aristera den iparxei kaneis epistrefei 0
		if(getLeftCannibals() + getLeftMissionaries() == 0) return 0;
		
		if (!isBoatLocation()) { // an varka deksia kai aristerh oxthi >0 epestrepse 2*aristerh oxthi
			if(getLeftCannibals() + getLeftMissionaries() > 0) 
				return 2 * (getLeftCannibals() + getLeftMissionaries()); 
		}
		else {
			if(getLeftCannibals() + getLeftMissionaries() > 1) //an varka aristera kai aristerh oxthi >1 2*aristerh oxthi -3
				return 2 * (getLeftCannibals() + getLeftMissionaries()) - 3;
			else if(getLeftCannibals() + getLeftMissionaries() == 1)// an varka aristera kai aristerh oxthi =1 epestrepse 1 
				return 1;
		}
		return -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(getLeftCannibals() != ((State)obj).getLeftCannibals()) return false;
		if(getRightCannibals() != ((State)obj).getRightCannibals()) return false;
		if(isBoatLocation() != ((State)obj).isBoatLocation()) return false;
		if(getLeftMissionaries() != ((State)obj).getLeftMissionaries()) return false;
		if(getRightMissionaries() != ((State)obj).getRightMissionaries()) return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 21;
		
		hash = 69 * hash + getLeftCannibals();
		hash = 69 * hash + getRightCannibals();
		hash = 69 * hash + getLeftMissionaries();
		hash = 69 * hash + getRightMissionaries();
		
		return hash;
	}
	
	@Override
	public int compareTo(State other) { //sygkrinei me vash thn euretikh kai to poses diasxiseis exoun ginei 
		
		if((this.Heuristic() + this.Cost - this.getK()) > (other.Heuristic() + other.Cost  - other.getK())) 
		{
			return 1;
		}
		else if((this.Heuristic() + this.Cost - this.getK()) < (other.Heuristic() + other.Cost  - other.getK()))
		{
			return -1;
		}
		else
			return 0;
	}


	State getFatherState() {
		return fatherState;
	}


	void setFatherState(State fatherState) {
		this.fatherState = fatherState;
	}
	
}

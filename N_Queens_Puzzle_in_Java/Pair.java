package N_Queens_Puzzle;

class Pair { //mia dikh mas klash pair pou xrhsimopoieitai sto board

	private int row;
	private int column;
	
	Pair(int row,int column) {
		this.row = row;
		this.column = column;
	}

	int getRow() {
		return row;
	}

	void setRow(int row) {
		this.row = row;
	}

	int getColumn() {
		return column;
	}

	void setColumn(int column) {
		this.column = column;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this.getColumn() == ((Pair)obj).getColumn() && this.getRow() == ((Pair)obj).getRow())
			return true;
		return false;
	}
}

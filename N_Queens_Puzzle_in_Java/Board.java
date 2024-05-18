package N_Queens_Puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

class Board implements Comparable<Board>{
	
	private int Dimension;
	private int[][] chessBoard;
	
	
	Board(int Dimension) {
		this.Dimension = Dimension;
		this.chessBoard = new int[Dimension][Dimension];
		for(int i = 0; i < Dimension; i++) {
			for(int j = 0; j < Dimension; j++) {
				this.chessBoard[i][j] = 0;
			}
		}
		this.GenerateRandomQueens();
	}
	
	Board(int[][] board) {
		this.Dimension = board.length;
		this.chessBoard = board;
	}
	
	Board() {
		
	}

	int getDimension() {
		return Dimension;
	}


	void setDimension(int dimension) {
		Dimension = dimension;
	}


	int[][] getChessBoard() {
		return chessBoard;
	}


	void setChessBoard(int[][] chessBoard) {
		this.chessBoard = chessBoard;
	}
	
	private void GenerateRandomQueens() { //topothetei tyxaia tis n vasilises
		Random r = new Random();
		for(int i = 0; i < Dimension; i++) {
			int tmp = r.nextInt(Dimension - 1);
			this.chessBoard[tmp][i] = 1;
		}
	}
	
	Vector<Pair> GetQueenPositions() { //edw vazoume se ena vector ta pairs (pou ftiaksame) ths theshs ths kathe vasilisas
		Vector<Pair> positions = new Vector<Pair>();
		for(int i = 0; i < Dimension; i++) {
			for(int j = 0; j < Dimension; j++) {
				if(this.chessBoard[i][j] == 1) 
					positions.add(new Pair(i,j));
			}
		}
		return positions;
	}
	
	Vector<Pair> FindQueenThreats(Pair queen) { //edw elenxoume gia kathe vasilisa poies theseis ths skakieras apeilei kai tis vazoume se ena vector
		Vector<Pair> threats = new Vector<Pair>();
		for(int i = queen.getColumn() - 1; i >= 0; i--) {
			threats.add(new Pair(queen.getRow(),i));
		}
		for(int i = queen.getColumn() + 1; i < this.Dimension; i++) {
			threats.add(new Pair(queen.getRow(),i));
		}
		int i = queen.getRow() - 1;
		int j = queen.getColumn() - 1;
		
		while(i >= 0 && j >= 0) {
			threats.add(new Pair(i,j));
			i--;
			j--;
		}
		
		i = queen.getRow() + 1;
		j = queen.getColumn() + 1;
		
		while(i < Dimension && j < Dimension ) {
			threats.add(new Pair(i,j));
			i++;
			j++;
		}
		
		i = queen.getRow() - 1;
		j = queen.getColumn() + 1;
		
		while(i >= 0 && j < Dimension ) {
			threats.add(new Pair(i,j));
			i--;
			j++;
		}
		
		i = queen.getRow() + 1 ;
		j = queen.getColumn() - 1;
		
		while(i < Dimension && j >= 0) {
			threats.add(new Pair(i,j));
			i++;
			j--;
		}
		return threats;
	}
	
	int CountThreats() {      //metrame poses vasilises apeiloun h mia thn allh kathe fora
		int totalThreats = 0;
		for(Pair p1 : this.GetQueenPositions()) {
			Vector<Pair> threats = this.FindQueenThreats(p1);
			for(Pair p2 : this.GetQueenPositions()) {
				if (p1 != p2) {
					if(threats.contains(p2))
						totalThreats++;
				}
			}
		}
		return totalThreats;
	}
	
	ArrayList<Board> generateSuccesors(int column) {   //dhmiourgoume katastaseis paidia ths kathe skakieras
		ArrayList<Board> children = new ArrayList<>();
		
		for(int i = 0; i < this.Dimension; i++) {
			if(this.chessBoard[i][column] != 1) {
				
				int[][] newChessBoard = new int[this.Dimension][this.Dimension];
				newChessBoard[i][column] = 1;
				
				for(int k = 0;k < this.Dimension; k++) {
					for(int j = 0; j < this.Dimension; j++) {
						if(j != column) {
							newChessBoard[k][j] = this.chessBoard[k][j];
						}
					}
				}
				children.add(new Board(newChessBoard));
			}
		}
		return children;
	}
	
	@Override
	public int compareTo(Board other) {
		if(this.CountThreats() < other.CountThreats())
			return -1;
		if(this.CountThreats() == other.CountThreats())
			return 0;
		else
			return 1;
	}
	
	Board FindBestSuccesor() {   //vriskoume thn kaluterh katastash-paidi analoga me to poses vasilises apeilountai kathe fora
		
		ArrayList<Board> children = new ArrayList<>();
		
		Board bestBoard = new Board();
		children = generateSuccesors(0);
		bestBoard = Collections.min(children);
		
		for(int j =1; j < getDimension(); j++) {
			children = generateSuccesors(j);
			Board currentBestBoard = Collections.min(children);
			if(currentBestBoard.CountThreats() < bestBoard.CountThreats())
				bestBoard = currentBestBoard;
			
		}
		
		return bestBoard;
	}
	
	@Override
	public String toString() {
		String output = "";
		for (int i = 0; i < this.Dimension; i++) {
			for (int j = 0; j < this.Dimension; j++) {
				if (this.chessBoard[i][j] == 0) {
					output += " # ";
				} else {
					output += " Q ";
				}

			}
			output += "\n";
		}
		return output;
	}
	
	
}

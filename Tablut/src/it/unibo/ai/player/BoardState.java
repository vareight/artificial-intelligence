package it.unibo.ai.player;

import java.util.ArrayList;
import java.util.List;

public class BoardState {
	
	public final static List<Integer> escapeTiles = new ArrayList<Integer>();
	public final int castle=40;
	public final static  List<Integer> campo1 = new ArrayList<Integer>();
	public final static List<Integer> campo2 = new ArrayList<Integer>();
	public final static List<Integer> campo3 = new ArrayList<Integer>();
	public final static List<Integer> campo4 = new ArrayList<Integer>();
	public final static int DIM =9;
	
	
	public BoardState() {
		escapeTiles.add(1); 
		escapeTiles.add(2);
		escapeTiles.add(6);
		escapeTiles.add(7);
		escapeTiles.add(9);
		escapeTiles.add(17);
		escapeTiles.add(18);
		escapeTiles.add(26);
		escapeTiles.add(54);
		escapeTiles.add(62);
		escapeTiles.add(63);
		escapeTiles.add(71);
		escapeTiles.add(73);
		escapeTiles.add(74);
		escapeTiles.add(78);
		escapeTiles.add(79);
		
		campo1.add(3);
		campo1.add(4);
		campo1.add(5);
		campo1.add(13);
		campo2.add(27);
		campo2.add(36);
		campo2.add(37);
		campo2.add(45);
		campo3.add(35);
		campo3.add(43);
		campo3.add(44);
		campo3.add(53);
		campo4.add(67);
		campo4.add(75);
		campo4.add(76);
		campo4.add(77);
	}
	
	
	public  boolean isCamp(int row, int column) {
		return escapeTiles.contains(row*DIM+column);
	}
	public  boolean isCamp(int index ) {
		return escapeTiles.contains(index);
	}
	
	public  boolean inCampo(int index, int campo) {
		if (campo ==1 && campo1.contains(index)) {
			return true;
		}
		if (campo ==2 && campo2.contains(index)) {
			return true;
		}
		if (campo ==3 && campo3.contains(index)) {
			return true;
		}
		if (campo ==4 && campo4.contains(index)) {
			return true;
		}
		return false;
	}

	public  boolean inCampo(int row, int column, int campo) {
		return inCampo(row*DIM+column, campo);
	}

	public  int getCampo(int index) {
		if (campo1.contains(index)){
			return 1;
		}
		if (campo2.contains(index)){
			return 2;
		}
		if (campo3.contains(index)){
			return 3;
		}
		if (campo4.contains(index)){
			return 4;
		}
		return -1;
	}
	public  int getCampo(int row, int column) {
		return getCampo(row*9+column);
	}
	
	public  boolean isEscapeTile(int index) {
		return escapeTiles.contains(index);
	}
	public  boolean isEscapeTile(int row, int column) {
		return isEscapeTile(row*9+column);
	}
	
	public  boolean sameCampo(int from, int to) {
		return getCampo(from) == getCampo(to) && getCampo(from)!=-1;
	}


	public int getCastle() {
		return castle;
	}
	
	
	
}

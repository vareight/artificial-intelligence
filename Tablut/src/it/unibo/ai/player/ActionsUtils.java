package it.unibo.ai.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class ActionsUtils {
	private final int DIM = 9;
	private int NUM_WHITE_PAWNS = 8;
	private int NUM_BLACK_PAWNS = 16;
	private int NUM_PAWNS = 25;
	
	private int king;
	private int[] whitePawns = new int[NUM_WHITE_PAWNS];
	private int[] blackPawns = new int[NUM_BLACK_PAWNS];
	private int[] pawns = new int[NUM_PAWNS];
	private BoardState board = new BoardState();
	
	
	
	public ActionsUtils(int king, int[] whitePawns, int[] blackPawns, int[] pawns) {
		super();
		this.king = king;
		this.whitePawns = whitePawns;
		this.blackPawns = blackPawns;
		this.pawns = pawns;
	}

	/*
	 * Azioni possibili nel caso di pedine nere
	 */
	public List<Action> blackActions(){
		List<Action> azioni = new ArrayList<Action>();
		
		for(int i=0; i<NUM_BLACK_PAWNS; i++) {
			if(blackPawns[i]!=-1) {
				azioni.addAll(calculateActions(blackPawns[i], Turn.BLACK));
			}else break;
		}
		
		return azioni;
	}
	
	/*
	 * Azioni possibili nel caso delle pedine bianche
	 */
	public List<Action> whiteActions(){
		List<Action> azioni = new ArrayList<Action>();
		
			for(int i=0; i<NUM_WHITE_PAWNS; i++) {
				if(whitePawns[i]!=-1) { // se vale -1, la pedina i-esima � stata mangiata
					// aggiungiamo tutte le azioni possibili per la pedina i-esima
					azioni.addAll(calculateActions(whitePawns[i], Turn.WHITE));
				}else break;
			}
			
			//aggiungiamo tutte le azioni possibili per il Re
			azioni.addAll(calculateActions(king, Turn.WHITE));
		
		return azioni;
	}
	
	/*
	 * Data una riga e una colonna, restituisce la corrispondente stringa della scacchiera
	 * Es: (0,0) -> A1
	 */
	private String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}
	
	/**
	 * Funzione che calcola le azioni possibili per la pedina presente in "boardValue".
	 * @param pawns : tutte le pedine sulla scacchiera
	 * @param boardValue: la pedina di cui si devono calcolare le azioni
	 * @param t: turno corrente
	 */
	public List<Action> calculateActions(int pawnValue, Turn t){
		int row= pawnValue/DIM;
		int column= pawnValue-(row*DIM);
		List<Action> azioni = new ArrayList<Action>();
		
		// pawnValueIndex: indica l'indice di pawnValue all'interno della scacchiera
		// indexPawnsToCheck: indica l'indice di una pedina da controllare (su, gi�, dx, sx)
		int pawnValueIndex = -1, indexPawnToCheck;
		try {
			// cerchiamo l'indice j della pedina corrente (corrispondente a pawnValue)
			for(int j=0; j<NUM_PAWNS; j++) {
				if(pawns[j]==pawnValue) {
					pawnValueIndex = j;
					break; //j � la posizione del piedino
				}
			}
			
			// ***************************************************************
			// ***controlliamo la strada percorribile a DESTRA della pedina***
			// ***************************************************************
			
			// se � l'ultimo indice (della pedina pawnValue), non c'� una pedina pi� a destra
			if( pawnValueIndex<NUM_PAWNS-1 && pawns[pawnValueIndex+1]!=-1) {
				indexPawnToCheck=pawnValueIndex+1;
			}
			else indexPawnToCheck = pawnValueIndex; //� l'ultima pedina

			// Partiamo dal valore della scacchiera successivo a quello corrente
			// ed esploriamo fino alla fine della riga
			for(int currentPawnValue=pawnValue+1; currentPawnValue<=(row+1)*DIM-1; currentPawnValue++) {
				if(pawns[indexPawnToCheck]==currentPawnValue || board.breakCampoOrCastle(pawnValue,currentPawnValue)){
					//indexPawnToCheck++; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					azioni.add(action);
				}
			}
			
			// ***************************************************************
			// ***controlliamo la strada percorribile SOTTO alla pedina***
			// ***************************************************************
			for(int currentPawnValue=pawnValue+DIM; currentPawnValue<=DIM*(DIM-1)+column; currentPawnValue+=DIM) { //sotto
				while(pawns[indexPawnToCheck]<currentPawnValue && pawns[indexPawnToCheck]!=-1 && indexPawnToCheck<NUM_PAWNS-1) {
					indexPawnToCheck++;
				}
				if(pawns[indexPawnToCheck]==currentPawnValue||  board.breakCampoOrCastle(pawnValue,currentPawnValue)){
					//indexPawnToCheck++; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					azioni.add(action);
				}
			}
		
			// ***************************************************************
			// ***controlliamo la strada percorribile a SINISTRA della pedina***
			// ***************************************************************
			if(pawnValueIndex!=0) { // se � il primo indice, non c'� una pedina pi� a sinistra
				indexPawnToCheck=pawnValueIndex-1;
			}
			else indexPawnToCheck=pawnValueIndex;
			
			for(int currentPawnValue=pawnValue-1; currentPawnValue>=row*DIM; currentPawnValue--) { //sx
				if(pawns[indexPawnToCheck]==currentPawnValue || board.breakCampoOrCastle(pawnValue,currentPawnValue)){
					//indexPawnToCheck--; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					azioni.add(action);
				}
			}
			
			
			// ***************************************************************
			// ***controlliamo la strada percorribile SOPRA alla pedina***
			// ***************************************************************
			for(int currentPawnValue=pawnValue-DIM; currentPawnValue>=column; currentPawnValue-=DIM) { //sopra
				while(pawns[indexPawnToCheck]>currentPawnValue && pawns[indexPawnToCheck]!=-1 && indexPawnToCheck>0) {
					indexPawnToCheck--;
				}
				if(pawns[indexPawnToCheck]==currentPawnValue || board.breakCampoOrCastle(pawnValue,currentPawnValue)){
					//indexPawnToCheck--; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					azioni.add(action);
				}
		
			}

		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return azioni;
	}
	
	
	/*
	 * Funzione che stabilisce se l'azione dalla casella "from" alla casella "to" � lecita
	 */
//	private boolean isPermitted(int from, int to, Turn t) {
//		if (to == board.getCastle()) return false;
//		
//		if(board.isCamp(to)) {
//			if(t.equals(Turn.WHITE)) {
//				return !(board.isCamp(to));
//			}else {
//				return board.sameCampo(from, to);
//			}
//		}return true;
//
//	}
	
	
	
}

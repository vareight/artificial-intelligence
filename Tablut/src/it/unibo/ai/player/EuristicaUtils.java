package it.unibo.ai.player;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class EuristicaUtils {
	private final int DIM = 9;
	private int NUM_WHITE_PAWNS = 8;
	private int NUM_BLACK_PAWNS = 16;

	private BoardState board;
	
	public EuristicaUtils() {
		board = BoardState.getIstance();
	}

	public double euristicaBlack(StateTablut s,int[] whitePawns, int king) {

		double bonusAccerchiamento=1000/accerchiamento(s, whitePawns, king);
		double bonusVuote=this.righeColonne(s, Turn.BLACK);
		double bonusVeggente = veggente(s, Turn.BLACK);
		return bonusAccerchiamento+bonusVeggente+bonusVuote;

	}
	
	public double euristicaWhite(StateTablut s,int[] whitePawns, int king) {
	
		double bonusVuote=this.righeColonne(s, Turn.WHITE); // circa [-1.5, 3]
		double bonusKeyCells= kingOpenRoads(s) > 1 ? 10000 : 0; // num celle chiave disponibili [0-4]
		double bonusVeggente = veggente(s, Turn.WHITE)*2; // inizio: 4
		double bonusArrocco = noArrocco(s, whitePawns,king);
		
		return bonusVuote + bonusVeggente + bonusKeyCells + bonusArrocco;
	}
	
	
	

	/**
	 * Per ogni pedina bianca quanto si avvicina il nero
	 * @param s
	 * @return
	 */
	private double accerchiamento(StateTablut s, int[] whitePawns, int king) {
		double distanzaTot=0;
		double bonusKing=5;
		for(int i=0; i<NUM_WHITE_PAWNS; i++) {
			int pawnValue=whitePawns[i];
			if(pawnValue==-1) break;
			
			distanzaTot+=calcolaDistanza(pawnValue, s);
			
		}
		distanzaTot+=calcolaDistanza(king, s)*bonusKing;
		
		return distanzaTot;
	}
	
	private int calcolaDistanza(int pawnValue, StateTablut s) {
		int row= pawnValue/DIM;
		int column= pawnValue-(row*DIM);
		int distanza=0;
		BoardState board = BoardState.getIstance();
		for(int currentPawnValue=pawnValue+1; currentPawnValue<=(row+1)*DIM-1; currentPawnValue++) {
			int newRow = currentPawnValue/DIM;
			int newColumn = currentPawnValue-(newRow*DIM);
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
				break;
			}else {
				distanza++;
				
			}
		}
		
		// ***************************************************************
		// ***controlliamo la strada percorribile SOTTO alla pedina***
		// ***************************************************************
		for(int currentPawnValue=pawnValue+DIM; currentPawnValue<=DIM*(DIM-1)+column; currentPawnValue+=DIM) { //sotto
			int newRow = currentPawnValue/DIM;
			int newColumn = currentPawnValue-(newRow*DIM);
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
				break;
			}else {
				distanza++;
			}
		}
	
		// ***************************************************************
		// ***controlliamo la strada percorribile a SINISTRA della pedina***
		// ***************************************************************
		for(int currentPawnValue=pawnValue-1; currentPawnValue>=row*DIM; currentPawnValue--) { //sx
			int newRow = currentPawnValue/DIM;
			int newColumn = currentPawnValue-(newRow*DIM);
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
				break;
			}else {
				distanza++;
				
			}
		}
		
		// ***************************************************************
		// ***controlliamo la strada percorribile SOPRA alla pedina***
		// ***************************************************************
		for(int currentPawnValue=pawnValue-DIM; currentPawnValue>=column; currentPawnValue-=DIM) { //sopra
			int newRow = currentPawnValue/DIM;
			int newColumn = currentPawnValue-(newRow*DIM);
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
				break;
			}else {
				distanza++;
				
			}
	
		}
		return distanza;
	}
	
	/**
	 * Conta quante righe e colonne sono vuote, in quante righe/colonne c'� solo un bianco/nero,
	 * e se il re � in una riga/colonna vuota
	 * @param s
	 * @param t
	 * @return
	 */
	private double righeColonne(StateTablut s, Turn t) {
		double result = 0;
		double onlyBlack=0;
		double onlyWhite=0;
		double onlyKing=0;
		double vuote=0;
		int numBlackR, numWhiteR,numBlackC, numWhiteC;
		boolean kingR,kingC;
		int keyCells[] = {2, 6};
		
		for(Integer i : keyCells) {
			numBlackR=0;
			numWhiteR=0;
			numBlackC=0;
			numWhiteC=0;
			kingR=false;
			kingC=false;
			for(int j=0; j<DIM;j++) {
				if(s.getPawn(i, j).equals(Pawn.BLACK)) numBlackR++;
				if(s.getPawn(i, j).equals(Pawn.WHITE)) numWhiteR++;
				if(s.getPawn(i, j).equals(Pawn.KING)) kingR=true;
				if(s.getPawn(j, i).equals(Pawn.BLACK)) numBlackC++;
				if(s.getPawn(j, i).equals(Pawn.WHITE)) numWhiteC++;
				if(s.getPawn(j, i).equals(Pawn.KING)) kingC=true;
			}
			if(numBlackR==0 && numWhiteR==0 && !kingR) vuote++;
			if(numBlackC==0 && numWhiteC==0 && !kingC) vuote++;
			if(numBlackR>=1 && numWhiteR==0 && !kingR) onlyBlack++;
			if(numBlackR==0 && numWhiteR>=1 && !kingR) onlyWhite++;
			if(numBlackR==0 && numWhiteR==0 && kingR) onlyKing=5;
			if(numBlackC>=1 && numWhiteC==0 && !kingC) onlyBlack++;
			if(numBlackC==0 && numWhiteC>=1 && !kingC) onlyWhite++;
			if(numBlackC==0 && numWhiteC==0 && kingC) onlyKing=5;
		}
		
		if(t.equals(Turn.BLACK)) {
			result = onlyBlack/2.0 - onlyWhite - 4*vuote;
		}else { //WHITE
			result = onlyKing+4*vuote - onlyBlack/2.0;
		}	
		return result;
	}
	

	/**
	 * Funzione che trova la posizione del re nella scacchiera
	 * @param state
	 * @return the position of the king, -1 if not found
	 */
	private int findKing(StateTablut state) {
		for(int i=0; i<DIM; i++) {
			for(int j=0; j<DIM; j++) {
				if(state.getPawn(i, j).equals(Pawn.KING)) {
					return i*DIM +j;	
				}
			}
		}
		return -1;
	}
	
	/**
	 * scritta per far si che se ci sono due strade libere l'euristica si rafforzi
	 * @param state
	 * @return
	 */
	private double kingOpenRoads(StateTablut state) {
		int posKing=findKing(state);
		
		boolean closeRoadSx=false, closeRoadTop=false ;
		boolean closeRoadDx=false, closeRoadBottom=false ;
		int openRoads=0; 
		if(posKing!=-1) { //controllo che la posizione del re sia valida
			int riga=posKing/DIM;
			int col=posKing-(riga*DIM);
			if(board.isEscapeTile(riga, 0)) {							//controlla escape tile a sinistra del re
				for(int i=col-1; i>=0 && closeRoadSx==false; i--) {		//se trova un pedone in mezzo esce subito dal ciclo per non perdere tempo
					if(state.getPawn(riga, i)!= Pawn.EMPTY) {
						closeRoadSx=true;
					}
				}
				if(closeRoadSx==false) {								//se la strada non � chiusa incrementa openRoads
					openRoads++;
				}
			}
			
			if(board.isEscapeTile(0, col)) {							//controlla escape tile sopra al re
				for(int i=riga-1; i>=0 && closeRoadTop==false; i--) {
					if(state.getPawn(i, col)!= Pawn.EMPTY) {
						closeRoadTop=true;
					}
				}
				if(closeRoadTop==false) {								
					openRoads++;
				}
			}
			if(board.isEscapeTile(riga, DIM)) {							//controlla escape tile a destra del re
				for(int i=col+1; i<DIM && closeRoadDx==false; i++) {	
					if(state.getPawn(riga, i)!= Pawn.EMPTY) {
						closeRoadDx=true;
					}
				}
				if(closeRoadDx==false) {								
					openRoads++;
				}
			}
			
			if(board.isEscapeTile(DIM, col)) {							//controlla escape tile sotto al re
				for(int i=riga+1; i<DIM && closeRoadBottom==false; i++) {
					if(state.getPawn(i, col)!= Pawn.EMPTY) {
						closeRoadBottom=true;
					}
				}
				if(closeRoadBottom==false) {		
					openRoads++;
				}
			}
		}
									
			return openRoads; 
	}
	
	private double veggente(StateTablut state, Turn turn) {
		double result = 0;
		int numBlack = state.getNumberOf(Pawn.BLACK);
		int numWhite = state.getNumberOf(Pawn.WHITE);
		double weightBlack = 0.5, weightWhite = 1; 
		int whiteOut = NUM_WHITE_PAWNS-numWhite;
		int blackOut = NUM_BLACK_PAWNS-numBlack;
		
		if(turn.equalsTurn(Turn.BLACK.toString())) {
			weightBlack *=3;
			result = numBlack*weightBlack - weightWhite*numWhite;
		}
		if(turn.equalsTurn(Turn.WHITE.toString())) {
			weightWhite *=4;
			if(state.getTurnCount()<=6) {
				weightBlack*=20;
			}
			result = weightWhite*blackOut - weightBlack*whiteOut;
			if(numWhite<=3) {
				result -= whiteOut;
			}
			if(numWhite+1<=numBlack/3.5) {
				result -= 2*(whiteOut);
			}
		}
		
		return result;
	}
	

	private double noArrocco(StateTablut s,int[] whitePawns, int king) {
		int row = king/DIM;
		int col = king-(row*DIM);
		double punti=0;
		
		int bonrow= Math.abs(5-row);
		int boncol= Math.abs(5-col);
		
		punti=3*bonrow+3*boncol;
	
		return punti;
	}

}

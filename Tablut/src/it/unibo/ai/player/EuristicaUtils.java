package it.unibo.ai.player;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class EuristicaUtils {
	private final int DIM = 9;
	private int NUM_WHITE_PAWNS = 8;
	private int NUM_BLACK_PAWNS = 16;
	private int NUM_PAWNS = 25;
	
	public EuristicaUtils() {
	}

	public double euristicaBlack(StateTablut s,int[] whitePawns, int king) {
//		double lateGame = this.getTurnCount();
		double bonusAccerchiamento=1/accerchiamento(s, whitePawns, king);
		double bonusVuote=this.righeColonne(s, Turn.BLACK);
		double bonusNumPawn= blackpawnInTrouble(s,Turn.BLACK);
		return bonusAccerchiamento + bonusVuote + bonusNumPawn;
		//return Math.random();
	}
	
	public double euristicaWhite(StateTablut s,int[] whitePawns, int king) {
		// TODO
		double bonusAccerchiamento=accerchiamento(s, whitePawns, king);
		double bonusVuote=this.righeColonne(s, Turn.WHITE);
		double bonusNumPawn= blackpawnInTrouble(s,Turn.WHITE);
		return bonusAccerchiamento + bonusVuote + bonusNumPawn;
//		return Math.random();
	}
	

	/**
	 * Per ogni pedina bianca quanto si avvicina il nero
	 * @param s
	 * @return
	 */
	private double accerchiamento(StateTablut s, int[] whitePawns, int king) {
		double distanzaTot=0;
		double bonusKing=3;
		
		if(s.getTurnCount() > 5) bonusKing = Math.pow(s.getTurnCount(), 2);
		System.out.println("******BONUS KING= "+bonusKing+" ********");
		
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
		// ***************************************************************
		// ***controlliamo la strada percorribile a DESTRA della pedina***
		// ***************************************************************

		for(int currentPawnValue=pawnValue+1; currentPawnValue<=(row+1)*DIM-1; currentPawnValue++) {
			int newRow = currentPawnValue/DIM;
			int newColumn = currentPawnValue-(newRow*DIM);
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK)){
				//indexPawnToCheck++; //probabilmente non serve
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
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK)){
				//indexPawnToCheck++; //probabilmente non serve
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
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK)){
				//indexPawnToCheck++; //probabilmente non serve
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
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK)){
				//indexPawnToCheck++; //probabilmente non serve
				break;
			}else {
				distanza++;
				
			}
	
		}
		return distanza;
	}
	
	private double righeColonne(StateTablut s, Turn t) {
		double onlyBlack=0;
		double onlyWhite=0;
		double onlyKing=0;
		double vuote=0;
		int numBlackR, numWhiteR,numBlackC, numWhiteC;
		boolean kingR,kingC;
		
		for(int i=0; i<DIM; i++) {
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
			if(numBlackR==1 && numWhiteR==0 && !kingR) onlyBlack++;
			if(numBlackR==0 && numWhiteR==1 && !kingR) onlyWhite++;
			if(numBlackR==0 && numWhiteR==0 && kingR) onlyKing=1;
			if(numBlackC==0 && numWhiteC==0 && !kingC) vuote++;
			if(numBlackC==1 && numWhiteC==0 && !kingC) onlyBlack++;
			if(numBlackC==0 && numWhiteC==1 && !kingC) onlyWhite++;
			if(numBlackC==0 && numWhiteC==0 && kingC) onlyKing=1;
		}
		
		if(t.equals(Turn.BLACK)) {
			if(onlyKing>=1) return -1; 
			return (vuote/18 + onlyWhite/18)*-1;
		}else { //WHITE
			if(onlyKing>=1) return 1; 
			return (vuote/18 + onlyWhite/18);
		}
		
	}
	
	
	private double blackpawnInTrouble(StateTablut state, Turn turn) {
		int whiteRemoved= NUM_WHITE_PAWNS - state.getNumberOf(Pawn.WHITE);
		int blackRemoved= NUM_BLACK_PAWNS - state.getNumberOf(Pawn.BLACK);
		int oddsBlackWhite = blackRemoved-whiteRemoved;

		if(oddsBlackWhite >=2 && oddsBlackWhite<8) {
			//black in serious trouble
			if(turn.equals(Turn.BLACK)) 
				return (-1)/(8 - oddsBlackWhite); 
			else 
				return 1/(8 - oddsBlackWhite);
		}
		if(oddsBlackWhite >=0 && oddsBlackWhite < 2) {
			//black in trouble
			if(turn.equals(Turn.BLACK)) 
				return (0.5)*(-1)/(8 - oddsBlackWhite); 
			else 
				return 1/(8 - oddsBlackWhite);
		}
		if(oddsBlackWhite < 0 ) {
			//black in advantage
			if(turn.equals(Turn.BLACK)) 
				return 1/(8 - oddsBlackWhite); 
			else 
				return 2*(-1)/(8 + oddsBlackWhite); //negativo oddsBalckWhite
			
		}
		
		return 0.0;
	}

}

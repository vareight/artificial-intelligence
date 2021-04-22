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
//		capire se utilizzare anche onlyBlack oppure inutile
		
	}
	
	//pensare se può avere meno peso visto che questa euristica toglie punti a chi è in svantaggio e moltiplicare tutto per 1/2 per esempio
	private double blackpawnInTrouble(StateTablut state, Turn turn) {
		int whiteRemoved= NUM_WHITE_PAWNS - state.getNumberOf(Pawn.WHITE);
		int blackRemoved= NUM_BLACK_PAWNS - state.getNumberOf(Pawn.BLACK);
		int oddsBlackWhite = blackRemoved-whiteRemoved;
		
		if( oddsBlackWhite ==0 ) 
			//same number of pawn deleted
			return 0;
		else
			{
			int resultBlackInTrouble= 1 - (1/oddsBlackWhite);
			int resultNormalSituationBlack= 1/(NUM_WHITE_PAWNS-oddsBlackWhite);
			int resultNormalSituationWhite= 1/(NUM_WHITE_PAWNS+oddsBlackWhite);
			int resultWhiteInTrouble= 1 + (1/oddsBlackWhite); //oddsblackWhite negativo 
			
			if(oddsBlackWhite >=2 ) {
				//black in serious trouble
				if(turn.equals(Turn.BLACK)) 			
					return -resultBlackInTrouble;      		 //toglie all'euristica nera valori -1/2 -2/3 -3/4 -4/5 -5/6... se in svantaggio
				else 
					return resultBlackInTrouble;			 //toglie all'euristica bianca valori 1/2 2/3 3/4 4/5 5/6... se in vantaggio
			}
			if( oddsBlackWhite ==1 ) { 
				//black in trouble
				if(turn.equals(Turn.BLACK)) 
					return -resultNormalSituationBlack;  	// toglie all'euristica nera -1/7 o -1/8
				else 
					return resultNormalSituationBlack; 		//aggiunge all'euristica bianca un vantaggio di 1/7 o 1/8
			}
			
			if(oddsBlackWhite ==-1 ) {
				//white in trouble
				if(turn.equals(Turn.BLACK)) 
					return resultNormalSituationWhite; 		//aggiunge all'euristica nera un vantaggio di 1/7 o 1/8
				else 
					return -resultNormalSituationWhite;		// toglie all'euristica  bianca -1/7 o -1/8
			}
			
			if(oddsBlackWhite < -1 ) {
				//black in advantage
				if(turn.equals(Turn.BLACK)) 
					return resultWhiteInTrouble; 
				else 
					return -resultWhiteInTrouble; 
				
			}
		}
		
		return 0;
	}

}

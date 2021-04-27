package it.unibo.ai.player;

import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class EuristicaUtils {
	private final int DIM = 9;
	private int NUM_WHITE_PAWNS = 8;
	private int NUM_BLACK_PAWNS = 16;
//	private int NUM_PAWNS = 25;
	private BoardState board= BoardState.getIstance();
	private static TurnNumberSingleton turn; 
	private ActionsUtils actionsUtils;
	
	public EuristicaUtils(ActionsUtils actionsUtils) {
		turn= TurnNumberSingleton.getIstance();
		this.actionsUtils=actionsUtils;
	}

	public double euristicaBlack(StateTablut s,int[] whitePawns, int king) {
//		double lateGame = this.getTurnCount();
		System.out.println("*****BLACK*****");
		double bonusAccerchiamento=accerchiamento(s, whitePawns, king)/100;
		System.out.println("Bonus accerchiamento "+bonusAccerchiamento);
		double bonusVuote=this.righeColonne(s, Turn.BLACK);
		System.out.println("Bonus vuote "+bonusVuote);
		double bonusNumPawn= blackpawnInTrouble(s,Turn.BLACK);
		System.out.println("Bonus numero pedoni "+bonusNumPawn);
		double bonusStradeLibere= -kingOpenRoads(s);
		System.out.println("Bonus strade libere re "+bonusStradeLibere);
		System.out.println("*****FINE BLACK*****");
		return bonusAccerchiamento + bonusVuote + bonusNumPawn + bonusStradeLibere;

	}
	
	public double euristicaWhite(StateTablut s,int[] whitePawns, int king) {
		// TODO
		//double bonusAccerchiamento=1-1/accerchiamento(s, whitePawns, king);   
		//secondo me solo con accerchiamento riceveva un valore altissimo e  non confrontabile con gli altri valori dell'euristica
		//facendo 1-1/accerchiamento dovremmo avere un valore complementare rispetto a quello del black
		//se il ragionamento è corretto cambiatelo
		
		double bonusAccerchiamento=-accerchiamento(s, whitePawns, king)/1000;
		System.out.println("*****WHITE*****");
		System.out.println("Bonus accerchiamento "+bonusAccerchiamento);
		double bonusVuote=this.righeColonne(s, Turn.WHITE)*100;
		System.out.println("Bonus vuote "+bonusVuote);
		double bonusNumPawn= blackpawnInTrouble(s,Turn.WHITE);
		System.out.println("Bonus numero pedoni "+bonusNumPawn);
		double bonusStradeLibere= kingOpenRoads(s);
		System.out.println("Bonus strade libere re "+bonusStradeLibere);
		double bonusMovimentoKing = movimentoKing(s);
		System.out.println("*****FINE WHITE*****");
		return bonusVuote + bonusNumPawn+ bonusStradeLibere + bonusMovimentoKing + bonusAccerchiamento;
	}
	
	/**
	 * Funzione che calcola le mosse possibili del Re
	 * @return il numero di mosse possibili del re
	 */
	private double movimentoKing(StateTablut state) {
		return this.actionsUtils.calculateActions(this.findKing(state), state.getTurn()).size();
	}
	

	/**
	 * Per ogni pedina bianca quanto si avvicina il nero
	 * @param s
	 * @return
	 */
	private double accerchiamento(StateTablut s, int[] whitePawns, int king) {
		double distanzaTot=0;
		double bonusKing=5;
		
//		if(turn.getTurn() > 4) 
//			//bonusKing = Math.pow(turn.getTurn(), 2);
			bonusKing*=turn.getTurn();
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
	
	/**
	 * Conta quante righe e colonne sono vuote, in quante righe/colonne c'è solo un bianco/nero,
	 * e se il re è in una riga/colonna vuota
	 * @param s
	 * @param t
	 * @return
	 */
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
			return (vuote/18 + onlyWhite/9)*-1; 
		}else { //WHITE
			if(onlyKing>=1) return 1; 
			return (vuote/18 + onlyWhite/9);
		}	
//		capire se utilizzare anche onlyBlack oppure inutile
		
	}
	
	//pensare se può avere meno peso visto che questa euristica toglie punti a chi è in svantaggio e moltiplicare tutto per 1/2 per esempio
	private double blackpawnInTrouble(StateTablut state, Turn turn) {
		int whiteRemoved= NUM_WHITE_PAWNS - state.getNumberOf(Pawn.WHITE);
		int blackRemoved= NUM_BLACK_PAWNS - state.getNumberOf(Pawn.BLACK);
		int oddsBlackWhite = blackRemoved-whiteRemoved;
		
		if( oddsBlackWhite ==0 || oddsBlackWhite>7 ) {
			if (oddsBlackWhite > 7 ) {
				return 10;
			}
			//same number of pawn deleted
			return 0;
		}
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
		int weight=5;
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
				if(closeRoadSx==false) {								//se la strada non è chiusa incrementa openRoads
					//return 10;
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
					//return 10;
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
					//return 10;
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
					//return 10;
					openRoads++;
				}
			}
		}
//		if(openRoads > 1) {											//restituisce un valore molto alto se ci sono due o più strade libere 
//			return openRoads*weight;											//in modo che il re sia avvantaggiato a scappare
//		}else {															//sotto la soglia 2 non restituisce nulla
			return openRoads*weight; 
//		}
		
	}
}

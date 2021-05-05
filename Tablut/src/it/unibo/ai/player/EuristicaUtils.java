package it.unibo.ai.player;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
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
//		double lateGame = turn.getTurn();
		//System.out.println("*****BLACK*****");
		double bonusAccerchiamento=1000/accerchiamento(s, whitePawns, king);
		//System.out.println("Bonus accerchiamento "+bonusAccerchiamento);
		double bonusVuote=this.righeColonne(s, Turn.BLACK);
		//System.out.println("Bonus vuote "+bonusVuote);
//		double bonusNumPawn= blackpawnInTrouble(s,Turn.BLACK)*5;
//		//System.out.println("Bonus numero pedoni "+bonusNumPawn);
//		double bonusStradeLibere= -20*kingOpenRoads(s);
//		//System.out.println("Bonus strade libere re "+bonusStradeLibere);
//		double biancheGoing = this.pedineBiancheGoingToDie(s);
//		double nereGoing = -this.pedineNereGoingToDie(s);
//		double kingGoing = this.kingCaptured(king, s)*100;
		double bonusVeggente = veggente(s, Turn.BLACK);
//		double inTrouble=0;
//		if(bonusNumPawn<0) {
//			inTrouble=10*( nereGoing+biancheGoing);
//		}
		//System.out.println("Going BIANCHE-NERE-KING: "+biancheGoing+"|"+nereGoing+"|"+kingGoing);
//		double diagonali=(lateGame <=8)? this.diagonalizzazioneNero(s) : 0 ;
		//System.out.println("Diagonali "+ diagonali);
		//System.out.println("*****FINE BLACK*****");
		return bonusAccerchiamento+bonusVeggente+bonusVuote;

	}
	
	public double euristicaWhite(StateTablut s,int[] whitePawns, int king) {
		// TODO
		//double bonusAccerchiamento=accerchiamento(s, whitePawns, king)*0.02;
//		if(s.getTurnCount() >= 10) bonusAccerchiamento = 0;
		//if(bonusAccerchiamento <= 2 || s.getNumberOf(Pawn.WHITE)<=2) return -100;
		//secondo me solo con accerchiamento riceveva un valore altissimo e  non confrontabile con gli altri valori dell'euristica
		//facendo 1-1/accerchiamento dovremmo avere un valore complementare rispetto a quello del black
		//se il ragionamento ï¿½ corretto cambiatelo
		
//		double bonusAccerchiamento=-accerchiamento(s, whitePawns, king)/1000;
		//System.out.println("*****WHITE*****");
		//System.out.println("Bonus accerchiamento "+bonusAccerchiamento);
		double bonusVuote=this.righeColonne(s, Turn.WHITE); // circa [-1.5, 3]
//		//System.out.println("Bonus vuote "+bonusVuote);
//		double bonusNumPawn= blackpawnInTrouble(s,Turn.WHITE);
//		//System.out.println("Bonus numero pedoni "+bonusNumPawn);
		double bonusKeyCells= kingOpenRoads(s) > 1 ? 10000 : 0; // num celle chiave disponibili [0-4]
//		//System.out.println("Bonus strade libere re "+bonusStradeLibere);
//		double bonusMovimentoKing = movimentoKing(s)*0.1; // circa [0, 1]
//		double biancheGoing = -this.pedineBiancheGoingToDie(s);
//		double nereGoing = this.pedineNereGoingToDie(s);
//		double kingGoing = - this.kingCaptured(king, s)*100;
		double bonusVeggente = veggente(s, Turn.WHITE)*2; // inizio: 4
		//System.out.println("Going BIANCHE-NERE-KING: "+biancheGoing+"|"+nereGoing+"|"+kingGoing);
//		double inTrouble=0;
//		if(bonusNumPawn<0) {
//			inTrouble=10*( nereGoing+biancheGoing);
//		}
//		double bonusMobilita = mobilita(s, whitePawns, king)*0.02;
		double penalita = 0;
		double noArrocco= noArrocco(s,whitePawns,king);
		
		if (king == 40) penalita = -5; // il re è ancora sul trono
		
		//System.out.println("*WHITE* vuote: "+bonusVuote+ " veg: "+ bonusVeggente+ " keycel: "+ bonusKeyCells+ " arr: "+ noArrocco);
		return bonusVuote + bonusVeggente + bonusKeyCells +penalita  + noArrocco;
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
		bonusKing+=turn.getTurn();
		//System.out.println("******BONUS KING= "+bonusKing+" ********");
		
		for(int i=0; i<NUM_WHITE_PAWNS; i++) {
			int pawnValue=whitePawns[i];
			if(pawnValue==-1) break;
			
			distanzaTot+=calcolaDistanza(pawnValue, s);
			
		}
		distanzaTot+=calcolaDistanza(king, s)*bonusKing;
		
		return distanzaTot;
	}
	
	private double noArrocco(StateTablut s,int[] whitePawns, int king) {
		int row = king/DIM;
		int col = king-(row*DIM);
		double punti=0;
		
		if((row<=2 && (col<=2 || col>=7)) ||(row>=7 && (col<=2 || col>=7))) {
			punti +=5;
		}else if(row>=3 && row<=5 && col>=3 && col <=5) {
			punti-=2;
		}
		
		for(int pawn : whitePawns) {
			 row = pawn/DIM;
			 col = pawn-(row*DIM);
			if((row<=2 && (col<=2 || col>=7)) ||(row>=7 && (col<=2 || col>=7))) {
				punti +=1;
			}else if(row>=3 && row<=5 && col>=3 && col <=5) {
				punti-=0.5;
			}
		}
		
		return punti;
	}
	
	private int calcolaDistanza(int pawnValue, StateTablut s) {
		int row= pawnValue/DIM;
		int column= pawnValue-(row*DIM);
		int distanza=0;
		BoardState board = BoardState.getIstance();
		
		// ***************************************************************
		// ***controlliamo la strada percorribile a DESTRA della pedina***
		// ***************************************************************
		for(int currentPawnValue=pawnValue+1; currentPawnValue<=(row+1)*DIM-1; currentPawnValue++) {
			int newRow = currentPawnValue/DIM;
			int newColumn = currentPawnValue-(newRow*DIM);
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
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
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
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
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
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
			if(s.getPawn(newRow, newColumn).equals(Pawn.BLACK) || 
					s.getPawn(newRow, newColumn).equals(Pawn.THRONE) || 
					board.isCamp(newRow, newColumn)){
				//indexPawnToCheck++; //probabilmente non serve
				break;
			}else {
				distanza++;
				
			}
	
		}
		return distanza;
	}
	
	/**
	 * Conta quante righe e colonne sono vuote, in quante righe/colonne c'ï¿½ solo un bianco/nero,
	 * e se il re ï¿½ in una riga/colonna vuota
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
//			if(i==0 || i==DIM-1) continue;
			if(numBlackR>=1 && numWhiteR==0 && !kingR) onlyBlack++;
			if(numBlackR==0 && numWhiteR>=1 && !kingR) onlyWhite++;
			if(numBlackR==0 && numWhiteR==0 && kingR) onlyKing=5;
			if(numBlackC>=1 && numWhiteC==0 && !kingC) onlyBlack++;
			if(numBlackC==0 && numWhiteC>=1 && !kingC) onlyWhite++;
			if(numBlackC==0 && numWhiteC==0 && kingC) onlyKing=5;
		}
		
		if(t.equals(Turn.BLACK)) {
			result = onlyBlack/2.0 - onlyWhite - 4*vuote;
//			if(onlyKing>=1) return -1; 
//			return (vuote/18 + onlyWhite/9)*-1; 
		}else { //WHITE
			result = 10*vuote - onlyBlack/2.0; //tolgo onlyWhite?
//			if(onlyKing>=1) return 1; 
//			return (vuote/18 + onlyWhite/9);
		}	
//		capire se utilizzare anche onlyBlack oppure inutile
		return result;
	}
	
	//pensare se puï¿½ avere meno peso visto che questa euristica toglie punti a chi ï¿½ in svantaggio e moltiplicare tutto per 1/2 per esempio
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
				if(closeRoadSx==false) {								//se la strada non ï¿½ chiusa incrementa openRoads
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
//		if(openRoads > 1) {											//restituisce un valore molto alto se ci sono due o piï¿½ strade libere 
//			return openRoads*weight;											//in modo che il re sia avvantaggiato a scappare
//		}else {															//sotto la soglia 2 non restituisce nulla
			return openRoads; 
//		}
	}
	
	
	 /* Funzione che stabilisce se una pedina si va a suicidare
	 * @param state
	 * @return
	 */
	private double isGoingToDie(int pawnValue, Pawn pawn, StateTablut state) {
		int riga = pawnValue/DIM;
		int col = pawnValue - (riga*DIM);
		//boolean inDanger = false, dead = false;
		Pawn otherColor;
		List<Action> azioni;
		double result = 0;
		
		if(pawn.equals(Pawn.BLACK)) {
			otherColor = Pawn.WHITE;
			azioni = this.actionsUtils.whiteActions();
		}
		else {
			otherColor = Pawn.BLACK;
			azioni = this.actionsUtils.blackActions();
		}

		if (isDead(riga+1, col, riga-1, col, state, azioni)) result++; //arriva sotto
		else if (isDead(riga-1, col, riga+1, col, state, azioni)) result++; //arriva sopra
		else if (isDead(riga, col-1, riga, col+1, state, azioni)) result++; //arriva a sinistra
		else if (isDead(riga, col+1, riga, col-1, state, azioni)) result++; //arriva a destra
		
		return result;
	}
	
	private boolean isDead(int riga, int colonna, int newRiga, int newColonna, StateTablut state, List<Action> azioni) {
		boolean result = false;
		if(newRiga >=0 && newRiga<=8 && newColonna >= 0 && newColonna <=8 &&
				riga >=0 && riga<=8 && colonna >= 0 && colonna <=8) {
			if(state.getPawn(riga, colonna)!= Pawn.EMPTY || board.isCamp(riga, colonna)) {
				for (Action a : azioni) {
					if(a.getRowTo() == newRiga && a.getColumnTo() == newColonna) {
						result = true;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Funzione che rileva se il re viene catturato
	 * @param posKing
	 * @param state
	 * @return true if is captured, false otherwise
	 */
	public double kingCaptured(int posKing, StateTablut state) { //
		
		//se posKing == 40 (ovvero si trova nel trono), al momento non lo consideriamo (return 0?)
		if(posKing == 40 ) return 0;
		if(posKing != 31 && posKing != 39 && posKing!=41 && posKing != 49)
			return this.isGoingToDie(posKing, Pawn.KING, state);
		
		int riga=posKing/DIM;
		int col=posKing-(riga*DIM);
		int circondato = 0;
		Pawn sopra=state.getPawn(riga-1, col);
		Pawn sotto =state.getPawn(riga+1, col);
		Pawn destra =state.getPawn(riga, col+1); 
		Pawn sinistra =state.getPawn(riga, col-1);
		
	
		if(sopra.equals(Pawn.BLACK)) circondato++;
		if(sotto.equals(Pawn.BLACK)) circondato++;
		if(destra.equals(Pawn.BLACK)) circondato++;
		if(sinistra.equals(Pawn.BLACK)) circondato++;
		
		if(circondato != 2) return 0; 
		
		List<Action> azioni = this.actionsUtils.blackActions();		
		
		if(sopra.equals(Pawn.EMPTY)) {
			for(Action a : azioni) {
				if(a.getRowTo() == riga-1 && a.getColumnTo() == col) {
					return 1;
				}
			}
		}
		
		if(sotto.equals(Pawn.EMPTY)) {
			for(Action a : azioni) {
				if(a.getRowTo() == riga+1 && a.getColumnTo() == col) {
					return 1;
				}
			}
		}
		
		if(sinistra.equals(Pawn.EMPTY)) {
			for(Action a : azioni) {
				if(a.getRowTo() == riga && a.getColumnTo() == col-1) {
					return 1;
				}
			}
		}
		
		if(destra.equals(Pawn.EMPTY)) {
			for(Action a : azioni) {
				if(a.getRowTo() == riga && a.getColumnTo() == col+1) {
					return 1;
				}
			}
		}
		return 0;		
	}
	
	private double pedineNereGoingToDie(StateTablut state) {
		double result = 0;
		for(Integer pawnValue : this.actionsUtils.getBlackPawns()) {
			result+=this.isGoingToDie(pawnValue, Pawn.BLACK, state);
		}
		return result;
	}
	
	private double pedineBiancheGoingToDie(StateTablut state) {
		double result = 0;
		for(Integer pawnValue : this.actionsUtils.getWhitePawns()) {
			result+=this.isGoingToDie(pawnValue, Pawn.WHITE, state);
		}
		return result;
	}
	
	private double veggente(StateTablut state, Turn turn) {
		double result = 0;
		int numBlack = state.getNumberOf(Pawn.BLACK);
		int numWhite = state.getNumberOf(Pawn.WHITE);
		int mangiateBlack= NUM_BLACK_PAWNS - numBlack;
		int mangiateWhite= NUM_WHITE_PAWNS - numWhite;
		double weightBlack = 0.5, weightWhite = 1; 
		int totPawns = numBlack+numWhite;
		
		if(turn.equalsTurn(Turn.BLACK.toString())) {
			weightBlack *=3;
			result = numBlack*weightBlack - weightWhite*numWhite;
		}/*
		if(turn.equalsTurn(Turn.WHITE.toString())) {
			weightWhite *=3;
			result = weightWhite*numWhite - numBlack*weightBlack;
		}*/
		if(turn.equalsTurn(Turn.WHITE.toString())) {
			if(numWhite<=4) weightWhite *=3;
			weightBlack *=3;
			result = weightBlack*mangiateBlack - weightWhite*mangiateWhite ;
		}
		return result;
	}
	
	
	private double diagonalizzazioneNero(StateTablut state) {
		double diagonaliFull=0;
		if (state.getPawn(2, 3).equals(Pawn.BLACK)) { //primo quadrante
			diagonaliFull++;
		}
		if (state.getPawn(3, 2).equals(Pawn.BLACK)) {
			diagonaliFull++;
		}
		if (state.getPawn(2, 5).equals(Pawn.BLACK)) { //
			diagonaliFull++;
		}
		if (state.getPawn(3, 6).equals(Pawn.BLACK)) {
			diagonaliFull++;
		}
		if (state.getPawn(2, 5).equals(Pawn.BLACK)) {
			diagonaliFull++;
		}
		if (state.getPawn(3, 6).equals(Pawn.BLACK)) {
			diagonaliFull++;
		}
		if (state.getPawn(5, 6).equals(Pawn.BLACK)) {
			diagonaliFull++;
		}
		if (state.getPawn(6, 5).equals(Pawn.BLACK)) {
			diagonaliFull++;
		}
		return diagonaliFull;
	}
	
	private double mobilita(StateTablut state, int[] whitePawns, int king) {
		double result = 0;
		double bonusKing = 2;
		
		for(int i=0; i<NUM_WHITE_PAWNS; i++) {
			int pawnValue=whitePawns[i];
			if(pawnValue==-1) break;
			
//			result+=checkMobilita(pawnValue, state);
			result+=this.actionsUtils.calculateActions(pawnValue, state.getTurn()).size();
			
		}
//		result+=checkMobilita(king, state)*bonusKing;
		result+=this.actionsUtils.calculateActions(king, state.getTurn()).size()*bonusKing;
		return result;
		
	}
	
	private int checkMobilita(int pawnValue, StateTablut s) {
		int result = 0;
		
		// ***************************************************************
		// ***controlliamo la strada percorribile a DESTRA della pedina***
		// ***************************************************************	
		int newRow = (pawnValue+1)/DIM;
		int newColumn = (pawnValue+1)-(newRow*DIM);
		if(isInsideBoard(newRow, newColumn) && s.getPawn(newRow, newColumn).equalsPawn(Pawn.EMPTY.toString())){
			result++;
		}
		
		// ***************************************************************
		// ***controlliamo la strada percorribile SOTTO alla pedina***
		// ***************************************************************
		newRow = (pawnValue+DIM)/DIM;
		newColumn = (pawnValue+DIM)-(newRow*DIM);
		if(isInsideBoard(newRow, newColumn) && s.getPawn(newRow, newColumn).equalsPawn(Pawn.EMPTY.toString())){
			result++;
		}
	
		// ***************************************************************
		// ***controlliamo la strada percorribile a SINISTRA della pedina***
		// ***************************************************************
		newRow = (pawnValue-1)/DIM;
		newColumn = (pawnValue-1)-(newRow*DIM);
		if(isInsideBoard(newRow, newColumn) && s.getPawn(newRow, newColumn).equalsPawn(Pawn.EMPTY.toString())){
			result++;
		}
		
		// ***************************************************************
		// ***controlliamo la strada percorribile SOPRA alla pedina***
		// ***************************************************************
		newRow = (pawnValue-DIM)/DIM;
		newColumn = (pawnValue-DIM)-(newRow*DIM);
		if(isInsideBoard(newRow, newColumn) && s.getPawn(newRow, newColumn).equalsPawn(Pawn.EMPTY.toString())){
			result++;
		}

		return result;
	}
	
	private boolean isInsideBoard(int row, int column) {
		return row >= 0 && row <=8 && column >=0 && column <=8;
	}
}

package it.unibo.ai.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;

public class MyGame implements Game<StateTablut, Action, State.Turn> {
	//Da capire se ci va il turn o il player
	
	/*
	 * A game can be formally defined as a kind of search problem with the following elements:
		S0: The initial state, which specifies how the game is set up at the start.
		PLAYER(s): Defines which player has the move in a state.
		ACTIONS(s): Returns the set of legal moves in a state.
		RESULT(s, a): The transition model, which defines the result of a move.
		TERMINAL-TEST(s): A terminal test, which is true when the game is over and false TERMINAL STATES otherwise. 
		States where the game has ended are called terminal states.
		UTILITY(s, p): A utility function (also called an objective function or payoff function), defines the final numeric value 
		for a game that ends in terminal state s for a player p. In chess, the outcome is a win, loss, or draw, with values +1, 0, or 1/2 . 
		Some games have a wider variety of possible outcomes; the payoffs in backgammon range from 0 to +192. A zero-sum game is (confusingly) 
		defined as one where the total payoff to all players is the same for every instance of the game. Chess is zero-sum because every game has
		payoff of either 0 + 1, 1 + 0 or 1/2 + 1/2 . "Constant-sum" would have been a better term, but zero-sum is traditional and makes sense if 
		you imagine each player is charged an entry fee of 1/2.
	 * */

	/**
	 * in base allo stato attuale calcoliamo le azioni possibili
	 */
	
	
	private final int DIM = 9;
	private int NUM_WHITE_PAWNS = 8;
	private int NUM_BLACK_PAWNS = 16;
	private int NUM_PAWNS = 25;
	
	private int king;
	private int[] whitePawns = new int[NUM_WHITE_PAWNS];
	private int[] blackPawns = new int[NUM_BLACK_PAWNS];
	private int[] pawns = new int[NUM_PAWNS];
	private BoardState board= new BoardState();
	private GameAshtonTablut game;
	
	
	
	public MyGame(GameAshtonTablut game) {
		super();
		this.game = game;
	}

	/*
	 * Inizializzazione degli array delle verie pedine
	 */
	private void initializePawns() {
		for(int i=0; i<NUM_PAWNS; i++) {
			pawns[i] = -1;
			if(i<NUM_WHITE_PAWNS) whitePawns[i] = -1;
			if(i<NUM_BLACK_PAWNS) blackPawns[i] = -1;
			
		}
		return;
	}
	
	/*
	 * Riempiamo gli array di pedine con i valori di dove si trovano
	 */
	private void populatePawnsArrays(StateTablut s) {
		int indexWhite=0, indexBlack=0, indexPawns=0;
		boolean kingfound=false;
		
		for(int i=0; i<DIM; i++) {
			for(int j=0; j<DIM; j++) {
				// aggiunta pedine bianche
				if(indexWhite<NUM_WHITE_PAWNS && s.getPawn(i, j).equals(Pawn.WHITE)) {
					whitePawns[indexWhite]=i*DIM+j;
					indexWhite++;
					pawns[indexPawns]=i*DIM+j;
					indexPawns++;
				}
				
				// aggiunta del re
				if(!kingfound && s.getPawn(i, j).equals(Pawn.KING)) {
					pawns[indexPawns]=i*DIM+j;
					indexPawns++;
					king=i*DIM+j;
					kingfound=true;
				}
				
				//aggiunta pedine nere
				if(indexBlack<NUM_BLACK_PAWNS && s.getPawn(i, j).equals(Pawn.BLACK)) {
					blackPawns[indexBlack]=i*DIM+j;
					indexBlack++;
					pawns[indexPawns]=i*DIM+j;
					indexPawns++;
				} //decidere se mettere break raggiunto il numero
			}
		}
	}
	
	@Override
	public List<Action> getActions(StateTablut s) {
		Turn turn= s.getTurn();
		
		initializePawns();
		
		populatePawnsArrays(s);
		
		if (turn.equals(Turn.WHITE)) return whiteActions();
		if (turn.equals(Turn.BLACK)) return blackActions();
		
		return null; //TODO capire se restituire altro quando non � W o B
	}
	
	/*
	 * Azioni possibili nel caso delle pedine bianche
	 */
	private List<Action> whiteActions(){
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
	private boolean isPermitted(int from, int to, Turn t) {
		if (to == board.getCastle()) return false;
		
		if(board.isCamp(to)) {
			if(t.equals(Turn.WHITE)) {
				return !(board.isCamp(to));
			}else {
				return board.sameCampo(from, to);
			}
		}return true;

	}
	
	/*
	 * Azioni possibili nel caso di pedine nere
	 */
	private List<Action> blackActions(){
		List<Action> azioni = new ArrayList<Action>();
		
		for(int i=0; i<NUM_BLACK_PAWNS; i++) {
			if(blackPawns[i]!=-1) {
				azioni.addAll(calculateActions(blackPawns[i], Turn.BLACK));
			}else break;
		}
		
		return azioni;
	}

	@Override
	public StateTablut getInitialState() {
		return new StateTablut();
	}


	@Override
	public StateTablut getResult(StateTablut s, Action a) {
		StateTablut newState = null;
		
			try {
				newState= (StateTablut) game.checkMove(s, a);
			} catch (BoardException | ActionException | StopException | PawnException | DiagonalException
					| ClimbingException | ThroneException | OccupitedException | ClimbingCitadelException
					| CitadelException e) {
				System.out.println("PROBLEMINIII");
				e.printStackTrace();
			}
		
		return newState;
	}
	

	@Override
	public boolean isTerminal(StateTablut s) {
		
		Turn t= s.getTurn();
		boolean finishTurn = t.equals(Turn.BLACKWIN) || t.equals(Turn.WHITEWIN) || t.equals(Turn.DRAW);
		boolean noMoves=false;
		if (getActions(s).isEmpty()) {
			noMoves=true;
		}
		return finishTurn || noMoves;
		/*
		// situazione finale vittoria - sconfitta - pareggio
		//white win 
		int posKing = findKing(s);
		boolean kingEscapes = kingEscapes(posKing);
		
		//black win
		boolean kingCaptured= kingCaptured(posKing, s);
		
		//no moves avaible 
		boolean noMoves=false;
		if (getActions(s).isEmpty()) {
			noMoves=true;
		}
		return kingEscapes || kingCaptured ; //manca caso pareggio = stato ripetuto
		*/
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
	 * Funzione che rileva se il re raggiunge una posizione di salvataggio sul bordo
	 * @param posKing
	 * @return true if the king escapes, false otherwise
	 */
	private boolean kingEscapes(int posKing) {
		return board.isEscapeTile(posKing);
	}
	
	/**
	 * Funzione che rileva se il re viene catturato
	 * @param posKing
	 * @param state
	 * @return true if is captured, false otherwise
	 */
	private boolean kingCaptured(int posKing, StateTablut state) { // 
		int riga=posKing/DIM;
		int col=posKing-(riga*DIM);
		Pawn sopra ;
		Pawn sotto ;
		Pawn destra; 
		Pawn sinistra; 
		if( riga !=0) sopra=state.getPawn(riga-1, col);
		else sopra =Pawn.EMPTY;
		if( riga <DIM ) sotto =state.getPawn(riga+1, col);
		else sotto =Pawn.EMPTY;
		if(col <DIM) destra =state.getPawn(riga, col+1); 
		else destra =Pawn.EMPTY;
		if(col !=0) sinistra =state.getPawn(riga, col-1);
		else sinistra =Pawn.EMPTY;
	
		// il re si trova nel castello ed � circondato
		if(posKing==board.getCastle() && sopra.equals(Pawn.BLACK)
				&& sotto.equals(Pawn.BLACK)
				&& destra.equals(Pawn.BLACK)
				&& sinistra.equals(Pawn.BLACK)) {
			return true; 
			
		}
		// posizioni adiacenti al castello: [31,39,41,49]
		// il re si trova in una posizione adiacente al castello e vine catturato se circondato sui tre lati restanti
		if(posKing==31 && sopra.equals(Pawn.BLACK)
				&& destra.equals(Pawn.BLACK)
				&& sinistra.equals(Pawn.BLACK)) {
			return true; 
			
		}
		if(posKing==39 && sopra.equals(Pawn.BLACK)
				&& sotto.equals(Pawn.BLACK)
				&& sinistra.equals(Pawn.BLACK)) {
			return true; 
			
		}
		if(posKing==41 && sopra.equals(Pawn.BLACK)
				&& sotto.equals(Pawn.BLACK)
				&& destra.equals(Pawn.BLACK)){
			return true; 
			
		}
		if(posKing==49 && sotto.equals(Pawn.BLACK)
				&& destra.equals(Pawn.BLACK)
				&& sinistra.equals(Pawn.BLACK)) {
			return true; 
			
		}
		//il re viene catturato in una qualunque altra posizione
		return pawnCaptured(state, riga, col, sopra, sotto, destra, sinistra);
				
	}
	
	/**
	 * Funzione generale per controllare se una pedina viene catturata
	 * @param state
	 * @param riga
	 * @param col
	 * @param sopra
	 * @param sotto
	 * @param destra
	 * @param sinistra
	 * @return true if the pawn is captured, false otherwise
	 */
	private boolean pawnCaptured(StateTablut state,int riga, int col, Pawn sopra, Pawn sotto, Pawn destra, Pawn sinistra) {
		
		Turn turnVittima = state.getTurn();
		Pawn pawnPredatore = turnVittima.equals(Turn.WHITE) ? Pawn.BLACK : Pawn.WHITE ;
		 sopra = sopra.equals(Pawn.KING) ? Pawn.WHITE : sopra;
		 sotto = sotto.equals(Pawn.KING) ? Pawn.WHITE : sotto;
		 destra = destra.equals(Pawn.KING) ? Pawn.WHITE : destra;
		 sinistra = sinistra.equals(Pawn.KING) ? Pawn.WHITE : sinistra;
		
		if ((sopra.equals(pawnPredatore) || board.isCamp(riga*DIM+col-DIM) || riga*DIM+col-DIM == board.castle ) && 
				(sotto.equals(pawnPredatore) || board.isCamp(riga*DIM+col+DIM) || riga*DIM+col+DIM == board.castle)) {
			return true;
		}
		if ((destra.equals(pawnPredatore) || board.isCamp(riga*DIM+col +1) || riga*DIM+col+1 == board.castle) && 
				(sinistra.equals(pawnPredatore) || board.isCamp(riga*DIM+col-1) ||riga*DIM+col-1 == board.castle)) {
			return true;
		}
		return false; 
	}
	
	@Override
	public Turn getPlayer(StateTablut s) {
		return s.getTurn();
	}

	@Override
	public Turn[] getPlayers() {
		Turn[] turns = {Turn.BLACK, Turn.WHITE};
		return turns;
	}

	@Override
	public double getUtility(StateTablut s, Turn t) {
		double punteggio = 0;
		if(t.equals(Turn.BLACK)) {
			switch(s.getTurn()) {
			case DRAW : punteggio=0; break;
			case WHITEWIN : punteggio=Double.MIN_VALUE; break;
			case BLACKWIN : punteggio=Double.MAX_VALUE; break;
			default : punteggio=euristicaBlack(s);
			}
			
		}
		if(t.equals(Turn.WHITE)) {
			switch(s.getTurn()) {
			case DRAW : punteggio=0; break;
			case WHITEWIN : punteggio=Double.MAX_VALUE; break;
			case BLACKWIN : punteggio=Double.MIN_VALUE;; break;
			default : punteggio=euristicaWhite(s);
			}
		}
		//EURISTICA (dovrebbe essere meglio il valore pi� grande)
		return punteggio;
	}
	
	private double euristicaBlack(StateTablut s) {
		
		double bonusAccerchiamento=1/accerchiamento(s);
		return bonusAccerchiamento;
	}
	
	private double euristicaWhite(StateTablut s) {
		// TODO
		return 0;
	}
	
	/**
	 * Per ogni pedina bianca quanto si avvicina il nero
	 * @param s
	 * @return
	 */
	private double accerchiamento(StateTablut s) {
		double distanzaTot=0;
		double bonusKing=3;
		
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
	
	
	//lasciare riga vuota
	
	//scacco al re

}

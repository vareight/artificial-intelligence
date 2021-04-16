package it.unibo.ai.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

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
	private int[] whitePawns;
	private int[] blackPawns;
	private int[] pawns;
	
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
		king=-1;
		
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
				if(king!=-1 && s.getPawn(i, j).equals(Pawn.KING)) {
					pawns[indexPawns]=i*DIM+j;
					indexPawns++;
					king=i*DIM+j;
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
	private List<Action> calculateActions(int pawnValue, Turn t){
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
				if(pawns[indexPawnToCheck]==currentPawnValue){
					//indexPawnToCheck++; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					if(isPermitted(pawnValue, currentPawnValue, t)) azioni.add(action);
				}
			}
			
			// ***************************************************************
			// ***controlliamo la strada percorribile SOTTO alla pedina***
			// ***************************************************************
			for(int currentPawnValue=pawnValue+DIM; currentPawnValue<=DIM*(DIM-1)+column; currentPawnValue+=DIM) { //sotto
				while(pawns[indexPawnToCheck]<currentPawnValue && pawns[indexPawnToCheck]!=-1) {
					indexPawnToCheck++;
				}
				if(pawns[indexPawnToCheck]==currentPawnValue){
					//indexPawnToCheck++; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					if(isPermitted(pawnValue, currentPawnValue, t)) azioni.add(action);
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
				if(pawns[indexPawnToCheck]==currentPawnValue){
					//indexPawnToCheck--; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					if(isPermitted(pawnValue, currentPawnValue, t)) azioni.add(action);
				}
			}
			
			
			// ***************************************************************
			// ***controlliamo la strada percorribile SOPRA alla pedina***
			// ***************************************************************
			for(int currentPawnValue=pawnValue-DIM; currentPawnValue>=column; currentPawnValue-=DIM) { //sopra
				while(pawns[indexPawnToCheck]>currentPawnValue && pawns[indexPawnToCheck]!=-1) {
					indexPawnToCheck--;
				}
				if(pawns[indexPawnToCheck]==currentPawnValue){
					//indexPawnToCheck--; //probabilmente non serve
					break;
				}else {
					int newRow = currentPawnValue/DIM;
					int newColumn = currentPawnValue-(newRow*DIM);
					Action action = new Action(getBox(row,column), getBox(newRow, newColumn), t);
					if(isPermitted(pawnValue, currentPawnValue, t)) azioni.add(action);
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
		int[] proibiteNere = {3,4,5,13,27,35,36,37,43,44,45,53,67,75,76,77};
		List<Integer> campo1 = new ArrayList<Integer>();
		List<Integer> campo2 = new ArrayList<Integer>();
		List<Integer> campo3 = new ArrayList<Integer>();
		List<Integer> campo4 = new ArrayList<Integer>();
		
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
		
		int castello = 40;
		
		if (to == castello) return false;
		
		if(t.equals(Turn.WHITE)) {
			 for(int casella : proibiteNere) {
				 if(casella == to) return false;
			 }
		}else {
			if(campo1.contains(to) && !(campo1.contains(from))) return false; 
			if(campo2.contains(to) && !(campo2.contains(from))) return false; 
			if(campo3.contains(to) && !(campo3.contains(from))) return false; 
			if(campo4.contains(to) && !(campo4.contains(from))) return false; 
		}
		return true;
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
		// TODO Auto-generated method stub
		// stato futuro in base all'attuale 
		return null;
	}
	

	@Override
	public boolean isTerminal(StateTablut s) {
		// TODO Auto-generated method stub
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
		return kingEscapes || kingCaptured || noMoves; //manca caso pareggio = stato ripetuto
	}
	
	
	
	private int findKing(StateTablut state) { //trova la posizione del re nella scacchiera
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				if(state.getPawn(i, j).equals(Pawn.KING)) {
					return i*9 +j;	
				}
			}
		}
		return -1;
	}
	
	private boolean kingEscapes(int posKing) { //il re raggiunge una posizione di salvataggio sul bordo
		
		List<Integer> escapeTiles = new ArrayList<Integer>();
		
		escapeTiles.add(1); //posizioni caselle blu per vincita re
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
		if (escapeTiles.contains(posKing)) {
			return true; //vittoria del re il re � scappato sul bordo
		}else {
			return false;
		}
		
	}
	
	private boolean kingCaptured(int posKing, StateTablut state) { // il re viene catturato
		int riga=posKing/9;
		int col=posKing-(riga*9);
		Pawn sopra ;
		Pawn sotto ;
		Pawn destra; 
		Pawn sinistra; 
		if( riga !=0) sopra=state.getPawn(riga-1, col);
		else sopra =Pawn.EMPTY;
		if( riga <9 ) sotto =state.getPawn(riga+1, col);
		else sotto =Pawn.EMPTY;
		if(col <9) destra =state.getPawn(riga, col+1); 
		else destra =Pawn.EMPTY;
		if(col !=0) sinistra =state.getPawn(riga, col-1);
		else sinistra =Pawn.EMPTY;
	
		// il re si trova nel castello ed � circondato
		if(posKing==40 && sopra.equals(Pawn.BLACK)
				&& sotto.equals(Pawn.BLACK)
				&& destra.equals(Pawn.BLACK)
				&& sinistra.equals(Pawn.BLACK)) {
			return true; 
			
		}
		//il re si trova in una posizione adiacente al castello e vine catturato se circondato sui tre lati restanti
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
	//funzione generale per controllare se una pedina � stata catturata
	
	private boolean pawnCaptured(StateTablut state,int riga, int col, Pawn sopra, Pawn sotto, Pawn destra, Pawn sinistra) {
		
		Turn turnVittima = state.getTurn();
		Pawn pawnPredatore = turnVittima.equals(Turn.WHITE) ? Pawn.BLACK : Pawn.WHITE ;
		 sopra = sopra.equals(Pawn.KING) ? Pawn.WHITE : sopra;
		 sotto = sotto.equals(Pawn.KING) ? Pawn.WHITE : sotto;
		 destra = destra.equals(Pawn.KING) ? Pawn.WHITE : destra;
		 sinistra = sinistra.equals(Pawn.KING) ? Pawn.WHITE : sinistra;
		 
		List<Integer> campo = new ArrayList<Integer>();
		campo.add(3);
		campo.add(4);
		campo.add(5);
		campo.add(13);
		campo.add(27);
		campo.add(36);
		campo.add(37);
		campo.add(45);
		campo.add(35);
		campo.add(43);
		campo.add(44);
		campo.add(53);
		campo.add(67);
		campo.add(75);
		campo.add(76);
		campo.add(77);
		
		if ((sopra.equals(pawnPredatore) || campo.contains(riga*9+col-9) || riga*9+col-9 ==40 ) && (sotto.equals(pawnPredatore) || campo.contains(riga*9+col+9) || riga*9+col+9 ==40)) {
			return true;
		}
		if ((destra.equals(pawnPredatore) || campo.contains(riga*9+col +1) || riga*9+col+1 == 40) && (sinistra.equals(pawnPredatore) || campo.contains(riga*9+col-1) ||riga*9+col-1 ==40)) {
			return true;
		}
		return false; 
	}
	
	@Override
	public Turn getPlayer(StateTablut s) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public Turn[] getPlayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getUtility(StateTablut s, Turn arg1) {
		// TODO Auto-generated method stub
		//EURISTICA (dovrebbe essere meglio il valore pi� grande)
		return 0;
	}

	

}

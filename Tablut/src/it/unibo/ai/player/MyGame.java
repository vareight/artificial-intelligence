package it.unibo.ai.player;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;


public class MyGame implements Game<StateTablut, Action, State.Turn> {
	
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
	
	private int expansion=1000;
	private final int DIM = 9;
	//private int NUM_WHITE_PAWNS = 8;
	//private int NUM_BLACK_PAWNS = 16;
	//private int NUM_PAWNS = 25;
	
	//private int king;
	//private int[] whitePawns = new int[NUM_WHITE_PAWNS];
	//private int[] blackPawns = new int[NUM_BLACK_PAWNS];
	//private int[] pawns = new int[NUM_PAWNS];
	private BoardState board= BoardState.getIstance();
	//private GameAshtonTablut game;
	private StateTablut initialState;
	private MoveResult moveResult;
	private ActionsUtils actions;
	private TurnNumberSingleton turn=TurnNumberSingleton.getIstance();
	private int isTerminalCalls;

	
		
	public MyGame(StateTablut initialState, GameAshtonTablut game) {
		super();
		this.initialState = initialState;
		this.isTerminalCalls=0;
		//this.game=game;
		this.actions = new ActionsUtils(initialState);
		this.moveResult = new MoveResult(game.getRepeated_moves_allowed(), game.getCache_size());
	}

	
	@Override
	public List<Action> getActions(StateTablut s) {
		Turn turn= s.getTurn();
		this.actions = new ActionsUtils(s);
		List <Action> act= new ArrayList<Action>(); 
		if (turn.equals(Turn.WHITE)) act= actions.whiteActions();
		if (turn.equals(Turn.BLACK)) act= actions.blackActions();
		
		return act; //TODO capire se restituire altro quando non � W o B
	}
	

	@Override
	public StateTablut getInitialState() {
		return this.initialState;
	}


	@Override
	public StateTablut getResult(StateTablut s, Action a) {
		StateTablut newState = null;
		StateTablut clonedState = s.clone();
		//newState= (StateTablut) game.movePawn(clonedState, a);
		newState= moveResult.makeMove(clonedState, a);
		newState.setTurnCount(newState.getTurnCount()+1);
			
		
		return newState;
	}
	

	@Override
	public boolean isTerminal(StateTablut s) {
		isTerminalCalls++;	
		Turn t= s.getTurn();
		boolean finishTurn = t.equals(Turn.BLACKWIN) || t.equals(Turn.WHITEWIN) || t.equals(Turn.DRAW);
		
		if(finishTurn) {
			return true;
		}
		else if(isTerminalCalls-turn.getTurn() >=expansion) {
			return true;
		}
	
		
		return false; // TODO modificare
//		return finishTurn || noMoves;
		

		
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
		this.actions = new ActionsUtils(s);
		EuristicaUtils euristica= new EuristicaUtils(this.actions);
		
		double punteggio = 0;
		if(t.equals(Turn.BLACK)) {
			switch(s.getTurn()) {
			case DRAW : punteggio=0; break;
			case WHITEWIN : punteggio=-10000; break;
			case BLACKWIN : punteggio=10000; break;
			default : punteggio=euristica.euristicaBlack(s, actions.getWhitePawns(),actions.getKing());
			}
			
		}
		if(t.equals(Turn.WHITE)) {
			switch(s.getTurn()) {
			case DRAW : punteggio=0; break;
			case WHITEWIN : punteggio=10000; break;
			case BLACKWIN : punteggio=-10000; break;
			default : punteggio=euristica.euristicaWhite(s, actions.getWhitePawns(),actions.getKing());
			}
		}
		//EURISTICA (dovrebbe essere meglio il valore pi� grande)
		return punteggio;
	}
	
/*---------------------------------FUNZIONI DA SPOSTARE o ELIMINARE-------------------------------------------------------- 	
	
	/**
	 * Funzione che trova la posizione del re nella scacchiera
	 * @param state
	 * @return the position of the king, -1 if not found
	 */
/*	private int findKing(StateTablut state) {
		for(int i=0; i<DIM; i++) {
			for(int j=0; j<DIM; j++) {
				if(state.getPawn(i, j).equals(Pawn.KING)) {
					return i*DIM +j;	
				}
			}
		}
		return -1;
	}
*/	
	/**
	 * Funzione che rileva se il re raggiunge una posizione di salvataggio sul bordo
	 * @param posKing
	 * @return true if the king escapes, false otherwise
	 */
/*	private boolean kingEscapes(int posKing) {
		return board.isEscapeTile(posKing);
	}
*/	
	/**
	 * Funzione che rileva se il re viene catturato
	 * @param posKing
	 * @param state
	 * @return true if is captured, false otherwise
	 */
/*	private boolean kingCaptured(int posKing, StateTablut state) { // 
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
*/	
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
/*	private boolean pawnCaptured(StateTablut state,int riga, int col, Pawn sopra, Pawn sotto, Pawn destra, Pawn sinistra) {
		
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
	*/
	
	
	
	

}

package it.unibo.ai.player;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
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
	
	
	private final int expansionTurn=4;
	private StateTablut initialState;
	private MoveResult moveResult;
	private ActionsUtils actions;
	private int nodiEspansi;

	
		
	public MyGame(StateTablut initialState, GameAshtonTablut game) {
		super();
		this.initialState = initialState;
		this.nodiEspansi=0;
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
		
		return act; 
	}
	

	@Override
	public StateTablut getInitialState() {
		return this.initialState;
	}


	@Override
	public StateTablut getResult(StateTablut s, Action a) {
		StateTablut newState = null;
		StateTablut clonedState = s.clone();
		newState= moveResult.makeMove(clonedState, a);
		newState.setTurnCount(newState.getTurnCount()+1);
		return newState;
	}
	

	@Override
	public boolean isTerminal(StateTablut s) {
			
		Turn t= s.getTurn();
		boolean finishTurn = t.equals(Turn.BLACKWIN) || t.equals(Turn.WHITEWIN) || t.equals(Turn.DRAW);
		
		if(finishTurn) {
			return true;
		}
		else if(s.getTurnCount() - this.initialState.getTurnCount()>=expansionTurn) {
			return true;
		}
		
		nodiEspansi++;
		
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
		this.actions = new ActionsUtils(s);
		EuristicaUtils euristica= new EuristicaUtils();
		
		double punteggio = 0;
		if(t.equals(Turn.BLACK)) {
			switch(s.getTurn()) {
			case DRAW : punteggio=0; break;
			case WHITEWIN : punteggio=Double.NEGATIVE_INFINITY; break;
			case BLACKWIN : punteggio=Double.POSITIVE_INFINITY; break;
			default : punteggio=euristica.euristicaBlack(s, actions.getWhitePawns(),actions.getKing());
			}
			
		}
		if(t.equals(Turn.WHITE)) {
			switch(s.getTurn()) {
			case DRAW : punteggio=0; break;
			case WHITEWIN : punteggio=Double.POSITIVE_INFINITY; break;
			case BLACKWIN : punteggio=Double.NEGATIVE_INFINITY; break;
			default : punteggio=euristica.euristicaWhite(s, actions.getWhitePawns(),actions.getKing());
			}
		}
		return punteggio;
	}
	public int getNodiEspansi() {
		return nodiEspansi;
	}
}

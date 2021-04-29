package it.unibo.ai.player;

import aima.core.search.adversarial.AlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

//classe che si occupa dell'alpha beta search da invocare su game

public class BestMoveFinder {
	private MyGame game;
	private AlphaBetaSearch<StateTablut, Action, State.Turn> alphabeta;

	public BestMoveFinder(StateTablut state, GameAshtonTablut rules) {
		game = new MyGame(state, rules);
		//game = new MyGame(rules);
		alphabeta = AlphaBetaSearch.createFor(game);
	}

	public Action findBestAction(StateTablut state) {
		
		return alphabeta.makeDecision(state);
	}

	public void setCurrentGame(MyGame game) {
		this.game = game;
	}


}

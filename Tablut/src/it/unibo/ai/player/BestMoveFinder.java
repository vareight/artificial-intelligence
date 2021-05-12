package it.unibo.ai.player;

import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

//classe che si occupa dell'alpha beta search da invocare su game

public class BestMoveFinder {
	private MyGame game;
	private IterativeDeepeningAlphaBetaSearch<StateTablut,Action,State.Turn> iterative;

	public BestMoveFinder(StateTablut state, GameAshtonTablut rules, int timeout) {
		game = new MyGame(state, rules);
		iterative = IterativeDeepeningAlphaBetaSearch.createFor(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, timeout-5);
	}

	public Action findBestAction(StateTablut state) {
		return iterative.makeDecision(state);
	}

	public void setCurrentGame(MyGame game) {
		this.game = game;
	}

	public MyGame getCurrentGame() {
		return this.game;
	}
}

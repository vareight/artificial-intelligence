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
	@Override
	public List<Action> getActions(StateTablut s) {
		Turn turn= s.getTurn();
		int[] whitePawns = {-1,-1,-1,-1,-1,-1,-1,-1};
		int[] blackPawns = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		int[] pawns = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		int king=-1,indexWhite=0, indexBlack=0, indexPawns=0;
		
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				if(indexWhite<8 && s.getPawn(i, j).equals(Pawn.WHITE)) {
					whitePawns[indexWhite]=i*9+j;
					indexWhite++;
					pawns[indexPawns]=i*9+j;
					indexPawns++;
				}
				if(king!=-1 && s.getPawn(i, j).equals(Pawn.KING)) {
					pawns[indexPawns]=i*9+j;
					indexPawns++;
					king=i*9+j;
				}
				if(indexBlack<16 && s.getPawn(i, j).equals(Pawn.BLACK)) {
					blackPawns[indexBlack]=i*9+j;
					indexBlack++;
					pawns[indexPawns]=i*9+j;
					indexPawns++;
				} //decidere se mettere break raggiunto il numero
			}
		}
		
		
		
		if (turn.equals(Turn.WHITE)) return whiteActions(whitePawns, king, pawns);
		if (turn.equals(Turn.BLACK)) return blackActions(blackPawns, pawns);
		
		return null; //TODO capire se restituire altro quando non è W o B
	}
	
	private List<Action> whiteActions(int[] whitePawns, int king, int[] pawns){
		List<Action> azioni = new ArrayList<Action>();
		
			for(int i=0; i<8; i++) {
				if(whitePawns[i]!=-1) {
					azioni.addAll(calculateActions(pawns, whitePawns[i], Turn.WHITE));
				}else break;
			}
			
			azioni.addAll(calculateActions(pawns, king, Turn.WHITE));
			
		
		return azioni;
	}
	
	private String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}
	
	private List<Action> calculateActions( int[] pawns, int val, Turn t){
		int riga= val/9;
		int col= val-(riga*9);
		List<Action> azioni = new ArrayList<Action>();
		int j, indexPawns;
		try {
			for(j=0; j<25; j++) {
				
				if(pawns[j]==val) break; //j è la posizione del piedino
			}
			indexPawns = j;
			if( j<24 && pawns[j+1]!=-1) {
				indexPawns=j+1;
			}

			for(int k=val+1; k<=riga*9+8 && k<81; k++) { //dx
				
				if(pawns[indexPawns]==k){
					indexPawns++; //probabilmente non serve
					break;
				}else {
					Action a = new Action(getBox(riga,col), getBox(k/9, k-(k/9*9)), t);
					if(isPermitted(val, k, t)) azioni.add(a);
				}
			}
			for(int k=val+9; k<=9*8+col && k<81; k+=9) { //sotto
				while(pawns[indexPawns]<k && pawns[indexPawns]!=-1) {
					indexPawns++;
				}
				if(pawns[indexPawns]==k){
					indexPawns++; //probabilmente non serve
					break;
				}else {
					Action a = new Action(getBox(riga,col), getBox(k/9, k-(k/9)*9), t);
					if(isPermitted(val, k, t)) azioni.add(a);
				}
			}
		
			if(j!=0) {
				indexPawns=j-1;
			}
			for(int k=val-1; k>=riga*9 && k>=0; k--) { //sx

				if(pawns[indexPawns]==k){
					indexPawns--; //probabilmente non serve
					break;
				}else {
					Action a = new Action(getBox(riga,col), getBox(k/9, k-(k/9)*9), t);
					if(isPermitted(val, k, t)) azioni.add(a);
				}
			}
			for(int k=val-9; k>=col && k>=0; k-=9) { //sopra
				while(pawns[indexPawns]>k && pawns[indexPawns]!=-1) {
					indexPawns--;
				}
				if(pawns[indexPawns]==k){
					indexPawns--; //probabilmente non serve
					break;
				}else {
					Action a = new Action(getBox(riga,col), getBox(k/9, k-(k/9)*9), t);
					if(isPermitted(val, k, t)) azioni.add(a);
				}
		
			}

		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return azioni;
	}
	
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
	
	
	private List<Action> blackActions(int[] blackPawns, int[] pawns){
		List<Action> azioni = new ArrayList<Action>();
		
		for(int i=0; i<16; i++) {
			if(blackPawns[i]!=-1) {
				azioni.addAll(calculateActions(pawns, blackPawns[i], Turn.BLACK));
			}else break;
		}
		
		return azioni;
	}

	@Override
	public StateTablut getInitialState() {
		// TODO Auto-generated method stub
		return null;
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
		//EURISTICA (dovrebbe essere meglio il valore più grande)
		return 0;
	}

	

}

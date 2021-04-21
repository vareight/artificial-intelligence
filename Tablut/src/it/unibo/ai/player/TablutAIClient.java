package it.unibo.ai.player;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.client.TablutRandomClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameModernTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateBrandub;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutAIClient extends TablutClient {

	public TablutAIClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		String role = "";
		String name = "tulbaTeam";
		String ipAddress = "localhost";
		int timeout = 60;
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			timeout = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			ipAddress = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		TablutAIClient client = new TablutAIClient(role, name, timeout, ipAddress);
		client.run();
	}
	


	
	@Override
	public void run() {

		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

	
		StateTablut state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		GameAshtonTablut rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
		System.out.println("Ashton Tablut game");
	
	

		System.out.println("You are player " + this.getPlayer().toString() + "!");

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			state = (StateTablut) this.getCurrentState(); //non sono sicura del casting
			System.out.println(state.toString());
			
			if (this.getPlayer().equals(Turn.WHITE)) {
				// Mio turno WHITE
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					BestMoveFinder finder= new BestMoveFinder(state, rules);
					Action action =finder.findBestAction(state);
					if(action == null) {
						State stateProva = this.getCurrentState();
						Action action2 = finder.findBestAction(state);
					}
					System.out.println("Mossa scelta: " + action.toString());
				
					try {
						this.write(action);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				// Turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			} else {

				// Mio turno BLACK
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					BestMoveFinder finder= new BestMoveFinder(state, rules);
					Action action =finder.findBestAction(state);
					if(action == null) {
						State stateProva = this.getCurrentState();
						Action action2 = finder.findBestAction(state);
					}
					System.out.println("Mossa scelta: " + action.toString());
				
					try {
						this.write(action);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}			

				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} 
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} 
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} 
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
		}

	}

}
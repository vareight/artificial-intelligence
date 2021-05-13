package it.unibo.ai.player;

import java.io.IOException;
import java.net.UnknownHostException;


import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutAIClient extends TablutClient {

	private static int timeout = 60;
	
	public TablutAIClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		String role = "";
		String name = "tulbaTeam";
		String ipAddress = "localhost";

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
		
			System.out.println("\n"+
					   "+---------------------- Tablut Challenge 2021  ----------------------+");
	        System.out.println(
	        		   "|                                                                    |\n" + 
		               "|                     t  u  l  b  a  T  e  a  m                      |\n" +
		               "|                                                                    |\n" );
	        System.out.println(
	                   "+---------------------- Autori  Ila, Ric, Sof -----------------------+\n");
		
		TurnNumberSingleton turn= TurnNumberSingleton.getIstance();
		

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
			
			try { 
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			if (this.getPlayer().equals(Turn.WHITE)) {
				// Mio turno WHITE
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					state.setTurnCount(turn.getTurn());
					BestMoveFinder finder= new BestMoveFinder(state, rules, timeout);
					Action action =finder.findBestAction(state);
					System.out.println("***Turno " + turn.getTurn());
//					if(action == null) { // a che servono? non li uso mai questi due..
//						State stateProva = this.getCurrentState();
//						Action action2 = finder.findBestAction(state);
//					}
					System.out.println("Mossa scelta: " + action.toString());
					System.out.println("Nodi espansi: "+finder.getCurrentGame().getNodiEspansi());
					try {
						this.write(action);
						turn.newTurn();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				// Turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
					turn.newTurn();
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
					state.setTurnCount(turn.getTurn());
					BestMoveFinder finder= new BestMoveFinder(state, rules, timeout);
					Action action =finder.findBestAction(state);
					System.out.println("***Turno " + turn.getTurn());
					System.out.println("***Mossa scelta: " + action.toString());
					System.out.println("Nodi espansi: "+finder.getCurrentGame().getNodiEspansi());
					try {
						this.write(action);
						turn.newTurn();
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}			

				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
					turn.newTurn();
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

package it.unibo.ai.player;

public class TurnNumberSingleton {
	 private static TurnNumberSingleton turnNumberIstance=null; // riferimento all' istanza
	 private static int turnNumber;
	  private TurnNumberSingleton() {
		 turnNumber=0;
	  }

	  public static TurnNumberSingleton getIstance() {
	    if(turnNumberIstance==null)
	    	turnNumberIstance = new TurnNumberSingleton();
	    return turnNumberIstance;
	  }

	public void newTurn() {
		TurnNumberSingleton.turnNumber ++;
	}
	public int getTurn() {
		return TurnNumberSingleton.turnNumber;
	}

	  
}



package it.unibo.ai.test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.player.MyGame;

class MyGameTest {
	   private MyGame mygame;
	   private StateTablut state;
	   
	   private String getBox(int row, int column) {
			String ret;
			char col = (char) (column + 97);
			ret = col + "" + (row + 1);
			return ret;
		}
		
	   
	    @BeforeEach                                         
	    public void setUp() throws Exception {
	        this.mygame = new MyGame();
	        this.state = new StateTablut();
	    }

	    @Test  
	    public void testGetActionsBlackBegin() throws IOException {
	    	Turn turn = Turn.BLACK;
	    	state.setTurn(turn);
	    	List <Action> actions = new ArrayList<>();
	    	
	    	 //pedone nero posizione 03
	    	actions.add( new Action(getBox(0,3), getBox(0,2), turn));
	    	actions.add(new Action(getBox(0,3), getBox(0,1), turn));
	    	actions.add( new Action(getBox(0,3), getBox(0,0), turn));
	    	actions.add( new Action(getBox(0,3), getBox(1,3), turn));
	    	actions.add( new Action(getBox(0,3), getBox(2,3), turn));
	    	actions.add( new Action(getBox(0,3), getBox(3,3), turn));
	    	 
	    	//pedone nero posizione 05
	    	actions.add(new Action(getBox(0,5), getBox(0,6), turn));
	    	actions.add( new Action(getBox(0,5), getBox(0,7), turn));
	    	actions.add(new Action(getBox(0,5), getBox(0,8), turn));
	    	actions.add(new Action(getBox(0,5), getBox(1,5), turn));
	    	actions.add( new Action(getBox(0,5), getBox(2,5), turn));
	    	actions.add( new Action(getBox(0,5), getBox(3,5), turn));
	    	
	    	
	    	//pedone nero posizione 14
	    	actions.add( new Action(getBox(1,4), getBox(1,0), turn));
	    	actions.add( new Action(getBox(1,4), getBox(1,1), turn));
	    	actions.add( new Action(getBox(1,4), getBox(1,2), turn));
	    	actions.add( new Action(getBox(1,4), getBox(1,3), turn));
	    	actions.add(new Action(getBox(1,4), getBox(1,5), turn));
	    	actions.add( new Action(getBox(1,4), getBox(1,6), turn));
	    	actions.add( new Action(getBox(1,4), getBox(1,7), turn));
	    	actions.add( new Action(getBox(1,4), getBox(1,8), turn));
	    	
	    	//pedone nero posizione 30
	    	actions.add( new Action(getBox(3,0), getBox(3,1), turn));
	    	actions.add( new Action(getBox(3,0), getBox(3,2), turn));
	    	actions.add( new Action(getBox(3,0), getBox(3,3), turn));
	    	actions.add( new Action(getBox(3,0), getBox(2,0), turn));
	    	actions.add( new Action(getBox(3,0), getBox(1,0), turn));
	    	actions.add( new Action(getBox(3,0), getBox(0,0), turn));
	    	
	    	
	    	//pedone nero posizione 38
	    	actions.add( new Action(getBox(3,8), getBox(3,7), turn));
	    	actions.add( new Action(getBox(3,8), getBox(3,6), turn));
	    	actions.add( new Action(getBox(3,8), getBox(3,5), turn));
	    	actions.add( new Action(getBox(3,8), getBox(2,8), turn));
	    	actions.add( new Action(getBox(3,8), getBox(1,8), turn));
	    	actions.add( new Action(getBox(3,8), getBox(0,8), turn));
	    	
	    	//pedone  nero posizione 41
	    	actions.add( new Action(getBox(4,1), getBox(3,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(2,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(1,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(0,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(5,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(6,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(7,1), turn));
	    	actions.add( new Action(getBox(4,1), getBox(8,1), turn));
	    	
	    	//pedone  nero posizione 47
	    	actions.add( new Action(getBox(4,7), getBox(3,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(2,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(1,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(0,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(5,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(6,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(7,7), turn));
	    	actions.add( new Action(getBox(4,7), getBox(8,7), turn));
	    	
	    	//pedone nero posizione 50
	    	actions.add( new Action(getBox(5,0), getBox(5,1), turn));
	    	actions.add( new Action(getBox(5,0), getBox(5,2), turn));
	    	actions.add( new Action(getBox(5,0), getBox(5,3), turn));
	    	actions.add( new Action(getBox(5,0), getBox(6,0), turn));
	    	actions.add( new Action(getBox(5,0), getBox(7,0), turn));
	    	actions.add( new Action(getBox(5,0), getBox(8,0), turn));
	    	
	    	
	    	//pedone nero posizione 58
	    	actions.add( new Action(getBox(5,8), getBox(5,7), turn));
	    	actions.add( new Action(getBox(5,8), getBox(5,6), turn));
	    	actions.add( new Action(getBox(5,8), getBox(5,5), turn));
	    	actions.add( new Action(getBox(5,8), getBox(6,8), turn));
	    	actions.add( new Action(getBox(5,8), getBox(7,8), turn));
	    	actions.add( new Action(getBox(5,8), getBox(8,8), turn));
	    	
	    	//pedone nero posizione 74
	    	
	    	actions.add( new Action(getBox(7,4), getBox(7,0), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,1), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,2), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,3), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,5), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,6), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,7), turn));
	    	actions.add( new Action(getBox(7,4), getBox(7,8), turn));
	    	
	    	 //pedone nero posizione 83
	    	actions.add( new Action(getBox(8,3), getBox(8,2), turn));
	    	actions.add( new Action(getBox(8,3), getBox(8,1), turn));
	    	actions.add( new Action(getBox(8,3), getBox(8,0), turn));
	    	actions.add( new Action(getBox(8,3), getBox(7,3), turn));
	    	actions.add( new Action(getBox(8,3), getBox(6,3), turn));
	    	actions.add( new Action(getBox(8,3), getBox(5,3), turn));
	    	 
	    	//pedone nero posizione 85
	    	actions.add( new Action(getBox(8,5), getBox(8,6), turn));
	    	actions.add( new Action(getBox(8,5), getBox(8,7), turn));
	    	actions.add( new Action(getBox(8,5), getBox(8,8), turn));
	    	actions.add( new Action(getBox(8,5), getBox(7,5), turn));
	    	actions.add( new Action(getBox(8,5), getBox(6,5), turn));
	    	actions.add( new Action(getBox(8,5), getBox(5,5), turn));
	    	
	    	
	        assertEquals(80, mygame.getActions(state).size(), "Numer of initial moves avaible for black paws should be 80");     
	       // assertEquals(actions, mygame.getActions(state), "Initial moves for black pawns"); // funziona se non sono ordinate uguali le due liste ?
	       
	        List <Action> calculatedactions= mygame.getActions(state);
	        
	        for(Action a : actions) {
	        	 assertTrue(calculatedactions.contains(a));
	        }
	        
	       
	        
	    }

	    @Test  
	    public void testGetActionsWhiteBegin() throws IOException {
	    	Turn turn = Turn.WHITE;
	    	List <Action> actions = new ArrayList<>();
	    	state.setTurn(turn);
	    	
	    	 //pedone bianco posizione 24
	    	actions.add( new Action(getBox(2,4), getBox(2,0), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,1), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,2), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,3), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,5), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,6), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,7), turn));
	    	actions.add( new Action(getBox(2,4), getBox(2,8), turn));
	    	
	    	 //pedone bianco posizione 34
	    	actions.add( new Action(getBox(3,4), getBox(3,1), turn));
	    	actions.add( new Action(getBox(3,4), getBox(3,2), turn));
	    	actions.add( new Action(getBox(3,4), getBox(3,3), turn));
	    	actions.add( new Action(getBox(3,4), getBox(3,5), turn));
	    	actions.add( new Action(getBox(3,4), getBox(3,6), turn));
	    	actions.add( new Action(getBox(3,4), getBox(3,7), turn));
	    
	    	 
	    	 //pedone bianco posizione 42
	    	actions.add( new Action(getBox(4,2), getBox(0,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(1,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(2,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(3,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(5,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(6,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(7,2), turn));
	    	actions.add( new Action(getBox(4,2), getBox(8,2), turn));
	    	
	    	 //pedone bianco posizione 43
	    	actions.add( new Action(getBox(4,3), getBox(1,3), turn));
	    	actions.add( new Action(getBox(4,3), getBox(2,3), turn));
	    	actions.add( new Action(getBox(4,3), getBox(3,3), turn));
	    	actions.add( new Action(getBox(4,3), getBox(5,3), turn));
	    	actions.add( new Action(getBox(4,3), getBox(6,3), turn));
	    	actions.add( new Action(getBox(4,3), getBox(7,3), turn));
	    	
	    	
	    	 //pedone bianco posizione 45
	    	actions.add( new Action(getBox(4,5), getBox(1,5), turn));
	    	actions.add( new Action(getBox(4,5), getBox(2,5), turn));
	    	actions.add( new Action(getBox(4,5), getBox(3,5), turn));
	    	actions.add( new Action(getBox(4,5), getBox(5,5), turn));
	    	actions.add( new Action(getBox(4,5), getBox(6,5), turn));
	    	actions.add( new Action(getBox(4,5), getBox(7,5), turn));
	    	
	    	//pedone bianco posizione 46
	    	actions.add( new Action(getBox(4,6), getBox(0,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(1,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(2,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(3,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(5,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(6,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(7,6), turn));
	    	actions.add( new Action(getBox(4,6), getBox(8,6), turn));
	    	
	   	 	//pedone bianco posizione 54
	    	actions.add( new Action(getBox(5,4), getBox(5,1), turn));
	    	actions.add( new Action(getBox(5,4), getBox(5,2), turn));
	    	actions.add( new Action(getBox(5,4), getBox(5,3), turn));
	    	actions.add( new Action(getBox(5,4), getBox(5,5), turn));
	    	actions.add( new Action(getBox(5,4), getBox(5,6), turn));
	    	actions.add( new Action(getBox(5,4), getBox(5,7), turn));
	    	
	    	 //pedone bianco posizione 64
	    	actions.add( new Action(getBox(6,4), getBox(6,0), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,1), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,2), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,3), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,5), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,6), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,7), turn));
	    	actions.add( new Action(getBox(6,4), getBox(6,8), turn));
	    	
	   
	    	List <Action> calculatedactions= mygame.getActions(state);
	        assertEquals(56, calculatedactions.size(), "Numer of initial moves avaible for white paws should be 56");     
	        //assertEquals(actions, mygame.getActions(state), "Initial moves for white pawns");
	        
	        for(Action a : actions) {
	        	if(!calculatedactions.contains(a)) {
	        		System.out.println(a.toString());
	        	}
	        	 assertTrue(calculatedactions.contains(a));
	        }
	        
	    }
	    

}

package restaurant.gui;

import restaurant.CookAgent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CookGui implements Gui {

    private CookAgent agent = null;
    
    private int xPos, yPos;//default waiter position
    private int xDestination, yDestination;//default start position

    Graphics2D Plating;
    Graphics2D Grill;
    Graphics2D Fridge;
    public static final int checkIn = -10;
    public static final int waiterSize = 20;
    public static final int waitingX = 100;
    public static final int waitingY = 380;
    public static final int xPlating = 160;
    public static final int yPlating = 420;
    public static final int xFridge = 45;
    public static final int yFridge = 375;
   
    private boolean transferring = false;
    private String food = new String();
    
    public Map <Integer, Dimension> TableLocations = new HashMap<Integer, Dimension>();
    private List <grillSpot> GrillSpots = new ArrayList<grillSpot>();
    private List <plateSpot> PlateSpots = new ArrayList<plateSpot>();
    
    public class plateSpot{
        	int x;
        	int y;
        	boolean used;
        	String food;
        	public plateSpot(int position){
        		x = 160 + 30*position;
        		y = 420;
        		used = false;
        		food = "poop";
        	}
    }
    public class grillSpot{
    	int x;
    	int y;
    	boolean used;
    	String food;
    	public grillSpot(int position){
    		x = 80 + 30*position;
    		y = 420;
    		used = false;
    		food = "poop";
    	}
    }

    public CookGui(CookAgent c) {
        agent = c;
		xPos = 100;
		yPos = 380;
		xDestination = xPos;
		yDestination = yPos;
		for(int i = 0; i<3; i++){
			GrillSpots.add(new grillSpot(i));
		}
		for(int i = 0; i<3; i++){
			PlateSpots.add(new plateSpot(i));
		}
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination && (xPos != 100 && yPos != 380)) {
           agent.msgAtDestination();
           if (yDestination == 420)
        	   transferring = false;
           xDestination = waitingX;
           yDestination = waitingY;
        }
    }

    public void draw(Graphics2D g) {
    	Plating = (Graphics2D)g;
        Grill = (Graphics2D)g;
        Fridge = (Graphics2D)g;
    	Plating.setColor(Color.yellow);
        Plating.fillRect(160, 420, 60, 20);
        Grill.setColor(Color.RED);
        Grill.fillRect(80, 420, 60, 20);
        Fridge.setColor(Color.white);
        Fridge.fillRect(20, 350, 50, 50);
        
        g.setColor(Color.BLUE);
        g.fillRect(xPos, yPos, waiterSize, waiterSize);
        if(transferring){
        	g.setColor(Color.BLACK);
        	g.drawString(food, xPos, yPos);
        }
        else{
            for(grillSpot gs : GrillSpots){
            	if(gs.used && !transferring){
            		g.setColor(Color.BLACK);
            		g.drawString(gs.food, gs.x, gs.y + 10);	
            	}
            }
            for(plateSpot ps : PlateSpots){
            	if(ps.used && !transferring){
            		g.setColor(Color.BLACK);
            		g.drawString(ps.food, ps.x, ps.y + 10);	
            	}
            }
        }
    }

    public boolean isPresent() {
        return true;						//USed to just return true
    }

    public void DoGoToOrigin() {
    	transferring = false;
    	xDestination = checkIn;
    	yDestination = checkIn;
    }
    
    public void DoGoToFridge() {
        xDestination = xFridge;
        yDestination = yFridge;
    }
    
    public void DoGrillIt(String f){
    	for(grillSpot gs : GrillSpots){
    		if(!gs.used){
    			gs.food = f;
    			xDestination = gs.x;
    			yDestination = gs.y;
    			transferring = true;
    			food = f;
    			gs.used = true;
    			return;
    		}
    	}
    }
    
    public void DoRemoveFromGrill(String f){
    	for(grillSpot gs : GrillSpots){
    		if(gs.food.equals(f)){
    			xDestination = gs.x;
    			yDestination = gs.y;
    			gs.food = " ";
    			gs.used = false;
    			return;
    		}
    	}
    }
    
    public void DoBringToPlating(String f){
    	for(grillSpot gs : GrillSpots){
    		if(gs.food.equals(f)){
    			gs.used = false;
    			break;
    		}
    	}
    	for(plateSpot ps : PlateSpots){
    		if(!ps.used){
    			ps.food = f;
    			xDestination = ps.x;
    			yDestination = ps.y;
    			transferring = true;
    			food = f;
    			ps.used = true;
    			return;
    		}
    	}
    }
    
    public void DoPlateIt(String f){
    	transferring = false;
    }
    
    public void DoOrderRemoved(String f){
    	for(plateSpot ps : PlateSpots){
    		if(ps.food.equals(f)){
    			ps.used = false;
    			return;
    		}
    	}
    }
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    
}

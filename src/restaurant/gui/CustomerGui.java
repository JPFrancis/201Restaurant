package restaurant.gui;

import restaurant.CustomerAgent;

import java.awt.*;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;
	private int xHome;
	private int yHome;

	//private HostAgent host;
	RestaurantGui gui;

	Graphics foodIcon;
	
	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 200;
	public static final int yTable = 250;
	
	private boolean ordering = false;
	private String myOrder = new String();
	private boolean eating = false;
	
	public CustomerGui(CustomerAgent c, RestaurantGui gui, int place){ //HostAgent m) {
		agent = c;
		xPos = 450;
		yPos = 50 + place*50;
		xDestination = xPos;
		yDestination = yPos;
		xHome = xPos;
		yHome = yPos;
		//maitreD = m;
		this.gui = gui;
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

		if (xPos == xDestination && yPos == yDestination) {
			if (command==Command.GoToSeat) agent.msgAnimationFinishedGoToSeat();
			else if (command==Command.LeaveRestaurant) {
				agent.msgAnimationFinishedLeaveRestaurant();
				System.out.println("about to call gui.setCustomerEnabled(agent);");
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, xTable/10, xTable/10);
		if(ordering){
			g.setColor(Color.BLACK);
			g.drawString(myOrder + "?", xPos, yPos);
		}
		if(eating){
			g.setColor(Color.BLACK);
			g.drawString(myOrder, xPos + 20, yPos + 20);
		}
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToSeat(int seatnumber) {//later you will map seatnumber to table coordinates.
		xDestination = xTable;
		yDestination = yTable - (seatnumber - 1)*100;
		System.out.println("Going to table" + seatnumber);
		command = Command.GoToSeat;
	}

	public void DoPlaceOrder(String myO){
		ordering = true;
		myOrder = myO;
	}
	
	public void DoEatFood(){
		ordering = false;
		eating = true;
	}
	
	public void DoExitRestaurant() {
		eating = false;
		xDestination = xHome;
		yDestination = yHome;
		command = Command.LeaveRestaurant;
	}
}

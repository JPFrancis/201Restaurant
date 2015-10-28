package restaurant;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant.gui.WaiterGui;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import agent.Agent;

public class WaiterAgent extends Agent implements Waiter {
																		//DATA
	private List<MyCustomer> myCustomers = new ArrayList<MyCustomer>();
	CookAgent cook;
	HostAgent host;
	CashierAgent cashier;
	Menu menu = new Menu();
	public String name;
	private boolean onBreak = false;
	private boolean hasRequestedBreak = false;
	private boolean comingBackFromBreak = false;
	public class MyCustomer
	{
		CustomerAgent customer;
		String choice;
		state s;
		Table table;
		Float check;
		
		CustomerAgent getCustomer(){
			return customer;
		}
		Table getTable(){
			return table;
		}
		public void myCustomer(){
			customer = new CustomerAgent("NULL");
			choice = new String();
			table = new Table(0);
		}
	}
	public enum state{waiting, seated, readyToOrder, decidingOrder, ordered, reOrder, waitingForFood, foodReady, served, checkReady, checkReceived, done};

	private Semaphore atDestination = new Semaphore(0,true);
	public WaiterGui waiterGui;
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public void setCook(CookAgent c)
	{
		cook = c;
	}
	
	public void setHost(HostAgent h)
	{
		host = h;
	}
	
	public void setCashier(CashierAgent csh){
		cashier = csh;
	}
	
	public WaiterAgent(String n) {
		super();
		name = n;					
	}
	
	public String getName() {
		return name;
	}
	
	public List<MyCustomer> getCustomers(){
		return myCustomers;
	}
	
	public void removeAllCustomers(){
		myCustomers.clear();
	}

//MSGS---------------------------------------------------------------------------------
	
	public void msgSitAtTable(CustomerAgent cust, Table t) {
		Do("SeatCustomer message received from Host");
		MyCustomer myC = new MyCustomer();
	    myC.customer = cust;
	    myC.s = state.waiting;
	    myC.table = t;
		myCustomers.add(myC);
		stateChanged();
	}
	
	public void msgImReadyToOrder(Customer cust) {
		Do(this + " ReadyToOrder message received from " + cust.toString());
		for(MyCustomer myC : myCustomers){
			if(myC.customer == cust){//Changed this line from comparing current table
				myC.s = state.readyToOrder;
			}
		}
		stateChanged();
	}
	public void msgAtDestination() {//from animation
		atDestination.release();// = true;
		stateChanged();
	}
	public void msgHereIsMyChoice(String choice, Customer cust){	
		Do("HereIsMyChoice message received from " + cust.toString());
		for(MyCustomer myC : myCustomers){
			if(myC.customer == cust){
				myC.choice = choice;
				myC.s =state.ordered;
			}
		}
		stateChanged();
	}
	
	public void msgOutOf(String choice, Table table){
		Do("Out of " + choice + " message received from Cook");
		menu.remove(choice);
		for(MyCustomer myC : myCustomers){
			if(myC.table.equals(table))
				myC.s = state.reOrder;
		}
		stateChanged();
	}
	
	public void msgOrderIsReady(String choice, Table t){
		Do("OrderDone message received from Cook");
		for(MyCustomer myC : myCustomers){
			if(myC.table == t)
				if(myC.choice == choice)
					myC.s = state.foodReady;
		}
		stateChanged();
	}
	
	public void msgHereIsCheck(Float check, CashierAgent csh, Customer c){
		Do("Check received from cashier");
		for(MyCustomer myC : myCustomers){
			if(myC.customer == c){
				myC.check = check;
				cashier = csh;
				myC.s = state.checkReady;
			}
		}
		stateChanged();
	}
	
	public void msgDoneEatingAndLeaving(Customer cust){
		Do("DoneAndLeaving message received from " + cust.toString());
		for(MyCustomer myC : myCustomers){
			if(myC.customer == cust)
				myC.s =state.done;
		}
		stateChanged();
	}

	public void msgAskForBreak(){
		Do("Received message GoOnBreak");
		host.msgWantToGoOnBreak(this);
		hasRequestedBreak = true;
		stateChanged();
	}
	
	public void msgBreakGranted(boolean answer){
		Do("Heard back from Waiter");
		if(answer == true)
			onBreak = true;
		else 
			hasRequestedBreak = false;
	}
	
	public void msgComeBackFromBreak(){
		Do("received message ComeBackFromBreak");
		comingBackFromBreak = true;
		stateChanged();
	}
	
	/*public void msgPickUpHisCustomers(WaiterAgent w){
		Do("PickUpCustomers message received from Host");
		HandleTransfer(w);
		stateChanged();
	}*/
//---------------------------------------------------------------------------
																	//SCHEDULER
	protected boolean pickAndExecuteAnAction() {//MIGHT HAVE A PROBLEM WITH IF THINGS
		//ready, seating, takingOrder, processingOrder, gettingFood, serving, leaving
	  try{	
		for(MyCustomer myC : myCustomers)
		{
			if(myC.s == state.waiting && onBreak==false){
				BringCustomerToTable(myC);
				//myC.s = state.orderReady;
				return true;
			}
		}
		for(MyCustomer myC : myCustomers){
			if(myC.s == state.readyToOrder){
				TakeOrder(myC);
				return true;
			}
		}
		for(MyCustomer myC : myCustomers){
			if(myC.s == state.ordered){
				GiveOrderToCook(myC);
				return true;
			}
		}
		for(MyCustomer myC : myCustomers){
			if(myC.s == state.reOrder){
				RetakeOrder(myC);
				return true;
			}
		}
		for(MyCustomer myC : myCustomers){
			if(myC.s == state.foodReady){
				DeliverFood(myC);
				return true;
			}
		}
		for(MyCustomer myC : myCustomers){
			if(myC.s == state.checkReady){
				DeliverCheck(myC);
				return true;
			}
		}
		for(MyCustomer myC : myCustomers){
			if(myC.s == state.done){
				LeaveCustomer(myC);
				return true;
			}
		}
		
		if(comingBackFromBreak){
			TellHostYoureBack();
			return true;
		}
		
		return false;
	  }
	  catch(ConcurrentModificationException e){
		  return false;
	  }
	  
	}

//ACTIONS--------------------------------------------------------------------------------

	private void BringCustomerToTable(MyCustomer myC) {
		Do("Waiter is Seating " + myC.customer + " at " + myC.table);
		waiterGui.DoPickUpCustomer();
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		myC.customer.msgFollowMeToTable(menu, myC.table.tableNumber, this);
		waiterGui.DoGoToTable(myC.table.tableNumber);
		
		try {
				atDestination.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		myC.table.setOccupant(myC.customer);
		Do("Setting seated");
		myC.s = state.seated;
						
	}

	private void TakeOrder(MyCustomer myC) {
		waiterGui.DoGoToTable(myC.table.tableNumber);
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//waiterGui.DoTakeOrder(myC.table);
		myC.s = state.decidingOrder;
		myC.customer.msgWhatWouldYouLike();
	}
	private void GiveOrderToCook(MyCustomer myC){
		waiterGui.DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myC.s = state.waitingForFood;
		cook.msgHereIsAnOrder(this, myC.choice, myC.table);
	}
	
	private void RetakeOrder(MyCustomer myC){
		waiterGui.DoGoToTable(myC.table.tableNumber);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myC.s = state.decidingOrder;
		myC.customer.msgOutOfChoice(menu);
	}
	
	private void DeliverFood(MyCustomer myC){
		waiterGui.DoGoToCook();
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cook.msgFoodRetrieved(myC.choice);
		waiterGui.DoDeliverFood(myC.choice);
		waiterGui.DoGoToTable(myC.table.tableNumber);
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//myC.s = state.served;
		waiterGui.setDelivering(false);
		myC.s = state.served;
		myC.customer.msgHereIsYourFood();
		cashier.msgComputeBill(this, myC.customer, myC.choice);
		//waiterGui.DoDeliverFood(myC.choice, myC.table.tableNumber);
	}
	private void DeliverCheck(MyCustomer myC){
		myC.customer.msgHereIsCheck(myC.check, cashier);
		myC.s = state.checkReceived;
	}
	private void LeaveCustomer(MyCustomer myC){
		Do("Waiter is Leaving " + myC.customer.getName());
		waiterGui.DoLeaveCustomer();
		host.msgTableIsFree(myC.table);
		myCustomers.remove(myC);
	}
	
	private void TellHostYoureBack(){
		host.msgOffBreak(this);							
		onBreak = false;
		comingBackFromBreak = false;
	}
	
	/*private void HandleTransfer(WaiterAgent w){
		List<MyCustomer> transferCustomers = w.getCustomers();
		for(MyCustomer tCust : transferCustomers){
			myCustomers.add(tCust);
			tCust.customer.setWaiter(this);
		}
		//w.removeAllCustomers();
		w.waiterGui.DoLeaveCustomer();
		cook.msgWaiterHasGoneOnBreak(w, this);
	}*/
	
	public WaiterGui getGui(){
		return waiterGui;
	}
	
	public boolean onBreak(){
		return onBreak;
	}
	
	public boolean hasRequestedBreak(){
		return hasRequestedBreak;
	}
}


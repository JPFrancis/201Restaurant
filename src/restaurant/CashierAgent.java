package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

public class CashierAgent extends Agent implements Cashier {
															//DATA	
	public EventLog log = new EventLog();
	public List<MyBill> Bills = Collections.synchronizedList(new ArrayList<MyBill>());
	String name;
	public Float funds = (float) 100;
	public class MyBill{
		public state s;
		Waiter w = null;
		public Market m = null;
		public String choice;
		public Customer c;
		Float tab = (float) 0;
		public MyBill(Waiter wait, Market market, String ch, Customer cust){
			s = state.pending;
			w = wait;
			m = market;
			choice = ch;
			c = cust;
		}
	};
	public enum state{pending, computing, charging, finished};
	
	Map<String, Float> Prices = new HashMap<String, Float>();

	public CashierAgent(String n) {
		super();
		name = n;
		Prices.put("Steak", (float) 12.99);
		Prices.put("Chicken", (float) 10.99);
		Prices.put("Salad", (float) 5.99);
		Prices.put("Pizza", (float) 7.99);					//HACKHACKHACK
	}
	public String getName() {
		return name;
	}
	public void setFunds(int m){
		funds = (float) m;
	}
//MSGS-----------------------------------------------------------------------------------------------------------
	
	public void msgComputeBill(Waiter w, Customer c, String choice) {
		log.add(new LoggedEvent("ComputeBill message received"));
		Do("ComputeBill message received");
		synchronized(Bills){
		Bills.add(new MyBill(w, null, choice, c));
		}
		stateChanged();
	}

	public void msgPayment(Customer c, Float cash){
		log.add(new LoggedEvent("Payment received"));
		Customer temp = c;
		Do("Payment received from " + temp.toString());
		
		synchronized(Bills){
		for(MyBill b : Bills){
			if(b.c == c){
				if(b.tab.equals(cash))
				b.s = state.charging;
			}
		}
		}
		stateChanged();
	}
	
	public void msgFlaking(Customer c, Float cash){
		log.add(new LoggedEvent("Customer flaked!"));
		Do("Customer flaked!");
		stateChanged();
	}
	
	public void msgPayForMarketOrder(String f, Market m){
		log.add(new LoggedEvent("Market Order charge received"));
		Do("Market order charge received from " + m.toString() + " for " + f);
		synchronized(Bills){
		Bills.add(new MyBill(null, m, f, null));
		}
		stateChanged();
	}
	/*public void msgWaiterHasGoneOnBreak(WaiterAgent oldWaiter, WaiterAgent newWaiter){
		Do("Handling change in waiter");
		for(Order o : orders){
			if(o.w == oldWaiter)
				o.w = newWaiter;
		}
	}*/
	
//SCHEDULER------------------------------------------------------------------------
	
	public boolean pickAndExecuteAnAction() {
		//ready, makingOrder, orderReady
		synchronized(Bills){
			for(MyBill b : Bills){
			if(b.s == state.pending)
			{
				ComputeIt(b);
				return true;
			}
		}
		}
		synchronized(Bills){
		for(MyBill b : Bills){
			if(b.s == state.charging)
			{
				ChargeIt(b);
				return true;
			}
		}
		}
		return false;
	}

// ACTIONS----------------------------------------------------------------------------------------

	private void ComputeIt(MyBill b) {
		if(b.m == null){
			b.s = state.computing;
			b.tab += Prices.get(b.choice);
			b.w.msgHereIsCheck(b.tab, this, b.c);
		}
		if(b.w == null){
			b.tab = Prices.get(b.choice)*5;
			b.s = state.charging;
		}
	}
	private void ChargeIt(MyBill b) {
		if(b.m == null){
			funds += b.tab;
			synchronized(Bills){
				Bills.remove(b);
			}
		}
		if(b.w == null){
			if(funds >= b.tab){
				funds -= b.tab;
				b.m.msgChargePaid(b.choice);
			}
			else
				b.m.msgCantAfford(b.choice);
			synchronized(Bills){
			Bills.remove(b);
			}
		}
	}
	
}



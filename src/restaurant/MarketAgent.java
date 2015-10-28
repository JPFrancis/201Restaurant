package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;

public class MarketAgent extends Agent implements Market{
	//DATA	
	String name;
	Cook cook = null;
	CashierAgent cashier = null;
	private List<MarketOrder> MarketOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());
	public class MarketOrder{
		state s;
		String food;
		public MarketOrder(String f){
			food = f;
			s = state.pending;
		}
	};
	
	public enum state{pending, verifying, charging, packaging, done, finished};
	
	private List<MyFood> Stock = Collections.synchronizedList(new ArrayList<MyFood>());
	public class MyFood{
		String type;
		int inventory;
		public MyFood(String t, int i){
			type = t;
			inventory = i;
		}
	};
	
	Timer timer = new Timer();

	public MarketAgent(String n) {
		super();
		name = n;
	}
	public void setCashier(CashierAgent c){
		cashier = c;
	}
	public String getName() {
		return name;
	}
	
//MSGS-----------------------------------------------------------------------------------------------------------
	
	public void msgPrepareFood(){
		Stock.add(new MyFood("Steak", 5));
		Stock.add(new MyFood("Chicken", 5));
		Stock.add(new MyFood("Salad", 5));
		Stock.add(new MyFood("Pizza", 5));
		stateChanged();
	}
	
	public void msgNeedFood(String food, Cook c) {
		cook = c;
		Do("Need " + food + " message received from " + c.toString());
		synchronized(MarketOrders){
		MarketOrders.add(new MarketOrder(food));
		}
		stateChanged();
	}	
	
	public void msgChargePaid(String f){
		Do("Cashier has payed charge for " + f);
		synchronized(MarketOrders){
		for(MarketOrder mo : MarketOrders){
			if(mo.food.equals(f) && mo.s == state.verifying)
				mo.s = state.charging;
		}
		}
		stateChanged();
	}
	
	public void msgCantAfford(String f){
		Do("Cashier cannot afford market order. Shipment cancelled.");
		try{
		synchronized(MarketOrders){
		for(MarketOrder mo : MarketOrders){
			if(mo.food.equals(f) && mo.s == state.verifying)
				MarketOrders.remove(mo);
		}
		}
		}
		catch(ConcurrentModificationException e){
			msgCantAfford(f);
		}
		stateChanged();
	}
	
//SCHEDULER------------------------------------------------------------------------
	
	protected boolean pickAndExecuteAnAction() {
		//ready, makingOrder, orderReady
		boolean empty = true;
		for(MyFood mf : Stock){
			if(mf.inventory > 0)
				empty = false;
		}
		if(empty && !Stock.isEmpty() && cook != null){
			cook.msgMarketDry(this);
		}
		synchronized(MarketOrders){
		for(MarketOrder mo : MarketOrders){
			if(mo.s == state.pending)
			{
				CheckStockAndCharge(mo);
				return true;
			}
		}
		}
		synchronized(MarketOrders){
		for(MarketOrder mo : MarketOrders){
			if(mo.s == state.charging){
				TryToPrepareShipment(mo);
				return true;
			}
		}
		}
		synchronized(MarketOrders){
		for(MarketOrder mo : MarketOrders){
			if(mo.s == state.done)
			{
				ShipIt(mo);
				return true;
			}
		}
		}

		return false;
	}

// ACTIONS----------------------------------------------------------------------------------------

	private void CheckStockAndCharge(MarketOrder mo) {
			for(MyFood mf : Stock){
				if(mf.type.equals(mo.food) && mf.inventory == 0){
					cook.msgOrderCannotBeFulfilled(mo.food, this);
					synchronized(MarketOrders){
					MarketOrders.remove(mo);
					}
					return;
				}	
			}
			cashier.msgPayForMarketOrder(mo.food, this);
			mo.s = state.verifying;
	}		
	
	private void TryToPrepareShipment(final MarketOrder mo){
			Do(name + " is preparing order");
			mo.s = state.packaging;
			timer.schedule(new TimerTask() {
				public void run() {
					mo.s = state.done;
					stateChanged();
				}
				},
				10000);												//HACK
	}

	private void ShipIt(MarketOrder mo) {
		mo.s = state.finished;
		for(MyFood mf : Stock){
			if(mf.type.equals(mo.food)){
				mf.inventory-=5;
			}
		}
		cook.msgShipmentReady(mo.food);
		synchronized(MarketOrders){
		MarketOrders.remove(mo);
		}
	}
	
}



package restaurant;

import agent.Agent;
import restaurant.gui.CookGui;
import restaurant.gui.HostGui;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;

import java.util.*;
import java.util.concurrent.Semaphore;

public class CookAgent extends Agent implements Cook {
															//DATA	
	private CookGui gui;
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public List<MyMarket> Markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	boolean ordering = false;
	private Semaphore atDestination = new Semaphore(0,true);
	class MyMarket{
		MarketAgent m;
		boolean Steak = true;
		boolean Chicken = true;
		boolean Pizza = true;
		boolean Salad = true;
		public MyMarket(MarketAgent mark){
			m = mark;
		}
		public boolean isStocked(String f){
			if(f.equals("Pizza"))
				return Pizza;
			if(f.equals("Steak"))
				return Steak;
			if(f.equals("Chicken"))
				return Chicken;
			if(f.equals("Salad"))
				return Salad;
			return false;
		}
		public void setUnstocked(String f){
			if(f.equals("Pizza"))
				Pizza = false;
			if(f.equals("Steak"))
				Steak = false;
			if(f.equals("Chicken"))
				Chicken = false;
			if(f.equals("Salad"))
				Salad = false;
		}
	}
	String name;
	class Order{
		state s;
		WaiterAgent w;
		String choice;
		Table table;
	};
	public enum state{pending, cooking, done, finished, taken};
	
	public HostGui hostGui = null;
	Timer timer = new Timer();
	Map<String, Food> Foods = new HashMap<String, Food>();
	
	public class Food {
		int cookingTime;
		int inventory;
		String type;
		public Food(int cTime, int count, String t){
			cookingTime = cTime;
			inventory = count;
			type = t;
		}
		public Food(){
		}
	}
	
	int low = 2;
	int marketCount = 0;

	public CookAgent(String n) {
		super();
		name = n;
		Foods.put("Steak", new Food(5000, 5, "Steak"));
		Foods.put("Chicken", new Food(4000, 0, "Chicken"));
		Foods.put("Salad", new Food(3000, 5, "Salad"));
		Foods.put("Pizza", new Food(2000, 0, "Pizza"));					//HACKHACKHACK
	}
	public String getName() {
		return name;
	}
	
	public void setGui(CookGui g){
		gui = g;
	}
	public void addMarket(MarketAgent m){
		Markets.add(new MyMarket(m));
	}
//MSGS-----------------------------------------------------------------------------------------------------------
	
	public void msgHereIsAnOrder(WaiterAgent w, String choice, Table table) {
		Do("HereIsOrder message received from " + w.getName());
		Order o1 = new Order();
		o1.w = w;
		o1.s = state.pending;
		o1.choice = choice;
		o1.table = table;
		synchronized(orders){
		orders.add(o1);
		}
		stateChanged();
	}
	
	public void msgOrderCannotBeFulfilled(String food, Market m){
		Do("Out of food message received from " + m.toString());
		synchronized(Markets){
			for(MyMarket myM : Markets){
			if (myM.m == m){
				Do("Setting " + food + " unstocked for " + m.toString());
				myM.setUnstocked(food);
				}
		}
		}
		stateChanged();
	}
	
	public void msgShipmentReady(String f){
		Do("Shipment received from Market");
		if(Foods.containsKey(f))
			Foods.get(f).inventory += 5;
		stateChanged();
	}
	
	public void msgMarketDry(MarketAgent m){
		Do("Market dry message received from " + m.getName());
		synchronized(Markets){
		Markets.remove(m);
		}
		stateChanged();
	}
	
	public void msgAtDestination() {//from animation
		atDestination.release();// = true;
		stateChanged();
	}
	/*public void msgWaiterHasGoneOnBreak(WaiterAgent oldWaiter, WaiterAgent newWaiter){
		Do("Handling change in waiter");
		for(Order o : orders){
			if(o.w == oldWaiter)
				o.w = newWaiter;
		}
	}*/
	public void msgFoodRetrieved(String f){
		Do("Food retrieved");
		synchronized(orders){
		for(Order o : orders){
			if(o.s == state.finished && o.choice.equals(f))
				o.s = state.taken;
		}
		}
		stateChanged();
	}
	
//SCHEDULER------------------------------------------------------------------------
	
	protected boolean pickAndExecuteAnAction() {
		if(!ordering){
		for (Map.Entry<String, Food> entry : Foods.entrySet())
		{
		    if(entry.getValue().inventory <= low){
		    	List<String> neededFoods = new ArrayList<String>();
				neededFoods.add(entry.getValue().type);
				for(MyMarket m : Markets){
					for(String food : neededFoods){
						if(m.isStocked(food)){
							m.m.msgNeedFood(food, this);
							ordering = true;
							return true;
						}
					}
				}
		    }
		}
		}
		
		synchronized(orders){
		for(Order o : orders){
			if(o.s == state.pending)
			{
				TryToCookIt(o);
				return true;
			}
		}
		}
		synchronized(orders){
		for(Order o : orders){
			if(o.s == state.done)
			{
				PlateIt(o);
				return true;
			}
		}
		}
		synchronized(orders){
		for(Order o : orders){
			if(o.s == state.taken)
			{
				OrderFinished(o);
				return true;
			}
		}
		return false;
		}
	}

// ACTIONS----------------------------------------------------------------------------------------

	private void TryToCookIt(final Order o) {
		
		Food temp = new Food();
		temp = Foods.get(o.choice);
		
		if(temp.inventory == 0){
			o.w.msgOutOf(o.choice, o.table);
			synchronized(orders){
			orders.remove(o);
			}
		}	
		else{
			Do("Cook is cooking " + o.choice);
			gui.DoGoToFridge();
			try {
				atDestination.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gui.DoGrillIt(o.choice);
			try {
				atDestination.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			o.s = state.cooking;
		
			timer.schedule(new TimerTask() {
				public void run() {
					gui.DoRemoveFromGrill(o.choice);
					try {
						atDestination.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gui.DoBringToPlating(o.choice);
					try {
						atDestination.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gui.DoPlateIt(o.choice);
					o.s = state.done;
					stateChanged();
				}},
				temp.cookingTime);
			temp.inventory--;
		}
	}
	
	
	private void PlateIt(Order o) {
		o.s = state.finished;
		o.w.msgOrderIsReady(o.choice, o.table);
	}
	
	private void OrderFinished(Order o) {
		Do("Order finished action");
		gui.DoOrderRemoved(o.choice);
		orders.remove(o);
	}
}



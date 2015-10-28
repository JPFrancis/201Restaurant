package restaurant.interfaces;

import restaurant.MarketAgent;
import restaurant.Table;
import restaurant.WaiterAgent;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Cook {
	/**
	 * @param total The cost according to the cashier
	 *
	 * Sent by the waiter prompting the customer to give cashier check after the customer is done eating.
	 */
	public void msgHereIsAnOrder(WaiterAgent w, String choice, Table table);
	
	public void msgOrderCannotBeFulfilled(String food, Market m);
	
	public void msgShipmentReady(String f);
	
	public void msgMarketDry(MarketAgent m);
	
	public void msgFoodRetrieved(String f);
	
}
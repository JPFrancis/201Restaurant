package restaurant.test.mock;


import restaurant.MarketAgent;
import restaurant.Table;
import restaurant.WaiterAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCook extends Mock implements Cook {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Market Market2;
	public MockCook(String name) {
		super(name);

	}
	@Override
	public void msgHereIsAnOrder(WaiterAgent w, String choice, Table table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderCannotBeFulfilled(String food, Market m) {
		log.add(new LoggedEvent(food + " order could not be fulfilled"));
		// TODO Auto-generated method stub
		Market2.msgNeedFood("Pizza", this);
	}

	@Override
	public void msgShipmentReady(String f) {
		
		
	}

	@Override
	public void msgMarketDry(MarketAgent m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFoodRetrieved(String f) {
		// TODO Auto-generated method stub
		
	}

}

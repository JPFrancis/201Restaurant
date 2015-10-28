package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 */
public class MockWaiter extends Mock implements Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;

	public MockWaiter(String name) {
		super(name);

	}

	@Override
	public void msgHereIsCheck(Float check, CashierAgent csh, Customer c) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ check));

		c.msgHereIsCheck(check, csh);
	}

	@Override
	public void msgImReadyToOrder(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEatingAndLeaving(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMyChoice(String myOrder, Customer customer) {
		// TODO Auto-generated method stub
		
	}

}

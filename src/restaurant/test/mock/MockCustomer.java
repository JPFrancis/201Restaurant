package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;

	public MockCustomer(String name) {
		super(name);

	}

	@Override
	public void msgHereIsCheck(Float check, CashierAgent csh) {
		log.add(new LoggedEvent("Received check."));

		if(check.equals((float)10)){
			csh.msgFlaking(this, (float)0);
			return;
		}
			
		Float bill = check;
		cashier.msgPayment(this, bill);
	}

}

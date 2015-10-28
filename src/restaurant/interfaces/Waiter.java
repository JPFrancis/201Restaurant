package restaurant.interfaces;

import restaurant.CashierAgent;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 *
 */
public interface Waiter {
	/**
	 * @param total The cost according to the cashier
	 *
	 * Sent by the cashier giving the waiter a check to pass on to the cashier.
	 */
	public abstract void msgHereIsCheck(Float check, CashierAgent csh, Customer c);

	public abstract void msgImReadyToOrder(Customer customer);

	public abstract void msgDoneEatingAndLeaving(Customer customer);

	public abstract void msgHereIsMyChoice(String myOrder, Customer customer);

}
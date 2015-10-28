package restaurant.interfaces;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Market {
	/**
	 * @param total The cost according to the cashier
	 *
	 * Sent by the waiter prompting the customer to give cashier check after the customer is done eating.
	 */
	public abstract void msgPrepareFood();
	
	public abstract void msgNeedFood(String food, Cook c);
	
	public void msgChargePaid(String f);
	
	public void msgCantAfford(String f);
}
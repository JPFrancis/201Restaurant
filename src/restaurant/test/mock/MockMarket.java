package restaurant.test.mock;


import restaurant.CashierAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public CashierAgent cashier;
	public Cook cook;
	boolean unstocked = false;
	public MockMarket(String name) {
		super(name);

	}
	public void setUnstocked(){
		unstocked = true;
	}
	
	@Override
	
	public void msgNeedFood(String food, Cook c) {
		log.add(new LoggedEvent("Need " + food + " message received"));
		cook = c;
		if(unstocked && food.equals("Pizza"))
			cook.msgOrderCannotBeFulfilled("Pizza", this);
		else
			cashier.msgPayForMarketOrder(food, this);
	}	
	
	public void msgChargePaid(String f){
		log.add(new LoggedEvent("Charge has been paid"));
	}
	
	public void msgCantAfford(String f){
		log.add(new LoggedEvent("Cashier cannot afford market order. Shipment cancelled."));
	}
	
	public void msgHereIsCheck(Float check, CashierAgent csh, Customer c) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ check));

		c.msgHereIsCheck(check, csh);
	}

	@Override
	public void msgPrepareFood() {
		// TODO Auto-generated method stub
		
	}

}

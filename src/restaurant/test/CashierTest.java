package restaurant.test;

import junit.framework.TestCase;
import restaurant.CashierAgent;
import restaurant.CashierAgent.state;
import restaurant.test.mock.MockCook;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket market1;
	MockMarket market2;
	MockCook cook;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		cook = new MockCook("mockcook");
		market1 = new MockMarket("mockmarket1");
		market2 = new MockMarket("mockmarket2");
		
		cashier.setFunds(100);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testLabNormativeCustomerScenario()
	{
		//setUp() runs first before this test!
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		
		//check preconditions		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//STEP 1 - postconditions step 1, preconditions step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertFalse("Cashier's scheduler should have returned false (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction()); // ??
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//STEP 2
		cashier.msgComputeBill(waiter, customer, "Steak");
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("CashierBill should contain a bill with state == pending. It doesn't.",
				cashier.Bills.get(0).s == state.pending);
		
		assertTrue("Cashier should have logged \"ComputeBill message received\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("ComputeBill message received"));

		assertTrue("CashierBill should contain a bill of choice = Steak. It contains something else instead: " 
				+ cashier.Bills.get(0).choice, cashier.Bills.get(0).choice.equals("Steak"));
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.Bills.get(0).c == customer);
	}//end one normal customer scenario
	
	public void testOneBillNormativeMarketScenario(){
		//setUp() runs first before this test!
			market1.cashier = cashier;
				//check preconditions		
				assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
								+ cashier.log.toString(), 0, cashier.log.size());
				
				//STEP 1 - postconditions step 1, preconditions step 2
				assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
								+ waiter.log.toString(), 0, market1.log.size());
				assertEquals("MockMarket2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ waiter.log.toString(), 0, market2.log.size());
				assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
						+ waiter.log.toString(), 0, cook.log.size());
				assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
				assertEquals(
						"MockMarket1 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the event log reads: "
								+ market1.log.toString(), 0, market1.log.size());
				assertEquals(
						"MockMarket2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the event log reads: "
								+ market2.log.toString(), 0, market2.log.size());
				assertEquals(
						"MockCook should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the event log reads: "
								+ cook.log.toString(), 0, cook.log.size());
				
				//STEP 2
				market1.msgNeedFood("Pizza", cook);
				//check postconditions for step 2 / preconditions for step 3
				assertTrue("Market should have logged \"Need Pizza message received\" but didn't. His log reads instead: " 
						+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Need Pizza message received"));
				assertTrue("Cashier should have logged \"Market Order charge received\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Market Order charge received"));
				assertTrue("CashierBill should contain a bill with state == pending. It doesn't.",
						cashier.Bills.get(0).s == state.pending);
				assertTrue("CashierBill should contain a bill of choice = Pizza. It contains something else instead: " 
						+ cashier.Bills.get(0).choice, cashier.Bills.get(0).choice.equals("Pizza"));
				assertTrue("CashierBill should contain a bill with the right market in it. It doesn't.", 
							cashier.Bills.get(0).m == market1);
				cashier.pickAndExecuteAnAction();
				cashier.pickAndExecuteAnAction();
				assertTrue("Market1 should have logged \"Charge has bene paid\" but didn't. His log reads instead: " 
							+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Charge has been paid"));
				assertTrue("Cashier funds should have been reduced by 39.95. Instead funds equal " + cashier.funds, cashier.funds < 60.06);			
	}
	
	public void testTwoBillsNormativeMarketScenario(){
		//setUp() runs first before this test!
			market1.cashier = cashier;
			market2.cashier = cashier;
			cook.Market2 = market2;
			
			market1.setUnstocked();
			
				//check preconditions		
				assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
								+ cashier.log.toString(), 0, cashier.log.size());
				
				//STEP 1 - postconditions step 1, preconditions step 2
				assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
								+ market1.log.toString(), 0, market1.log.size());
				assertEquals("MockMarket2 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market2.log.toString(), 0, market2.log.size());
				assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
				assertFalse("Cashier's scheduler should have returned false, but didn't.", cashier.pickAndExecuteAnAction());
				assertEquals(
						"MockMarket1 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the event log reads: "
								+ market1.log.toString(), 0, market1.log.size());
				assertEquals(
						"MockMarket2 should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the event log reads: "
								+ market2.log.toString(), 0, market2.log.size());
				assertEquals(
						"MockCook should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the event log reads: "
								+ cook.log.toString(), 0, cook.log.size());
				
				//STEP 2
				market1.msgNeedFood("Chicken", cook);
				market1.msgNeedFood("Pizza", cook);
				
				//check postconditions for step 2 / preconditions for step 3
				assertTrue("Market1 should have logged \"Need Pizza message received\" but didn't. His log reads instead: " 
						+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Need Pizza message received"));
				assertTrue("Market1 should have logged \"Need Chicken message received\" but didn't. His log reads instead: " 
						+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Need Chicken message received"));
				assertTrue("Cook should have logged \"Pizza order could not be fulfilled\" but didn't. His log reads instead: "
						+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Pizza order could not be fulfilled"));
				
				assertTrue("Market2 should have logged \"Need Pizza message received\" but didn't. His log reads instead: " 
						+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Need Pizza message received"));
				
				assertTrue("Cashier should have logged \"Market Order charge received\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Market Order charge received"));
				assertTrue("Cashier should have logged \"Market Order charge received\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Market Order charge received"));
				
				assertTrue("CashierBill should contain two bills with state == pending. It doesn't.",
						cashier.Bills.get(0).s == state.pending && cashier.Bills.get(1).s == state.pending);
				
				assertTrue("CashierBill should contain a bill of choice = Chicken. It contains something else instead: " 
						+ cashier.Bills.get(0).choice, cashier.Bills.get(0).choice.equals("Chicken"));
				
				assertTrue("CashierBill should contain a bill of choice = Pizza. It contains something else instead: " 
						+ cashier.Bills.get(1).choice, cashier.Bills.get(1).choice.equals("Pizza"));
				
				assertTrue("CashierBill should contain bills with the right markets in it. It doesn't.", 
							cashier.Bills.get(0).m == market1 && cashier.Bills.get(1).m == market2);
				
				cashier.pickAndExecuteAnAction();
				cashier.pickAndExecuteAnAction();
				cashier.pickAndExecuteAnAction();
				cashier.pickAndExecuteAnAction();
				
				assertTrue("Market1 should have logged \"Charge has bene paid\" but didn't. His log reads instead: " 
							+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Charge has been paid"));
				assertTrue("Market2 should have logged \"Charge has bene paid\" but didn't. His log reads instead: " 
						+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Charge has been paid"));
				assertTrue("Cashier funds should have been reduced by 94.90. Instead funds equal " + cashier.funds, cashier.funds < 5.11);		
	}

	public void testCashierCantAffordNonNormMarketScenario(){
		//setUp() runs first before this test!
			market1.cashier = cashier;
			cashier.setFunds(0);
				//check preconditions		
				assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
								+ cashier.log.toString(), 0, cashier.log.size());
				
				//STEP 1 - postconditions step 1, preconditions step 2
				assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
								+ market1.log.toString(), 0, market1.log.size());
				assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
				
				
				//STEP 2
				market1.msgNeedFood("Steak", cook);
				//check postconditions for step 2 / preconditions for step 3
				assertTrue("Market should have logged \"Need Steak message received\" but didn't. His log reads instead: " 
						+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Need Steak message received"));
				assertTrue("Cashier should have logged \"Market Order charge received\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Market Order charge received"));
				assertTrue("CashierBill should contain a bill with state == pending. It doesn't.",
						cashier.Bills.get(0).s == state.pending);
				assertTrue("CashierBill should contain a bill of choice = Steak. It contains something else instead: " 
						+ cashier.Bills.get(0).choice, cashier.Bills.get(0).choice.equals("Steak"));
				assertTrue("CashierBill should contain a bill with the right market in it. It doesn't.", 
							cashier.Bills.get(0).m == market1);
				cashier.pickAndExecuteAnAction();
				cashier.pickAndExecuteAnAction();
				assertTrue("Market1 should have logged \"Cashier cannot afford market order. Shipment cancelled.\" but didn't. His log reads instead: " 
							+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier cannot afford market order. Shipment cancelled."));
				assertTrue("Cashier funds should remain at zero. Instead funds equal " + cashier.funds, cashier.funds <.01);			
	}
	public void testCustomerFlakesCashierScenario(){
		//setUp() runs first before this test!
			customer.cashier = cashier;
			market1.cashier = cashier;
			cashier.setFunds(60);
				//check preconditions		
				assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
								+ cashier.log.toString(), 0, cashier.log.size());
				
				//STEP 1 - postconditions step 1, preconditions step 2
				assertEquals("Customer should have an empty event log before the Cashier's scheduler is called. Instead, the Customer's event log reads: "
								+ waiter.log.toString(), 0, market1.log.size());
				assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market1.log.toString(), 0, market1.log.size());
				assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
				
				
				customer.msgHereIsCheck((float)10, cashier);
				
				assertTrue("Customer should have logged \"Received check.\" but didn't. His log reads instead: " 
						+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received check."));
				assertTrue("Cashier should have logged \"Customer flaked!\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Customer flaked!"));
				
				//STEP 2: Make sure no money went through
				market1.msgNeedFood("Steak", cook);
				//check postconditions for step 2 / preconditions for step 3
				assertTrue("Market should have logged \"Need Steak message received\" but didn't. His log reads instead: " 
						+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Need Steak message received"));
				assertTrue("Cashier should have logged \"Market Order charge received\" but didn't. His log reads instead: " 
						+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Market Order charge received"));
				assertTrue("CashierBill should contain a bill with state == pending. It doesn't.",
						cashier.Bills.get(0).s == state.pending);
				assertTrue("CashierBill should contain a bill of choice = Steak. It contains something else instead: " 
						+ cashier.Bills.get(0).choice, cashier.Bills.get(0).choice.equals("Steak"));
				assertTrue("CashierBill should contain a bill with the right market in it. It doesn't.", 
							cashier.Bills.get(0).m == market1);
				cashier.pickAndExecuteAnAction();
				cashier.pickAndExecuteAnAction();
				assertTrue("Market1 should have logged \"Cashier cannot afford market order. Shipment cancelled.\" but didn't. His log reads instead: " 
							+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier cannot afford market order. Shipment cancelled."));
				assertTrue("Cashier funds should remain at initial point of 60. Instead funds equal " + cashier.funds, cashier.funds < 60.01);			
	}
	
	public void testCashierTransactionResultsInEnoughToAffordShipment(){
		customer.cashier = cashier;
		market1.cashier = cashier;
		cashier.setFunds(60);
			//check preconditions		
			assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
							+ cashier.log.toString(), 0, cashier.log.size());
			
			//STEP 1 - postconditions step 1, preconditions step 2
			assertEquals("Customer should have an empty event log before the Cashier's scheduler is called. Instead, the Customer's event log reads: "
							+ waiter.log.toString(), 0, market1.log.size());
			assertEquals("MockMarket1 should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
					+ market1.log.toString(), 0, market1.log.size());
			assertEquals("MockCook should have an empty event log before the Cashier's scheduler is called. Instead, the MockCook's event log reads: "
					+ cook.log.toString(), 0, cook.log.size());
			
			
			cashier.msgComputeBill(waiter, customer, "Salad");
			customer.msgHereIsCheck((float)5.99, cashier);
			
			assertTrue("Customer should have logged \"Received check.\" but didn't. His log reads instead: " 
					+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received check."));
			assertTrue("Cashier should have logged \"Payment received\" but didn't. His log reads instead: " 
					+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Payment received"));
			
			market1.msgNeedFood("Steak", cook);
			//check postconditions for step 2 / preconditions for step 3
			assertTrue("Market should have logged \"Need Steak message received\" but didn't. His log reads instead: " 
					+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Need Steak message received"));
			assertTrue("Cashier should have logged \"Market Order charge received\" but didn't. His log reads instead: " 
					+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Market Order charge received"));
			assertTrue("CashierBill should contain a bill with state == pending. It doesn't.",
					cashier.Bills.get(0).s == state.pending);
			cashier.pickAndExecuteAnAction();
			cashier.pickAndExecuteAnAction();
			cashier.pickAndExecuteAnAction();
			cashier.pickAndExecuteAnAction();
			assertTrue("Market1 should have logged \"Charge has bene paid\" but didn't. His log reads instead: " 
						+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Charge has been paid"));
			assertTrue("Cashier funds should have been reduced by 64.95. Instead funds equal " + cashier.funds, cashier.funds > .06);
	}
}

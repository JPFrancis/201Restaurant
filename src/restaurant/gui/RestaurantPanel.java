package restaurant.gui;

import restaurant.CashierAgent;
import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.MarketAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Panel in frame that contains all the restaurant information,
 * including host, cook, waiters, and customers.
 */
public class RestaurantPanel extends JPanel implements ActionListener {

    //Host, cook, waiters and customers
    private HostAgent host = new HostAgent("Host");
    private HostGui hostGui = new HostGui(host);
    private CookAgent cook = new CookAgent("Cook");
    private CashierAgent cashier = new CashierAgent("Cashier");
    
    private Vector<CustomerAgent> customers = new Vector<CustomerAgent>();
    private Vector<WaiterAgent> waiters = new Vector<WaiterAgent>();
    private Vector<MarketAgent> markets = new Vector<MarketAgent>();

    private JPanel restLabel = new JPanel();
    private ListPanel customerPanel = new ListPanel(this, "Customers");
    private ListPanel waiterPanel = new ListPanel(this, "Waiters");
    private JPanel group = new JPanel();
    private JButton pause = new JButton("Pause");

    private RestaurantGui gui; //reference to main gui

    public RestaurantPanel(RestaurantGui gui) {
        this.gui = gui;
     
        gui.animationPanel.addGui(hostGui);
        
        host.startThread();						
        cook.startThread();
        CookGui g = new CookGui(cook);

		gui.animationPanel.addGui(g);
		cook.setGui(g);
        
        MarketAgent m1 = new MarketAgent("Market1");
        MarketAgent m2 = new MarketAgent("Market2");
        MarketAgent m3 = new MarketAgent("Market3");
        
        m1.msgPrepareFood();
        m2.msgPrepareFood();
        m3.msgPrepareFood();
        
        markets.add(m1);
        markets.add(m2);
        markets.add(m3);
        
        m1.startThread();
        m2.startThread();
        m3.startThread();
        
        cashier.startThread();
        
        for(MarketAgent m : markets){
        	m.setCashier(cashier);
        }
        cook.addMarket(m1);
        cook.addMarket(m2);
        cook.addMarket(m3);
        
        setLayout(new GridLayout(1, 2, 20, 20));
        group.setLayout(new GridLayout(1, 2, 10, 10));

        group.add(customerPanel);
        group.add(waiterPanel);

        initRestLabel();
        add(restLabel);
        add(group);
    }

    /**
     * Sets up the restaurant label that includes the menu,
     * and host and cook information
     */
    private void initRestLabel() {
        JLabel label = new JLabel();
        //restLabel.setLayout(new BoxLayout((Container)restLabel, BoxLayout.Y_AXIS));
        restLabel.setLayout(new BorderLayout());
        label.setText(
                "<html><h3><u>Tonight's Staff</u></h3><table><tr><td>host:</td><td>" + host.getName() + "</td></tr></table><h3><u> Menu</u></h3><table><tr><td>Steak</td><td>$15.99</td></tr><tr><td>Chicken</td><td>$10.99</td></tr><tr><td>Salad</td><td>$5.99</td></tr><tr><td>Pizza</td><td>$8.99</td></tr></table><br></html>");

        restLabel.setBorder(BorderFactory.createRaisedBevelBorder());
        restLabel.add(label, BorderLayout.CENTER);
        restLabel.add(new JLabel("               "), BorderLayout.EAST);
        restLabel.add(new JLabel("               "), BorderLayout.WEST);
        restLabel.add(pause, BorderLayout.SOUTH);
        pause.addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == pause) {
        	if(pause.getText() == "Pause"){
        		host.pause();
        		host.stateChanged();
        		for(WaiterAgent w : waiters){
        			w.pause();
        			w.stateChanged();
        		}
        		for(CustomerAgent c : customers){
        			c.pause();
        			c.stateChanged();
        		}
        		for(MarketAgent m : markets){
        			m.pause();
        			m.stateChanged();
        		}
        		cook.pause();
        		cook.stateChanged();
        		
        		cashier.pause();
        		cashier.stateChanged();
        		
        		pause.setText("Restart");
        	}
        	else if(pause.getText() == "Restart"){
            		cook.restart();
            		host.restart();
            		cashier.restart();
            		for(WaiterAgent w : waiters){
            			w.restart();
            		}
            		for(CustomerAgent c : customers){
            			c.restart();
            		}
            		for(MarketAgent m :markets){
            			m.restart();
            		}
            		pause.setText("Pause");
        	}
        }
    }

    /**
     * When a customer or waiter is clicked, this function calls
     * updatedInfoPanel() from the main gui so that person's information
     * will be shown
     * @param type indicates whether the person is a customer or waiter
     * @param name name of person
     */
    public void showInfo(String type, String name) {

        if (type.equals("Customers")) {

            for (int i = 0; i < customers.size(); i++) {
                CustomerAgent temp = customers.get(i);
                if (temp.getName() == name)
                    gui.updateInfoPanel(temp);
            }
        }
        if(type.equals("Waiters")){
        	for(int i =0; i<waiters.size(); i++){
        		WaiterAgent temp = waiters.get(i);
        		if (temp.getName() == name)
        			gui.updateInfoPanel(temp);
        	}
        }
    }

    /**
     * Adds a customer or waiter to the appropriate list
     * @param type indicates whether the person is a customer or waiter (later)
     * @param name name of person
     */
    public void addPerson(String type, String name, int answer) {			

    	if (type.equals("Customers")) {
    		CustomerAgent c = new CustomerAgent(name);	
    		CustomerGui g = new CustomerGui(c, gui, customers.size());

    		gui.animationPanel.addGui(g);// dw
    		c.setHost(host);
    		c.setGui(g);
    		customers.add(c);
    		c.startThread();
    		if(answer == JOptionPane.YES_OPTION)
    			c.getGui().setHungry();
    	}
    	
    	if(type.equals("Waiters")){
    	  	WaiterAgent waiter = new WaiterAgent(name);
        	waiter.setCook(cook);
        	waiter.setHost(host);
        	WaiterGui wGui = new WaiterGui(waiter, waiters.size());
        	waiter.setGui(wGui);
        	waiter.setCashier(cashier);
        	
        	gui.animationPanel.addGui(wGui);
        	host.addWaiter(waiter, waiter.name);
        	waiter.setCook(cook);
        	waiter.setHost(host);
        	waiters.add(waiter);
        	waiter.startThread();
    	}
    }

}

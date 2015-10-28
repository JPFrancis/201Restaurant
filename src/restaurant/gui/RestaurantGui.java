package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {

	//JFrame animationFrame = new JFrame("Restaurant Animation");
	
	AnimationPanel animationPanel = new AnimationPanel();
	
	JPanel MainPanel = new JPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 500;
        int WINDOWY = 500;

        //animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //animationFrame.setBounds(WINDOWX, WINDOWX, WINDOWX, WINDOWX);
        //animationFrame.setVisible(true);
    	//animationFrame.add(animationPanel); 
    	
        animationPanel.setVisible(true);
        MainPanel.setLayout(new BorderLayout());
    	//MainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	MainPanel.setVisible(true);
    	MainPanel.setBounds(WINDOWX*2, WINDOWX*2, WINDOWY*2, WINDOWY*2);
    	
    	MainPanel.add(animationPanel, BorderLayout.CENTER);
        
    	//setBounds(WINDOWX*3, WINDOWX*3, WINDOWX*3, WINDOWY*3);
    	setBounds(1000, 1000, 1000, 1000);
    	
        setLayout(new BorderLayout(1, 1));

        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .6));
        restPanel.setVisible(true);
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        
        MainPanel.add(restPanel, BorderLayout.EAST);
        //add(restPanel, BorderLayout.CENTER);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .25));
        infoPanel = new JPanel();
        infoPanel.setVisible(true);
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new FlowLayout());
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        
        MainPanel.add(infoPanel, BorderLayout.NORTH);
        //add(infoPanel, BorderLayout.NORTH);
        
        ImageIcon icon = new ImageIcon("/Users/johnpaulfrancis/cs201/restaurant_jpfranci/src/Photo 5.jpg");
        JPanel imagePanel = new JPanel();
        imagePanel.setVisible(true);
        imagePanel.setLayout(new BorderLayout(1, 1));
        imagePanel.setPreferredSize(infoDim);
        imagePanel.add(new JLabel(icon), BorderLayout.EAST);
        imagePanel.add(new JLabel("JP Francis"), BorderLayout.WEST);
        
        //add(imagePanel, BorderLayout.SOUTH);
        
        MainPanel.add(imagePanel, BorderLayout.SOUTH);
        
        add(MainPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        stateCB.setEnabled(false);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        else if (person instanceof WaiterAgent) {
            WaiterAgent waiter = (WaiterAgent) person;
            if(!waiter.onBreak())
            	stateCB.setText("Go on Break?");
            if(waiter.onBreak())
            	stateCB.setText("Come back from break?");
            stateCB.setEnabled(true);
            stateCB.setSelected(false);
            infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        }
        
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            }
            else if (currentPerson instanceof WaiterAgent) {
            	WaiterAgent w = (WaiterAgent) currentPerson;
            	if(!w.onBreak()){
                	w.msgAskForBreak();
            	}
            	else{
            		w.msgComeBackFromBreak();
            		stateCB.setText("Go on break?");
            	}
            	stateCB.setSelected(false);
            }
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

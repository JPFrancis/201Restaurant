package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add");
    private String type;
    private JTextField nameInput = new JTextField("Type " + type + " name here", 23);

    private RestaurantPanel restPanel;
    

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        //setLayout(new FlowLayout());
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        nameInput.setText("*" + type + " Name*");
        nameInput.setMaximumSize( nameInput.getPreferredSize() );
        add(nameInput);
        addPersonB.addActionListener(this);
        add(addPersonB);
        
        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        //view.setLayout(new FlowLayout());
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
        	if(type.equals("Customers")){
        		int answer = JOptionPane.showConfirmDialog(null, "Set Hungry?\n");
        		addPerson(nameInput.getText(), answer);
        	}	
        	else
        		addPerson(nameInput.getText(), 0);
        	//addPerson(nameInput.getText());
        }
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        }
    }

    private String showInputDialog(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name, int answer) {
        if (name != null && answer != JOptionPane.CANCEL_OPTION) {
            JButton button = new JButton(name);
            button.setBackground(Color.red);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson(type, name, answer);//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
        if(answer == JOptionPane.YES_OPTION){
        	
        }
    }
}

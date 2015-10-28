package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 500;
    private final int WINDOWY = 500;
    private final int TABLEORIGINX = 200;
    private final int TABLEORIGINY = 250;
    private final int TABLELENGTH = 50;
    private final int TABLESEPARATION = 100;
    private Image bufferImage;
    private Dimension bufferSize;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
 
    	Timer timer = new Timer(20, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, WINDOWX, WINDOWY );

        //Here is the table
        for(int i=0; i<3; i++)			//HACK, need 3 to be a variable!
        {
        g2.setColor(Color.ORANGE);
        g2.fillRect(TABLEORIGINX, TABLEORIGINY - i*TABLESEPARATION, TABLELENGTH, TABLELENGTH);
        }
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WaiterGui gui){
    	guis.add(gui);
    }
    public void addGui(CookGui gui){
    	guis.add(gui);
    }
}

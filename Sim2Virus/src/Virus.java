import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.lang.Math.*;

/*
 * class Virus defines the main part of the applet
 * It sets up a separate frame (window) for statistics,
 * then it sets up the control panel at the top of the main
 * applet window, above the main display canvas.
 * It also sets up communication by passing pointers
 * (dispcan and statdisparea) specifying the display
 * canvas and the statistics canvas
 */

public class Virus extends Applet {
    ControlPanel controls;
    
	// The frame and panel for displaying statistics
	Frame statframe;
	Frame statframe2;
	StatArea statdisparea;
	StatArea statdisparea2;
		
    public void init() {
		statframe = new Frame("Stats");
		statdisparea = new StatArea();
		statframe.setSize(500, 600);
		statframe.add(statdisparea);
		statframe.setVisible( true );
		
		statframe2 = new Frame("Stats2");
		statdisparea2 = new StatArea();
		statframe2.setSize(500, 600);
		statframe2.add(statdisparea2);
		statframe2.setVisible( true );
		
		statdisparea.addString( "The leftmost column is the time and the\n" );
		statdisparea.addString( "next column is the number of cells that are\n" );
		statdisparea.addString( "all green.  The rightmost column is the number\n" );
		statdisparea.addString( "of cells that are all red.  The remaining columns\n" );
		statdisparea.addString( "are ten percentile counts for cells that have\n" );
		statdisparea.addString( "a mix of red and green.\n\n" );		
		
		setLayout(new BorderLayout());
		DisplayCanvas dispcan = new DisplayCanvas( statdisparea, statdisparea2 );
		add("Center", dispcan);
		add("North", controls = new ControlPanel( dispcan ));
    }

    public void start() {
		controls.setEnabled(true);
    }

    public void stop() {
		controls.setEnabled(false);
    }

    public boolean handleEvent(Event e) {
		if (e.id == Event.WINDOW_DESTROY) {
		    statframe.dispose();
		    System.exit(0);
		}
		return false;
    }

    public static void main(String args[]) {
		Frame f = new Frame("Virus");
		Virus virus = new Virus();

		virus.init();
		virus.start();

		f.add("Center", virus);
		f.setSize(900, 500);
		f.show();
    }
}
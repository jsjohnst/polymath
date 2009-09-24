import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.lang.Math.*;

/*
 * class StatArea defines a panel that appears as a second
 * frame (window) giving statistics on the simulation.
 */

public class StatArea extends Panel {
	
	TextArea statField;
	
	public StatArea() {
		super();		
		add(statField = new TextArea("", 40, 50 ));
	}
	
	// the addString method can be used as a println
	// to append a string to the contents of the panel
	public void addString( String s ) {
		statField.append( s );
	}
}
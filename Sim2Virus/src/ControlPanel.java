import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.lang.Math.*;

/*
 * class ControlPanel defines the user interface that controls
 * the simulation
 *
 * The ControlPanel constructor sets up the buttons, fields,
 * labels and menu choices in a 4 by 8 grid layout
 *
 * The actionPerformed method listens for user interface
 * events and passes values to the appropriate methods
 */
 
class ControlPanel extends Panel
                  implements ActionListener {
    /*
     * User interface widgets:
     *
     * Menus:
     *   RedAgeChoice and GreenAgeChoice give the maximum number
     *      of time steps that a virus can stay inside a cell before
     *      budding out
     *
     *   FocusChoice specifies the shape of the focus
     *
     * Input Fields:
     *   RedMOIField and GreenMOIField give the aferage number of
     *      each type of virus per call in the focus for the
     *      initial infection
     *
     *   FocusField gives the radius of the focus
     *
     *   RateField gives the infection rate
     *
     *   IterField specifies how many time steps to simulate
     *      with each click of the "Run" button
     *
     *   MinField and MaxField give the range for the uniform
     *      random variable that specifies how long a cell remains
     *      susceptible to infection
     *
     * Buttons:
     *   "Border on/off" toggles a thin black line that shows the
     *      border of the focal region
     *
     *   "Restart" reinitializes the simulation by clearing
     *      everything and reinfecting the focal region
     *
     *   "Run" continues the current simulation for the number
     *       of time steps specified by IterField
     *
     *   "Red only", "Green only", and "Red+Green" specify whether to
     *      display only one type of virus or both.  This only affects
     *      the display - both types are still present in the simulation
     *
     *   SuperYes and SuperNo are radio buttons that specify whether
     *      to allow superinfection (simultaneous infection of a single
     *      cell by both red and green)
     *
     */

	// Flag to indicate whether canvas.init() has been called
	static boolean initflag = false;

    Choice RedAgeChoice, GreenAgeChoice;
    Choice RedShape, GreenShape;
    TextField RedMOIField, GreenMOIField;
    TextField RedRadius, GreenRadius;
    TextField RateField;
    TextField IterField;
    TextField MinField;
    TextField MaxField;
    TextField DeltaXField;
    TextField DeltaTField;
    TextField Filename;
    CheckboxGroup SuperButtons;
    Checkbox SuperYes, SuperNo;
    DisplayCanvas canvas;

    public ControlPanel(DisplayCanvas canvas) {
		Button b = null;
		
		setLayout( new GridLayout( 5, 8, 5, 5 ) );
		this.canvas = canvas;

		add(new Label(" "));
		add(new Label("MOI"));
		add(new Label("Max Age"));
		add(new Label("Focus shape"));
		add(new Label("Focus radius"));
		SuperButtons = new CheckboxGroup();
		add(new Label("Superinfection?"));
		SuperYes = new Checkbox( "Yes", SuperButtons, true );
		add(SuperYes);
		SuperNo = new Checkbox( "No", SuperButtons, false );
		add(SuperNo);

		add(new Label("Red virus"));
		add(RedMOIField = new TextField("2.0", 5));
		RedAgeChoice = new Choice();
		RedAgeChoice.add( "1" );
		RedAgeChoice.add( "2" );
		RedAgeChoice.add( "3" );
		RedAgeChoice.add( "4" );
		add( RedAgeChoice );
		RedShape = new Choice();
		RedShape.add( "Disc" );
		RedShape.add( "Hexagon" );
		RedShape.add( "Hex2" );
		RedShape.add( "Hex3" );
		add( RedShape );
		add(RedRadius = new TextField("20", 4));
		b = new Button("Green only");
		b.addActionListener(this);
		add(b);
		b = new Button("Red+Green");
		b.addActionListener(this);
		add(b);
		b = new Button("Red only");
		b.addActionListener(this);
		add(b);

		add(new Label("Green virus"));
		add(GreenMOIField = new TextField("2.0", 5));
		GreenAgeChoice = new Choice();
		GreenAgeChoice.add( "1" );
		GreenAgeChoice.add( "2" );
		GreenAgeChoice.add( "3" );
		GreenAgeChoice.add( "4" );
		add( GreenAgeChoice );
		GreenShape = new Choice();
		GreenShape.add( "Disc" );
		GreenShape.add( "Hexagon" );
		GreenShape.add( "Hex2" );
		GreenShape.add( "Hex3" );
		add( GreenShape );
		add(GreenRadius = new TextField("20", 4));
		b = new Button("Border on/off");
		b.addActionListener(this);
		add(b);
		b = new Button("Restart");
		b.addActionListener(this);
		add(b);
		b = new Button("Run");
		b.addActionListener(this);
		add(b);
		
		add(new Label("Delta X"));
		add(new Label("Delta T"));
		add(new Label("% infectible"));
		add(new Label("Min RRT"));
		add(new Label("Max RRT"));
		add(new Label("Filename"));
		add(new Label("Iterations"));
		add(new Label(" "));

		add(DeltaXField = new TextField("0", 4));
		add(DeltaTField = new TextField("0", 4));
		add(RateField = new TextField("5.0", 5));
		add(MinField = new TextField("8", 4));
		add(MaxField = new TextField("12", 4));
		add(Filename = new TextField("data.txt", 4));
		add(IterField = new TextField("15", 4));
		add(new Label(" "));
		
		canvas.repaint();
    }

	// Handle clicks on "Run", "Red only", "Green only", "Red+Green",
	// "Border on/off", and "Restart" buttons
    public void actionPerformed(ActionEvent ev) {
    	double inf_rate;
    	
		double red_moi = new Double(RedMOIField.getText().trim()).doubleValue();
		double green_moi = new Double(GreenMOIField.getText().trim()).doubleValue();
		double inf_pct = new Double(RateField.getText().trim()).doubleValue();
		
		inf_rate = inf_pct/100.0;
		String label = ev.getActionCommand();
		if (label.equals("Run")) {
			if (!initflag) canvas.init(Integer.parseInt(RedRadius.getText().trim()),
				Integer.parseInt(GreenRadius.getText().trim()),
				red_moi, green_moi,
				Integer.parseInt(RedAgeChoice.getSelectedItem().trim()),
				Integer.parseInt(GreenAgeChoice.getSelectedItem().trim()),
				RedShape.getSelectedItem().trim(), GreenShape.getSelectedItem().trim(),
				inf_rate, SuperYes.getState(),
				Integer.parseInt(DeltaXField.getText().trim()),
				Integer.parseInt(DeltaTField.getText().trim()), Filename.getText().trim());
			initflag = true;
			canvas.iterate(Integer.parseInt(IterField.getText().trim()),
				inf_rate, Integer.parseInt(RedAgeChoice.getSelectedItem().trim()),
    			Integer.parseInt(GreenAgeChoice.getSelectedItem().trim()),
    			Integer.parseInt(MinField.getText().trim()),
    			Integer.parseInt(MaxField.getText().trim()),
    			SuperYes.getState());
    	}
 		else if (label.equals("Red only")) canvas.setColorMode("RedOnly");
		else if (label.equals("Green only")) canvas.setColorMode("GreenOnly");
		else if (label.equals("Red+Green")) canvas.setColorMode("Both");
		else if (label.equals("Border on/off")) canvas.toggleBorder();
		else {
			canvas.init(Integer.parseInt(RedRadius.getText().trim()),
				Integer.parseInt(GreenRadius.getText().trim()),
				red_moi, green_moi,
				Integer.parseInt(RedAgeChoice.getSelectedItem().trim()),
				Integer.parseInt(GreenAgeChoice.getSelectedItem().trim()),
				RedShape.getSelectedItem().trim(), GreenShape.getSelectedItem().trim(),
				inf_rate, SuperYes.getState(),
				Integer.parseInt(DeltaXField.getText().trim()),
				Integer.parseInt(DeltaTField.getText().trim()), Filename.getText().trim());
			initflag = true;
		}
		canvas.repaint();
    }
}

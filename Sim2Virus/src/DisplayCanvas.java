import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.io.Console;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.Math.*;

/*
 * class DisplayCanvas defines the canvas that displays the simulation
 * and contains the methods that define how the cells and viruses
 * behave and how they're displayed
 *
 * The cells are arranged in a hexagonal grid, so each cell
 * has six immediate neighbors
 *
 * The constructor also initiates the statistics frame (window)
 */

class DisplayCanvas extends Canvas {

	// The number of cells along one edge of the hexagon
	static final int SIZE = 200;
	
	// State names for cell states
	static final int normal = 0;
	static final int infectible = 1;
	static final int infected1 = 2;
	static final int infected2 = 3;
	static final int infected3 = 4;
	static final int infected4 = 5;
	static final int infected5 = 6;
	static final int infected6 = 7;
	static final int deceased = 8;
	
	// State names to specify which viruses are currently displayed	
	static final int both = 0;
	static final int green_only = 1;
	static final int red_only = 2;
	
	// Color tables for displaying both, red only, and green only	
	static Color[] ctable = new Color[256];
	static Color[] rtable = new Color[256];
	static Color[] gtable = new Color[256];
	
	// Mode flag to indicate which colors are currently displayed
	static int colormode;
	
	// The maximum number of each type of virus at the current
	// time step.  Used for scaling the color when displaying
	// just one color
	static int max_red, max_green;
	
	// x and y coordinates of the corners of the focus borders
	static int[] focusBorderX = new int[6];
	static int[] focusBorderY = new int[6];
	
	// Multiplicity of infection for red and green
	static double Red_MOI;
	static double Green_MOI;

	// the number of time steps each type of virus can live outside a cell	 
	static int max_red_age;
	static int max_green_age;
	
	// String specifying the shapes of the foci
	static String redfocustype;
	static String greenfocustype;
	
	// The radii of the focal areas
	static int red_focal_radius = 20;
	static int green_focal_radius = 20;
	
	// The number of cells in each focus
	static int red_focal_size;
	static int green_focal_size;
	
	// Flag that indicates whether focus border is visible
	static boolean show_border;
	
	// The simulator clock
	static int timestep;
		
	// The time at which the red focus gets its initial infection
	static int RedTime;
		
	// The offset of the red focus from the center
	static int RedOffset;
		
	// Random number generator
	Random randval = new Random();
	
	// The state of each cell (normal, infectible, infected, etc.)
	int[][] cell_state = new int[2*SIZE-1][];
	
	// An event timer for each cell
	int[][] cell_timer = new int[2*SIZE-1][];
	
	// The numbers of red and green viruses of different
	// ages in or waiting to enter each cell
	int[][][] cell_red = new int[5][2*SIZE-1][];
	int[][][] cell_green = new int[5][2*SIZE-1][];
	
	// The canvas in the statistics frame
	StatArea statdisparea;
	StatArea statdisparea2;
	
	PrintStream fileout;
	
	/*
	 * Method enter_virus is called for each infectible cell
	 * to allow any waiting viruses to enter the cell.
	 * It sums all viruses waiting to enter the cell.
	 * If there are none, the cell remains infectible.
	 * If the cell becomes infected, its timer is set to a
	 * random value of 4, 5, or 6.
	 * If superinfection is not allowed, the majority virus wins.
	 * If superinfection is allowed, it is also possible (with
	 * probability proportional to the relative numbers of the
	 * two viruses) that only one type of virus enters the cell.
	 */
	private void enter_virus( int row, int col, boolean superInf ) {
		cell_red[0][row][col] = 0;
		cell_green[0][row][col] = 0;
		for (int i=1; i<5; i++) {
			cell_red[0][row][col] += cell_red[i][row][col];
			cell_green[0][row][col] += cell_green[i][row][col];
		}
		if ((cell_red[0][row][col]+cell_green[0][row][col]) > 0) {
			cell_state[row][col] = infected1;
			cell_timer[row][col] = 4 + Math.abs( randval.nextInt() ) % 3;
			if (!superInf) {
				if (cell_red[0][row][col] > cell_green[0][row][col])
					cell_green[0][row][col] = 0;
				else if (cell_red[0][row][col] == cell_green[0][row][col]) {
					if ((Math.abs( randval.nextInt() ) % 2) == 0)
						cell_green[0][row][col] = 0;
					else cell_red[0][row][col] = 0;
				}
				else cell_red[0][row][col] = 0;
			} else {
				int denom = cell_red[0][row][col] + cell_green[0][row][col];
				double redfrac = (double) cell_red[0][row][col] / denom;
				double minfrac = ( redfrac < 0.5 ) ? redfrac : ( 1.0 - redfrac );
				if ((Math.abs( randval.nextInt() ) % 10000) > 20000*minfrac) {
					if ((Math.abs( randval.nextInt() ) % 10000) < 10000*redfrac)
						cell_green[0][row][col] = 0;
					else cell_red[0][row][col] = 0;					
				}
			}
		} else {
			cell_state[row][col] = infectible;
		}
	}
	
	/*
	 * Method bud is called at alternate time steps after a cell
	 * becomes infected to simulate viruses budding out of the cell.
	 * It distributes viruses to the six neighboring cells in proportion
	 * to the ratio of the two types within the cell.  On the average
	 * about 100 viruses are distributed, up to a maximum of about 200.
	 * If superinfection is allowed and the neighboring cell is already
	 * infected, the viruses enter the cell immediately, otherwise they
	 * wait outside the cell.
	 */
	private void bud( int row, int col, int max_red_age, int max_green_age,
			boolean superInf ) {
	
	int maxred, maxgreen, numred, numgreen, prevcol, nextcol;

	int denom = cell_red[0][row][col] + cell_green[0][row][col];
	if (denom > 0) {
		
		if (row > SIZE-1) {prevcol = 0; nextcol = -1;}
		else if (row < SIZE-1) {prevcol = -1; nextcol = 0;}
		else {prevcol = -1; nextcol = -1;}	
		double redfrac = (double) cell_red[0][row][col] / denom;
		maxred = (int)(redfrac * 34);
		maxgreen = (int)((1-redfrac) * 34);
		
		if (maxred == 0) numred = 0;
		else numred = Math.abs( randval.nextInt() ) % maxred;
		if (maxgreen == 0) numgreen = 0;
		else numgreen = Math.abs( randval.nextInt() ) % maxgreen;
		if ((cell_state[row-1][col+prevcol] == normal)
				|| (cell_state[row-1][col+prevcol] == infectible)) {
			cell_red[max_red_age][row-1][col+prevcol] += numred;
			cell_green[max_green_age][row-1][col+prevcol] += numgreen;
		}
		else if ((cell_state[row-1][col+prevcol] != deceased)
				&& (superInf)) {
			cell_red[0][row-1][col+prevcol] += numred;
			cell_green[0][row-1][col+prevcol] += numgreen;
		}
				
		if (maxred == 0) numred = 0;
		else numred = Math.abs( randval.nextInt() ) % maxred;
		if (maxgreen == 0) numgreen = 0;
		else numgreen = Math.abs( randval.nextInt() ) % maxgreen;
		if ((cell_state[row-1][col+prevcol+1] == normal)
				|| (cell_state[row-1][col+prevcol+1] == infectible)) {
			cell_red[max_red_age][row-1][col+prevcol+1] += numred;
			cell_green[max_green_age][row-1][col+prevcol+1] += numgreen;
		}
		else if ((cell_state[row-1][col+prevcol+1] != deceased)
				&& (superInf)) {
			cell_red[0][row-1][col+prevcol+1] += numred;
			cell_green[0][row-1][col+prevcol+1] += numgreen;
		}
				
		if (maxred == 0) numred = 0;
		else numred = Math.abs( randval.nextInt() ) % maxred;
		if (maxgreen == 0) numgreen = 0;
		else numgreen = Math.abs( randval.nextInt() ) % maxgreen;
		if ((cell_state[row][col-1] == normal)
				|| (cell_state[row][col-1] == infectible)) {
			cell_red[max_red_age][row][col-1] += numred;
			cell_green[max_green_age][row][col-1] += numgreen;
		}
		else if ((cell_state[row][col-1] != deceased)
				&& (superInf)) {
			cell_red[0][row][col-1] += numred;
			cell_green[0][row][col-1] += numgreen;
		}
		
		if (maxred == 0) numred = 0;
		else numred = Math.abs( randval.nextInt() ) % maxred;
		if (maxgreen == 0) numgreen = 0;
		else numgreen = Math.abs( randval.nextInt() ) % maxgreen;
		if ((cell_state[row][col+1] == normal)
				|| (cell_state[row][col+1] == infectible)) {
			cell_red[max_red_age][row][col+1] += numred;
			cell_green[max_green_age][row][col+1] += numgreen;
		}
		else if ((cell_state[row][col+1] != deceased)
				&& (superInf)) {
			cell_red[0][row][col+1] += numred;
			cell_green[0][row][col+1] += numgreen;
		}
						
		if (maxred == 0) numred = 0;
		else numred = Math.abs( randval.nextInt() ) % maxred;
		if (maxgreen == 0) numgreen = 0;
		else numgreen = Math.abs( randval.nextInt() ) % maxgreen;
		if ((cell_state[row+1][col+nextcol] == normal)
				|| (cell_state[row+1][col+nextcol] == infectible)) {
			cell_red[max_red_age][row+1][col+nextcol] += numred;
			cell_green[max_green_age][row+1][col+nextcol] += numgreen;
		}
		else if ((cell_state[row+1][col+nextcol] != deceased)
				&& (superInf)) {
			cell_red[0][row+1][col+nextcol] += numred;
			cell_green[0][row+1][col+nextcol] += numgreen;
		}
				
		if (maxred == 0) numred = 0;
		else numred = Math.abs( randval.nextInt() ) % maxred;
		if (maxgreen == 0) numgreen = 0;
		else numgreen = Math.abs( randval.nextInt() ) % maxgreen;
		if ((cell_state[row+1][col+nextcol+1] == normal)
				|| (cell_state[row+1][col+nextcol+1] == infectible)) {
			cell_red[max_red_age][row+1][col+nextcol+1] += numred;
			cell_green[max_green_age][row+1][col+nextcol+1] += numgreen;
		}
		else if ((cell_state[row+1][col+nextcol+1] != deceased)
				&& (superInf)) {
			cell_red[0][row+1][col+nextcol+1] += numred;
			cell_green[0][row+1][col+nextcol+1] += numgreen;
		}
	}
	}

	/*
	 * Method initFocus
	 */
	private void initFocus( int offset, int red_count, int green_count,
		String focus_type, int focal_radius ) {
		
		int row, col;
			
		// Four shapes are currently implemented for the focus:
		//   Hexagon - a regular hexagon
		//   Hex2 - a smaller hexagonal area formed by moving one
		//      vertex of a regular hexagon to the center
		//   Hex3 - move the opposite vertex to the center as well
		//      (this gives two triangles joined at a vertex)
		//   Disc - an approximation of a disc on the hexagonal grid
		//
		// For each shape of focus, the following sections calculate
		// the number of cells in the focus, then spread the appropriate
		// numbers of each type of virus at random among the cells
		// of the focus
		if (focus_type == "Hexagon") {
			while ((red_count+green_count) > 0) {
				row = Math.abs( randval.nextInt() ) % (2*focal_radius+1) +
					(SIZE-(focal_radius+1));
				col = Math.abs( randval.nextInt() ) % (2*focal_radius+1) +
					(SIZE-(focal_radius+1));
				if (col < (cell_state[row].length+focal_radius+1-SIZE)) {
					if (red_count > 0) {
						cell_red[max_red_age][row][col+offset]++;
						red_count--;
					} else {
						cell_green[max_green_age][row][col+offset]++;
						green_count--;
					}
				}
			}
		}
		else if (focus_type == "Hex2") {
			while ((red_count+green_count) > 0) {
				row = Math.abs( randval.nextInt() ) % (2*focal_radius+1) +
					(SIZE-(focal_radius+1));
				col = Math.abs( randval.nextInt() ) % (focal_radius+1) +
					(SIZE-(focal_radius+1));
				if (red_count > 0) {
					cell_red[max_red_age][row][col+offset]++;
					red_count--;
				} else {
					cell_green[max_green_age][row][col+offset]++;
					green_count--;
				}
			}
		}
		else if (focus_type == "Hex3") {
			while ((red_count+green_count) > 0) {
				row = Math.abs( randval.nextInt() ) % (2*focal_radius+1) +
					(SIZE-(focal_radius+1));
				if (row < SIZE) {
					col = Math.abs( randval.nextInt() ) % (SIZE - row) +
						row;
				} else {
					col = Math.abs( randval.nextInt() ) % (row + 2 - SIZE) +
						2*SIZE - (row + 2);
				}
				if (red_count > 0) {
					cell_red[max_red_age][row][col+offset]++;
					red_count--;
				} else {
					cell_green[max_green_age][row][col+offset]++;
					green_count--;
				}
			}
		}
		else if (focus_type == "Disc") {
			int height = (int) Math.floor(2*focal_radius/Math.sqrt(3.0));
			while ((red_count+green_count) > 0) {
				row = Math.abs( randval.nextInt() ) % (2*height+1) +
					(SIZE - (1 + height));
				col = Math.abs( randval.nextInt() ) % (3*focal_radius+1) +
					(SIZE-(2*focal_radius+1));
				if (row<SIZE) {
					if (3*(row+1-SIZE)*(row+1-SIZE) + (2*col+1-(SIZE+row))*(2*col+1-(SIZE+row))
							<= 4*focal_radius*focal_radius) {
						if (red_count > 0) {
							cell_red[max_red_age][row][col+offset]++;
							red_count--;
						} else {
							cell_green[max_green_age][row][col+offset]++;
							green_count--;
						}
					}
				} else {
					if (3*(row+1-SIZE)*(row+1-SIZE) + (2*col+row+3-3*SIZE)*(2*col+row+3-3*SIZE)
							<= 4*focal_radius*focal_radius) {
						if (red_count > 0) {
							cell_red[max_red_age][row][col+offset]++;
							red_count--;
						} else {
							cell_green[max_green_age][row][col+offset]++;
							green_count--;
						}
					}
				}
			}
		}
	}
	
	/*
	 * Constructor for class DisplayCanvas
	 * The sda parameter provides for communication between this class
	 * and the instance of Statarea used to display statistics in a
	 * separate frame (window)
	 */
	public DisplayCanvas( StatArea sda, StatArea sda2, PrintStream p ) {
//		statdisparea = sda;
//		statdisparea2 = sda2;
//		fileout = p;
		
		colormode = both;
		show_border = false;
		red_focal_size = 0;
		green_focal_size = 0;
		
		// set max_red and max_green to 1 in case paint() is called
		// before these are calculated
		max_red = 1;
		max_green = 1;
		
		// Allocate space for arrays and initialize
		for (int row=0; row<SIZE; row++) {
			cell_state[row] = new int[SIZE+row];
			cell_timer[row] = new int[SIZE+row];
			for (int i=0; i<5; i++) {
				cell_red[i][row] = new int[SIZE+row];
				cell_green[i][row] = new int[SIZE+row];
			}
		}
		for (int row=SIZE; row<cell_state.length; row++) {
			cell_state[row] = new int[3*SIZE-(row+2)];
			cell_timer[row] = new int[3*SIZE-(row+2)];
			for (int i=0; i<5; i++) {
				cell_red[i][row] = new int[3*SIZE-(row+2)];
				cell_green[i][row] = new int[3*SIZE-(row+2)];
			}
		}
	    for (int row=0; row<2*SIZE-1; row++) {
			for (int col=0; col<cell_state[row].length; col++) {
				cell_state[row][col] = normal;
				cell_timer[row][col] = 0;
				for (int i=0; i<5; i++) {
					cell_red[i][row][col] = 0;
					cell_green[i][row][col] = 0;
				}
			}
		}
		
		// Initialize color tables
		for (int i=0; i<128; i++) ctable[i] = new Color(2*i, 255, 0);
		for (int i=128; i<256; i++) ctable[i] = new Color(255, 511-2*i, 0);
		for (int i=0; i<256; i++) rtable[i] = new Color(255, 255-i, 255-i);
		for (int i=0; i<256; i++) gtable[i] = new Color(255-i, 255, 255-i);
	}

	/*
	 * Initializations for class DisplayCanvas
	 * This is called when the "Restart" button is clicked
	 *
	 * Parameters:
	 *   radius - the radius of the focal area
	 *   RedMOI, GreenMOI - Multiplicity of Initial Infection, the average
	 *      number of each type of virus per cell in the focus
	 *   max_red_age, max_green_age - the number of time steps each type
	 *      of virus can live outside a cell
	 *   focus_type - a string giving the name for the shape of the focus
	 *   inf_rate - the rate at which cells get receptors making them
	 *       susceptible to infection
	 *   superInf - a boolean specifying whether superinfection is allowed
	 */
    public void init( int RedRadius, int GreenRadius, double RedMOI, double GreenMOI,
    				int maxredage, int maxgreenage,
    				String red_focus_type, String green_focus_type,
    				double inf_rate, boolean superInf,
    				int deltaX, int deltaT, String filename) {
    
		int i, row, col;
		
		try {
			FileOutputStream output = new FileOutputStream(filename);
			fileout = new PrintStream(output);	
			
			System.out.println("Opening: " + filename);
		} catch(Exception e) {
			System.err.println("error!");
		}
		
		red_focal_radius = RedRadius;
		green_focal_radius = GreenRadius;
		Red_MOI = RedMOI;
		Green_MOI = GreenMOI;
		max_red_age = maxredage;
		max_green_age = maxgreenage;
		redfocustype = red_focus_type;
		greenfocustype = green_focus_type;
		red_focal_size = 0;
		green_focal_size = 0;
		show_border = false;
		timestep = 0;
		RedTime = deltaT;
		RedOffset = deltaX;
				
		// Reinitialize cell variables
	    for (row=0; row<2*SIZE-1; row++) {
			for (col=0; col<cell_state[row].length; col++) {
				cell_state[row][col] = normal;
				cell_timer[row][col] = 0;
				for (i=0; i<5; i++) {
					cell_red[i][row][col] = 0;
					cell_green[i][row][col] = 0;
				}
			}
		}

		if (redfocustype == "Hexagon")
			red_focal_size = 3*red_focal_radius*red_focal_radius+3*red_focal_radius+1;
		else if (redfocustype == "Hex2")
			red_focal_size = 2*red_focal_radius*red_focal_radius+3*red_focal_radius+1;
		else if (redfocustype == "Hex3")
			red_focal_size = red_focal_radius*red_focal_radius+3*red_focal_radius+1;
		else if (redfocustype == "Disc")
			red_focal_size = (int) Math.floor(2*Math.PI*red_focal_radius*red_focal_radius/
				Math.sqrt(3.0));
		if (greenfocustype == "Hexagon")
			green_focal_size = 3*green_focal_radius*green_focal_radius+3*green_focal_radius+1;
		else if (greenfocustype == "Hex2")
			green_focal_size = 2*green_focal_radius*green_focal_radius+3*green_focal_radius+1;
		else if (greenfocustype == "Hex3")
			green_focal_size = green_focal_radius*green_focal_radius+3*green_focal_radius+1;
		else if (greenfocustype == "Disc")
			green_focal_size = (int) Math.floor(2*Math.PI*green_focal_radius*green_focal_radius/
				Math.sqrt(3.0));
		
		
		initFocus( -deltaX, 0, (int)(Green_MOI*green_focal_size), green_focus_type, GreenRadius );
		if ( RedTime == 0 )
			initFocus( deltaX, (int)(Red_MOI*red_focal_size), 0, red_focus_type, RedRadius );
		
		// Start the initial infection
		max_red = 0;
		max_green = 0;
	    for (row=1; row<2*SIZE-2; row++)
		  for (col=1; col<cell_state[row].length-1; col++) {     // avoid edges
		  	if ((Math.abs( randval.nextInt() ) % 10000) < 10000*inf_rate) {
			  enter_virus( row, col, superInf );
			  if (cell_red[0][row][col] > max_red)
				max_red = cell_red[0][row][col];
			  if (cell_green[0][row][col] > max_green)
				max_green = cell_green[0][row][col];
			}
		  }
		repaint();
	}
	
	/*
	 * Method to change the display mode to display both types
	 * of virus, just green, or just red
	 */
	public void setColorMode( String mode ) {
		if (mode == "Both") colormode = both;
		else if (mode == "GreenOnly") colormode = green_only;
		else if (mode == "RedOnly") colormode = red_only;
	}
	
	/*
	 * Method to change the display mode to show or hide
	 * a thin black line around the edge of the focal area
	 */
	public void toggleBorder() {
		if (show_border) show_border = false;
		else show_border = true;
		repaint();
	}

	/*
	 * Method paint is called by repaint() and whenever the
	 * canvas needs to be redrawn
	 *
	 * Uninfected cells are drawn as white, otherwise the
	 * appropriate color table is used to set the color.
	 * Each cell is represented by two pixels.
	 */
    public void paint(Graphics g) {
		for (int row=0; row<2*SIZE-1; row++) {
			String output = "";
			for (int col=0; col<cell_state[row].length; col++) {
				if ((cell_state[row][col] == normal) ||
					(cell_state[row][col] == infectible))
						g.setColor(Color.white);
				else if ((cell_red[0][row][col]+cell_green[0][row][col]) == 0)
					g.setColor(Color.white);
				else if (colormode == both) g.setColor(
					ctable[255*cell_red[0][row][col]/
						(cell_green[0][row][col]+cell_red[0][row][col])] );
				else if (colormode == green_only) g.setColor(
					gtable[255*cell_green[0][row][col]/max_green] );
				else if (colormode == red_only) g.setColor(
					rtable[255*cell_red[0][row][col]/max_red] );
				
				g.drawLine( 2*col+Math.abs(1+row-SIZE),
					row,
					2*col+Math.abs(1+row-SIZE)+1,
					row);
				
				int denom = cell_green[0][row][col]+cell_red[0][row][col];
				if (denom > 0) {
					int binnum = 255*cell_red[0][row][col]/denom;
				
					output = output.concat(binnum < 128 ? "1 " : "2 ");
				}  else {
					output = output.concat("0 ");
				}
			}
			
			if(fileout != null) {
				fileout.println(output);
			}
		}
		
		if(fileout != null) {
			fileout.println("\n");
		}
		
		// If desired, a thin black line is drawn around the foci
		g.setColor(Color.black);
		if (show_border) {
			if (redfocustype == "Hexagon") {
				focusBorderX[0] = 2*(SIZE + RedOffset)-red_focal_radius-2;
				focusBorderX[1] = 2*(SIZE + RedOffset)+red_focal_radius;
				focusBorderX[2] = 2*(SIZE + RedOffset)+2*red_focal_radius;
				focusBorderX[3] = 2*(SIZE + RedOffset)+red_focal_radius;
				focusBorderX[4] = 2*(SIZE + RedOffset)-red_focal_radius-2;
				focusBorderX[5] = 2*(SIZE + RedOffset)-2*red_focal_radius-2;
				focusBorderY[0] = SIZE-(red_focal_radius+1);
				focusBorderY[1] = SIZE-(red_focal_radius+1);
				focusBorderY[2] = SIZE;
				focusBorderY[3] = SIZE+red_focal_radius-1;
				focusBorderY[4] = SIZE+red_focal_radius-1;
				focusBorderY[5] = SIZE;
				g.drawPolygon( focusBorderX, focusBorderY, 6 );
			}
			if (redfocustype == "Hex2") {
				focusBorderX[0] = 2*(SIZE + RedOffset)-red_focal_radius-2;
				focusBorderX[1] = 2*(SIZE + RedOffset)+red_focal_radius;
				focusBorderX[2] = 2*(SIZE + RedOffset);
				focusBorderX[3] = 2*(SIZE + RedOffset)+red_focal_radius;
				focusBorderX[4] = 2*(SIZE + RedOffset)-red_focal_radius-2;
				focusBorderX[5] = 2*(SIZE + RedOffset)-2*red_focal_radius-2;
				focusBorderY[0] = SIZE-(red_focal_radius+1);
				focusBorderY[1] = SIZE-(red_focal_radius+1);
				focusBorderY[2] = SIZE;
				focusBorderY[3] = SIZE+red_focal_radius-1;
				focusBorderY[4] = SIZE+red_focal_radius-1;
				focusBorderY[5] = SIZE;
				g.drawPolygon( focusBorderX, focusBorderY, 6 );
			}
			if (redfocustype == "Hex3") {
				focusBorderX[0] = 2*(SIZE + RedOffset)-red_focal_radius-2;
				focusBorderX[1] = 2*(SIZE + RedOffset)+red_focal_radius;
				focusBorderX[2] = 2*(SIZE + RedOffset);
				focusBorderX[3] = 2*(SIZE + RedOffset)+red_focal_radius;
				focusBorderX[4] = 2*(SIZE + RedOffset)-red_focal_radius-2;
				focusBorderX[5] = 2*(SIZE + RedOffset);
				focusBorderY[0] = SIZE-(red_focal_radius+1);
				focusBorderY[1] = SIZE-(red_focal_radius+1);
				focusBorderY[2] = SIZE;
				focusBorderY[3] = SIZE+red_focal_radius-1;
				focusBorderY[4] = SIZE+red_focal_radius-1;
				focusBorderY[5] = SIZE;
				g.drawPolygon( focusBorderX, focusBorderY, 6 );
			}
			if (redfocustype == "Disc") {
				int height = (int) Math.floor( 2*red_focal_radius/Math.sqrt(3.0) );
				g.drawOval( 2*(SIZE + RedOffset) - 2*(red_focal_radius+1),
							SIZE - (1 + height),
							4*red_focal_radius,
							2*height );
			}
			if (greenfocustype == "Hexagon") {
				focusBorderX[0] = 2*(SIZE - RedOffset)-green_focal_radius-2;
				focusBorderX[1] = 2*(SIZE - RedOffset)+green_focal_radius;
				focusBorderX[2] = 2*(SIZE - RedOffset)+2*green_focal_radius;
				focusBorderX[3] = 2*(SIZE - RedOffset)+green_focal_radius;
				focusBorderX[4] = 2*(SIZE - RedOffset)-green_focal_radius-2;
				focusBorderX[5] = 2*(SIZE - RedOffset)-2*green_focal_radius-2;
				focusBorderY[0] = SIZE-(green_focal_radius+1);
				focusBorderY[1] = SIZE-(green_focal_radius+1);
				focusBorderY[2] = SIZE;
				focusBorderY[3] = SIZE+green_focal_radius-1;
				focusBorderY[4] = SIZE+green_focal_radius-1;
				focusBorderY[5] = SIZE;
				g.drawPolygon( focusBorderX, focusBorderY, 6 );
			}
			if (greenfocustype == "Hex2") {
				focusBorderX[0] = 2*(SIZE - RedOffset)-green_focal_radius-2;
				focusBorderX[1] = 2*(SIZE - RedOffset)+green_focal_radius;
				focusBorderX[2] = 2*(SIZE - RedOffset);
				focusBorderX[3] = 2*(SIZE - RedOffset)+green_focal_radius;
				focusBorderX[4] = 2*(SIZE - RedOffset)-green_focal_radius-2;
				focusBorderX[5] = 2*(SIZE - RedOffset)-2*green_focal_radius-2;
				focusBorderY[0] = SIZE-(green_focal_radius+1);
				focusBorderY[1] = SIZE-(green_focal_radius+1);
				focusBorderY[2] = SIZE;
				focusBorderY[3] = SIZE+green_focal_radius-1;
				focusBorderY[4] = SIZE+green_focal_radius-1;
				focusBorderY[5] = SIZE;
				g.drawPolygon( focusBorderX, focusBorderY, 6 );
			}
			if (greenfocustype == "Hex3") {
				focusBorderX[0] = 2*(SIZE - RedOffset)-green_focal_radius-2;
				focusBorderX[1] = 2*(SIZE - RedOffset)+green_focal_radius;
				focusBorderX[2] = 2*(SIZE - RedOffset);
				focusBorderX[3] = 2*(SIZE - RedOffset)+green_focal_radius;
				focusBorderX[4] = 2*(SIZE - RedOffset)-green_focal_radius-2;
				focusBorderX[5] = 2*(SIZE - RedOffset);
				focusBorderY[0] = SIZE-(green_focal_radius+1);
				focusBorderY[1] = SIZE-(green_focal_radius+1);
				focusBorderY[2] = SIZE;
				focusBorderY[3] = SIZE+green_focal_radius-1;
				focusBorderY[4] = SIZE+green_focal_radius-1;
				focusBorderY[5] = SIZE;
				g.drawPolygon( focusBorderX, focusBorderY, 6 );
			}
			if (greenfocustype == "Disc") {
				int height = (int) Math.floor( 2*green_focal_radius/Math.sqrt(3.0) );
				g.drawOval( 2*(SIZE - RedOffset) - 2*(green_focal_radius+1),
							SIZE - (1 + height),
							4*green_focal_radius,
							2*height );
			}
		}
		
		// Display the current time, focus size, and maximum
		// number of each type of virus in any cell
		g.drawString("Time: "+timestep, 10, 20);
		g.drawString(green_focal_size+" cells in green focus", 10, 40);
		g.drawString(red_focal_size+" cells in red focus", 10, 60);
		g.drawString("Max red: "+max_red, 10, 80);
		g.drawString("Max green: "+max_green, 10, 100);
		
		// Display the three color tables
		for (int i=0; i<256; i++) {
			g.setColor( ctable[i] );
			g.drawLine( 2*i, 2*SIZE+2, 2*i+1, 2*SIZE+2 );
			g.drawLine( 2*i, 2*SIZE+3, 2*i+1, 2*SIZE+3 );
		}
		for (int i=0; i<256; i++) {
			g.setColor( rtable[i] );
			g.drawLine( 2*i, 2*SIZE+5, 2*i+1, 2*SIZE+5 );
			g.drawLine( 2*i, 2*SIZE+6, 2*i+1, 2*SIZE+6 );
		}
		for (int i=0; i<256; i++) {
			g.setColor( gtable[i] );
			g.drawLine( 2*i, 2*SIZE+8, 2*i+1, 2*SIZE+8 );
			g.drawLine( 2*i, 2*SIZE+9, 2*i+1, 2*SIZE+9 );
		}
		g.setColor(Color.black);
		g.drawString("Virus Competition Simulation", 10, 2*SIZE+28);
    }

	/*
	 * Method iterate is the main loop of the simulation, called whenever
	 * the "Run" button is clicked
	 *
	 * Parameters:
	 *   iterations - the number of times steps to simulate in this call
	 *   inf_rate - the rate at which cells get receptors making them
	 *       susceptible to infection
	 *   max_red_age, max_green_age - the number of time steps each type
	 *      of virus can live outside a cell
	 *   min_infect, max_infect - the cell timer for a newly infectible cell
	 *      is set to a uniform random value in this range to determine how
	 *      long it remains infectible (how long it retains its receptor)
	 *   superInf - a boolean specifying whether superinfection is allowed
	 */
    public void iterate(int iterations, double inf_rate, int max_red_age, int max_green_age, int min_infect, int max_infect, boolean superInf) {
    
    	// bins for calculating statistcs to be displayed in statistics frame
    	int bin[] = new int[12];
    	int binnum, denom;
    	
		while (iterations > 0) {
		    timestep++; iterations--;
		    for (int row=1; row<2*SIZE-2; row++) {
				for (int col=1; col<cell_state[row].length-1; col++) {      // avoid edges
			    
				    // Handle cell state transitions
				    if (cell_state[row][col] == normal) {
					  if ((Math.abs( randval.nextInt() ) % 10000) < 10000*inf_rate) {
					  	cell_state[row][col] = infectible;
					  	cell_timer[row][col] = min_infect +
					  		Math.abs( randval.nextInt() ) % (1 + max_infect - min_infect);
					  }
				    }
				    else if (cell_state[row][col] == infectible) {
						if (--cell_timer[row][col] <= 0) cell_state[row][col] = normal;
					}
				    else if (cell_state[row][col] == infected1) {
				    	cell_state[row][col] = infected2;
				    	cell_timer[row][col]--;
				    }
				    else if (cell_state[row][col] == infected2) {
				    	bud( row, col, max_red_age, max_green_age, superInf );
				    	cell_state[row][col] = infected3;
				    	cell_timer[row][col]--;
				    }
				    else if (cell_state[row][col] == infected3) {
				    	cell_state[row][col] = infected4;
				    	cell_timer[row][col]--;
				    }
				    else if (cell_state[row][col] == infected4) {
				    	if (--cell_timer[row][col] <= 0)
				    		cell_state[row][col] = deceased;
				    	else {
				    		bud( row, col, max_red_age, max_green_age, superInf );
				    		cell_state[row][col] = infected5;
				    	}
				    }
				    else if (cell_state[row][col] == infected5) {
				    	if (--cell_timer[row][col] <= 0)
				    		cell_state[row][col] = deceased;
				    	else cell_state[row][col] = infected6;
				    }
				    else if (cell_state[row][col] == infected6) {
				    	bud( row, col, max_red_age, max_green_age, superInf );
				    	cell_state[row][col] = deceased;
				    }
				}
			}
		
			// Scan through array, aging viruses outside cells and
			// allowing viruses to enter infectible cells
		    for (int row=1; row<2*SIZE-2; row++) {
				for (int col=1; col<cell_state[row].length-1; col++) {
					if (cell_state[row][col] == normal) {
						for (int i=2; i<5; i++) {
							cell_red[i-1][row][col] = cell_red[i][row][col];
							cell_green[i-1][row][col] = cell_green[i][row][col];
						}
						cell_red[4][row][col] = 0;
						cell_green[4][row][col] = 0;
					}
					else if (cell_state[row][col] == infectible) {
					  enter_virus( row, col, superInf );
					}
				}
			}
		
			if ( timestep == RedTime ) {
				initFocus( RedOffset, (int)(Red_MOI*red_focal_size), 0, redfocustype, red_focal_radius );		
			}
		
			// Calculate and display statistics
			for (int i=0; i<12; i++) bin[i] = 0;
			
			for (int row=1; row<2*SIZE-2; row++) {
//				String rowData = "";
				
				for (int col=1; col<cell_state[row].length-1; col++) {
					denom = cell_green[0][row][col]+cell_red[0][row][col];
					if (denom > 0) {
						binnum = 255*cell_red[0][row][col]/denom;
						if (binnum == 0) bin[0]++;
						else if (binnum == 255) bin[11]++;
						else if (binnum < 26) bin[1]++;
						else if (binnum > 230) bin[10]++;
						else if (binnum < 52) bin[2]++;
						else if (binnum > 204) bin[9]++;
						else if (binnum < 77) bin[3]++;
						else if (binnum > 179) bin[8]++;
						else if (binnum < 103) bin[4]++;
						else if (binnum > 153) bin[7]++;
						else if (binnum < 128) bin[5]++;
						else bin[6]++;
					
//						rowData = rowData.concat(binnum < 128 ? "1 " : "2 ");
					}  else {
//						rowData = rowData.concat("0 ");
					}
				}
				
//				statdisparea2.addString(rowData + "\n");		
			} 
//			statdisparea.addString( timestep+"\t"+bin[0]+"\t"+bin[1]+"\t"+
//					bin[2]+"\t"+bin[3]+"\t"+bin[4]+"\t"+bin[5]+"\t"+bin[6]+"\t"+
//					bin[7]+"\t"+bin[8]+"\t"+bin[9]+"\t"+bin[10]+"\t"+bin[11]+"\n" );   
		}
		
//		statdisparea2.addString("\n");
	
		
		// After main loop, calculate new values for max_red and max_green
		max_red = 0;
		max_green = 0;
	    for (int row=1; row<2*SIZE-2; row++) {
			for (int col=1; col<cell_state[row].length-1; col++) {
				if ((cell_red[0][row][col]+cell_green[0][row][col]) > 0) {
					if (cell_red[0][row][col] > max_red) {
						max_red = cell_red[0][row][col];
					}
					if (cell_green[0][row][col] > max_green) {
						max_green = cell_green[0][row][col];
					}
				}
			}
	    }
		repaint();
	}
}

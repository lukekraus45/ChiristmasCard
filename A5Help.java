// CS 401 Fall 2014
// Assignment 5 Help Program
// Read this program carefully and run it as well.  This shows you how to
// do a number of things that are required for Assignment 5.  You may even
// use this as a starting point to build your completed assignment.  Note
// however that the credit will be given for what you ADD to this program,
// not for what has already been implemented by me!  Also note that you are
// not required to use this program at all, as long as you implement the
// assignment correctly! 

// For additional help, see Sections 13.8, 14.5-14.6 in the text




/*
 * 
 * The purpose of this program was to use a Graphical interface to portray a Holliday Greeting card that can be saved and reopened as well as printed,
 * There were different types of functions in the program. First I created the images that would be able to be displayed on the screen. Next I 
 * took all of the buttons and handeled them differently so that the program could execute in different ways depending on what was selected by the user
 * The save and save as buttons allow the user to save their work and continue on it later. They also have the option to open a file that they
 * have previusly worked on. The option to print allows the user to print out a picture form of their work. They have the option of creating 
 * a new image which allows them to start over. Within the execution of the program they also have the ability to cut copy and paste selected images
 * Finally they can resize or deleted selected images. 
 */

import javax.swing.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.awt.print.*;

// Create enum types that will be useful in the program
enum Figures {TREE,SNOWFLAKE,GREETING, CLOUD, CABIN, SNOWMAN};
enum Mode {NONE,DRAW,SELECTED,MOVING};

// Code extracted from Oracle Java Example programs.  See link below for full code:
// http://docs.oracle.com/javase/tutorial/2d/printing/examples/PrintUIWindow.java
class thePrintPanel implements Printable
{
	JPanel panelToPrint;
	
	public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException
    {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform t = new AffineTransform();
        t.scale(0.9, 0.9);
        g2d.transform(t);
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        /* Now print the window and its visible contents */
        panelToPrint.printAll(g);

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
    
    public thePrintPanel(JPanel p)
    {
    	panelToPrint = p;
    }
}

public class A5Help
{	
	private ShapePanel drawPanel;
	private JPanel buttonPanel;
	private JButton makeShape;
	private JRadioButton makeTree, makeFlake, makeGreet, makeCabin, makeSnowman, makeCloud;
	private ButtonGroup shapeGroup;
	private Figures currShape;
	private JLabel msg, msg2;
	private JMenuBar theBar;
	private JMenu fileMenu, editMenu;
	private JMenuItem endProgram, printScene, copy, cut, paste, newp,open, save, saveas;
	private JPopupMenu popper;
	private JMenuItem delete, resize;
	private JFrame theWindow;
	private Container c;
	private File infile;
	private Scanner fscan;
	private String text;
	private static String index1;
	
	// This ArrayList is used to store the shapes in the program.
	// It is specified to be of type MyShape, so objects of any class
	// that implements the MyShape interface can be stored in here.
	// See Section 7.13 in your text for more info on ArrayList.
	private ArrayList<MyShape> shapeList;	
	private MyShape newShape;

	public A5Help()
	{
		drawPanel = new ShapePanel(640, 480);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(3, 3));

		makeShape = new JButton("Click to Draw");

		ButtonHandler bhandler = new ButtonHandler();
		makeShape.addActionListener(bhandler);

		buttonPanel.add(makeShape);
		msg = new JLabel("");
		msg2 = new JLabel(" ");
		buttonPanel.add(msg);
		buttonPanel.add(msg2);

		makeTree = new JRadioButton("Tree", false);
		makeFlake = new JRadioButton("Snowflake", true);
		makeGreet = new JRadioButton("Greeting", false);
		makeCloud = new JRadioButton("Cloud", false);
		makeCabin = new JRadioButton("Cabin", false);
		makeSnowman = new JRadioButton("Snowman" , false);

		RadioHandler rhandler = new RadioHandler();
		makeTree.addItemListener(rhandler);
		makeFlake.addItemListener(rhandler);
		makeGreet.addItemListener(rhandler);
		makeCloud.addItemListener(rhandler);
		makeCabin.addItemListener(rhandler);
		makeSnowman.addItemListener(rhandler);

		buttonPanel.add(makeFlake);
		buttonPanel.add(makeTree);
		buttonPanel.add(makeGreet);
		buttonPanel.add(makeCloud);
		buttonPanel.add(makeCabin);
		buttonPanel.add(makeSnowman);
		

		// A ButtonGroup allows a set of JRadioButtons to be associated
		// together such that only one can be selected at a time
		shapeGroup = new ButtonGroup();
		shapeGroup.add(makeFlake);
		shapeGroup.add(makeTree);
		shapeGroup.add(makeGreet);
		shapeGroup.add(makeCloud);
		shapeGroup.add(makeCabin);
		shapeGroup.add(makeSnowman);

		currShape = Figures.SNOWFLAKE;
		drawPanel.setMode(Mode.NONE);

		theWindow = new JFrame("CS 401 Assig5 Help Program");
		c = theWindow.getContentPane();
		drawPanel.setBackground(Color.lightGray);
		c.add(drawPanel, BorderLayout.NORTH);
		c.add(buttonPanel, BorderLayout.SOUTH);

		// Note how the menu is created.  First we make a JMenuBar, then
		// we put a JMenu in it, then we put JMenuItems in the JMenu.  We
		// can have multiple JMenus if we like.  JMenuItems generate
		// ActionEvents, just like JButtons, so we just have to link an
		// ActionListener to them.
		theBar = new JMenuBar();
		theWindow.setJMenuBar(theBar);
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		
		
		theBar.add(fileMenu);
		theBar.add(editMenu);
		printScene = new JMenuItem("Print");
		endProgram = new JMenuItem("Exit");
		copy = new JMenuItem("Copy");
		cut = new JMenuItem("Cut");
		paste = new JMenuItem("Paste");
		newp = new JMenuItem("New");
		open = new JMenuItem("Open");
		save = new JMenuItem("Save");
		saveas = new JMenuItem("Save As");
		
		
		editMenu.add(cut);
		editMenu.add(copy);
		editMenu.add(paste);
		cut.setEnabled(false);
		copy.setEnabled(false);
		fileMenu.add(newp);
		fileMenu.add(open);
		fileMenu.add(save);
		fileMenu.add(saveas);
		fileMenu.add(printScene);
		fileMenu.add(endProgram);
		printScene.addActionListener(bhandler);
		endProgram.addActionListener(bhandler);
		cut.addActionListener(bhandler);
		copy.addActionListener(bhandler);
		paste.addActionListener(bhandler);
		newp.addActionListener(bhandler);
		open.addActionListener(bhandler);
		save.addActionListener(bhandler);
		saveas.addActionListener(bhandler);
		paste.setEnabled(false);
		
		// JPopupMenu() also holds JMenuItems.  To see how it is actually
		// brought out, see the mouseReleased() method in the ShapePanel class
		// below.
		popper = new JPopupMenu();
		delete = new JMenuItem("Delete");
		resize = new JMenuItem("Resize");
		delete.addActionListener(bhandler);
		resize.addActionListener(bhandler);
		popper.add(delete);
		popper.add(resize);
		
		theWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		theWindow.pack();
		theWindow.setVisible(true);
	}

	public static void main(String [] args)
	{
		new A5Help();
	}

	// See Section 12.4 for information on JRadioButtons.  Note that the
	// text uses ActionListeners to handle JRadioButtons.  Clicking on
	// a JRadioButton actually generates both an ActionEvent and an
	// ItemEvent.  I am using the ItemEvent here.  To handle the event,
	// all I am doing is changing a state variable that will affect the
	// MouseListener in the ShapePanel.
	private class RadioHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			if (e.getSource() == makeTree)
				currShape = Figures.TREE;
			else if (e.getSource() == makeFlake)
				currShape = Figures.SNOWFLAKE;
			else if (e.getSource() == makeGreet)
				currShape = Figures.GREETING;
			else if(e.getSource() == makeCloud)
				currShape = Figures.CLOUD;
			else if(e.getSource() == makeCabin)
				currShape = Figures.CABIN;
			else if(e.getSource() == makeSnowman)
				currShape = Figures.SNOWMAN;
		}
	}

	// Note how the makeShape button and moveIt menu item are handled 
	// -- we again simply set the state in the panel so that the mouse will 
	// actually do the work.  The state needs to be set back in the mouse
	// listener.
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//if the button is make shape than the shape will be drawn
			if (e.getSource() == makeShape)
			{
				if (makeShape.getText().equals("Click to Draw"))
				{
					drawPanel.setMode(Mode.DRAW);
					msg.setText("Position new shapes with mouse");
					makeShape.setText("Click to Edit");
				} 
				else
				{
					drawPanel.setMode(Mode.NONE);
					msg.setText("Edit shapes with mouse");
					makeShape.setText("Click to Draw");
				}
			} 
			//if the user selects the delete button the object will be deleted
			else if (e.getSource() == delete)
			{
				boolean ans = drawPanel.deleteSelected();
				if (ans)
				{
					msg.setText("Shape deleted");
					drawPanel.repaint();
				}
			}
			//if the user selects print they will be able to print a picutre 
			else if (e.getSource() == printScene)
			{
				Printable thePPanel = new thePrintPanel(drawPanel); 
			    PrinterJob job = PrinterJob.getPrinterJob();
         		job.setPrintable(thePPanel);
         		boolean ok = job.printDialog();
         		if (ok) 
         		{
             	 	try {
                  		job.print();
             		} 
             		catch (PrinterException ex) {
              		/* The job did not successfully complete */
             		JOptionPane.showMessageDialog(null, "Print job did not complete sucessfully! Try again later.");
             		}
             	}
            }
			
			//if the user wishes to exit the program than they will close without saving
			else if (e.getSource() == endProgram)
			{
				System.exit(0);
			}
			
			//The user can cut an image and paste it later
			else if (e.getSource() == cut){
				paste.setEnabled(true);
				drawPanel.cut();
				drawPanel.repaint();
				
			}
			//The user can copy an image and paste it later
			
			else if (e.getSource() == copy){
				paste.setEnabled(true);
				drawPanel.copy();
				drawPanel.repaint();
			}
			//this allows the user to paste something that has previously been cut or copied
			else if (e.getSource() == paste){
				drawPanel.paste();
				drawPanel.repaint();
			}
			//creates a new picture of the user. They will be asked to save if they wish
			else if (e.getSource() == newp){
			int l = JOptionPane.showConfirmDialog(null, "Do you want to save?");
			
			if(l == JOptionPane.YES_OPTION){
				
				if(infile == (null)){
					String filename = JOptionPane.showInputDialog(null, "Enter the name of the file");
					
					try {
						infile = new File(filename);
						PrintWriter writer = new PrintWriter(infile);
						for(int i=0;i<shapeList.size();i++){
							MyShape object = shapeList.get(i);
							writer.println(object.saveData());
						}
						
						writer.close();
					} catch (FileNotFoundException e1) {
						System.exit(0);
					} 
					catch(NullPointerException e2){
						
					}
				}else {
					PrintWriter writer;
					try {
						writer = new PrintWriter(infile);
						for(int i=0;i<shapeList.size();i++){
							MyShape object = shapeList.get(i);
							writer.println(object.saveData());
						}
						
						writer.close();
					} catch (FileNotFoundException e1) {
						System.exit(0);
					}
					catch(NullPointerException e2){
						
					}
				}
			}else if(l == JOptionPane.NO_OPTION){
				JOptionPane.showMessageDialog(null, "You wont save");
				
				shapeList.clear();
				drawPanel.repaint();
				drawPanel.revalidate();
				
				}
			else{
				
			}
			
			}
			//ran if the user wishes to open a new file. The user will enter the name and the text will be read in and displayed on screen as images
			else if (e.getSource() == open){
				String openfile = JOptionPane.showInputDialog(null, "Enter the name of the file you wish to open");
				
				
					
						
					try{
					fscan = new Scanner(new FileInputStream(openfile));
					fscan.useDelimiter(":");
					while(fscan.hasNextLine()){
						
						String name = fscan.next();
						int x = (int)(Double.parseDouble(fscan.next()));
						int y =  (int)(Double.parseDouble(fscan.next()));
						int size = (int)(Double.parseDouble(fscan.next()));
						if(name.equals("Greeting")){
						 text = fscan.next();
						}
						
						fscan.nextLine();
						if (name.equals("Tree"))
						{
							newShape = new Tree(x,y,size);
						}
						else if (name.equals("Snowflake"))
						{
							newShape = new Snowflake(x,y,size);
						}
						else if (name.equals("Greeting"))
						{
							newShape = new Greeting(x,y,size, text);
						}
						else if (name.equals("Cabin"))
						{
							newShape = new Cabin(x, y, size);
						}
						else if(name.equals("Cloud"))
						{
							newShape = new Cloud(x, y, size);
						}else if(name.equals("Snowman"))
						{
							newShape = new Snowman(x, y, size);	
							
						}
						drawPanel.add(newShape);
					}
					
					
					
			}catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "File not found");
				}
			catch(Exception e2){
					
				}
				
			}
			//if the user chooses this option they will save the image to a text file to be used later
			else if (e.getSource() == save){
				if(infile == (null)){
					String filename = JOptionPane.showInputDialog(null, "Enter the name of the file");
					
					try {
						infile = new File(filename);
						PrintWriter writer = new PrintWriter(infile);
						for(int i=0;i<shapeList.size();i++){
							MyShape object = shapeList.get(i);
							writer.println(object.saveData());
						}
						
						writer.close();
					} catch (FileNotFoundException e1) {
						
					} catch(Exception e2){
						
					}
				}
				else{
					PrintWriter writer;
					try {
						writer = new PrintWriter(infile);
						for(int i=0;i<shapeList.size();i++){
							MyShape object = shapeList.get(i);
							writer.println(object.saveData());
						}
						
						writer.close();
					} catch (FileNotFoundException e1) {
						System.exit(0);
					}catch(Exception e2){
						
					}
					
				}
			}
			//Allows the user to type in the name of a file and save it to a txt file
			else if (e.getSource() == saveas){
				String filename = JOptionPane.showInputDialog(null, "Enter the name of the file");
				
				try {
					infile = new File(filename);
					PrintWriter writer = new PrintWriter(infile);
					for(int i=0;i<shapeList.size();i++){
						MyShape object = shapeList.get(i);
						writer.println(object.saveData());
					}
					
					writer.close();
				} catch (FileNotFoundException e1) {
					System.exit(0);
				} catch(NullPointerException e2){
					
				}
				
				
			}
			//allows the user to resize the method to a custom size rather than the default
			else if(e.getSource() == resize){
				String size = JOptionPane.showInputDialog(null, "Enter the new size.");
				try{
					int sizei = (int) Double.parseDouble(size);
					drawPanel.resize(sizei);
					msg.setText("Resizing");
					drawPanel.repaint();
					newShape.resize(sizei);
					drawPanel.repaint();		
				}
				catch(Exception e1){		
					
				}
				
			}
			
		}
	}

	// Here we are extending JPanel.  This way we can use all of the
	// properties of JPanel (including generating MouseEvents) and also
	// add new instance data and methods, as shown below.  Since this is
	// an inner class, it can access instance variables from the A5Help
	// class if necessary.
	private class ShapePanel extends JPanel
	{
		
		// These instance variables are used to store the desired size
		// of the panel.  See method getPreferredSize() below.
		private int prefwid, prefht;

		// Store index of the selected MyShape.  This allows the Shape
		// to be moved and updated.
		private int selindex;

		// Keep track of positions where mouse is moved on the display.
		// This is used by mouse event handlers when moving the shapes.
		private int x1, y1, x2, y2; 
		
		private boolean popped; // has popup menu been activated?

		private Mode mode;   // Keep track of the current Mode

		public ShapePanel (int pwid, int pht)
		{
			shapeList = new ArrayList<MyShape>(); // create empty ArrayList
			selindex = -1;
		
			prefwid = pwid;	// values used by getPreferredSize method below
			prefht = pht;   // (which is called implicitly).  This enables
			// the JPanel to request the room that it needs.
			// However, the JFrame is not required to honor
			// that request.

			setOpaque(true);// Paint all pixels here (See API)

			setBackground(Color.lightGray);

			addMouseListener(new MyMouseListener());
			addMouseMotionListener(new MyMover());
			popped = false;
		}  // end of constructor


		// This class is extending MouseAdapter.  MouseAdapter is a predefined
		// class that implements MouseListener in a trivial way (i.e. none of
		// the methods actually do anything).  Extending MouseAdapter allows
		// a programmer to implement only the MouseListener methods that
		// he/she needs but still satisfy the interface (recall that to
		// implement an interface one must implement ALL of the methods in the
		// interface -- in this case I do not need 3 of the 5 MouseListener
		// methods)
		
		// Note that there is a lot of logic in this class to test for the
		// different state conditions of the program.  The idea is that clicking
		// on and releasing the mouse will do different things at different 
		// times in the program execution.  As an alternative, you could in fact
		// have MouseListeners for different circumstances (ex: for being
		// in DRAW mode vs. being in NONE mode).  In this case, you could
		// actually swap the listeners in and out as appropriate using the 
		// removeMouseListener method in addition to the addMouseListener method
		// for the JPanel.
		



		private class MyMouseListener extends MouseAdapter
		{
			public void mousePressed(MouseEvent e)
			{
				x1 = e.getX();  // store where mouse is when clicked
				y1 = e.getY();

				if (!e.isPopupTrigger() && (mode == Mode.NONE ||
										    mode == Mode.SELECTED)) // left click and
				{												    // either NONE or
					if (selindex >= 0)								// SELECTED mode
					{
						unSelect();			// unselect previous shape
						mode = Mode.NONE;
					}
					selindex = getSelected(x1, y1);  // find shape mouse is
													 // clicked on
					if (selindex >= 0)
					{
						mode = Mode.SELECTED;  	// Now in SELECTED mode for shape
						
					// Check for double-click.  If so, show dialog to update text of
					// the current text shape (will do nothing if shape is not a MyText)
						MyShape curr = shapeList.get(selindex);
						if (curr instanceof MyText && e.getClickCount() == 2)
						{
							String newText = JOptionPane.showInputDialog(theWindow, 
							                  "Enter new text [Cancel for no change]");
							if (newText != null)
								((MyText) curr).setText(newText);
						}
					}
					repaint();	//  Make sure updates are redrawn
				}
				else if (e.isPopupTrigger() && selindex >= 0)  // if button is
				{								               // the popup menu
					popper.show(ShapePanel.this, x1, y1);      // trigger, show
					popped = true;							   // popup menu
				}											  
			}
			
			public void mouseReleased(MouseEvent e)
			{
				if (mode == Mode.DRAW) // in DRAW mode, create the new Shape
				{					   // and add it to the list of Shapes.  In this
									   // case we need to distinguish between the
									   // shapes since we are calling constructors
					if (currShape == Figures.TREE)
					{
						newShape = new Tree(x1,y1,50);
					}
					else if (currShape == Figures.SNOWFLAKE)
					{
						newShape = new Snowflake(x1,y1,10);
					}
					else if (currShape == Figures.GREETING)
					{
						newShape = new Greeting(x1,y1,30);
					}
					else if (currShape == Figures.CABIN){
						newShape = new Cabin(x1, y1, 65); 
					}
					else if(currShape == Figures.CLOUD){
						newShape = new Cloud(x1, y1, 15);
					}else if(currShape == Figures.SNOWMAN){
						newShape = new Snowman(x1, y1, 20);
					}
					addShape(newShape);
				}
				// In MOVING mode, set mode back to NONE and unselect shape (since 
				// the move is finished when we release the mouse).
				else if (mode == Mode.MOVING) 
				{
					mode = Mode.NONE;
					unSelect();  
					makeShape.setEnabled(true);
					repaint();
				}
				else if (e.isPopupTrigger() && selindex >= 0) // if button is
				{							// the popup menu trigger, show the
					popper.show(ShapePanel.this, x1, y1); // popup menu
				}
				else if(mode == Mode.SELECTED){
					cut.setEnabled(true);
					copy.setEnabled(true);
				}
				popped = false;  // unset popped since mouse is being released
			}
		}

		// the MouseMotionAdapter has the same idea as the MouseAdapter
		// above, but with only 2 methods.  The method not implemented
		// here is mouseMoved.  The difference between the two is whether or
		// not the mouse is pressed at the time.  Since we require a "click and
		// hold" to move our objects, we are using mouseDragged and not
		// mouseMoved.
		private class MyMover extends MouseMotionAdapter
		{
			public void mouseDragged(MouseEvent e)
			{
				x2 = e.getX();   // store where mouse is now
				y2 = e.getY();

				// Note how easy moving the shapes is, since the "work"
				// is done within the various shape classes.  All we do
				// here is call the appropriate method.  However, we don't 
				// want to accidentally move the selected shape with a right click
				// so we make sure the popup menu has not been activated.
				if ((mode == Mode.SELECTED || mode == Mode.MOVING) && !popped)
				{
					MyShape s = shapeList.get(selindex);
					mode = Mode.MOVING;
					s.move(x2, y2);
				}
				repaint();	// Repaint screen to show updates
			}
		}

		// Check to see if point (x,y) is within any of the shapes.  If
		// so, select that shape and highlight it so user can see.  Again,
		// note that we do not care which shape we are selecting here --
		// it only matters that it has the MyShape interface methods.
		// This version of getSelected() always considers the shapes from
		// beginning to end of the ArrayList.  Thus, if a shape is "under"
		// or "within" a shape that was previously created, it will not
		// be possible to select the "inner" shape.  In your assignment you
		// must redo this method so that it allows all shapes to be selected.
		// Think about how you would do this.
		private int getSelected(double x, double y)
		{                                             
			for (int i = 0; i < shapeList.size(); i++)
			{
				if (shapeList.get(i).contains(x, y))
				{
					shapeList.get(i).highlight(true); 
					return i;
				}
			}
			return -1;
		}

		public void unSelect()
		{
			if (selindex >= 0)
			{
				shapeList.get(selindex).highlight(false);
				selindex = -1;
			}
		}
		
		public boolean deleteSelected()
		{
			if (selindex >= 0)
			{
				shapeList.remove(selindex);
				selindex = -1;
				return true;
			}
			else return false;
		}

		public void setMode(Mode newMode)            // set Mode
		{
			mode = newMode;
		}

		private void addShape(MyShape newshape)      // Add shape
		{
			shapeList.add(newshape);
			repaint();	// repaint so we can see new shape
		}
		
		public void add(MyShape newshape) {

			shapeList.add(newshape);
			repaint();
			
		}
		public void resize(int size){
			
			
				if (currShape == Figures.TREE)
				{
					newShape = new Tree(x1,y1,size);
					if (selindex >= 0)
					{
						shapeList.remove(selindex);
						selindex = -1;
						
					}
				}
				else if (currShape == Figures.SNOWFLAKE)
				{
					newShape = new Snowflake(x1,y1,size);
					if (selindex >= 0)
					{
						shapeList.remove(selindex);
						selindex = -1;
						
					}
				}
				else if (currShape == Figures.GREETING)
				{
					newShape = new Greeting(x1,y1,size);
					if (selindex >= 0)
					{
						shapeList.remove(selindex);
						selindex = -1;
						
					}
				}
				else if (currShape == Figures.CABIN){
					newShape = new Cabin(x1, y1, size); 
					if (selindex >= 0)
					{
						shapeList.remove(selindex);
						selindex = -1;
						
					}
				}
				else if(currShape == Figures.CLOUD){
					newShape = new Cloud(x1, y1, size);
					if (selindex >= 0)
					{
						shapeList.remove(selindex);
						selindex = -1;
						
					}
				}else if(currShape == Figures.SNOWMAN){
					newShape = new Snowman(x1, y1, size);
					if (selindex >= 0)
					{
						shapeList.remove(selindex);
						selindex = -1;
						
					}
				}
				addShape(newShape);
			}
		private void cut(){
			if (selindex >= 0)
			{
				index1 = shapeList.get(selindex).saveData();
				shapeList.remove(selindex);
				selindex = -1;
				
			}
						
		}
		private void paste(){
			
			String shape1 = index1;
			String delims = "[:]";
			String[] tokens = shape1.split(delims);
			String name = tokens[0];
			int size = (int)Double.parseDouble(tokens[3]);
			int x3 = (int) Double.parseDouble(tokens[1]) + size;
			int y3 = (int) Double.parseDouble(tokens[2]) + size;
			Random randomnum = new Random();
			int randomX = randomnum.nextInt(380);
			int randomY = randomnum.nextInt(380);
			if(name.equals("Greeting")){
				 text = tokens[4];
				}
			if (name.equals("Tree"))
			{
				newShape = new Tree(randomX,randomY,size);
			}
			else if (name.equals("Snowflake"))
			{
				newShape = new Snowflake(randomX,randomY,size);
			}
			else if (name.equals("Greeting"))
			{
				newShape = new Greeting(randomX,randomY,size, text);
			}
			else if (name.equals("Cabin")){
				newShape = new Cabin(randomX, randomY, size); 
			}
			else if(name.equals("Cloud")){
				newShape = new Cloud(randomX, randomY, size);
			}else if(name.equals("Snowman")){
				newShape = new Snowman(randomX, randomY, size);
			}
			addShape(newShape);
			
		}
		public void copy(){
			index1 = shapeList.get(selindex).saveData();
		}

		// Method called implicitly by the JFrame to determine how much
		// space this JPanel wants.  Be sure to include this method in
		// your program so that your panel will be sized correctly.
		public Dimension getPreferredSize()
		{
			return new Dimension(prefwid, prefht);
		}

		// This method enables the shapes to be seen.  Note the parameter,
		// which is implicitly passed.  To draw the shapes, we in turn
		// call the draw() method for each shape.  The real work is in the draw()
		// method for each MyShape
		public void paintComponent (Graphics g)    
		{
			super.paintComponent(g);         // don't forget this line!
			Graphics2D g2d = (Graphics2D) g;
			for (int i = 0; i < shapeList.size(); i++)
			{
				shapeList.get(i).draw(g2d);
			}
		}
	} // end of ShapePanel
}
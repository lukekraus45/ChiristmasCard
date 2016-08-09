// CS 401 Fall 2014
// Tree class as another implementation of the MyShape interface.
// This class also uses composition, with 2 Polygons being the primary
// components of a Tree object.  For more information on Polygons, see
// the Java API.

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

class Cabin implements MyShape
{
	// Represent a Tree in two parts -- a Polygon for the top part 
	// (the branches) and another Polygon for the trunk.  Since the
	// trunk is rectangular, a Rectangle2D could have been used, but
	// to keep consistent (especially with the move() method) I used
	// Polygon objects for both.
	private Polygon base;
	private Polygon door;
	private Polygon chimney;
	private Polygon roof;

	// X, Y and size instance variables
	private int X, Y;
	private int size;

	private boolean isHighlighted;
	
	// Create a new Tree object.  Note how the Polygons are built,
	// adding one point at a time to each.  If you plot the points out
	// on paper you will see how the shapes are formed.  This method
	// uses a setUp method as shown below to allow for the Tree to
	// be regenerated internally (i.e. outside the constructor)
	public Cabin(int startX, int startY, int sz)
	{
		X = startX;
		Y = startY;
		size = sz;

		setUp();
	}
	
	// Create the actual parts of the Tree.  For your shapes you
	// will likely use trial and error to get nice looking results
	// (I used a LOT of trial and error for mine).
	private void setUp()
	{
		base = new Polygon();
		base.addPoint(X,Y);
		base.addPoint(X-size,Y);
		base.addPoint(X-size,Y-size);
		base.addPoint(X,Y-size);
		door = new Polygon();
		door.addPoint(X-3*size/8,Y);
		door.addPoint(X-5*size/8,Y);
		door.addPoint(X-5*size/8,Y-size/2);
		door.addPoint(X-3*size/8,Y-size/2);
		roof = new Polygon();
		roof.addPoint(X,Y-size);
		roof.addPoint(X-size,Y-size);
		roof.addPoint(X-(size/2), Y-size*2);
		chimney = new Polygon();
		chimney.addPoint(X,Y-size);
		chimney.addPoint(X-(size/5),Y-size);
		chimney.addPoint(X-(size/5),Y-(size+(size/2)));
		chimney.addPoint(X,Y-(size+(size/2)));
		
		
	}

	public void highlight(boolean b)
	{
		isHighlighted = b;
	}

	// The Polygon class can also be drawn with a predefined method in
	// the Graphics2D class.  There are two versions of this method:
	// 1) draw() which only draws the outline of the shape
	// 2) fill() which draws a solid shape
	// In this class the draw() method is used when the object is
	// highlighted.
	// The colors chosen are RGB colors I looked up on the Web.  Take a
	// look and use colors you like for your shapes.
	public void draw(Graphics2D g)
	{
		g.setColor(new Color(128,128,128));
		if (isHighlighted)
			g.draw(base);
		else
			g.fill(base);
		g.setColor(new Color(0,0,0));
		if (isHighlighted)
			g.draw(door);
		else
			g.fill(door);
		g.setColor(new Color(0,0,0));
		if (isHighlighted)
			g.draw(roof);
		else
			g.fill(roof);
		g.setColor(new Color(255,51,51));
		if(isHighlighted)
			g.draw(chimney);
		else
			g.fill(chimney);
	}

	// Looking at the API, we see that Polygon has a translate() method
	// which can be useful to us.  All we have to do is calculate the
	// difference of the new (x,y) and the old (X,Y) and then call
	// translate() for both parts of the tree.
	public void move(int x, int y)
	{
		int deltaX = x - X;
		int deltaY = y - Y;
		base.translate(deltaX, deltaY);
		door.translate(deltaX, deltaY);
		roof.translate(deltaX, deltaY);
		chimney.translate(deltaX, deltaY);
		X = x;
		Y = y;
	}

	// Polygon also has a contains() method, so this method is also
	// simple
	public boolean contains(double x, double y)
	{
		if (base.contains(x,y))
			return true;
		if (door.contains(x,y))
			return true;
		if(roof.contains(x,y))
			return true;
		if(chimney.contains(x,y))
			return true;
		return false;
	}

	// The move() method for the Polygons that are in Tree are not
	// reconfigured like in Snowflake, so we can't use the trick used
	// there.  Instead, we just change the size and call setUp() to
	// regenerate all of the objects.
	public void resize(int newsize)
	{
		size = newsize;
		setUp();
	}

	// Note again the format
	public String saveData()
	{
		return ("Cabin:" + X + ":" + Y + ":" + size + ":");
	}
}
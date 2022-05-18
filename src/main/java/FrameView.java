import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * FrameView class to display a graphical representation of the elevators and
 * floors. To use; Create an instance in your project. In the constructor add
 * the bottom floor number(like 1), the top floor number, the number of lifts
 * you are running and an ArrarList of your elevators, making sure to add them
 * in the correct order in the ArrayList. Create a thread with this class and
 * call the start() method to start it. There are five optional methods that can
 * be used to add more information in the display. Their use is related to how much of the
 * FrameGUI you have implemented in the Elevator class. The methods are;
 * drawValueAbove - draws a sting above the elevator. 
 * drawValueToSide - draws a string value to the right of the elevator.
 * drawStringToSide - draws a string value to the right of the elevator, below the previous one.
 * drawStringInBase - draws a string in the base below each elevator.
 * getStateColor - Sets the state colour for the elevator.
 * doMouseHighlight - draws the elevator number when the
 * mouse is held above Comment out the ones you want to use.
 *
 */
public class FrameView implements Runnable
{
	private final int LEFT_OFFSET = 10;
	private final int LABEL_OFFSET = 20;
	private final int FONT_SIZE = 14;
	{

	}
	private int gHeight = 34; // Height of section below of floors
	private int liftWidth; // The lift width
	int leftOffset = 50; // left offset for floors
	private int maxFloor; // Maximum number of floors
	private int minFloor; // Minimum number of floors
	private int numLift;
	private int spacer; // Gap between elevators
	JFrame jframe; // Frame for the graphics
	private List<Elevator> elevators; // List of the elevators
	private Point liftPoint; // Point for the lift location

	final Timer TIMER; // Timer to draw the graphics
	Dimension liftDimension; // The lift dimensions

	Dimension levelLabelDimension; // Label dimensions
	FontMetrics fm; // Font metrics for font used in graphics

	/**
	 * Constructor for the FrameFiew
	 * 
	 * @param minFloor  the bottom floor
	 * @param maxFloor  the top floor usually add one to make them all display
	 *                  clearly
	 * @param numLift   the number of lifts used
	 * @param elevators A list of the elevator instances used in simulation
	 */
	public FrameView(final int minFloor, final int maxFloor, final int numLift, final List<Elevator> elevators)
	{
		this.minFloor = minFloor;
		this.maxFloor = maxFloor;
		this.numLift = numLift;
		this.elevators = elevators;

		levelLabelDimension = new Dimension();
		liftPoint = new Point();

		jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(1080, 1050); // set size of the graphics
		jframe.setTitle("Simulation for " + (maxFloor - 1) + " Floors and " + numLift + " Elevators");

		TIMER = new Timer(10, null); // call every 10 milliseconds

	}

	/**
	 * Entry point for the thread
	 */
	@Override
	public void run()
	{
		startGraphics();

	}

	private void startGraphics()
	{

		jframe.add(new JComponent()
		{
			/**
			 * timer for the redraw of component
			 */
			private static final long serialVersionUID = 1L;
			{

				TIMER.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						repaint();
					}
				});
				TIMER.start();
			}

			/**
			 * paintComponent is called each time the JComponent is repainted
			 */
			public void paintComponent(Graphics graphics)
			{
				int numFloors = maxFloor - minFloor;
				int levelHeight = (getHeight() / (numFloors));
				int adjustment = -minFloor;

				setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
				fm = graphics.getFontMetrics(this.getFont());
				liftWidth = 40;

				drawBackgroundGrid(graphics, getHeight(), getWidth());

				// ELEVATORS
				liftDimension = new Dimension(liftWidth, levelHeight);
				spacer = (int) (getWidth() - leftOffset - numLift * liftDimension.width) / (numLift + 1);

				while (spacer < 10) // REDUCE LIFT DIMENSIONS SPACING < MIN SPACING
				{
					liftWidth--;
					liftDimension.width--;
					spacer = (int) (getWidth() - leftOffset - numLift * liftDimension.width) / (numLift + 1);
				}

				for (int i = 0; i < numLift; i++) // Draw elevators
				{
					String liftLabelText = String.valueOf(elevators.get(i).getELEVATOR_ID());

					liftPoint.x = leftOffset + spacer + i * (spacer + liftDimension.width);
					liftPoint.y = (getHeight() - liftDimension.height - gHeight)
					        - Math.abs(elevators.get(i).getCurrentFloor() + adjustment) * levelHeight;

					// Display values options near lift
					// Uncomment any display option you want
					// drawValueToSide("" + elevators.get(i).getCurrentFloor(), graphics);
					drawValueAbove(graphics, "" + elevators.get(i).getCurrentCapacity(), liftPoint);
					// drawStringToSide(liftLabelText, graphics);
					drawStringInBase(this, graphics, liftLabelText, liftPoint);

					graphics.setColor(getStateColor(i)); // use to show elevator state if implemented
					// graphics.setColor(Color.GRAY); // if not using state use this

					graphics.fillRect(liftPoint.x, liftPoint.y, liftDimension.width, liftDimension.height);

					graphics.setColor(Color.BLACK); // Draw frame for elevator
					graphics.drawRect(liftPoint.x, liftPoint.y, liftDimension.width, liftDimension.height);
				}

				// Optional to display the elevator number when mouse held over it
				doMouseHighlight(graphics, getHeight());

			}
		});

		/*
		 * By default frame is not visible so we are setting the visibility to true to
		 * make it visible.
		 */
		jframe.setVisible(true);

	}

	/**
	 * Paint the background with floor lines, level numbers and the base
	 * 
	 * @param graphics
	 * @param height
	 * @param width
	 */
	private void drawBackgroundGrid(Graphics graphics, int height, int width)
	{
		int numFloors = maxFloor - minFloor;
		int levelHeight = height / (numFloors);
		StringBuilder levelLabelText = new StringBuilder();

		// Left line in grid
		graphics.setColor(Color.GRAY);
		graphics.drawLine(leftOffset, 0, leftOffset, jframe.getHeight());

		levelLabelDimension.height = (int) fm.getStringBounds(levelLabelText.toString(), graphics).getHeight();

		gHeight = levelLabelDimension.height + 2 * LEFT_OFFSET;

		// Draw the floors, lines and level numbers and background
		for (int i = 0; i <= (numFloors + 1); i++)
		{
			levelLabelText.setLength(0); // clear it
			levelLabelText.append("L" + Integer.toString(i + minFloor));

			levelLabelDimension.width = (int) fm.getStringBounds(levelLabelText.toString(), graphics).getWidth();
			levelLabelDimension.height = (int) fm.getStringBounds(levelLabelText.toString(), graphics).getHeight();

			graphics.setColor(Color.BLACK); // Draw text
			graphics.drawString(levelLabelText.toString(), leftOffset - levelLabelDimension.width - 5,
			        height - gHeight - levelHeight * i - levelHeight / 2 + levelLabelDimension.height / 2);

			graphics.setColor(Color.GRAY); // Draw floor lines
			graphics.drawLine(leftOffset, height - gHeight - levelHeight * i, width,
			        height - gHeight - levelHeight * i);

		}

		// Draw bottom part
		graphics.setColor(Color.CYAN);
		graphics.fillRect(0, height - gHeight, width, gHeight);
		graphics.setColor(Color.GRAY);
		graphics.drawRect(0, height - gHeight, width, gHeight);

	}

	/**
	 * Optional method to get the lift colour from the elevator state;
	 * 
	 * @param i the elevator number
	 * @return the colour to use
	 */
	private Color getStateColor(int i)
	{

		Color color = Color.GRAY;

		switch (elevators.get(i).getMoveState())
		{
			case UP:
				color = Color.GREEN;
				break;
			case DOWN:
				color = Color.YELLOW;
				break;
			case IDLE:
				color = Color.LIGHT_GRAY;
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + elevators.get(i).getMoveState());
		}

		return color;
	}

	/**
	 * Optional method to display a string value to the right of the elevator in a
	 * box
	 * 
	 * @param liftLabelText
	 * @param graphics
	 */
	private void drawStringToSide(String liftLabelText, Graphics graphics)
	{

		if (liftLabelText != null && liftLabelText.length() > 0) // only do if we have have text
		{

			Dimension liftLabelTextDimension = new Dimension(
			        (int) fm.getStringBounds(liftLabelText, graphics).getWidth(),
			        (int) fm.getStringBounds(liftLabelText, graphics).getHeight());

			Dimension liftLabelDimension = new Dimension(liftLabelTextDimension.width + LABEL_OFFSET,
			        liftLabelTextDimension.height + LEFT_OFFSET);

			if (liftLabelDimension.width < spacer) // make sure enough space to draw
			{

				graphics.setColor(new Color(244, 240, 232));
				graphics.fillRect(liftPoint.x + LEFT_OFFSET + liftDimension.width,
				        liftPoint.y + liftDimension.height / 2 + LABEL_OFFSET - liftLabelDimension.height,
				        liftLabelDimension.width, liftLabelDimension.height);

				graphics.setColor(Color.BLACK);
				graphics.drawRect(liftPoint.x + LEFT_OFFSET + liftDimension.width,
				        liftPoint.y + liftDimension.height / 2 + LABEL_OFFSET - liftLabelDimension.height,
				        liftLabelDimension.width, liftLabelDimension.height);

				graphics.drawString(liftLabelText,
				        liftPoint.x + 10 + +liftDimension.width
				                + (liftLabelDimension.width - liftLabelTextDimension.width) / 2,
				        liftPoint.y + liftDimension.height / 2 + liftLabelDimension.height / 2);

			}
		}

	}

	/**
	 * Option method to display a string value on the top of the elevator in a box
	 * 
	 * @param graphics
	 * @param liftLabelText
	 * @param liftPoint
	 */
	private void drawValueAbove(Graphics graphics, String liftLabelText, Point liftPoint)
	{

		if (liftLabelText != null && liftLabelText.length() > 0)
		{

			// Get passenger number and display it above elevator
			Dimension liftLabelTextDimension = new Dimension(
			        (int) fm.getStringBounds(liftLabelText, graphics).getWidth(),
			        (int) fm.getStringBounds(liftLabelText, graphics).getHeight());
			Dimension liftLabelDimension = new Dimension(liftLabelTextDimension.width,
			        liftLabelTextDimension.height + LEFT_OFFSET);

			if (liftLabelDimension.width <= liftWidth)		// check it fits
			{
				graphics.setColor(new Color(244, 240, 232));
				graphics.fillRect(liftPoint.x, liftPoint.y - liftLabelDimension.height, liftDimension.width,
				        liftLabelDimension.height);

				graphics.setColor(Color.BLACK);
				graphics.drawRect(liftPoint.x, liftPoint.y - liftLabelDimension.height, liftDimension.width,
				        liftLabelDimension.height);
				graphics.drawString(liftLabelText,
				        liftPoint.x + liftDimension.width / 2 - liftLabelTextDimension.width / 2,
				        liftPoint.y - (liftLabelDimension.height + (LEFT_OFFSET / 2)) / 2
				                + liftLabelTextDimension.height / 2);
			}
		}
	}

	/**
	 * Optional method to display another string value to the right of the elevator
	 * in a box above the string drawn in the drawStringToSide method.
	 * 
	 * @param liftLabelText
	 * @param graphics
	 */
	private void drawValueToSide(String liftLabelText, Graphics graphics)
	{
		if (liftLabelText != null && liftLabelText.length() > 0)
		{
			Dimension liftLabelTextDimension = new Dimension(
			        (int) fm.getStringBounds(liftLabelText, graphics).getWidth(),
			        (int) fm.getStringBounds(liftLabelText, graphics).getHeight());

			Dimension liftLabelDimension = new Dimension(liftLabelTextDimension.width + LABEL_OFFSET,
			        liftLabelTextDimension.height + LEFT_OFFSET);

			if (liftLabelDimension.width < spacer) // room for label to be drawn
			{
				graphics.setColor(new Color(244, 240, 232));
				graphics.fillRect(liftPoint.x + LEFT_OFFSET + liftDimension.width,
				        liftPoint.y + liftDimension.height / 2 - LEFT_OFFSET - liftLabelDimension.height,
				        liftLabelDimension.width, liftLabelDimension.height);

				graphics.setColor(Color.BLACK);
				graphics.drawRect(liftPoint.x + LEFT_OFFSET + liftDimension.width,
				        liftPoint.y + liftDimension.height / 2 - LEFT_OFFSET - liftLabelDimension.height,
				        liftLabelDimension.width, liftLabelDimension.height);

				graphics.drawString(liftLabelText,
				        liftPoint.x + liftDimension.width + LEFT_OFFSET + liftLabelDimension.width / 2
				                - liftLabelTextDimension.width / 2,
				        liftPoint.y + liftDimension.height / 2 - LEFT_OFFSET - liftLabelTextDimension.height / 2);
			}
		}

	}

	/**
	 * Option method to display a string value in the base below the elevator.
	 * @param jcomp
	 * @param graphics 
	 * @param liftLabelText
	 * @param liftPoint
	 */
	private void drawStringInBase(JComponent jcomp, Graphics graphics, String liftLabelText, Point liftPoint)
	{

		if (liftLabelText != null && liftLabelText.length() > 0)
		{
			// Get passenger number and display it above elevator
			Dimension liftLabelTextDimension = new Dimension(
			        (int) fm.getStringBounds(liftLabelText, graphics).getWidth(),
			        (int) fm.getStringBounds(liftLabelText, graphics).getHeight());
			Dimension liftLabelDimension = new Dimension(liftLabelTextDimension.width + LABEL_OFFSET,
			        liftLabelTextDimension.height);

			int hSpace = (gHeight - liftLabelDimension.height) / 4;

			if ((liftWidth + spacer) > liftLabelDimension.width) // make sure enough space to draw
			{

				graphics.setColor(new Color(244, 240, 232));

				graphics.fillRect(liftPoint.x + (liftWidth / 2) - (liftLabelDimension.width) / 2 - 1,
				        jcomp.getHeight() - gHeight + hSpace, liftLabelDimension.width, gHeight - (2 * hSpace));

				graphics.setColor(Color.BLACK);
				graphics.drawRect(liftPoint.x + liftWidth / 2 - (liftLabelDimension.width) / 2 - 1,
				        jcomp.getHeight() - gHeight + hSpace, liftLabelDimension.width, gHeight - (2 * hSpace));

				graphics.drawString(liftLabelText,
				        liftPoint.x + liftDimension.width / 2 - liftLabelTextDimension.width / 2 - 1,
				        jcomp.getHeight() - gHeight / 2 + liftLabelTextDimension.height / 2 - hSpace / 2);
			}
		}

	}

	/**
	 * Optional method to display a box containing the elevator number when the
	 * mouse is held over the elevator.
	 * 
	 * @param graphics
	 * @param height
	 */
	private void doMouseHighlight(Graphics graphics, int height)
	{
		int levelHeight = (height / (maxFloor - minFloor));
		int adjustment = -minFloor;
		StringBuilder liftLabelText = new StringBuilder();
		Dimension mliftLabelTextDimension = new Dimension();
		Dimension mliftLabelDimension = new Dimension();
		Point mLiftPoint = new Point();
		Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePoint, jframe);

		// HOVER OVER ELEVATOR LABEL (IN SERIES SO THERE IS PROCEEDING ELEVATORS DON'T
		// OVERLAP WITH THE LABEL)
		for (int i = 0; i < numLift; i++)
		{
			mLiftPoint.x = leftOffset + spacer + i * (spacer + liftDimension.width);
			mLiftPoint.y = (height - liftDimension.height - gHeight)
			        - (elevators.get(i).getCurrentFloor() + adjustment) * levelHeight;

			// COLLISION DETECTION
			if (mousePoint.x > mLiftPoint.x && mousePoint.x < mLiftPoint.x + liftDimension.width
			        && mousePoint.y > mLiftPoint.y && mousePoint.y < mLiftPoint.y + liftDimension.height)
			{

				liftLabelText.setLength(0);
				liftLabelText.append("E" + Integer.toString(i + 1));

				mliftLabelTextDimension.width = (int) fm.getStringBounds(liftLabelText.toString(), graphics).getWidth();
				mliftLabelTextDimension.height = (int) fm.getStringBounds(liftLabelText.toString(), graphics)
				        .getHeight();

				mliftLabelDimension.width = mliftLabelTextDimension.width + 30;
				mliftLabelDimension.height = mliftLabelTextDimension.height + LABEL_OFFSET;

				graphics.setColor(new Color(244, 240, 232));

				graphics.fillRect(mousePoint.x, mousePoint.y - mliftLabelDimension.height, mliftLabelDimension.width,
				        mliftLabelDimension.height);

				graphics.setColor(Color.RED);
				graphics.drawRect(mousePoint.x, mousePoint.y - mliftLabelDimension.height, mliftLabelDimension.width,
				        mliftLabelDimension.height);

				graphics.setColor(Color.BLACK);
				graphics.drawString(liftLabelText.toString(),
				        mousePoint.x + mliftLabelDimension.width / 2 - mliftLabelTextDimension.width / 2,
				        mousePoint.y - mliftLabelDimension.height / 2 + mliftLabelTextDimension.height / 2);
			}
		}
	}

	/**
	 * Stop the timer and close Graphics
	 */
	public void close()
	{
		jframe.setVisible(false);
		jframe.dispose();
		TIMER.stop();
	}

}

/*
 * 	name: Andrew Matteson
 * 	date: 1/22/13
 * 	course: CSCD 350 Software Engineering
 * 	compiler: Eclipse 4.2.0
 * 	
 * Requirements: 	implement basic dynamic, non-interactive radar display draws display (static elements)
 *					draws mock contents (dynamic elements)
 *					draws and rotates rudimentary sweep arm
 */

import java.awt.*;
import java.util.TimerTask;
import java.util.Timer;

import javax.swing.*;

public class Display extends JPanel {

	// Makes the compiler happy
	private static final long serialVersionUID = 1L;

	public static int HEIGHT = 800;
	public static int WIDTH = 800;
	public static int RADIUS = 345;

	private Image dbImage;
	private Graphics dbGraphics;

	public int degree = 0;

	public static JFrame frame = new JFrame("Display");

	Color opacGreen = new Color(0, 255, 0, 80);
	Color opacWhite = new Color(181, 177, 179, 50);
	Color opacRed = new Color(255, 0, 0, 255);

	public int xCentre = WIDTH / 2;
	public int yCentre = HEIGHT / 2;

	public Timer timer = new Timer();

	/*
	 * Updates the degree of rotation
	 */
	class Spinner extends TimerTask {
		@Override
		public void run() {
			degree = (degree + 1) % 360;
		}

	}

	/*
	 * Display Constructor
	 */
	public Display() {
		//adjusts how often the updating run() method is called. Currently every 9 milliseconds
		int rotateRate = 9;
		timer.schedule(new Spinner(), 0, rotateRate);
	}

	/*
	 * Draws the rotating inner arm
	 */
	public void rotatingLine(Graphics g, int angle) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(new BasicStroke(3));
		g.drawLine(xCentre, yCentre,xCentre + (int) (RADIUS * Math.cos(Math.toRadians(-angle))), xCentre + (int) (RADIUS * Math.sin(Math.toRadians(-angle))));
	}

	/*
	 * Draws the white grid
	 */
	public void paintGrid(Graphics g) {
		int gridDistance = 70;

		for (int i = -4; i <= 4; i++) {
			// Figured out using pythagorean theorem using the centre of the circle as the origin
			int delta = (int) (Math.sqrt(Math.pow(RADIUS, 2)
					- Math.pow(gridDistance * i, 2)));
			// vertical lines
			g.drawLine(xCentre + gridDistance * i, yCentre + delta, xCentre
					+ gridDistance * i, yCentre - delta);
			// horizontal lines
			g.drawLine(xCentre + delta, yCentre + gridDistance * i, xCentre
					- delta, yCentre + gridDistance * i);
		}

	}

	/*
	 * Draws tick marks around the circle
	 */
	public void paintTicks(Graphics g) {
		g.setColor(Color.green);
		Graphics2D g2 = (Graphics2D) g;
		
		//Anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < 360; i = i + 3) {
			
			//Paints every 15 degree lines thicker
			if (i % 15 == 0)
				g2.setStroke(new BasicStroke(3));
			else
				g2.setStroke(new BasicStroke(1));

			//Adjust how often tick marks are drawn
			int tickRad = RADIUS + 3;

			// starting (x,y)
			int xStart = (int) (tickRad * Math.cos(Math.toRadians(i)))
					+ xCentre;
			int yStart = (int) (tickRad * Math.sin(Math.toRadians(i)))
					+ yCentre;

			// ending (x,y)
			int xEnd = (int) ((tickRad + 5) * Math.cos(Math.toRadians(i)))
					+ xCentre;
			int yEnd = (int) ((tickRad + 5) * Math.sin(Math.toRadians(i)))
					+ yCentre;

			g2.drawLine(xStart, yStart, xEnd, yEnd);

			// Add degree words around the circle every 15 degrees
			if (i % 15 == 0) {
				String degree = Integer.toString((i + 90) % 360);
				int stringLen = (int) g2.getFontMetrics()
						.getStringBounds(degree, g2).getWidth();
				int start = stringLen / 2;

				// starting (x,y)
				int xWord = (int) ((RADIUS + 25) * Math.cos(Math.toRadians(i)))
						+ xCentre;
				// The +5 makes it align better
				int yWord = (int) ((RADIUS + 25) * Math.sin(Math.toRadians(i)))
						+ yCentre + 5;

				g2.drawString(degree, xWord - start, yWord);
			}

		}
	}

	public void paint(Graphics g) {
		dbImage = createImage(800, 800);
		dbGraphics = dbImage.getGraphics();
		paintComponent(dbGraphics);
		g.drawImage(dbImage, 0, 0, this);
	}

	/*
	 * Calls the drawing methods described above and little more
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw Grid
		g2.setStroke(new BasicStroke(1));
		g.setColor(opacWhite);
		paintGrid(g);

		// draw filled rectangle
		g.setColor(Color.blue);
		g.fillRect(300, 600, 50, 25);

		// draw building
		g.setColor(Color.green);
		g2.setStroke(new BasicStroke(1));
		g.drawLine(350, 470, 475, 470);
		g.drawLine(350, 470, 350, 545);
		g.drawLine(350, 545, 625, 545);
		g.drawLine(625, 545, 625, 245);
		g.drawLine(625, 245, 475, 245);
		g.drawLine(475, 245, 475, 470);

		// draw big circle
		g.setColor(opacGreen);
		g2.setStroke(new BasicStroke(4));
		g.setColor(opacGreen);
		g2.drawOval(xCentre - RADIUS, yCentre - RADIUS, RADIUS * 2, RADIUS * 2);

		// draw inner arm
		g2.setStroke(new BasicStroke(3));
		g.drawLine(xCentre, yCentre,
				xCentre + (int) (RADIUS * Math.cos(Math.toRadians(degree))),
				xCentre + (int) (RADIUS * Math.sin(Math.toRadians(degree))));
		
		//Refreshes the picture with the updated inner arm angle
		repaint();
		
		// Tick marks around the circle
		paintTicks(g);

		// draw inner white circles
		g2.setStroke(new BasicStroke(1));
		g.setColor(opacWhite);
		for (int i = 1; i < 3; i++) {
			int radius1 = 120 * i;
			g2.drawOval(400 - radius1, 400 - radius1, 2 * radius1, 2 * radius1);
		}

		// Draw the red Circle
		g.setColor(opacRed);
		g.fillOval(xCentre - 220, yCentre - 210, 30, 30);

		// Add words next to red Circle
		g.setColor(Color.yellow);
		Font f = new Font("TimesNewRoman", Font.PLAIN, 26);
		g.setFont(f);
		g.drawString("MyAirplane", xCentre - 180, yCentre - 185);

	}

	public static void main(String arg[]) {
		// The input to this is the title of the window

		frame.setBackground(Color.black);

		Display panel = new Display();

		// Tells it when to close
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the frame size
		frame.setSize(WIDTH, HEIGHT);

		//Sets the content pane
		frame.setContentPane(panel);

		// Makes the frame visible
		frame.setVisible(true);

	}

}
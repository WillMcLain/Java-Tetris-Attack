
package TetrisAttack;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import TetrisAttack.InputHandler.MyDispatcher;

public class Main {
	
	// APPLICATION STARTING RESOLUTION
	public static final int window_DefaultX = 256;
	public static final int window_DefaultY = 224;
	// Variables to be later derived by this default size and
	// the user's window size.
	public static final Dimension windowDimension_default = new Dimension(window_DefaultX, window_DefaultY);
	public static final Rectangle windowRectangle_default = new Rectangle(0, 0,window_DefaultX,window_DefaultY);
	public static int window_ResizeX;
	public static int window_ResizeY;	
	public static Dimension windowDimension_resize;	
	public static Rectangle windowRectangle_resize;
	public static double globalScale = 1.0;
	public static double window_InitRatio;
	
	private static KeyboardFocusManager keyManager;
	private static MyDispatcher keyDispatcher;
	
	private static JFrame f;
	public static Screen currentScreen;
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        f = new JFrame("Tetris Attack");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set the layoutManager to null so we can place everything manually.
        f.getContentPane().setLayout(null);
        f.setSize(windowDimension_default);
        
        // Add our first screen to the frame. Each screen will then prompt
        // a new one in from this point on.
        //currentScreen = new MainMenu(null);
        currentScreen = new Game();
        f.getContentPane().add(currentScreen);
        f.setVisible(true);
        
        // Resizes the window for the first time when the program is run.
        // This is so that we do not lose pixels in any direction to the
        // various sizes of OS window borders.
        Insets insets = f.getInsets();
        window_ResizeX = window_DefaultX + insets.left + insets.right;
        window_ResizeY = window_DefaultY + insets.top + insets.bottom;
        windowDimension_resize = new Dimension(window_ResizeX, window_ResizeY);
        f.setSize(windowDimension_resize);

        windowRectangle_resize = new Rectangle(0,0, window_DefaultX, window_DefaultY);
        
        globalScale = window_ResizeX / (window_DefaultX + insets.left + insets.right);
        window_InitRatio = (double)window_ResizeX / (double)window_ResizeY;
        

    	// Adds a keyboard manager to everything
    	// instead of worrying about the focus of
    	// a form or object with a KeyListener.        
    	keyManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    	keyDispatcher = new MyDispatcher();
    	keyManager.addKeyEventDispatcher(keyDispatcher);
        
        f.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // This is only called when the user releases the mouse button.
	            Update_onResize();
	            System.out.println("Main.createAndShowGUI: componentResized");
            }
        });
    }
	
	public static void newScreen(String screen) {
		// Close one menu and open the new one
		f.getContentPane().remove(currentScreen);
		currentScreen = new Game();
		f.getContentPane().add(currentScreen);
	}
	
	/* 
	 * Update_onResize(void)
	 * 
	 * This figures out how the user resized the window and adjust
	 * the shorter of the two dimensions so the image isn't distorted
	 * (enforcing our aspect ratio). We also need to make sure that if we
	 * make one dimension larger, that it doesn't go off the user's desktop
	 * because then they wouldn't be able to see everything, and resizing it
	 * would be painful for them since they can't reach that corner anymore.
	 * 
	 * 40 is used as a "safe margin" number just because I feel like it for now.
	 * 
	 * CURRENT BUG: Adjusts frame f with disregard to the insets, so everything time
	 * we need resize the frame, a little bit more of the right edge goes outside
	 * the rendered area.
	 * UPDATE: Now it just does it all weird. Last worked (with first bug) in BackUp_003.
	 */
	private static void Update_onResize() {
		Dimension newSize = f.getSize();
		
		// If the user somehow managed to keep the original aspect ratio, then ignore this.
		if (window_InitRatio == (newSize.getWidth() / newSize.getHeight())) {
			return;
		}
				
		double maxHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 40;
		double maxWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 40;
		if (newSize.getWidth() > newSize.getHeight()) {
			// Set based on width
			if ((newSize.getWidth() / window_InitRatio) > maxHeight) {
				newSize.setSize(maxHeight * window_InitRatio, maxHeight);
			} else {
				newSize.height = (int)(newSize.width / window_InitRatio);
			}
		} else {
			// Set based on height
			if ((newSize.getHeight() * window_InitRatio) > maxWidth) {
				newSize.setSize(maxWidth, maxWidth / window_InitRatio);
			} else {
				newSize.width = (int)(newSize.height * window_InitRatio);
			}
		}
		
        window_ResizeX = (int)newSize.getWidth();
        window_ResizeY = (int)newSize.getHeight();
        windowDimension_resize = new Dimension(window_ResizeX, window_ResizeY);
        windowRectangle_resize = new Rectangle(0,0, window_ResizeX, window_ResizeY);
        
        // Adjust our window, adjust our scale for everything, adjust our rendering area
        // for the screen, then finally resize everything in the screen.
        
		f.setSize(windowDimension_resize);
		globalScale = (double)window_ResizeX / (double)window_DefaultX;
		
		currentScreen.resizeGraphics(globalScale);
	}
	
	public static void Update_onKey(KeyEvent e) {
		//System.out.println("Updating keypress: " + e.getKeyChar());
    	currentScreen.UpdateOnKey(e);
	}
}
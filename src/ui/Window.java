package ui;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * The application window
 */
@SuppressWarnings("serial")
public class Window extends JFrame {
	
	private CanvasArea canvas;
	private MenuBar menu;
	public ToolBox toolbox;
	public SelectionPanel selectionPanel;
	
	private Env env = new Env(this);
	
	public Window() {
		setBounds(0, 0, 1000, 800);
		setMinimumSize(new Dimension(400, 300));
		setLocationRelativeTo(null);
		setTitle("Graphics Editor");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(confirm("Quit and abandon this drawing?"))
					System.exit(0);
			}
		});
		
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		menu = new MenuBar(this, env);
		toolbox = new ToolBox(this, env);
		selectionPanel = new SelectionPanel(this, env);
		canvas = new CanvasArea(env);
		CanvasMouseListener cml = new CanvasMouseListener(canvas, env);
		CanvasKeyListener ckl = new CanvasKeyListener(canvas, env);
		
		env.setToolbox(toolbox);
		env.setSelectionPanel(selectionPanel);
		env.setCanvas(canvas);
		env.setCanvasMouseListener(cml);
		
		canvas.addMouseListener(cml);
		canvas.addMouseMotionListener(cml);
		canvas.addKeyListener(ckl);
		
		setJMenuBar(menu);
		constraints.insets = new Insets(2, 2, 2, 2);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 0;
		pane.add(toolbox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		pane.add(selectionPanel, constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		pane.add(canvas, constraints);
		
		setVisible(true);
	}
	
	/**
	 * Ask the user a Yes / No question through a popup
	 * @param message : the question
	 * @param title : a title
	 * @return the boolean response
	 */
	public boolean confirm(String message, String title) {
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, message, title, 
										JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}
	
	/**
	 * Ask the user a Yes / No question through a popup
	 * @param message : the question
	 * @return the boolean response
	 */
	public boolean confirm(String message) {
		return confirm(message, null);
	}
	
	/**
	 * Displays an error message in a popup
	 * @param message : the message
	 */
	public void error(String message) {
		JOptionPane.showConfirmDialog(this, message, null, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void main(String[] args) {
		new Window();
	}
}

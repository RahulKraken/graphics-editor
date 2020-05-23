package ui;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import figure.*;

/**
 * Environment variable that contains all the context of the current drawing
 */
public class Env {
	
	protected Data data = new Data();
	
	protected Window window;
	
	protected CanvasArea canvas;
	protected CanvasMouseListener canvasMouseListener;
	
	protected ToolBox toolbox;
	protected SelectionPanel selectionPanel;

	protected Color bg = new Color(184,255,181);
	protected Color stroke = Color.LIGHT_GRAY;

	/**
	 * Exportable data
	 */
	@SuppressWarnings("serial")
	protected static class Data implements Serializable {
		/**
		 * The list of figures is sorted in order of priority for display
		 */
		List<FigureGraphic> figures = new ArrayList<FigureGraphic>();
	}
	
	public Env(Window window) {
		this.window = window;
	}

	public CanvasMouseListener getCanvasMouseListener() {
		return canvasMouseListener;
	}
	public void setCanvasMouseListener(CanvasMouseListener canvasMouseListener) {
		this.canvasMouseListener = canvasMouseListener;
	}
	
	public Color getBackgroundColor() {
		return bg;
	}
	public Color getStrokeColor() {
		return stroke;
	}
	public void setBackgroundColor(Color c) {
		bg = c;
	}
	public void setStrokeColor(Color c) {
		stroke = c;
	}
	
	public void setData(Data d) {
		data = d;
		canvas.repaint();
	}

	public void setCanvas(CanvasArea c) {
		canvas = c;
	}
	public CanvasArea getCanvas() {
		return canvas;
	}
	
	public void setToolbox(ToolBox t) {
		toolbox = t;
	}
	public ToolBox getToolbox() {
		return toolbox;
	}	
	public void setSelectionPanel(SelectionPanel t) {
		selectionPanel = t;
	}
	public SelectionPanel getSelectionPanel() {
		return selectionPanel;
	}

	public List<FigureGraphic> getFigures() {
		return data.figures;
	}
	public void setFigures(List<FigureGraphic> figures) {
		data.figures = figures;
	}
	
	/**
	 * Sort the figures so that the selected figures appear first.
	 * In addition to the selection, the order is kept.
	 */
	public void sortFigures() {
		List<FigureGraphic> newfigures = new ArrayList<FigureGraphic>();
		for(FigureGraphic f : data.figures)
			if(f.isSelected())
				newfigures.add(f);
		for(FigureGraphic f : data.figures)
			if(!f.isSelected())
				newfigures.add(f);
		data.figures = newfigures;
	}
	
	/**
	 * Save the current drawing to a file
	 * @param f the file
	 * @return true if saved successfully, false otherwise
	 */
	public boolean saveToFile(File f) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(data);
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			window.error("Unable to save the drawing in the chosen file");
		}
		return false;
	}
	
	/**
	 * Open a drawing from a file
	 * @param f the file to open
	 * @return true if successfully opened, false if the drawing opening failed
	 * (incompatible file, file error)
	 */
	public boolean openFromFile(File f) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
			setData((Data) ois.readObject());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			window.error("Impossible to open the file");
		}
		return false;
	}
	
	/**
	 * Add a figure to the drawing
	 */
	public void addFigure(FigureGraphic f) {
		getFigures().add(0, f);
	}
	
	/**
	 * Reset drawing (delete all figures)
	 */
	public void empty() {
		data = new Data();
	}
	
	/**
	 * compare two lists
	 * @param f
	 * @param g
	 * @return true if the two lists are equal
	 */
	public boolean listsAreSame(List<FigureGraphic> f, List<FigureGraphic> g) {
		return f.size() == g.size() && f.containsAll(g);
	}
	
	private List<FigureGraphic> lastSelection = new ArrayList<FigureGraphic>();
	/**
	 * Function called when the selection has changed
	 */
	public void onSelectionChanged() {
		List<FigureGraphic> s = new ArrayList<FigureGraphic>( getSelected() );
		if(!listsAreSame(s, lastSelection)) {
			canvas.repaint();
			selectionPanel.onSelectionChanged();
		}
		lastSelection = s;
	}
	
	// Selection

	/**
	 * Cancel the current selection
	 */
	private void emptySelection() {
		for(FigureGraphic f : getFigures())
			setSelected(f, false);
	}
	
	/**
	 * Deselect all figures
	 */
	public void unselectAll() {
		emptySelection();
		onSelectionChanged();
	}
	
	/**
	 * Find the first figure that contains the point p
	 * @param p
	 * @return the first figure found under point p, null if no figure found
	 */
	public FigureGraphic getOneByPosition(Point_2D p) {
		for(FigureGraphic f : getFigures())
			if(f.contain(p))
				return f;
		return null;
	}
	
	private void setSelected(FigureGraphic figure, boolean value) {
		figure.setSelected(value);
		figure.setTransparent(value && canvasMouseListener.mouseIsDown);
	}
	
	/**
	 * Get selected figures
	 * @return a list of all selected figures
	 */
	public List<FigureGraphic> getSelected() {
		List<FigureGraphic> figures = new ArrayList<FigureGraphic>();
		for(FigureGraphic f : getFigures())
			if(f.isSelected())
				figures.add(f);
		return figures;
	}
	
	/**
	 * Select one and only one figure
	 * @param figure : the figure to select
	 */
	public void selectFigure(FigureGraphic figure) {
		emptySelection();
		setSelected(figure, true);
		sortFigures();
		onSelectionChanged();
	}
	
	/**
	 * Select the first figure which contains p
	 * @param p : point
	 * @return figure selected or null
	 */
	public FigureGraphic selectOneByPosition(Point_2D p) {
		emptySelection();
		FigureGraphic figure = getOneByPosition(p);
		if(figure!=null) {
			setSelected(figure, true);
			sortFigures();
		}
		onSelectionChanged();
		return figure;
	}
	
	/**
	 * Select a set of figures using a selection.
	 * The center of the figures is decisive for knowing if they belong to this selection.
	 * @param selection
	 */
	public void selectPoints(Selection selection) {
		for(FigureGraphic f : getFigures())
			setSelected(f, selection.contain(f.getCenter()));
		sortFigures();
		onSelectionChanged();
	}
	
	/**
	 * Select all figures.
	 */
	public void selectAll() {
		for(FigureGraphic f : getFigures())
			f.setSelected(true);
		onSelectionChanged();
	}
	
	/**
	 * @return the number of selected figures
	 */
	public int countSelected() {
		return getSelected().size();
	}
	
	/**
	 * Move all selected figures
	 * @param dx : displacement in x
	 * @param dy : deplacement in y
	 */
	public void moveSelected(int dx, int dy) {
		for(FigureGraphic f : getSelected())
			f.move(dx, dy);
	}
	
	/**
	 * Remove a figure from the drawing
	 * @param figure
	 */
	public void remove(Figure figure) {
		getFigures().remove(figure);
		onSelectionChanged();
	}
	
	/**
	 * Removes selected figures from the drawing
	 */
	public void removeSelected() {
		List<FigureGraphic> figures = getFigures();
		for(FigureGraphic f : getSelected())
			figures.remove(f);
		onSelectionChanged();
	}
	
}

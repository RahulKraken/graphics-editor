package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * Toolbar
 */
@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	protected File openedFile = null;
	protected Window parent;
	protected Env env;
	
	public MenuBar(final Window parent, final Env env) {
		this.parent = parent;
		this.env = env;
		JMenu file = new JMenu("File");
		JMenuItem newFile = new JMenuItem("New");
		JMenuItem open = new JMenuItem("To open");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem saveAs = new JMenuItem("Save as");
		newFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!parent.confirm("Abandon the current job?") )
					return;
				env.empty();
				env.canvas.repaint();
			}
		});
		open.addActionListener(new OpenFileListener(this));
		save.addActionListener(new SaveFileListener(this, false));
		saveAs.addActionListener(new SaveFileListener(this, true));
		file.add(newFile);
		file.add(open);
		file.add(save);
		file.add(saveAs);
		add(file);
	}
	
	protected void open(File f) {
		if(parent.confirm("Abandon the current job?") )
			openedFile = env.openFromFile(f) ? f : null;
	}

	protected void save(File f) {
		openedFile = env.saveToFile(f) ? f : null;
	}
	
	protected class ShapeEditorFileFilter extends FileFilter {
		public String getDescription() {
			return "Shape Editor File (.sef)";
		}
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".sef");
		}
	}
	
	protected class OpenFileListener implements ActionListener {
		
		MenuBar menu;
		public OpenFileListener(MenuBar menu) {
			this.menu = menu;
		}
		
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("To open");
			fc.setFileFilter(new ShapeEditorFileFilter());
			fc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("ApproveSelection")) {
						menu.open(fc.getSelectedFile());
					}
				}
			});
			fc.showOpenDialog(menu.parent);
		}
	}
	
	protected class SaveFileListener implements ActionListener {

		MenuBar menu;
		boolean saveAs;
		public SaveFileListener(MenuBar menu, boolean saveAs) {
			this.menu = menu;
			this.saveAs = saveAs;
		}
		public void actionPerformed(ActionEvent e) {
			if(!saveAs && menu.openedFile!=null) {
				menu.save(menu.openedFile);
				return;
			}
			
			final JFileChooser fc = new JFileChooser();
			fc.setApproveButtonText("Record");
			fc.setDialogTitle("Record");
			fc.setFileFilter(new ShapeEditorFileFilter());
			fc.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(e.getActionCommand().equals("ApproveSelection")) {
						File selected = fc.getSelectedFile();
						if(!selected.getName().endsWith(".sef"))
							selected = new File(selected.getAbsolutePath()+".sef");
						menu.save(selected);
					}
				}
			});
			fc.showSaveDialog(menu.parent);
		}
	}
}

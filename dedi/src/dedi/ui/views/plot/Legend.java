package dedi.ui.views.plot;

import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import dedi.ui.GuiHelper;

public class Legend extends Observable{
	private Color detectorColour = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
	private Color beamstopColour = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private Color clearanceColour = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	private Color cameraTubeColour = new Color(Display.getDefault(), 255, 255, 204);
	
	private Color[] colours;
	private String[] labels;
	private Map<String, Color> legendColours = new TreeMap<>();
	
	
	public Legend(Composite parent, String[] labels, Color[] colours) {
		this.labels = labels;
		this.colours = colours;
		
		Group legendGroup = GuiHelper.createGroup(parent, "Legend", 3);
		
		for(int i = 0; i < labels.length; i++){
			legendColours.put(labels[i], colours[i]);
			Label label = GuiHelper.createLabel(legendGroup, labels[i]);
			Label colourLabel = GuiHelper.createLabel(legendGroup, "   ");
			Color colour = legendColours.get(label.getText());
			PaintListener listener = new PaintListener() {
				@Override
				public void paintControl(PaintEvent e) {
					createLegendColourLabel(e, colourLabel, legendColours.get(label.getText()));
				}
			};
			colourLabel.addPaintListener(listener);
			Button chooseColourButton = new Button(legendGroup, SWT.PUSH); 
			chooseColourButton.setText("Change colour");
			chooseColourButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					ColorDialog cd = new ColorDialog(parent.getShell());
					cd.setText("Choose colour");
					RGB newColour = cd.open();
					if(newColour == null) return;
					legendColours.put(label.getText(), new Color(Display.getDefault(), newColour));
					colourLabel.redraw();
					setChanged();
					notifyObservers();
				}
			});
		}
		
		legendGroup.setSize(legendGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private void createLegendColourLabel(PaintEvent e, Label label, Color colour){
		e.gc.setBackground(colour);
        e.gc.fillRectangle(0, 0, label.getBounds().width, label.getBounds().height);
	}
	
	
	public Color getColor(String label){
		return legendColours.get(label);
	}
}

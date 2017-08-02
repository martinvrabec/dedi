package dedi.ui.widgets.plotting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.TreeMap;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
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

public class Legend extends Composite {
	private ResourceManager resourceManager;
	
	private List<LegendItem> items;
	private Group legendGroup;
	
	
	public Legend(Composite parent){
		super(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(this);
		
		items = new ArrayList<>();
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), this);
		
		legendGroup = GuiHelper.createGroup(parent, "Legend", 1);
	}
	
	
	public LegendItem addLegendItem(String name, Color defaultColour){
		LegendItem item = getItem(name);
		if(item != null) return item;
		item = new LegendItem(legendGroup, name, defaultColour);
		items.add(item);
		legendGroup.layout();
		return item;
	}
	
	
	public void removeLegendItem(String name){
		Iterator<LegendItem> iter = items.iterator();
		while(iter.hasNext()){
			LegendItem item = iter.next();
			if(Objects.equals(item.getItemName(), name)){
				iter.remove();
				item.dispose();
			}
		}
	}
	
	
	public Color getColour(String name){
		for(LegendItem item : items){
			if(Objects.equals(item.getItemName(), name))
				return item.getColour();
		}
		return null;
	}
	
	
	private LegendItem getItem(String name){
		for(LegendItem item : items){
			if(Objects.equals(item.getItemName(), name))
				return item;
		}
		return null;
	}
	
	/*public Legend(Composite parent, String[] labels, Color[] colours) {
		super(parent, SWT.NONE);
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
	}*/
}

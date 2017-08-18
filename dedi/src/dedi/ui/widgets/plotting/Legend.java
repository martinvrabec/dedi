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
			if(item != null && Objects.equals(item.getItemName(), name)){
				iter.remove();
				item.dispose();
			}
		}
	}
	
	
	public Color getColour(String name){
		for(LegendItem item : items){
			if(item != null && Objects.equals(item.getItemName(), name))
				return item.getColour();
		}
		return null;
	}
	
	
	private LegendItem getItem(String name){
		for(LegendItem item : items){
			if(item != null && Objects.equals(item.getItemName(), name))
				return item;
		}
		return null;
	}
	
}

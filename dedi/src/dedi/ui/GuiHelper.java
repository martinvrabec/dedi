package dedi.ui;

import java.util.List;

import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class GuiHelper {
	private static Font boldFont = JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT);
	
	public static Font getBoldFont(){
		return boldFont;
	}
	
	
	public static Group createGroup(Composite parent, String name, int numOfCols){
		Group group = new Group(parent, SWT.NONE);
		group.setText(name);
		group.setFont(getBoldFont());
		
		GridLayout layout = new GridLayout(numOfCols, false);
		layout.marginBottom = 5;
		layout.horizontalSpacing = 30;
		layout.verticalSpacing = 15;
		layout.marginHeight = 5;
		layout.marginWidth = 0;
		group.setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		
		return group;
	}
	
	
	public static Text createText(Composite parent){
		return new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
	}
	
	
	public static Label createLabel(Composite parent, String name){
		Label label = new Label(parent, SWT.NONE);
		label.setText(name);
		label.setLayoutData(new GridData(SWT.BEGINNING));
		return label;
	}
	
	
	public static <T extends Quantity> ComboViewer createUnitsCombo(Composite parent, List<Unit<T>> UNITS){
		Combo unitsCombo = new Combo(parent, SWT.READ_ONLY);
		
		ComboViewer unitsComboViewer = new ComboViewer(unitsCombo);
		unitsComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		unitsComboViewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element){
					if(element instanceof Unit<?>){
						@SuppressWarnings("unchecked")
						Unit<T> unit = (Unit<T>) element;
						return unit.toString();
					}
					return super.getText(element);
				}
		});
		
		unitsComboViewer.setInput(UNITS);
		try{
			unitsComboViewer.setSelection(new StructuredSelection(UNITS.get(0)));
		} catch(Exception e){
		}
		
		return unitsComboViewer;
	}
	
}

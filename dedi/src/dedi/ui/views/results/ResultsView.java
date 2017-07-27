package dedi.ui.views.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.dawnsci.plotting.tools.preference.detector.DiffractionDetector;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.jscience.physics.amount.Amount;

import dedi.configuration.BeamlineConfiguration;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.scattering.D;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;
import dedi.ui.models.ResultsModel;
import dedi.ui.models.ResultsService;
import dedi.ui.views.IView;

public class ResultsView extends ViewPart implements PropertyChangeListener {
	private AbstractResultsViewController controller;
	private ResultsViewModel model;
	
	private Composite main;
	private Combo scatteringQuantitiesCombo;
	private ComboViewer scatteringQuantitiesComboViewer;
	private ComboViewer scatteringQuantitiesUnitsCombo;
	private Label minValueLabel;
	private Label maxValueLabel;
	private Label requestedMinValueLabel;
	private Label requestedMaxValueLabel;
	private Label minValue;
	private Label maxValue;
	private Text requestedMinValueText;
	private Text requestedMaxValueText;
	private Canvas drawingArea;
	
	private boolean isUserEdited = true;
	
	public static final String ID = "dedi.views.results";
	
	
	public ResultsView() {
		model = createModel();
		controller = createController(model);
		controller.addView(this);
	}
	
	
	public AbstractResultsViewController createController(ResultsViewModel model){
		return new DefaultResultsViewController(model);
	}
	
	
	public ResultsViewModel createModel(){
		return new ResultsViewModel();
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		
		
		main = new Composite(scrolledComposite, SWT.NONE);
		main.setLayout(new GridLayout(3, true));
		
		
		GuiHelper.createLabel(main, "Scattering quantity:");
		
		scatteringQuantitiesCombo = new Combo(main, SWT.READ_ONLY | SWT.RIGHT);
		scatteringQuantitiesComboViewer = new ComboViewer(scatteringQuantitiesCombo);
		scatteringQuantitiesComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		scatteringQuantitiesComboViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof ScatteringQuantity){
					ScatteringQuantity quantity = (ScatteringQuantity) element;
					return quantity.getQuantityName();
				}
				return super.getText(element);
			}
		});
		scatteringQuantitiesCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		scatteringQuantitiesComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if(!isUserEdited) return;
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			    if (selection.size() > 0){
			    	controller.updateCurrentQuantity((ScatteringQuantity) selection.getFirstElement());
			    }
			}
		});
		
		
		
		scatteringQuantitiesUnitsCombo = GuiHelper.createUnitsCombo(main, null);
		scatteringQuantitiesUnitsCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				if(!isUserEdited) return;
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0){
					 controller.updateCurrentUnit((Unit<?>) selection.getFirstElement());
				}
			}
		});
		
		
		
		minValueLabel = GuiHelper.createLabel(main, "");
		minValue = GuiHelper.createLabel(main, "");
		minValue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		new Label(main, SWT.NONE);
		
		
		maxValueLabel = GuiHelper.createLabel(main, "");
		maxValue = GuiHelper.createLabel(main, "");
		maxValue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		new Label(main, SWT.NONE);
		
		
		requestedMinValueLabel = GuiHelper.createLabel(main, "");
		requestedMinValueText = GuiHelper.createText(main);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(70, 20).applyTo(requestedMinValueText);
		requestedMinValueText.addModifyListener(e -> {if(isUserEdited) controller.updateRequestedMin(requestedMinValueText.getText());});
		new Label(main, SWT.NONE);
		
		
		requestedMaxValueLabel = GuiHelper.createLabel(main, "");
		requestedMaxValueText = GuiHelper.createText(main);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(70, 20).applyTo(requestedMaxValueText);
		requestedMaxValueText.addModifyListener(e -> {if(isUserEdited) controller.updateRequestedMax(requestedMaxValueText.getText());});
		new Label(main, SWT.NONE);
		
		
		drawingArea = new Canvas(main, SWT.NONE);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.widthHint = 700;
		data.heightHint = 70;
		drawingArea.setLayoutData(data);
		drawingArea.addPaintListener(e -> repaint(e));
		
		
		ArrayList<ScatteringQuantity> quantities = new ArrayList<>();
		quantities.add(new Q());
		quantities.add(new D());
		controller.updateQuantities(quantities);
		
		
		main.layout();
		scrolledComposite.setContent(main);	
		scrolledComposite.setMinSize(main.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	
	@Override
	public void setFocus() {
	}

	
	public void repaint(PaintEvent e){
		/*Rectangle bounds = drawingArea.getClientArea();
		
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		
		if(hasSolution == false) {
			 e.gc.drawText("No solution", bounds.width/2 - 40, bounds.height/2 - 10);
			 return;
		 }
		
		 if(requestedMaxValue == null || requestedMinValue == null) return;
		 
		 if(isSatisfied) e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_GREEN));
		 else e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
		 

         double slope = (bounds.width-120)/
        		 (Math.log(fullQRange.getMax()) - 
        		  Math.log(fullQRange.getMin()));
         
         double minRequestedX = slope*(Math.log(requestedMinValue.toQ().getValue().to(SI.METER.inverse()).getEstimatedValue()) - 
        		  Math.log(fullQRange.getMin())) + 60;
         
         double maxRequestedX = slope*(Math.log(requestedMaxValue.toQ().getValue().to(SI.METER.inverse()).getEstimatedValue()) - 
        		  Math.log(fullQRange.getMin())) + 60;
         
         if(minRequestedX > maxRequestedX){
        	 double temp = minRequestedX;
        	 minRequestedX = maxRequestedX;
        	 maxRequestedX = temp;
         }
         
         double minValueX = slope*(Math.log(minQValue.getValue().to(SI.METER.inverse()).getEstimatedValue()) - 
        		  Math.log(fullQRange.getMin())) + 60;
         
         double maxValueX = slope*(Math.log(maxQValue.getValue().to(SI.METER.inverse()).getEstimatedValue()) - 
        		  Math.log(fullQRange.getMin())) + 60;
         
         e.gc.fillRectangle((int) minValueX, bounds.height/2, (int) (maxValueX - minValueX), bounds.height/2);
         e.gc.drawLine((int) minRequestedX, 5, (int) minRequestedX, bounds.height);
         e.gc.drawLine((int) maxRequestedX, 5, (int) maxRequestedX, bounds.height);
         
         e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
         e.gc.drawText("Requested min", (int) minRequestedX - 40, 5);
         e.gc.drawText("Requested max", (int) maxRequestedX - 40, 5);*/
	}


	@Override
	public void propertyChange(PropertyChangeEvent e) {
		try{
			isUserEdited = false;
			
			// Deal with special cases
			if(e.getOldValue() != null && e.getNewValue() != null && e.getOldValue().equals(e.getNewValue())) return;
			if(e.getOldValue() == null && e.getNewValue() == null) return;
			
			if(e.getPropertyName().equals(ResultsViewModel.CURRENT_QUANTITY_PROPERTY)){
				// If the two quantities have the same class, then they are equivalent as far as the quantities combo is concerned.
				if(e.getOldValue() != null && e.getNewValue() != null && e.getNewValue().getClass().equals(e.getOldValue().getClass())) return;
				ScatteringQuantity currentQuantity = (ScatteringQuantity) e.getNewValue();
				if(currentQuantity != null){
					minValueLabel.setText("Min " + currentQuantity.getQuantityName() + " value:");
					maxValueLabel.setText("Max " + currentQuantity.getQuantityName() + " value:");
					requestedMinValueLabel.setText("Requested min " + currentQuantity.getQuantityName() + " value:");
					requestedMaxValueLabel.setText("Requested max " + currentQuantity.getQuantityName() + " value:");
				} else {
					minValueLabel.setText("");
					maxValueLabel.setText("");
					requestedMinValueLabel.setText("");
					requestedMaxValueLabel.setText("");
				}
				scatteringQuantitiesComboViewer.setSelection(new StructuredSelection(currentQuantity));
			}
			else if(e.getPropertyName().equals(ResultsViewModel.CURRENT_UNITS_PROPERTY)){
				scatteringQuantitiesUnitsCombo.setInput(e.getNewValue());
			}
			else if(e.getPropertyName().equals(ResultsViewModel.CURRENT_UNIT_PROPERTY)){
				scatteringQuantitiesUnitsCombo.setSelection(new StructuredSelection(e.getNewValue()));
			}
			else if(e.getPropertyName().equals(ResultsViewModel.QUANTITIES_PROPERTY)){
				scatteringQuantitiesComboViewer.setInput(e.getNewValue());
			}
			else if(e.getPropertyName().equals(ResultsViewModel.REQUESTED_MIN_PROPERTY)){
				if(e.getNewValue() == null) return;
				Double newValue = (Double) e.getNewValue();
				if(TextUtil.equals(String.valueOf(newValue), requestedMinValueText.getText())) return;
				requestedMinValueText.setText(TextUtil.format(newValue));
			}
			else if(e.getPropertyName().equals(ResultsViewModel.REQUESTED_MAX_PROPERTY)){
				if(e.getNewValue() == null) return;
				Double newValue = (Double) e.getNewValue();
				if(TextUtil.equals(String.valueOf(newValue), requestedMaxValueText.getText())) return;
				requestedMaxValueText.setText(TextUtil.format(newValue));
			}
			else if(e.getPropertyName().equals(ResultsViewModel.VISIBLE_MIN_PROPERTY)){
				if(e.getNewValue() == null) {
					minValue.setText("");
					return;
				}
				Double newValue = (Double) e.getNewValue();
				if(TextUtil.equals(String.valueOf(newValue), minValue.getText())) return;
				minValue.setText(TextUtil.format(newValue));
			}
			else if(e.getPropertyName().equals(ResultsViewModel.VISIBLE_MAX_PROPERTY)){
				if(e.getNewValue() == null) {
					maxValue.setText("");
					return;
				}
				Double newValue = (Double) e.getNewValue();
				if(TextUtil.equals(String.valueOf(newValue), maxValue.getText())) return;
				maxValue.setText(TextUtil.format(newValue));
			}
		} finally {
			isUserEdited = true;
			drawingArea.redraw();
			main.layout();
		}
	}
}

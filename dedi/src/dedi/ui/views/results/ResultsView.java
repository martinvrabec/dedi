package dedi.ui.views.results;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.results.controllers.AbstractResultsController;
import dedi.configuration.calculations.results.models.ResultConstants;
import dedi.configuration.calculations.results.models.ResultsService;
import dedi.configuration.calculations.scattering.D;
import dedi.configuration.calculations.scattering.DoubleTheta;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.S;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;

public class ResultsView extends ViewPart implements PropertyChangeListener {
	private AbstractResultsController controller;
	
	private ScatteringQuantity currentQuantity;
	private List<Unit<?>> currentUnits;
	private Unit<?> currentUnit;
	
	// UI elements
	private Composite resultsPanel;
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
	
	// One of the scattering quantities 
	private DoubleTheta doubleTheta;
	
	private boolean isUserEdited = true;
	
	public static final String ID = "dedi.views.results";
	
	
	public ResultsView() {
		controller = ResultsService.getInstance().getController();
		controller.addView(this);
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		
		resultsPanel = new Composite(scrolledComposite, SWT.NONE);
		resultsPanel.setLayout(new GridLayout(3, true));
		
		GuiHelper.createLabel(resultsPanel, "Scattering quantity:");
		
		
		// Scattering quantities and units combos
		
		Combo scatteringQuantitiesCombo = new Combo(resultsPanel, SWT.READ_ONLY | SWT.RIGHT);
		ComboViewer scatteringQuantitiesComboViewer = new ComboViewer(scatteringQuantitiesCombo);
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
		scatteringQuantitiesComboViewer.addSelectionChangedListener(e -> {
			IStructuredSelection selection = (IStructuredSelection) e.getSelection();
		    if (selection.size() > 0){
		    	currentQuantity = (ScatteringQuantity) selection.getFirstElement();
		    	String quantityName = currentQuantity.getQuantityName();
		    	minValueLabel.setText("Min " + quantityName + " value:");
				maxValueLabel.setText("Max " + quantityName + " value:");
				requestedMinValueLabel.setText("Requested min " + quantityName + " value:");
				requestedMaxValueLabel.setText("Requested max " + quantityName + " value:");
		    	currentUnits = currentQuantity.getUnits();
		    	scatteringQuantitiesUnitsCombo.setInput(currentUnits);
		    	scatteringQuantitiesUnitsCombo.setSelection(new StructuredSelection(currentUnits.get(0)));
		    	// Assumes that a scattering quantity won't have an empty set of units.
		    }
		});
		
		
		scatteringQuantitiesUnitsCombo = GuiHelper.createUnitsCombo(resultsPanel, null);
		scatteringQuantitiesUnitsCombo.addSelectionChangedListener(e -> {
			IStructuredSelection selection = (IStructuredSelection) e.getSelection();
			if (selection.size() > 0){
				 currentUnit = (Unit<?>) selection.getFirstElement();
				 updateValues();  // Convert all the values to the new unit.
			}
		});
		
		
		
		// UI elements for displaying results
		
		minValueLabel = GuiHelper.createLabel(resultsPanel, "");
		minValue = GuiHelper.createLabel(resultsPanel, "");
		minValue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		new Label(resultsPanel, SWT.NONE);  // Placeholder
		
		
		maxValueLabel = GuiHelper.createLabel(resultsPanel, "");
		maxValue = GuiHelper.createLabel(resultsPanel, "");
		maxValue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		new Label(resultsPanel, SWT.NONE); // Placeholder
		
		
		requestedMinValueLabel = GuiHelper.createLabel(resultsPanel, "");
		requestedMinValueText = GuiHelper.createText(resultsPanel);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(70, 20).applyTo(requestedMinValueText);
		requestedMinValueText.addModifyListener(e -> {
			if(isUserEdited) controller.updateRequestedMin(requestedMinValueText.getText(), currentQuantity, currentUnit);
		});
		requestedMinValueText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateRequestedMinMax(); // Make sure requested min <= requested max.
			}
		}); 
		new Label(resultsPanel, SWT.NONE); // Placeholder
		
		
		requestedMaxValueLabel = GuiHelper.createLabel(resultsPanel, "");
		requestedMaxValueText = GuiHelper.createText(resultsPanel);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(70, 20).applyTo(requestedMaxValueText);
		requestedMaxValueText.addModifyListener(e -> {
			if(isUserEdited) controller.updateRequestedMax(requestedMaxValueText.getText(), currentQuantity, currentUnit);
		});
		requestedMaxValueText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateRequestedMinMax(); // Make sure requested min <= requested max.
			}
		}); 
		new Label(resultsPanel, SWT.NONE); // Placeholder
		
		
		// The drawing that displays the results
		drawingArea = new Canvas(resultsPanel, SWT.NONE);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 4;
		data.widthHint = 700;
		data.heightHint = 70;
		drawingArea.setLayoutData(data);
		drawingArea.addPaintListener(e -> repaint(e));
		
		
		// Initialise the scattering quantities
		ArrayList<ScatteringQuantity> quantities = new ArrayList<>();
		quantities.add(new Q());
		quantities.add(new D());
		quantities.add(new S());
		doubleTheta = new DoubleTheta();
		quantities.add(doubleTheta);
		scatteringQuantitiesComboViewer.setInput(quantities);
        scatteringQuantitiesComboViewer.setSelection(new StructuredSelection(quantities.get(0)));		
		
        
		resultsPanel.layout();
		scrolledComposite.setContent(resultsPanel);	
		scrolledComposite.setMinSize(resultsPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		
		// Initialise values.
		updateValues();
	}


	@Override
	public void setFocus() {
		requestedMinValueText.setFocus();
	}

	
	public void repaint(PaintEvent e){
		Rectangle bounds = drawingArea.getClientArea();
		
		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
		
		if(!controller.getHasSolution()) {
			 e.gc.drawText("No solution", bounds.width/2 - 40, bounds.height/2 - 10);
			 return;
		 }
		
		Double requestedRangeMax = controller.getRequestedRangeMax(currentQuantity, currentUnit);
		Double requestedRangeMin = controller.getRequestedRangeMin(currentQuantity, currentUnit);
		NumericRange fullRange = controller.getFullRange(currentQuantity, currentUnit);
		NumericRange visibleRange = controller.getVisibleRange(currentQuantity, currentUnit);
		
		 if(requestedRangeMax == null || requestedRangeMin == null || fullRange == null || visibleRange == null) 
			 return;
		 
		 if(controller.getIsSatisfied()) 
			 e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_GREEN));
		 else 
			 e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_RED));
		 
		 
         double slope = (bounds.width-120)/ (Math.log(fullRange.getMax()) - Math.log(fullRange.getMin()));
         double minRequestedX = slope*(Math.log(requestedRangeMin) - Math.log(fullRange.getMin())) + 60;
         double maxRequestedX = slope*(Math.log(requestedRangeMax) - Math.log(fullRange.getMin())) + 60;
         double minValueX = slope*(Math.log(visibleRange.getMin()) - Math.log(fullRange.getMin())) + 60;
         
         double maxValueX = slope*(Math.log(visibleRange.getMax()) - Math.log(fullRange.getMin())) + 60;
         
         e.gc.fillRectangle((int) minValueX, bounds.height/2, (int) (maxValueX - minValueX), bounds.height/2);
         e.gc.drawLine((int) minRequestedX, 5, (int) minRequestedX, bounds.height);
         e.gc.drawLine((int) maxRequestedX, 20, (int) maxRequestedX, bounds.height);
         
         e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_WHITE));
         e.gc.drawText("Requested min", (int) minRequestedX - 40, 5);
         e.gc.drawText("Requested max", (int) maxRequestedX - 40, 20);
	}


	@Override
	public void propertyChange(PropertyChangeEvent e) {
		try{
			isUserEdited = false;
			
			if(e.getPropertyName().equals(ResultConstants.REQUESTED_Q_RANGE_MIN_PROPERTY))
				updateRequestedQRangeMin();
			else if(e.getPropertyName().equals(ResultConstants.REQUESTED_Q_RANGE_MAX_PROPERTY))
				updateRequestedQRangeMax();
			else if(e.getPropertyName().equals(ResultConstants.VISIBLE_Q_RANGE_PROPERTY))
				updateVisibleQRange();
			else if(e.getPropertyName().equals(AbstractResultsController.BEAMLINE_CONFIGURATION_PROPERTY))
				doubleTheta.setWavelength(controller.getBeamlineConfiguration().getWavelength());
		} finally {
			isUserEdited = true;
			drawingArea.redraw();
			resultsPanel.layout();
		}
	}
	
	
	private void updateValues() {
		updateRequestedMinMax();
		updateVisibleQRange();
		drawingArea.redraw();
		resultsPanel.layout();
	}
	
	
	// Updates the requested values. Ensures requested min <= requested max.
	private void updateRequestedMinMax() {
		Double requestedMin = controller.getRequestedRangeMin(currentQuantity, currentUnit);
		Double requestedMax = controller.getRequestedRangeMax(currentQuantity, currentUnit);
		
		if(requestedMin != null && requestedMax != null && requestedMin > requestedMax) {
			// Swap the values
			double temp = requestedMin;
			requestedMin = requestedMax;
			requestedMax = temp;
		}
		
		// This will cause the values in the model to get updated as well.
		if(requestedMax != null) requestedMaxValueText.setText(TextUtil.format(requestedMax));
		if(requestedMin != null) requestedMinValueText.setText(TextUtil.format(requestedMin));
	}
	
	
	// Updates just the min requested value. Does not ensure requested min <= requested max.
	private void updateRequestedQRangeMin() {
		Double requestedMin = controller.getRequestedRangeMin(currentQuantity, currentUnit);
		if(requestedMin == null) return;
		// Won't overwrite the value if it's the same as before.
		if(TextUtil.equalAsDoubles(TextUtil.format(requestedMin), requestedMinValueText.getText())) return;
		requestedMinValueText.setText(TextUtil.format(requestedMin));
	}
	
	
	// Updates just the max requested value. Does not ensure requested min <= requested max.
	private void updateRequestedQRangeMax() {
		Double requestedMax = controller.getRequestedRangeMax(currentQuantity, currentUnit);
		if(requestedMax == null) return;
		// Won't overwrite the value if it's the same as before.
		if(TextUtil.equalAsDoubles(TextUtil.format(requestedMax), requestedMaxValueText.getText())) return;
		requestedMaxValueText.setText(TextUtil.format(requestedMax));
	}
	
	
	private void updateVisibleQRange(){
		NumericRange newRange = controller.getVisibleRange(currentQuantity, currentUnit);
		
		if(newRange == null) {
			minValue.setText("");
			maxValue.setText("");
			return;
		}
		
		Double newMinValue = newRange.getMin();
		if(!TextUtil.equalAsDoubles(TextUtil.format(newMinValue), minValue.getText())) 
			minValue.setText(TextUtil.format(newMinValue));
		
		Double newMaxValue = newRange.getMax();
		if(!TextUtil.equalAsDoubles(TextUtil.format(newMaxValue), maxValue.getText())) 
			maxValue.setText(TextUtil.format(newMaxValue));
	}
	
	
	@Override
	public void dispose() {
		controller.removeView(this);
		controller = null;
		doubleTheta = null;
		super.dispose();
	}
}

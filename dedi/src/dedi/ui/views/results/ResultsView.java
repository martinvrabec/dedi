package dedi.ui.views.results;
import java.util.Observable;
import java.util.Observer;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import dedi.configuration.Results;
import dedi.configuration.calculations.NumericRange;
import dedi.configuration.calculations.scattering.D;
import dedi.configuration.calculations.scattering.Q;
import dedi.configuration.calculations.scattering.ScatteringQuantity;
import dedi.ui.GuiHelper;
import dedi.ui.TextUtil;

public class ResultsView extends ViewPart implements Observer {
	private Composite main;
	private Combo scatteringQuantitiesCombo;
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
	
	private ScatteringQuantity requestedMinValue;
	private ScatteringQuantity requestedMaxValue;
	private Q minQValue;
	private Q maxQValue;
	
	private String currentQuantity;
	private Unit<?> currentUnit;
	
	private NumericRange visibleQRange;
	private NumericRange fullQRange;
	
	private boolean isSatisfied;
	private boolean hasSolution;
	
	private boolean isEdited;
	
	public static final String ID = "dedi.views.results";
	
	
	public ResultsView() {
		Results.getInstance().addObserver(this);
		currentUnit = null;
		requestedMaxValue = null;
		requestedMinValue = null;
		isSatisfied = false;
		hasSolution = false;
		isEdited = true;
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		ScrolledComposite scrolledComposite = new ScrolledComposite( parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
		scrolledComposite.setExpandVertical( true );
		scrolledComposite.setExpandHorizontal( true );
		
		
		main = new Composite(scrolledComposite, SWT.NONE);
		GridLayout layout = new GridLayout(3, true);
		main.setLayout(layout);
		
		
		Label scattertingQuantityLabel = GuiHelper.createLabel(main, "Scattering quantity:");
		
		scatteringQuantitiesCombo = new Combo(main, SWT.READ_ONLY | SWT.RIGHT);
		scatteringQuantitiesCombo.setItems(new String[] {"q", "d"});
		scatteringQuantitiesCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		scatteringQuantitiesUnitsCombo = GuiHelper.createUnitsCombo(main, null);
		scatteringQuantitiesCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				currentQuantity = scatteringQuantitiesCombo.getText();
				
				switch(currentQuantity){
					case "q":
						scatteringQuantitiesUnitsCombo.setInput(Q.UNITS);
						scatteringQuantitiesUnitsCombo.setSelection(new StructuredSelection(Q.UNITS.get(0)));
						break;
					case "d":
						scatteringQuantitiesUnitsCombo.setInput(D.UNITS);
						scatteringQuantitiesUnitsCombo.setSelection(new StructuredSelection(D.UNITS.get(0)));
						break;
				}
			}
		});
		scatteringQuantitiesUnitsCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0){
					 currentUnit = (Unit<?>) selection.getFirstElement();
					 update(null, null);
				}
			}
		});
		
		
		minValueLabel = GuiHelper.createLabel(main, "");
		minValue = GuiHelper.createLabel(main, "");
		minValue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Label l1 = new Label(main, SWT.NONE);
		
		
		maxValueLabel = GuiHelper.createLabel(main, "");
		maxValue = GuiHelper.createLabel(main, "");
		maxValue.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Label l2 = new Label(main, SWT.NONE);
		
		
		requestedMinValueLabel = GuiHelper.createLabel(main, "");
		requestedMinValueText = GuiHelper.createText(main);
		requestedMinValueText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		requestedMinValueText.addModifyListener(e -> textChanged());
		Label l3 = new Label(main, SWT.NONE);
		
		
		requestedMaxValueLabel = GuiHelper.createLabel(main, "");
		requestedMaxValueText = GuiHelper.createText(main);
		requestedMaxValueText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		requestedMaxValueText.addModifyListener(e -> textChanged());
		Label l4 = new Label(main, SWT.NONE);
		
		
		drawingArea = new Canvas(main, SWT.NONE);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		data.widthHint = 700;
		data.heightHint = 70;
		drawingArea.setLayoutData(data);
		drawingArea.addPaintListener(e -> repaint(e));
		
		main.layout();
		
		scrolledComposite.setContent( main );	
		scrolledComposite.setMinSize( main.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
		
		scatteringQuantitiesCombo.select(0); // This will lead to update(null, null) being called. 
	}

	
	@Override
	public void setFocus() {
	}


	@Override
	public void update(Observable o, Object arg) {
		if(!isEdited) return;
		
		minValueLabel.setText("Min " + currentQuantity + " value:");
		maxValueLabel.setText("Max " + currentQuantity + " value:");
		requestedMinValueLabel.setText("Requested min " + currentQuantity + " value:");
		requestedMaxValueLabel.setText("Requested max " + currentQuantity + " value:");
		
		
		isEdited = false;
		if(requestedMaxValue != null){
			switch(currentQuantity){
				case "q":
					requestedMaxValue = requestedMaxValue.to(new Q());
					break;
				case "d":
					requestedMaxValue = requestedMaxValue.to(new D());
			}
			requestedMaxValueText.setText(TextUtil.format(requestedMaxValue.getValue().to(currentUnit).getEstimatedValue()));
		}
		
		
		if(requestedMinValue != null){
			switch(currentQuantity){
				case "q":
					requestedMinValue = requestedMinValue.to(new Q());
					break;
				case "d":
					requestedMinValue = requestedMinValue.to(new D());
			}
			requestedMinValueText.setText(TextUtil.format(requestedMinValue.getValue().to(currentUnit).getEstimatedValue()));
		}
		
		
		if(requestedMaxValue != null && requestedMinValue != null && 
		   requestedMinValue.getValue().to(currentUnit).getEstimatedValue() > requestedMaxValue.getValue().to(currentUnit).getEstimatedValue()){
			ScatteringQuantity temp = requestedMaxValue;
			requestedMaxValue = requestedMinValue;
			requestedMinValue = temp;
			requestedMaxValueText.setText(TextUtil.format(requestedMaxValue.getValue().to(currentUnit).getEstimatedValue()));
			requestedMinValueText.setText(TextUtil.format(requestedMinValue.getValue().to(currentUnit).getEstimatedValue()));
		}
		isEdited = true;
		
		Results.getInstance().setRequestedQRange(requestedMinValue, requestedMaxValue);
		
		main.layout();
		
		NumericRange qRange = Results.getInstance().getQRange();
		visibleQRange = qRange;
		if(qRange == null){
			minValue.setText("");
			maxValue.setText("");
			hasSolution = false;
			isSatisfied = false;
			drawingArea.redraw();
			return;
		}
		
		hasSolution = true;
		isSatisfied = isSatisfied();
		
		
		minQValue = new Q(qRange.getMin());
		maxQValue = new Q(qRange.getMax());
		
		fullQRange = Results.getInstance().getFullQRange();
		if(fullQRange == null) fullQRange = qRange;
		
		
		switch(currentQuantity){
			case "q":
				minValue.setText(TextUtil.format(Amount.valueOf(qRange.getMin(), SI.METER.inverse()).to(currentUnit).getEstimatedValue()));
				maxValue.setText(TextUtil.format(Amount.valueOf(qRange.getMax(), SI.METER.inverse()).to(currentUnit).getEstimatedValue()));
				break;
			case "d":
				minValue.setText(TextUtil.format(new Q(Amount.valueOf(qRange.getMax(), SI.METER.inverse())).to(new D()).getValue()
						                         .to(currentUnit).getEstimatedValue()));
				maxValue.setText(TextUtil.format(new Q(Amount.valueOf(qRange.getMin(), SI.METER.inverse())).to(new D()).getValue()
                        						.to(currentUnit).getEstimatedValue()));
		}
		
		
		drawingArea.redraw();
		main.layout();
	}

	
	private boolean isSatisfied(){
		return  visibleQRange != null && requestedMinValue != null &&
				visibleQRange.contains(requestedMinValue.toQ().getValue().to(SI.METER.inverse()).getEstimatedValue()) &&
				requestedMaxValue !=null &&
				visibleQRange.contains(requestedMaxValue.toQ().getValue().to(SI.METER.inverse()).getEstimatedValue());
	}
	
	
	private void textChanged(){
		if(!isEdited) return;
		double maxValue;
		double minValue;
		try{
			maxValue = Double.parseDouble(requestedMaxValueText.getText());
			switch(currentQuantity){
				case "q":
					requestedMaxValue = new Q(Amount.valueOf(maxValue, currentUnit));
					break;
				case "d":
					requestedMaxValue = new D(Amount.valueOf(maxValue, currentUnit));
			}
		} catch(NumberFormatException ex){
			requestedMaxValue = null;
		}
		
		try{
			minValue = Double.parseDouble(requestedMinValueText.getText());
			switch(currentQuantity){
			case "q":
				requestedMinValue = new Q(Amount.valueOf(minValue, currentUnit));
				break;
			case "d":
				requestedMinValue = new D(Amount.valueOf(minValue, currentUnit));
		}
		} catch(NumberFormatException ex){
			requestedMinValue = null;
		}
		
		isSatisfied = isSatisfied();
		
		isEdited = false;
		Results.getInstance().setRequestedQRange(requestedMinValue, requestedMaxValue);
		isEdited = true;
		
		drawingArea.redraw();
	}
	
	
	public void repaint(PaintEvent e){
		Rectangle bounds = drawingArea.getClientArea();
		
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
         e.gc.drawText("Requested max", (int) maxRequestedX - 40, 5);
	}
}

package dedi.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import dedi.ui.views.configuration.ConfigurationView;


public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) { 
		 String editorArea = layout.getEditorArea();
         layout.setEditorAreaVisible(false);
         
         IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.30f,
     		    editorArea);
         
         layout.addView("dedi.plottingview", IPageLayout.TOP, 0.7f, editorArea);
         
		 topLeft.addView(ConfigurationView.ID);
		 topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		 
		 layout.addView("dedi.views.results", IPageLayout.BOTTOM, 0.05f, editorArea);
	}

}

package de.jacavi.rcp.perspectives;



import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.rcp.editors.TrackEditor;
import de.jacavi.rcp.views.LapView;
import de.jacavi.rcp.views.PlayerSettingsView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
//		layout.setFixed(true);
		layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.8f, editorArea);
//		layout.addView(PlayerSettingsView.ID,  IPageLayout.LEFT, 0.3f, editorArea);
		layout.addView(TrackEditor.ID,  IPageLayout.RIGHT, 0.3f, PlayerSettingsView.ID);
		layout.getViewLayout(LapView.ID).setCloseable(false);
		
	}

}

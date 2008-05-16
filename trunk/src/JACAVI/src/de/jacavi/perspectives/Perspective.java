package de.jacavi.perspectives;



import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.views.LapView;
import de.jacavi.views.PlayerSettingsView;
import de.jacavi.views.TrackView;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
//		layout.setFixed(true);
		layout.addView(PlayerSettingsView.ID,  IPageLayout.LEFT, 0.3f, editorArea);
		layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.7f, PlayerSettingsView.ID);
		layout.addView(TrackView.ID,  IPageLayout.RIGHT, 0.3f, PlayerSettingsView.ID);
		layout.getViewLayout(LapView.ID).setCloseable(false);
		
	}

}

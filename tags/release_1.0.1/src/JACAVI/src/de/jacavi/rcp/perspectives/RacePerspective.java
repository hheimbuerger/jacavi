package de.jacavi.rcp.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.rcp.views.LapView;
import de.jacavi.rcp.views.RaceView;



public class RacePerspective implements IPerspectiveFactory {

    public static String ID = "JACAVI.racePerspective";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        // layout.setFixed(true);
        layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.8f, editorArea);

        // Alternative 1 - view with border and name, can't be closed
        // layout.addView(RaceView.ID, IPageLayout.LEFT, 0.31f, editorArea);
        // layout.getViewLayout(RaceView.ID).setCloseable(true);

        // Alternative 2 - standalone view without name and border (more space)
        layout.addStandaloneView(RaceView.ID, false, IPageLayout.LEFT, 0.31f, editorArea);
    }

}

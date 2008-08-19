package de.jacavi.rcp.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.rcp.views.TileExplorer;
import de.jacavi.rcp.views.TrackOutline;



public class Perspective implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        // layout.setFixed(true);
        // layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.8f, editorArea);
        layout.addView(TileExplorer.ID, IPageLayout.LEFT, 0.31f, editorArea);
        layout.addView(TrackOutline.ID, IPageLayout.RIGHT, 0.7f, editorArea);

    }

}

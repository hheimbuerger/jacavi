package de.jacavi.rcp.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.rcp.views.TileExplorer;
import de.jacavi.rcp.views.TrackOutline;



public class EditorPerspective implements IPerspectiveFactory {

    public static String ID = "JACAVI.editorPerspective";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        // layout.setFixed(true);
        // layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.8f, editorArea);
        layout.addView(TileExplorer.ID, IPageLayout.LEFT, 0.3f, editorArea);
        layout.addView(TrackOutline.ID, IPageLayout.RIGHT, 0.7f, editorArea);

        layout.getViewLayout(TileExplorer.ID).setCloseable(false);
        layout.getViewLayout(TrackOutline.ID).setCloseable(false);
    }

}

package de.jacavi.rcp.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.rcp.views.TileExplorerView;
import de.jacavi.rcp.views.TrackOutlineView;



public class EditorPerspective implements IPerspectiveFactory {

    public static String ID = "JACAVI.editorPerspective";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        // layout.setFixed(true);
        // layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.8f, editorArea);
        layout.addView(TileExplorerView.ID, IPageLayout.LEFT, 0.3f, editorArea);
        layout.addView(TrackOutlineView.ID, IPageLayout.RIGHT, 0.7f, editorArea);

        layout.getViewLayout(TileExplorerView.ID).setCloseable(false);
        layout.getViewLayout(TrackOutlineView.ID).setCloseable(false);
    }

}

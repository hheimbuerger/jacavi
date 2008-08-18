package de.jacavi.rcp.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.jacavi.rcp.views.TileExplorer;



public class Perspective implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(true);
        // layout.setFixed(true);
        // layout.addView(LapView.ID, IPageLayout.BOTTOM, 0.8f, editorArea);
        layout.addView(TileExplorer.ID, IPageLayout.LEFT, 0.31f, editorArea);

    }

}

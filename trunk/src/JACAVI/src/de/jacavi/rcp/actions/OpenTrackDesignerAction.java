package de.jacavi.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.editors.TrackDesignerInput;

public class OpenTrackDesignerAction extends Action {

	private final String trackName;

	public OpenTrackDesignerAction(String trackName) {
		this.trackName = trackName;
	}

	public void run() {
		// ISelection selection = viewer.getSelection();
		// Object obj = ((IStructuredSelection) selection).getFirstElement();
		// TreeObject treeObj = (TreeObject) obj;
		// if (treeObj instanceof TreeParent) {
		// return;
		// }

		TrackDesignerInput editorInput = new TrackDesignerInput(trackName);

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {

			page.openEditor(editorInput, TrackDesigner.ID);

			IWorkbenchPart active = page.getActivePart();
			active.setFocus();

		} catch (PartInitException e) {
			// TODO: handle exception
		}
	}

}

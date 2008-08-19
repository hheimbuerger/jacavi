package de.jacavi.rcp.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.jacavi.appl.track.Track;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.editors.TrackDesignerInput;

public class OpenTrackDesignerAction extends Action {

	private final Track track;

	public OpenTrackDesignerAction(Track track) {
		this.track = track;
	}

	public void run() {

		TrackDesignerInput editorInput = new TrackDesignerInput(track);

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
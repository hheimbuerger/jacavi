package de.jacavi.rcp.actions;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.editors.TrackDesignerInput;

public class OpenTrackFileAction implements IWorkbenchWindowActionDelegate {

	private static Log log = LogFactory.getLog(OpenTrackFileAction.class);

	private IWorkbenchWindow window;

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(IAction action) {
		String[] filterExt = { "*" + Track.FILE_EXTENSION };
		FileDialog dlg = new FileDialog(window.getShell(), SWT.MULTI);
		dlg.setFilterExtensions(filterExt);
		String file = dlg.open();
		if (file != null) {
			File f = new File(file);
			log.info(f.getAbsolutePath() + "selected");

			try {
				openEditor(f);
			} catch (FileNotFoundException e) {
				handleException(e);
			} catch (TrackLoadingException e) {
				handleException(e);
			} catch (PartInitException e) {
				handleException(e);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void openEditor(File file) throws FileNotFoundException,
			TrackLoadingException, PartInitException {
		Track track = new Track(file);
		TrackDesignerInput editorInput = new TrackDesignerInput(file
				.getAbsolutePath(), track);

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		page.openEditor(editorInput, TrackDesigner.ID);

		IWorkbenchPart active = page.getActivePart();
		active.setFocus();

	}

	private void handleException(Exception e) {
		log.error("Load failed", e);

		// Create the required Status object
		Status status = new Status(IStatus.ERROR, "JaCaVi", e.getMessage(), e);

		// Display the dialog
		ErrorDialog
				.openError(
						window.getShell(),
						"Load Error",
						"It has been occured an error during loading the track",
						status);
	}
}

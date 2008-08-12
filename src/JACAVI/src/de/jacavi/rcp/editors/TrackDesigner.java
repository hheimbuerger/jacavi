package de.jacavi.rcp.editors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.dlg.SafeSaveDialog;
import de.jacavi.rcp.widgets.TrackWidget;

public class TrackDesigner extends EditorPart {

	private static Log log = LogFactory.getLog(TrackDesigner.class);

	public static String ID = "JACAVI.TrackDesigner";

	boolean isDirty = true;

	private Track currentTrack;

	public TrackDesigner() {
	}

	@Override
	protected void setInput(IEditorInput input) {

		super.setInput(input);
		currentTrack = ((TrackDesignerInput) input).getTrack();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		monitor.beginTask("Save", IProgressMonitor.UNKNOWN);
		SafeSaveDialog dlg = new SafeSaveDialog(getSite().getShell());

		dlg.setText("Save");
		dlg.setFilterPath("C:/");
		String[] filterExt = { "*" + Track.FILE_EXTENSION };
		dlg.setFilterExtensions(filterExt);
		String selected = dlg.open();
		try {

			// TODO: currentTrack.saveToXml(filename);

			FileOutputStream fo = new FileOutputStream(selected);
			fo.write(10);
			fo.flush();
			fo.close();

			log.info("Track saved to " + selected);
		} catch (FileNotFoundException e) {
			handleException(e);
		} catch (IOException e) {
			handleException(e);
		}
		

		monitor.done();
		isDirty = false;
		firePropertyChange(EditorPart.PROP_DIRTY);

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		try {
			parent.setLayout(new GridLayout());

			// TODO: Get all track items from TileSetRepository
			ToolBar toolbar0 = new ToolBar(parent, SWT.BORDER | SWT.WRAP);
			ToolItem toolItem = new ToolItem(toolbar0, SWT.NONE);
			toolItem.setText("Item");
			toolItem.setImage(Activator.getImageDescriptor(
					"tiles/debug/straight.png").createImage());

			toolItem = new ToolItem(toolbar0, SWT.NONE);
			toolItem.setImage(Activator.getImageDescriptor(
					"tiles/debug/turn_30deg.png").createImage());
			toolItem.setText("Item");

			toolItem = new ToolItem(toolbar0, SWT.NONE);
			toolItem.setImage(Activator.getImageDescriptor(
					"tiles/debug/turn_90deg.png").createImage());
			toolItem.setText("Item");

			toolbar0.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// TODO: track needs to be stored locally and Track(TileSet)
			// constructor should be used (but GUI doesn't
			// allow choosing a TileSet yet)
			new TrackWidget(parent, currentTrack).setLayoutData(new GridData(
					GridData.FILL_BOTH));
		} catch (TrackLoadingException e) {
			throw new RuntimeException("Error while creating TrackWidget.");
		}
	}

	@Override
	public void setFocus() {
	}

	private void handleException(Exception e) {
		log.error("Save failed", e);
	}

}
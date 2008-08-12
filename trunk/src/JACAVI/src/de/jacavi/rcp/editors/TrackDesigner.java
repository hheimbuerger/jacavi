package de.jacavi.rcp.editors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
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

    public static String ID = "JACAVI.TrackDesigner";

    boolean isDirty = true;

    public TrackDesigner() {}

    @Override
    protected void setInput(IEditorInput input) {

        super.setInput(input);
        // try {
        // // IFile file = ((IFileEditorInput) input).getFile();
        // // ObjectInputStream in = new ObjectInputStream(file.getContents());
        // in.close();
        // setPartName(file.getName());
        // } catch (IOException e) {
        // handleLoadException(e);
        // } catch (CoreException e) {
        // handleLoadException(e);
        // }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        monitor.beginTask("Save", IProgressMonitor.UNKNOWN);
        try {
            Thread.sleep(1000);
            SafeSaveDialog dlg = new SafeSaveDialog(getSite().getShell());

            dlg.setText("Save");
            dlg.setFilterPath("C:/");
            String[] filterExt = { "*.jacavi" };
            dlg.setFilterExtensions(filterExt);
            String selected = dlg.open();
            System.out.println(selected);

            try {
                FileOutputStream fo = new FileOutputStream(selected);
                fo.write(10);
                fo.flush();
                fo.close();
            } catch(FileNotFoundException e) {
                handleException(e);
            } catch(IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch(InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        monitor.done();
        isDirty = false;

    }

    @Override
    public void doSaveAs() {
    // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
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
            // TODO: track needs to be stored locally and Track(TileSet) constructor should be used (but GUI doesn't
            // allow choosing a TileSet yet)
            new TrackWidget(parent, new Track(Activator.getResourceAsStream("/tracks/demo_with30degturns.track.xml")));
        } catch(TrackLoadingException e) {
            throw new RuntimeException("Error while creating TrackWidget.");
        }
    }

    @Override
    public void setFocus() {}

    private void handleException(Exception e) {
        System.err.println("** Save failed. **");
        e.printStackTrace();
    }

}

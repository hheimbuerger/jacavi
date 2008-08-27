package de.jacavi.rcp;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.editors.TrackDesignerInput;
import de.jacavi.rcp.perspectives.EditorPerspective;
import de.jacavi.rcp.util.ExceptionHandler;



public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public String getInitialWindowPerspectiveId() {
        return EditorPerspective.ID;
    }

    // DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public void postStartup() {
        super.postStartup();
        TrackDesignerInput editorInput;
        try {
            editorInput = new TrackDesignerInput(new Track(new File("tracks/demo_crossing.track.xml")));

            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            page.openEditor(editorInput, TrackDesigner.ID);

            IWorkbenchPart active = page.getActivePart();
            active.setFocus();

        } catch(PartInitException e) {
            // TODO: handle exception
        } catch(FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch(TrackLoadingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    @Override
    public void eventLoopException(Throwable exception) {
        ExceptionHandler.handleException(exception, true);
        super.eventLoopException(exception);
    }
}

package de.jacavi.rcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusManager;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.car.CarRepository;
import de.jacavi.appl.controller.CarControllerManager;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.hal.ConnectorConfigurationManager;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.editors.TrackDesignerInput;
import de.jacavi.rcp.perspectives.EditorPerspective;
import de.jacavi.rcp.util.JacaviErrorHandler;



public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    private AbstractStatusHandler workbenchErrorHandler = null;

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public String getInitialWindowPerspectiveId() {
        return EditorPerspective.ID;
    }

    // DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @SuppressWarnings("unchecked")
    @Override
    public void postStartup() {
        super.postStartup();
        TrackDesignerInput editorInput;
        try {
            // get the workbench page
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            // retrieve the last opened editors from the preferences and reopen them
            final IPreferenceStore store = Activator.getStore();
            int i = 0;
            while(true) {
                String filename = store.getString("editor" + i);
                if(filename.equals(""))
                    break;
                editorInput = new TrackDesignerInput(filename, new Track(new File(filename)));
                page.openEditor(editorInput, TrackDesigner.ID);
                i++;
            }

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

        // add a player to simplify testing
        List<Player> players = (List<Player>) ContextLoader.getBean("playersBean");
        CarRepository carRepository = (CarRepository) ContextLoader.getBean("carRepositoryBean");
        CarControllerManager carControllerManager = (CarControllerManager) ContextLoader
                .getBean("carControllerManagerBean");
        ConnectorConfigurationManager connectorManager = (ConnectorConfigurationManager) ContextLoader
                .getBean("connectorManager");
        Player initial = new Player();
        initial.setName("DEBUG");
        initial.setCar(carRepository.getCars().get(0));
        initial.setController(carControllerManager.getInputDevicesByType(KeyboardDevice.class).get(0));
        initial.setSlotCarSystemConnector(connectorManager.getConnectors().get(0));
        players.add(initial);
    }

    @Override
    public void eventLoopException(Throwable exception) {
        if(exception == null)
            return;
        // we log the exception
        Log log = LogFactory.getLog(this.getClass());
        log.error("Unhandled event loop exception", exception);
        // and we show an dialogue
        StatusManager.getManager().handle(
                new Status(IStatus.ERROR, "JACAVI", "Unhandled event loop exception", exception), StatusManager.SHOW);
    }

    @Override
    public synchronized org.eclipse.ui.statushandlers.AbstractStatusHandler getWorkbenchErrorHandler() {
        // here we create a new ErrorHandler if there is none
        // we use our own implementation of WorkbenchErrorHandler (extends AbstractStatusHandler)
        // so we are more flexible see
        if(workbenchErrorHandler == null)
            workbenchErrorHandler = new JacaviErrorHandler();
        return workbenchErrorHandler;
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
        configurer.setSaveAndRestore(true);
        super.initialize(configurer);
    }

    @Override
    public boolean preShutdown() {
        // switch back to the designer perspective
        try {
            PlatformUI.getWorkbench().showPerspective(EditorPerspective.ID,
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow());
        } catch(WorkbenchException e) {
            e.printStackTrace();
        }

        // store the list of open editors
        final IPreferenceStore store = Activator.getStore();
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = page.getEditorReferences();
        int i = 0;
        for(IEditorReference editorReference: editorReferences) {
            IEditorPart editor = editorReference.getEditor(false);
            if(editor instanceof TrackDesigner) {
                TrackDesigner designer = (TrackDesigner) editor;
                store.setValue("editor" + i, designer.getFilename());
                i++;
            }
        }
        store.setToDefault("editor" + i); // remove the following entry from the preferences store so the next loading
        // will stop here

        return super.preShutdown();
    }
}

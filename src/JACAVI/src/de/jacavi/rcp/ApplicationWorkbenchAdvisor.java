package de.jacavi.rcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.car.CarRepository;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.hal.ConnectorConfigurationManager;
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
    @SuppressWarnings("unchecked")
    @Override
    public void postStartup() {
        super.postStartup();
        TrackDesignerInput editorInput;
        try {
            editorInput = new TrackDesignerInput(new Track(new File("tracks/demo_analogue.track.xml")));

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

        // add a player to simplify testing
        List<Player> players = (List<Player>) ContextLoader.getBean("playersBean");
        CarRepository carRepository = (CarRepository) ContextLoader.getBean("carRepositoryBean");
        InputDeviceManager inputDeviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");
        ConnectorConfigurationManager connectorManager = (ConnectorConfigurationManager) ContextLoader
                .getBean("connectorManager");
        Player initial = new Player();
        initial.setName("DEBUG");
        initial.setCar(carRepository.getCars().get(0));
        initial.setController(inputDeviceManager.getInputDevicesByType(KeyboardDevice.class).get(0));
        initial.setSlotCarSystemConnector(connectorManager.getConnectors().get(0));
        players.add(initial);
    }

    @Override
    public void eventLoopException(Throwable exception) {
        ExceptionHandler.handleException(exception, true);
        super.eventLoopException(exception);
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
        configurer.setSaveAndRestore(true);
        super.initialize(configurer);
    }

    @Override
    public boolean preShutdown() {
        try {
            PlatformUI.getWorkbench().showPerspective(EditorPerspective.ID,
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow());
        } catch(WorkbenchException e) {
            e.printStackTrace();
        }
        return super.preShutdown();
    }
}

package de.jacavi.rcp.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.racelogic.Race;
import de.jacavi.appl.racelogic.RaceEngine;
import de.jacavi.rcp.dlg.RaceValidationDialog;
import de.jacavi.rcp.perspectives.RacePerspective;
import de.jacavi.rcp.util.PartFromIDResolver;
import de.jacavi.rcp.views.TrackView;



/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class StartRaceAction implements IWorkbenchWindowActionDelegate {

    private static Log log = LogFactory.getLog(StartRaceAction.class);

    private IWorkbenchWindow window;

    private final RaceEngine raceEngine;

    private final Race race;

    /**
     * The constructor.
     */
    public StartRaceAction() {
        raceEngine = (RaceEngine) ContextLoader.getBean("raceEngineBean");
        race = raceEngine.getRace();
    }

    /**
     * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
     * UI.
     * 
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action) {
        // TODO
        // # Check if all players have a non-empty name and a selected car. Abort and report if not.
        // # Check if all players have an input device that is connected (InputDeviceManager has to be asked for the ID)
        // . Abort and report if not.
        // # Check if all players have a technology that is connected and configured (TechnologyConfigurationManager has
        // to be asked for the ID). Abort and report if not.
        // # Check if all technologies are compatible with the used track type (e.g. no BlueRider on digital tracks).
        // Abort and report if not.
        // FIXED Switch the active perspective to the race perspective.
        // # Show the staging lights, and wait until they are done.
        // # Start the RaceEngine.

        if(new RaceValidationDialog(window.getShell(), race).open() == RaceValidationDialog.OK)
            log.debug("Race validated successfull");
        else {
            return;
        }

        try {
            log.debug("Opening Race Perspective");
            PlatformUI.getWorkbench().showPerspective(RacePerspective.ID, window);
        } catch(WorkbenchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TrackView trackView = (TrackView) PartFromIDResolver.resolveView(TrackView.ID);
        log.debug("Starting RaceEngine");
        raceEngine.startRaceTimer(trackView);

    }

    /**
     * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
     * this can only happen after the delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {}

    /**
     * We can use this method to dispose of any system resources we previously allocated.
     * 
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {}

    /**
     * We will cache window object in order to be able to provide parent shell for the message dialog.
     * 
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

}
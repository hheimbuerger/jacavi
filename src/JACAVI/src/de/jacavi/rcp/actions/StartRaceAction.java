package de.jacavi.rcp.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.jacavi.appl.track.Track;
import de.jacavi.rcp.dlg.RaceValidationDialog;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.perspectives.RacePerspective;
import de.jacavi.rcp.util.PartFromIDResolver;
import de.jacavi.rcp.views.RaceView;



/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class StartRaceAction extends RaceControlAction {

    private static Log log = LogFactory.getLog(StartRaceAction.class);

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

        // show the RaceValidationDialog (which will automatically do the actual validation)
        if(new RaceValidationDialog(window.getShell(), players).open() == Window.OK)
            log.debug("Race validated successfull");
        else {
            return;
        }

        // determine the current track from the active editor
        TrackDesigner activeEditor = (TrackDesigner) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().getActiveEditor();
        Track activeTrack = activeEditor.getTrack();

        // switch to the race perspective
        try {
            log.debug("Opening Race Perspective");
            PlatformUI.getWorkbench().showPerspective(RacePerspective.ID, window);
        } catch(WorkbenchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get the raceView
        RaceView raceView = (RaceView) PartFromIDResolver.resolveView(RaceView.ID);

        // tell the RaceEngine to start the race
        log.debug("Starting RaceEngine");
        raceEngine.startRace(activeTrack, raceView);
    }

}
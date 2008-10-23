package de.jacavi.rcp.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.jacavi.appl.track.Track;
import de.jacavi.rcp.dialogs.RaceValidationDialog;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.perspectives.EditorPerspective;
import de.jacavi.rcp.perspectives.RacePerspective;
import de.jacavi.rcp.util.ExceptionHandler;
import de.jacavi.rcp.util.PartFromIDResolver;
import de.jacavi.rcp.views.RaceView;



/**
 * Class that represents an action that leads through following steps:
 * <p>
 * <ul>
 * <li>validate race settings
 * <li>switch to race perspective
 * <li>show traffic lights
 * <li>start race engine
 * </ul>
 * 
 * @author Fabian
 */
public class StartRaceAction extends RaceControlAction {

    private static Log log = LogFactory.getLog(StartRaceAction.class);

    public void run(IAction action) {

        // show the RaceValidationDialog (which will automatically do the actual validation)
        if(new RaceValidationDialog(window.getShell(), players).open() == Window.OK) {
            log.debug("Race validated successfull");
        } else {
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
            ExceptionHandler.handleException(this, e, true);
        }

        // get the raceView
        RaceView raceView = (RaceView) PartFromIDResolver.resolveView(RaceView.ID);

        // show traffic lights
        // new TrafficLightsDialog();

        // tell the RaceEngine to start the race
        log.debug("Starting RaceEngine");
        try {
            raceEngine.startRace(activeTrack, raceView);
        } catch(Exception ex) {
            ExceptionHandler.handleException(this, ex, true);
            try {
                PlatformUI.getWorkbench().showPerspective(EditorPerspective.ID, window);
            } catch(WorkbenchException e) {
                ExceptionHandler.handleException(this, e, true);
            }

        }
    }
}
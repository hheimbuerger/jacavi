package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.jacavi.rcp.perspectives.EditorPerspective;



public class StopRaceAction extends RaceControlAction {

    @Override
    public void run(IAction action) {

        cleanup();

        try {
            PlatformUI.getWorkbench().showPerspective(EditorPerspective.ID, window);
        } catch(WorkbenchException e) {
            de.jacavi.rcp.util.ExceptionHandler.handleException(this, e, false);
        }
    }

    private void cleanup() {
        raceEngine.stopRace();
    }
}

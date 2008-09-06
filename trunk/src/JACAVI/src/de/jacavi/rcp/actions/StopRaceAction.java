package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.jacavi.rcp.perspectives.EditorPerspective;



public class StopRaceAction extends RaceControlAction {

    @Override
    public void run(IAction action) {

        // TODO: Do Cleanup stuff here
        cleanup();

        try {
            PlatformUI.getWorkbench().showPerspective(EditorPerspective.ID, window);
        } catch(WorkbenchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void cleanup() {
        raceEngine.stopRace();
    }
}

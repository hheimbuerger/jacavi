package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.jacavi.rcp.dialogs.PlayerOverviewDialog;



/**
 * @author Fabian Rohn This class shows an alternative to the PlayerSettingsView. Instead of the view we use a workbench
 *         action to open a dialog.
 */
public class PlayerSettingsAction implements IWorkbenchWindowActionDelegate {

    // private static Log log = LogFactory.getLog(PlayerSettingsAction.class);

    private IWorkbenchWindow window;

    public void dispose() {
    // TODO Auto-generated method stub

    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        PlayerOverviewDialog setting = new PlayerOverviewDialog(window.getShell());
        if(setting.open() == Window.OK) {
            // Removed following code as it wasn't working anyway. Right now, it's not possible to 'cancel' the
            // modifications to the players.
            /*race.setPlayers(setting.getPlayer());
            log.info(race.getPlayers().size() + " player set");*/
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    // TODO Auto-generated method stub

    }
}

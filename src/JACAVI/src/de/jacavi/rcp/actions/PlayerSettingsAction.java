package de.jacavi.rcp.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.racelogic.Race;
import de.jacavi.rcp.dlg.PlayerOverviewDialog;

/**
 * @author Fabian Rohn
 *
 * This class shows an alternative to the PlayerSettingsView.
 * Instead of the view we use a workbench action to open a dialog. 
 */
public class PlayerSettingsAction implements IWorkbenchWindowActionDelegate {
	
	private static Log log = LogFactory.getLog(PlayerSettingsAction.class);

	private IWorkbenchWindow window;
	private Race race;
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
		race = (Race)ContextLoader.getBean("raceBean");
	}

	public void run(IAction action) {
		PlayerOverviewDialog setting = new PlayerOverviewDialog(window.getShell());
		if (setting.open() == Dialog.OK) {
			race.setPlayers(setting.getPlayer());
			log.info(race.getPlayers().size() + " player set");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	public void setRace(Race race) {
		this.race = race;
	}

	public Race getRace() {
		return race;
	}

}

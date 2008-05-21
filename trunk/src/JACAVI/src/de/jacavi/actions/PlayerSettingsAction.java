package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.jacavi.rcp.dlg.PlayerSettingsDialog;

/**
 * @author Fabian Rohn
 *
 * This class shows an alternative to the PlayerSettingsView.
 * Instead of the view we use a workbench action to open a dialog. 
 */
public class PlayerSettingsAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		PlayerSettingsDialog setting = new PlayerSettingsDialog(window.getShell());
		if (setting.open() == Dialog.OK) {
			System.out.println("Alles OK");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}

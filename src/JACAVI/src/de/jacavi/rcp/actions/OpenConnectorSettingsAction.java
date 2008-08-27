package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.jacavi.rcp.dlg.ConnectorSettingsDialog;



/**
 * Action that opens the {@link ConnectorSettingsDialog}.
 */
public class OpenConnectorSettingsAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    @Override
    public void dispose() {}

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    @Override
    public void run(IAction action) {
        Dialog dialog = new ConnectorSettingsDialog(window.getShell());
        dialog.open();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {}

}

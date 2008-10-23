package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.jacavi.rcp.dialogs.InputDeviceSettingsDialog;



/**
 * Action that opens the {@link InputDeviceSettingsDialog}.
 */
public class OpenInputDeviceSettingsAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    @Override
    public void dispose() {}

    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    @Override
    public void run(IAction action) {
        InputDeviceSettingsDialog dlg = new InputDeviceSettingsDialog(window.getShell());
        if(dlg.open() == Window.OK) {

        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {}
}

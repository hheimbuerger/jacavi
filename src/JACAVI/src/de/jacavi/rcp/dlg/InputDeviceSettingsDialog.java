package de.jacavi.rcp.dlg;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;



public class InputDeviceSettingsDialog extends TitleAreaDialog {

    public InputDeviceSettingsDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        return super.createDialogArea(parent);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        getShell().setText("Input Device Settings");
        setTitle("Initialize and configure input devices");
        setMessage("Please initialize and configure the input devices you want to use in races.",
                IMessageProvider.INFORMATION);
        return super.createButtonBar(parent);
    }

    @Override
    protected void okPressed() {
        super.okPressed();
    }

}

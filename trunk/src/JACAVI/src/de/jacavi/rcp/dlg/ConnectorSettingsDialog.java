package de.jacavi.rcp.dlg;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.rcp.Activator;



public class ConnectorSettingsDialog extends AbstractSettingsDialog {

    public ConnectorSettingsDialog(Shell parentShell) {
        super(parentShell);

        // prepare the images
        imageManager.put("imageLib42", Activator.getImageDescriptor("/icons/input_devices/keyboard.png").createImage());
        imageManager.put("imageBlueRider", Activator.getImageDescriptor("/icons/input_devices/keyboard.png")
                .createImage());
        imageManager.put("imageAnalogue", Activator.getImageDescriptor("/icons/input_devices/keyboard.png")
                .createImage());
        imageManager.put("icon", Activator.getImageDescriptor("/icons/famfamfam-silk/controller.png").createImage());
    }

    @Override
    protected void setDescriptionTexts() {
        getShell().setText("Connector Settings");
        setTitle("Configure slot car systems");
        setMessage("Initialize and configure your slot car systems.", IMessageProvider.INFORMATION);
    }

    @Override
    protected void createTabItems(CTabFolder tabFolder) {
        createLib42Tab(prepareTabItem("Lib42", imageManager.get("icon"), imageManager.get("imageLib42")));
        createBlueRiderTab(prepareTabItem("BlueRider", imageManager.get("icon"), imageManager.get("imageBlueRider")));
        createAnalogueTab(prepareTabItem("Analogue", imageManager.get("icon"), imageManager.get("imageAnalogue")));
    }

    private void createLib42Tab(Composite parent) {
        Label dummy = new Label(parent, SWT.WRAP);
        dummy.setText("Insert Lib42 configuration widgets here.");
        dummy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void createBlueRiderTab(Composite parent) {
        Label dummy = new Label(parent, SWT.WRAP);
        dummy.setText("Insert BlueRider configuration widgets here.");
        dummy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void createAnalogueTab(Composite parent) {
        Label dummy = new Label(parent, SWT.WRAP);
        dummy.setText("Insert analogue configuration widgets here.");
        dummy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    @Override
    protected void fillDeviceList(List deviceList) {
        deviceList.add("dummy item 1");
        deviceList.add("dummy item 2");
        deviceList.add("dummy item 3");
    }

}

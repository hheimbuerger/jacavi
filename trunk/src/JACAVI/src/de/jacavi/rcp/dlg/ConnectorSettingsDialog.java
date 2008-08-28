package de.jacavi.rcp.dlg;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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
        FormLayout layout = new FormLayout();
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        parent.setLayout(layout);

        Combo comboCar = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0);
        fd1.left = new FormAttachment(0);
        fd1.right = new FormAttachment(50);
        comboCar.setLayoutData(fd1);
        comboCar.add("Car 1");
        comboCar.add("Car 2");
        comboCar.add("Car 3");
        comboCar.add("Car 4");
        comboCar.setText("Car 1");

        Button buttonInitCar = new Button(parent, SWT.PUSH);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(comboCar, 0, SWT.CENTER);
        fd2.left = new FormAttachment(comboCar, 10, SWT.RIGHT);
        buttonInitCar.setLayoutData(fd2);
        buttonInitCar.setText("Initialize this car");
    }

    private void createBlueRiderTab(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        parent.setLayout(layout);

        Combo comboCar = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0);
        fd1.left = new FormAttachment(0);
        fd1.right = new FormAttachment(50);
        comboCar.setLayoutData(fd1);
        comboCar.add("COM1:");
        comboCar.add("COM2:");
        comboCar.add("COM3:");
        comboCar.add("COM4:");
        comboCar.setText("COM2:");
    }

    private void createAnalogueTab(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        parent.setLayout(layout);

        Label labelHost = new Label(parent, SWT.WRAP);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0);
        fd1.left = new FormAttachment(0);
        labelHost.setLayoutData(fd1);
        labelHost.setText("Host");

        Text textHost = new Text(parent, SWT.BORDER | SWT.SINGLE);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(labelHost, 0, SWT.CENTER);
        fd2.left = new FormAttachment(labelHost, 10, SWT.RIGHT);
        fd2.right = new FormAttachment(50);
        textHost.setLayoutData(fd2);
        textHost.setText("192.168.1.1");

        Label labelPort = new Label(parent, SWT.WRAP);
        FormData fd3 = new FormData();
        fd3.top = new FormAttachment(labelHost, 10, SWT.BOTTOM);
        fd3.left = new FormAttachment(labelHost, 0, SWT.LEFT);
        labelPort.setLayoutData(fd3);
        labelPort.setText("Port");

        Text textPort = new Text(parent, SWT.BORDER | SWT.SINGLE);
        FormData fd4 = new FormData();
        fd4.top = new FormAttachment(labelPort, 0, SWT.CENTER);
        fd4.left = new FormAttachment(textHost, 0, SWT.LEFT);
        fd4.right = new FormAttachment(50);
        textPort.setLayoutData(fd4);
        textPort.setText("10001");
    }

    @Override
    protected void fillDeviceList(List deviceList) {
        deviceList.add("Lib42, car 1");
        deviceList.add("Lib42, car 2");
        deviceList.add("Lib42, car 3");
        deviceList.add("BlueRider on COM3");
        deviceList.add("Analogue slot car system, car 1 (192.168.1.1, port 10001)");
        deviceList.add("Analogue slot car system, car 2 (192.168.1.1, port 10001)");
    }

}

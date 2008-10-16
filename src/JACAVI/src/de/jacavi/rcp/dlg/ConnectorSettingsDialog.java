package de.jacavi.rcp.dlg;

import java.net.InetSocketAddress;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.appl.ContextLoader;
import de.jacavi.hal.ConnectorConfigurationManager;
import de.jacavi.hal.ConnectorFactory;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.hal.bluerider.BlueriderDriveConnector;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.widgets.controls.validators.IPV4ValidatedText;
import de.jacavi.rcp.widgets.controls.validators.RangeValidatedText;
import de.jacavi.rcp.widgets.controls.validators.ValidationGroup;



public class ConnectorSettingsDialog extends AbstractSettingsDialog {

    private ConnectorConfigurationManager connectorManager = null;

    private ConnectorFactory connectorFactory = null;

    private Combo comboBlueriderComPort = null;

    private ValidationGroup blueriderValidationGroup = null;

    private ValidationGroup analogueValidationGroup = null;

    private ValidationGroup lib42ValidationGroup = null;

    private Combo comboAnalogueLane = null;

    private Combo lib42CarID = null;

    @Validate(group = "Bluerider", error = "* Host must be an IP adress in Form 1-255.0-255.0-255.0-255")
    private IPV4ValidatedText textBlueriderHost = null;

    @Validate(group = "Bluerider", error = "* Port must be in range of 1 to 65535")
    private RangeValidatedText textBlueriderPort = null;

    @Validate(group = "Analogue", error = "* Host must be an IP adress in Form 1-255.0-255.0-255.0-255")
    private IPV4ValidatedText textHostAnalogue = null;

    @Validate(group = "Analogue", error = "* Port must be in range of 1 to 65535")
    private RangeValidatedText textPortAnalogue = null;

    public ConnectorSettingsDialog(Shell parentShell) {
        super(parentShell);
        // get the conncetorManager
        connectorManager = (ConnectorConfigurationManager) ContextLoader.getBean("connectorManager");
        // get the connectorFactory
        connectorFactory = (ConnectorFactory) ContextLoader.getBean("slotCarSystemConnectorFactory");
        // prepare the images
        imageRegistry.put("imageLib42", Activator.getImageDescriptor("/images/connectors/lib42_64x64.png"));
        imageRegistry.put("imageBluerider", Activator.getImageDescriptor("/images/connectors/bluerider_64x64.png"));
        imageRegistry.put("imageAnalogue", Activator.getImageDescriptor("/images/connectors/analogue_64x64.png"));
        imageRegistry.put("icon", Activator.getImageDescriptor("/images/actions/configure_connectors_16x16.png"));
    }

    @Override
    protected void setDescriptionTexts() {
        getShell().setText("Connector Settings");
        setTitle("Configure slot car systems");
        setMessage("Initialize and configure your slot car systems.", IMessageProvider.INFORMATION);
        super.setDeviceListLabel("List of configured connectors:");
    }

    @Override
    protected void createTabItems(CTabFolder tabFolder) {
        createLib42Tab(prepareTabItem("Lib42", imageRegistry.get("icon"), imageRegistry.get("imageLib42")));
        createBlueRiderTab(prepareTabItem("Bluerider", imageRegistry.get("icon"), imageRegistry.get("imageBluerider")));
        createAnalogueTab(prepareTabItem("Analogue", imageRegistry.get("icon"), imageRegistry.get("imageAnalogue")));
    }

    private void createLib42Tab(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        parent.setLayout(layout);

        lib42CarID = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0);
        fd1.left = new FormAttachment(0);
        fd1.right = new FormAttachment(50);
        lib42CarID.setLayoutData(fd1);
        lib42CarID.add("Car 1");
        lib42CarID.add("Car 2");
        lib42CarID.add("Car 3");
        lib42CarID.add("Car 4");
        lib42CarID.setText("Car 1");

        // create the button
        Button buttonInitCar = new Button(parent, SWT.PUSH);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(lib42CarID, 0, SWT.CENTER);
        fd2.left = new FormAttachment(lib42CarID, 10, SWT.RIGHT);
        buttonInitCar.setLayoutData(fd2);
        buttonInitCar.setText("Initialize this car");
        buttonInitCar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ConnectorSettingsDialog.this.handleClickInitializeLib42(e);
            }
        });

        // create the label describing the setup process
        Label labellib42HelpText = new Label(parent, SWT.WRAP);
        FormData fd7 = new FormData();
        fd7.top = new FormAttachment(lib42CarID, 10);
        fd7.left = new FormAttachment(lib42CarID, 0, SWT.LEFT);
        fd7.right = new FormAttachment(buttonInitCar, 0, SWT.RIGHT);
        labellib42HelpText.setLayoutData(fd7);
        labellib42HelpText
                .setText("Remove all cars from the course except the car you want to configure. Then push \"Initialize this car\".\n\nOn success you will see moving the car a bit.");

        lib42ValidationGroup = new ValidationGroup(parent, SWT.WRAP);
        FormData fd15 = new FormData();
        fd15.top = new FormAttachment(labellib42HelpText, 10);
        fd15.left = new FormAttachment(labellib42HelpText, 0, SWT.LEFT);
        fd15.right = new FormAttachment(labellib42HelpText, 0, SWT.RIGHT);
        fd15.height = 100;
        lib42ValidationGroup.setLayoutData(fd15);

    }

    protected void handleClickInitializeLib42(SelectionEvent e) {
        int carID = lib42CarID.getSelectionIndex() + 1;
        try {
            SlotCarSystemConnector lib42Connector = connectorFactory.createLib42Connector(lib42CarID.getText()
                    + " -> Lib42", carID);
            connectorManager.addConnector(lib42Connector);
            // connectorManager.testSystemConnector(lib42Connector);
            updateDeviceList();
        } catch(Exception ex) {
            lib42ValidationGroup.setText("Could not connect to lib42 on windows");
        }
    }

    private void createBlueRiderTab(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        parent.setLayout(layout);

        // create comboBox Label
        Label comPortLabel = new Label(parent, SWT.WRAP);
        FormData fd3 = new FormData();
        fd3.top = new FormAttachment(0);
        fd3.left = new FormAttachment(0);
        fd3.right = new FormAttachment(50);
        comPortLabel.setLayoutData(fd3);
        comPortLabel.setText("Bluetooth COM Port:");

        comboBlueriderComPort = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(comPortLabel, 10);
        fd1.left = new FormAttachment(comPortLabel, 0, SWT.LEFT);
        fd1.right = new FormAttachment(comPortLabel, 0, SWT.RIGHT);
        comboBlueriderComPort.setLayoutData(fd1);
        for(int i = 1; i <= 20; i++) {
            comboBlueriderComPort.add("COM" + i);
        }
        comboBlueriderComPort.setText("COM1");

        // create analogue feedback shit
        Label labelAnalogue = new Label(parent, SWT.WRAP);
        FormData fd11 = new FormData();
        fd11.top = new FormAttachment(comboBlueriderComPort, 10);
        fd11.left = new FormAttachment(comboBlueriderComPort, 0, SWT.LEFT);
        labelAnalogue.setLayoutData(fd11);
        labelAnalogue.setText("Embedded Device:");

        // create analogue feedback shit
        Label labelHost = new Label(parent, SWT.WRAP);
        FormData fd5 = new FormData();
        fd5.top = new FormAttachment(labelAnalogue, 10);
        fd5.left = new FormAttachment(labelAnalogue, 0, SWT.LEFT);
        labelHost.setLayoutData(fd5);
        labelHost.setText("Host:");

        textBlueriderHost = new IPV4ValidatedText(parent, SWT.BORDER | SWT.SINGLE);
        FormData fd8 = new FormData();
        fd8.top = new FormAttachment(labelHost, 0, SWT.CENTER);
        fd8.left = new FormAttachment(labelHost, 10, SWT.RIGHT);
        fd8.right = new FormAttachment(50);
        textBlueriderHost.setLayoutData(fd8);
        textBlueriderHost.setText("192.168.1.1");

        Label labelPort = new Label(parent, SWT.WRAP);
        FormData fd9 = new FormData();
        fd9.top = new FormAttachment(labelHost, 10, SWT.BOTTOM);
        fd9.left = new FormAttachment(labelHost, 0, SWT.LEFT);
        labelPort.setLayoutData(fd9);
        labelPort.setText("Port:");

        textBlueriderPort = new RangeValidatedText(parent, SWT.BORDER | SWT.SINGLE, 1, 65535);

        FormData fd10 = new FormData();
        fd10.top = new FormAttachment(labelPort, 0, SWT.CENTER);
        fd10.left = new FormAttachment(labelPort, 10, SWT.RIGHT);
        fd10.right = new FormAttachment(50);
        textBlueriderPort.setLayoutData(fd10);
        textBlueriderPort.setText("10001");
        textBlueriderPort.setToolTipText("HALLO TOOLTIP");

        // create the button
        Button buttonConnectBluerider = new Button(parent, SWT.PUSH);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(textBlueriderPort, 0, SWT.CENTER);
        fd2.left = new FormAttachment(textBlueriderPort, 10, SWT.RIGHT);
        buttonConnectBluerider.setLayoutData(fd2);
        buttonConnectBluerider.setText("Connect to Bluerider");
        buttonConnectBluerider.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ConnectorSettingsDialog.this.handleClickConnectBluerider(e);
            }
        });

        // create the label describing the setup process
        Label labelBlueriderHelpText = new Label(parent, SWT.WRAP);
        FormData fd7 = new FormData();
        fd7.top = new FormAttachment(labelPort, 10);
        fd7.left = new FormAttachment(labelPort, 0, SWT.LEFT);
        fd7.right = new FormAttachment(buttonConnectBluerider, 0, SWT.RIGHT);
        labelBlueriderHelpText.setLayoutData(fd7);
        labelBlueriderHelpText
                .setText("Put the Bluerider on the selected lane on the analogue course and push \"Connect to Bluerider\".\n On success Bluerider will move a bit.");
        blueriderValidationGroup = new ValidationGroup(parent, SWT.WRAP);
        FormData fd15 = new FormData();
        fd15.top = new FormAttachment(labelBlueriderHelpText, 10);
        fd15.left = new FormAttachment(labelBlueriderHelpText, 0, SWT.LEFT);
        fd15.right = new FormAttachment(labelBlueriderHelpText, 0, SWT.RIGHT);
        fd15.height = 100;
        blueriderValidationGroup.setLayoutData(fd15);
    }

    protected void handleClickConnectBluerider(SelectionEvent e) {

        if(blueriderValidationGroup.isValid(this, "Bluerider")) {
            String name = "Bluerider on " + comboBlueriderComPort.getText() + " with analogue feedback of "
                    + textBlueriderHost.getText() + ":" + textBlueriderPort.getText();

            SlotCarSystemConnector blueriderConnector = connectorFactory.createBlueriderConnector(name,
                    comboBlueriderComPort.getText(), new InetSocketAddress(textBlueriderHost.getText(), Integer
                            .valueOf(textBlueriderPort.getText())));
            if(((BlueriderDriveConnector) blueriderConnector.getDriveConnector()).connectBlueRider()) {
                connectorManager.addConnector(blueriderConnector);
                // connectorManager.testSystemConnector(blueriderConnector);
            } else {
                blueriderValidationGroup.setText("Could not connect to Bluerider");
            }
        }
        updateDeviceList();
    }

    private void createAnalogueTab(Composite parent) {
        FormLayout layout = new FormLayout();
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.marginTop = 5;
        layout.marginBottom = 5;
        parent.setLayout(layout);

        // create analogue feedback shit
        Label labelAnalogueLane = new Label(parent, SWT.WRAP);
        FormData fd13 = new FormData();
        fd13.top = new FormAttachment(0);
        fd13.left = new FormAttachment(0);
        labelAnalogueLane.setLayoutData(fd13);
        labelAnalogueLane.setText("Lane:");

        comboAnalogueLane = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        FormData fd16 = new FormData();
        fd16.top = new FormAttachment(labelAnalogueLane, 10);
        fd16.left = new FormAttachment(labelAnalogueLane, 0, SWT.LEFT);
        fd16.right = new FormAttachment(50);
        comboAnalogueLane.setLayoutData(fd16);
        comboAnalogueLane.add("Lane 1");
        comboAnalogueLane.add("Lane 2");
        comboAnalogueLane.add("Lane 3");
        comboAnalogueLane.add("Lane 4");
        comboAnalogueLane.setText("Lane 1");

        // create analogue feedback shit
        Label labelAnalogue = new Label(parent, SWT.WRAP);
        FormData fd11 = new FormData();
        fd11.top = new FormAttachment(comboAnalogueLane, 10);
        fd11.left = new FormAttachment(comboAnalogueLane, 0, SWT.LEFT);
        labelAnalogue.setLayoutData(fd11);
        labelAnalogue.setText("Embedded Device:");

        Label labelHost = new Label(parent, SWT.WRAP);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(labelAnalogue, 10);
        fd1.left = new FormAttachment(labelAnalogue, 0, SWT.LEFT);
        labelHost.setLayoutData(fd1);
        labelHost.setText("Host:");

        textHostAnalogue = new IPV4ValidatedText(parent, SWT.BORDER | SWT.SINGLE);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(labelHost, 0, SWT.CENTER);
        fd2.left = new FormAttachment(labelHost, 10, SWT.RIGHT);
        fd2.right = new FormAttachment(50);
        textHostAnalogue.setLayoutData(fd2);
        textHostAnalogue.setText("192.168.1.1");

        Label labelPort = new Label(parent, SWT.WRAP);
        FormData fd3 = new FormData();
        fd3.top = new FormAttachment(labelHost, 10, SWT.BOTTOM);
        fd3.left = new FormAttachment(labelHost, 0, SWT.LEFT);
        labelPort.setLayoutData(fd3);
        labelPort.setText("Port:");

        textPortAnalogue = new RangeValidatedText(parent, SWT.BORDER | SWT.SINGLE, 1, 65535);
        FormData fd4 = new FormData();
        fd4.top = new FormAttachment(labelPort, 0, SWT.CENTER);
        fd4.left = new FormAttachment(textHostAnalogue, 0, SWT.LEFT);
        fd4.right = new FormAttachment(50);
        textPortAnalogue.setLayoutData(fd4);
        textPortAnalogue.setText("10001");

        // create the button
        Button buttonConnectAnalogue = new Button(parent, SWT.PUSH);
        FormData fd6 = new FormData();
        fd6.top = new FormAttachment(textPortAnalogue, 0, SWT.CENTER);
        fd6.left = new FormAttachment(textPortAnalogue, 10, SWT.RIGHT);
        buttonConnectAnalogue.setLayoutData(fd6);
        buttonConnectAnalogue.setText("Create analogue connector");
        buttonConnectAnalogue.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ConnectorSettingsDialog.this.handleClickAnalogueConnector(e);
            }
        });

        // create the label describing the setup process
        Label labelAnalogueHelpText = new Label(parent, SWT.WRAP);
        FormData fd7 = new FormData();
        fd7.top = new FormAttachment(labelPort, 10);
        fd7.left = new FormAttachment(labelPort, 0, SWT.LEFT);
        fd7.right = new FormAttachment(buttonConnectAnalogue, 0, SWT.RIGHT);
        labelAnalogueHelpText.setLayoutData(fd7);
        labelAnalogueHelpText
                .setText("Put your car on the lane assigned to the embedded device configuration and push \"Create analogue connector\". On success the car will move a bit.");

        analogueValidationGroup = new ValidationGroup(parent, SWT.NONE);
        FormData fd15 = new FormData();
        fd15.top = new FormAttachment(labelAnalogueHelpText, 10);
        fd15.left = new FormAttachment(labelAnalogueHelpText, 0, SWT.LEFT);
        fd15.right = new FormAttachment(labelAnalogueHelpText, 0, SWT.RIGHT);
        fd15.height = 100;
        analogueValidationGroup.setLayoutData(fd15);
    }

    protected void handleClickAnalogueConnector(SelectionEvent e) {
        if(analogueValidationGroup.isValid(this, "Analogue")) {
            String name = "Analogue " + comboAnalogueLane.getText() + " on " + textHostAnalogue.getText() + ":"
                    + textPortAnalogue.getText();
            SlotCarSystemConnector analogueConnector = connectorFactory.createAnalogueConnector(name, comboAnalogueLane
                    .getSelectionIndex() + 1, new InetSocketAddress(textHostAnalogue.getText(), Integer
                    .valueOf(textPortAnalogue.getText())));

            // TODO: connect analogue devices

            connectorManager.addConnector(analogueConnector);
            // connectorManager.testSystemConnector(analogueConnector);
        }
        updateDeviceList();
    }

    @Override
    protected void fillDeviceList(List deviceList) {
        for(SlotCarSystemConnector connector: connectorManager.getConnectors()) {
            deviceList.add(connector.getName());
        }
    }

    @Override
    protected void createLowerSection(Composite parent) {
        Button buttonTest = new Button(parent, SWT.NONE);
        buttonTest.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        buttonTest.setText("Test selected connector");
        buttonTest.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ConnectorSettingsDialog.this.handleClickTestConnector(e);
            }
        });
    }

    protected void handleClickTestConnector(SelectionEvent e) {
        String deviceName = getSelectedDevice();
        if(deviceName.equals("")) {
            return;
        }

        for(SlotCarSystemConnector connector: connectorManager.getConnectors()) {
            if(connector.getName().equals(deviceName)) {
                connectorManager.testSystemConnector(connector);
            }
        }
    }
}

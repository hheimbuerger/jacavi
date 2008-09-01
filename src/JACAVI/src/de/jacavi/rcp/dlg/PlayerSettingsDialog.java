package de.jacavi.rcp.dlg;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.script.DrivingAgentController;
import de.jacavi.appl.controller.script.impl.DrivingAgentExample;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.hal.ConnectorConfigurationManager;



/**
 * @author Fabian Rohn
 */
public class PlayerSettingsDialog extends TitleAreaDialog {

    // private static Log log = LogFactory.getLog(PlayerSettingsDialog.class);

    private final String[] inputs = new String[] { "Device", "DrivingAgentExample" };

    private final Player player;

    private Text playerName;

    private Combo comboInput;

    private Combo comboDevices;

    private Combo comboConnectors;

    private final Shell parentShell;

    private Color c;

    private final InputDeviceManager inputDeviceManager;

    private final ConnectorConfigurationManager connectorManager;

    private ComboViewer comboDevicesViewer;

    private ComboViewer comboConnectorsViewer;

    public PlayerSettingsDialog(Shell parentShell, Player player) {
        super(parentShell);
        this.player = player;
        this.parentShell = parentShell;

        inputDeviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");

        connectorManager = (ConnectorConfigurationManager) ContextLoader.getBean("connectorManager");

    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Player Settings");
        setMessage("Please set Player specific options", IMessageProvider.INFORMATION);
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText("Player Settings");

        GridData gdInputs = new GridData();
        gdInputs.widthHint = 150;
        GridLayout layout = new GridLayout();
        layout.marginHeight = 20;
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;

        GridLayout parentlayout = new GridLayout();
        // parentlayout.numColumns = 2;
        parent.setLayout(parentlayout);

        // Player 1
        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setLayout(layout);
        group.setText("Player Settings");
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        CLabel lPlayer1 = new CLabel(group, SWT.NONE);
        lPlayer1.setText("Name:");

        playerName = new Text(group, SWT.BORDER);
        playerName.setLayoutData(gdInputs);
        playerName.setText(player.getName());

        CLabel labelInput1 = new CLabel(group, SWT.NONE);
        labelInput1.setText("Input:");

        comboInput = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        comboInput.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                String value = ((Combo) event.getSource()).getText();
                if(!value.equals("")) {
                    if(((Combo) event.getSource()).getText().equals(inputs[1])) {
                        comboDevices.setEnabled(false);
                        player.setController(new DrivingAgentExample());
                    } else {
                        comboDevices.setEnabled(true);
                    }
                }

            }
        });
        comboInput.setLayoutData(gdInputs);
        comboInput.setItems(inputs);
        CarController controller = player.getController();
        if(controller instanceof DeviceController) {
            comboInput.select(0);
        } else if(controller instanceof DrivingAgentController) {
            comboInput.select(1);
        }

        CLabel labelDevice1 = new CLabel(group, SWT.NONE);
        labelDevice1.setText("Devices:");

        comboDevices = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        comboDevices.setEnabled(comboInput.getSelectionIndex() != 1);
        comboDevices.setLayoutData(gdInputs);
        comboDevicesViewer = new ComboViewer(comboDevices);
        comboDevicesViewer.add(inputDeviceManager.getInputDevices().toArray(
                new Object[inputDeviceManager.getInputDevices().size()]));
        /*
         * comboDevices.setItems(devices.toArray(new String[devices.size()])); for(int i = 0; i < devices.size(); i++) {
         * if(controller instanceof DeviceController) { DeviceController devController = (DeviceController) controller;
         * if(devController.getClass().getSimpleName().equals(devices.get(i))) comboDevices.select(i); } }
         */

        CLabel labelProtocol1 = new CLabel(group, SWT.NONE);
        labelProtocol1.setText("Connector:");

        comboConnectors = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        comboConnectors.setLayoutData(gdInputs);
        comboConnectorsViewer = new ComboViewer(comboConnectors);
        comboConnectorsViewer.add(connectorManager.getConnectors().toArray(
                new Object[connectorManager.getConnectors().size()]));

        CLabel labelColor = new CLabel(group, SWT.NONE);
        labelColor.setText("Color:");

        final Button colorButton = new Button(group, SWT.NONE);
        colorButton.setText("Select Color...");
        colorButton.setLayoutData(gdInputs);
        colorButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ColorDialog dialog = new ColorDialog(parentShell, SWT.BORDER);
                if(player.getColor() != null)
                    dialog.setRGB(player.getColor().getRGB());
                RGB rgb = dialog.open();
                if(rgb != null) {
                    c = new Color(parentShell.getDisplay(), rgb);
                    System.out.println(c);
                    Image image = new Image(parentShell.getDisplay(), 10, 10);
                    GC gc = new GC(image);
                    gc.setBackground(c);
                    gc.fillRectangle(0, 0, 10, 10);
                    gc.dispose();
                    colorButton.setImage(image);
                }
            }
        });

        return parent;
    }

    @Override
    protected void okPressed() {
        player.setName(playerName.getText());

        player.setController(new DrivingAgentExample());
        player.setColor(c);
        super.okPressed();
        comboInput.dispose();

    }
}

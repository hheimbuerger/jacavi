package de.jacavi.rcp.dlg;

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.car.CarRepository;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.script.DrivingAgentController;
import de.jacavi.appl.controller.script.impl.DrivingAgentExample;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.hal.ConnectorConfigurationManager;
import de.jacavi.hal.SlotCarSystemConnector;



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

    private final InputDeviceManager inputDeviceManager;

    private final ConnectorConfigurationManager connectorManager;

    private ComboViewer comboDevicesViewer;

    private ComboViewer comboConnectorsViewer;

    private SlotCarSystemConnector connector;

    private CarController controller;

    private final CarRepository carRepository;

    public PlayerSettingsDialog(Shell parentShell, Player player) {
        super(parentShell);
        this.player = player;

        connector = player.getSlotCarSystemConnector();

        controller = player.getController();

        inputDeviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");

        connectorManager = (ConnectorConfigurationManager) ContextLoader.getBean("connectorManager");

        carRepository = (CarRepository) ContextLoader.getBean("carRepositoryBean");
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
                        controller = new DrivingAgentExample();
                    } else {
                        comboDevices.setEnabled(true);
                    }
                }

            }
        });
        comboInput.setLayoutData(gdInputs);
        comboInput.setItems(inputs);

        CLabel labelDevice1 = new CLabel(group, SWT.NONE);
        labelDevice1.setText("Devices:");

        comboDevices = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        comboDevices.setEnabled(comboInput.getSelectionIndex() != 1);
        comboDevices.setLayoutData(gdInputs);
        comboDevicesViewer = new ComboViewer(comboDevices);
        comboDevicesViewer.add(inputDeviceManager.getInputDevices().toArray(
                new Object[inputDeviceManager.getInputDevices().size()]));
        comboDevices.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int selected = ((Combo) event.getSource()).getSelectionIndex();
                controller = (DeviceController) comboDevicesViewer.getElementAt(selected);
            }
        });

        CLabel labelProtocol1 = new CLabel(group, SWT.NONE);
        labelProtocol1.setText("Connector:");

        comboConnectors = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        comboConnectors.setLayoutData(gdInputs);
        comboConnectorsViewer = new ComboViewer(comboConnectors);
        comboConnectorsViewer.add(connectorManager.getConnectors().toArray(
                new Object[connectorManager.getConnectors().size()]));
        comboConnectors.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int selected = ((Combo) event.getSource()).getSelectionIndex();
                connector = (SlotCarSystemConnector) comboConnectorsViewer.getElementAt(selected);
            }
        });

        CLabel labelColor = new CLabel(group, SWT.NONE);
        labelColor.setText("Car:");

        final Button carButton = new Button(group, SWT.NONE);
        carButton.setText("Select a Car...(Debug)");
        carButton.setLayoutData(gdInputs);
        setContent();
        return parent;
    }

    @Override
    protected void okPressed() {
        player.setName(playerName.getText());
        player.setSlotCarSystemConnector(connector);
        player.setController(controller);
        player.setCar(carRepository.getCars().get(0));
        super.okPressed();
        comboInput.dispose();
        comboDevices.dispose();
        comboConnectors.dispose();

    }

    private void setContent() {
        playerName.setText(player.getName());

        CarController controller = player.getController();
        if(controller instanceof DeviceController) {
            comboInput.select(0);
        } else if(controller instanceof DrivingAgentController) {
            comboInput.select(1);
            comboDevices.setEnabled(false);
        }

        List<DeviceController> availableController = inputDeviceManager.getInputDevices();
        if(player.getController() != null) {
            for(int i = 0; i < availableController.size(); i++) {
                if(availableController.get(i).getId() == player.getController().getId())
                    comboDevices.select(i);
            }
        }

        List<SlotCarSystemConnector> availableConnectors = connectorManager.getConnectors();
        if(player.getSlotCarSystemConnector() != null) {
            for(int i = 0; i < availableConnectors.size(); i++) {
                if(availableConnectors.get(i).getId() == player.getSlotCarSystemConnector().getId())
                    comboConnectors.select(i);
            }
        }
    }
}

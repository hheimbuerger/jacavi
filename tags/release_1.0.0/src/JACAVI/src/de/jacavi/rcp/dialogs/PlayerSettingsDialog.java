package de.jacavi.rcp.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.car.Car;
import de.jacavi.appl.car.CarRepository;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.CarControllerManager;
import de.jacavi.appl.controller.agent.DrivingAgentController;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.hal.ConnectorConfigurationManager;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.rcp.util.ImageCombo;



/**
 * @author Fabian Rohn
 */
public class PlayerSettingsDialog extends TitleAreaDialog {

    private final String[] inputs = new String[] { "Input Device", "Driving Agent" };

    // models / managers
    private final CarControllerManager carControllerManager;

    private final ConnectorConfigurationManager connectorManager;

    private final Player player;

    // view controlls
    private Text playerName;

    private Combo comboController;

    private Combo comboDevices;

    private ComboViewer comboDevicesViewer;

    private Combo comboAgents;

    private AbstractListViewer comboAgentsViewer;

    private Combo comboConnectors;

    private ComboViewer comboConnectorsViewer;

    private ImageCombo comboCar;

    private Label carImagePreview;

    private final CarRepository carRepository;

    public PlayerSettingsDialog(Shell parentShell, Player player) {
        super(parentShell);
        // set the models
        this.player = player;

        carControllerManager = (CarControllerManager) ContextLoader.getBean("carControllerManagerBean");

        connectorManager = (ConnectorConfigurationManager) ContextLoader.getBean("connectorManager");

        carRepository = (CarRepository) ContextLoader.getBean("carRepositoryBean");

    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Configure player");
        setMessage("Set the name, controller, input device or driving agent, connector and car of this player.",
                IMessageProvider.INFORMATION);
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

        // Player configuration group
        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setLayout(layout);
        group.setText("Player Settings");
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        // player name
        CLabel lPlayer1 = new CLabel(group, SWT.NONE);
        lPlayer1.setText("Name:");

        playerName = new Text(group, SWT.BORDER);
        playerName.setLayoutData(gdInputs);

        // The input controller
        CLabel labelInput1 = new CLabel(group, SWT.NONE);
        labelInput1.setText("Controller:");

        comboController = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        comboController.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                PlayerSettingsDialog.this.switchDeviceAgentVisibility(event);
            }
        });
        comboController.setLayoutData(gdInputs);
        comboController.setItems(inputs);

        // device
        CLabel labelDevice1 = new CLabel(group, SWT.NONE);
        labelDevice1.setText("Device:");

        comboDevices = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        // comboDevices.setVisible(comboInput.getSelectionIndex() != 1);
        comboDevices.setLayoutData(gdInputs);
        comboDevicesViewer = new ComboViewer(comboDevices);
        comboDevicesViewer.add(carControllerManager.getUnusedDevices(player).toArray(
                new Object[carControllerManager.getUnusedDevices(player).size()]));

        // agents
        CLabel labelAgent = new CLabel(group, SWT.NONE);
        labelAgent.setText("Agent:");

        comboAgents = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        // comboAgents.setVisible(comboInput.getSelectionIndex() == 1);
        comboAgents.setLayoutData(gdInputs);
        comboAgentsViewer = new ComboViewer(comboAgents);
        comboAgentsViewer.add(carControllerManager.getDrivingAgents().toArray(
                new Object[carControllerManager.getDrivingAgents().size()]));

        // connectors
        CLabel labelProtocol1 = new CLabel(group, SWT.NONE);
        labelProtocol1.setText("Connector:");

        comboConnectors = new Combo(group, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
        comboConnectors.setLayoutData(gdInputs);
        comboConnectorsViewer = new ComboViewer(comboConnectors);
        comboConnectorsViewer.add(connectorManager.getUnusedConnectors(player).toArray(
                new Object[connectorManager.getUnusedConnectors(player).size()]));

        // cars
        CLabel labelColor = new CLabel(group, SWT.NONE);
        labelColor.setText("Car:");

        // create a composite of the car IMageCombo and a preview image
        Composite c = new Composite(group, SWT.NONE);
        GridLayout cl = new GridLayout();
        cl.numColumns = 2;
        cl.marginWidth = 0;
        c.setLayout(cl);
        comboCar = new ImageCombo(c, SWT.READ_ONLY | SWT.BORDER);
        comboCar.setLayoutData(gdInputs);
        // fill ImageCombo
        for(Car car: carRepository.getCars()) {
            // hack to rotate the img 180 dec because its on head :-) why?
            comboCar.add(car.getName(), car.getSwtImage());
        }
        comboCar.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PlayerSettingsDialog.this.carSelectionChanged(event);
            }
        });
        // set an car preview imagebox
        carImagePreview = new Label(c, SWT.IMAGE_PNG);
        // setting players choice
        setContent();
        return parent;
    }

    protected void carSelectionChanged(SelectionEvent event) {
        ImageCombo combo = (ImageCombo) event.getSource();
        Composite c = combo.getParent();
        carImagePreview.setImage(carRepository.getCarByID(comboCar.getText()).getSwtImage());
        carImagePreview.pack(true);
        c.pack(true);
    }

    protected void switchDeviceAgentVisibility(SelectionEvent event) {
        int selectedControllerIndex = ((Combo) event.getSource()).getSelectionIndex();
        String selectedControllerType = comboController.getItem(selectedControllerIndex);

        // Device is selected
        if(selectedControllerType.equals(inputs[0])) {
            comboAgents.setEnabled(false);
            comboDevices.setEnabled(true);
        } else {
            comboAgents.setEnabled(true);
            comboDevices.setEnabled(false);
        }
    }

    @Override
    protected void okPressed() {
        // set player name
        player.setName(playerName.getText());
        // set players CarController
        CarController controller = null;
        if(comboController.getSelectionIndex() == 0) {
            controller = (CarController) comboDevicesViewer.getElementAt(comboDevices.getSelectionIndex());
        } else if(comboController.getSelectionIndex() == 1) {
            controller = (CarController) comboAgentsViewer.getElementAt(comboAgents.getSelectionIndex());
        }
        player.setController(controller);

        // set connector
        SlotCarSystemConnector connector = (SlotCarSystemConnector) comboConnectorsViewer.getElementAt(comboConnectors
                .getSelectionIndex());
        player.setSlotCarSystemConnector(connector);
        // Set the car
        player.setCar(carRepository.getCarByID(comboCar.getText()));
        super.okPressed();
        comboController.dispose();
        comboDevices.dispose();
        comboConnectors.dispose();
        comboAgents.dispose();
        comboCar.dispose();
    }

    /**
     * Set the content of the selected player
     */
    private void setContent() {
        playerName.setText(player.getName());

        CarController controller = player.getController();
        // DeviceController or DrivingAgentController get and set
        if(controller instanceof DeviceController) {
            comboController.select(0);
            comboAgents.setEnabled(false);
            List<DeviceController> availableController = carControllerManager.getUnusedDevices(player);
            if(player.getController() != null) {
                for(int i = 0; i < availableController.size(); i++) {
                    if(availableController.get(i).getId() == player.getController().getId())
                        comboDevices.select(i);
                }
            }
        } else if(controller instanceof DrivingAgentController) {
            comboController.select(1);
            comboDevices.setEnabled(false);
            List<DrivingAgentController> availableController = carControllerManager.getDrivingAgents();
            if(player.getController() != null) {
                for(int i = 0; i < availableController.size(); i++) {
                    if(availableController.get(i).getId() == player.getController().getId())
                        comboAgents.select(i);
                }
            }
        }

        // connector selection
        List<SlotCarSystemConnector> availableConnectors = connectorManager.getUnusedConnectors(player);
        if(player.getSlotCarSystemConnector() != null) {
            for(int i = 0; i < availableConnectors.size(); i++) {
                if(availableConnectors.get(i).getId() == player.getSlotCarSystemConnector().getId())
                    comboConnectors.select(i);
            }
        }

        // car selection
        if(player.getCar() != null) {
            for(TableItem item: comboCar.getItems()) {
                if(item.getText().equals(player.getCar().getName())) {
                    comboCar.setText(player.getCar().getName());
                    Image carImg = carRepository.getCarByID(comboCar.getText()).getSwtImage();
                    carImagePreview.setImage(carImg);
                }
            }
        } else {
            Car defaultCar = carRepository.getCars().get(0);
            comboCar.setText(defaultCar.getName());
            carImagePreview.setImage(defaultCar.getSwtImage());
        }

    }
}

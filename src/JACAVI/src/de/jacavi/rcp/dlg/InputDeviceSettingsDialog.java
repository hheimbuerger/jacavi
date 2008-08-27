package de.jacavi.rcp.dlg;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.device.impl.GameControllerDevice;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDevice;
import de.jacavi.rcp.Activator;



public class InputDeviceSettingsDialog extends AbstractSettingsDialog {

    private final InputDeviceManager inputDeviceManager;

    private final ArrayList<Button> checkboxesKeyboardConfigs = new ArrayList<Button>();

    private List listConnectedWiimotes;

    private List listConnectedDevices;

    private Label labelName;

    private Label labelAxes;

    private Label labelButtons;

    private Label labelCapabilities;

    private ProgressBar thrustGauge;

    private Timer thrustPreviewUpdater;

    private java.util.List<DeviceController> gameControllers;

    private static class DevicePreviewUpdater extends TimerTask {
        private final UUID deviceID;

        private final ProgressBar progressBar;

        private final InputDeviceManager inputDeviceManager;

        public DevicePreviewUpdater(InputDeviceManager inputDeviceManager, UUID deviceID, ProgressBar progressBar) {
            this.inputDeviceManager = inputDeviceManager;
            this.deviceID = deviceID;
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if(inputDeviceManager.isIdValid(deviceID))
                        progressBar.setSelection(inputDeviceManager.getDevice(deviceID).poll().getSpeed());
                }
            });
        }
    }

    public InputDeviceSettingsDialog(Shell parentShell) {
        super(parentShell);

        inputDeviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");

        // prepare the images
        imageManager.put("imageKeyboard", Activator.getImageDescriptor("/icons/input_devices/keyboard.png")
                .createImage());
        imageManager.put("imageMouse", Activator.getImageDescriptor("/icons/input_devices/mouse.png").createImage());
        imageManager.put("imageGameController", Activator
                .getImageDescriptor("/icons/input_devices/game_controller.png").createImage());
        imageManager
                .put("imageWiimote", Activator.getImageDescriptor("/icons/input_devices/wiimote.png").createImage());
        imageManager.put("imageWiimoteButtons", Activator.getImageDescriptor("/icons/wiimote_buttons.png")
                .createImage());
        imageManager.put("icon", Activator.getImageDescriptor("/icons/famfamfam-silk/controller.png").createImage());

        /*imageManager.put("imageKeyboard", new Image(Display.getDefault(), "icons/input_devices/keyboard.png"));
        imageManager.put("imageMouse", new Image(Display.getDefault(), "icons/input_devices/mouse.png"));
        imageManager.put("imageGameController", new Image(Display.getDefault(),
                "icons/input_devices/game_controller.png"));
        imageManager.put("imageWiimote", new Image(Display.getDefault(), "icons/input_devices/wiimote.png"));
        imageManager.put("imageWiimoteButtons", new Image(Display.getDefault(), "icons/wiimote_buttons.png"));
        imageManager.put("icon", new Image(Display.getDefault(), "icons/famfamfam-silk/controller.png"));*/
    }

    @Override
    protected void setDescriptionTexts() {
        getShell().setText("Input Device Settings");
        setTitle("Initialize and configure input devices");
        setMessage("Initialize and configure your input devices.", IMessageProvider.INFORMATION);
    }

    @Override
    protected void createTabItems(CTabFolder tabFolder) {
        createKeyboardSection(prepareTabItem("Keyboard", imageManager.get("icon"), imageManager.get("imageKeyboard")));
        createMouseSection(prepareTabItem("Mouse", imageManager.get("icon"), imageManager.get("imageMouse")));
        createGameControllerSection(prepareTabItem("Game Controller", imageManager.get("icon"), imageManager
                .get("imageGameController")));
        createWiimoteSection(prepareTabItem("Wiimote", imageManager.get("icon"), imageManager.get("imageWiimote")));
    }

    private void createKeyboardSection(Composite groupKeyboard) {
        for(DeviceController d: inputDeviceManager.getInputDevicesByType(KeyboardDevice.class)) {
            Button checkboxKeyboard = new Button(groupKeyboard, SWT.CHECK);
            checkboxKeyboard.setText(d.getName());
            checkboxKeyboard.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
            checkboxKeyboard.setData(d.getId());
            checkboxKeyboard.setSelection(true);
            checkboxesKeyboardConfigs.add(checkboxKeyboard);
        }
        groupKeyboard.pack();
    }

    private void createMouseSection(Composite groupMouse) {
        Label labelMouse = new Label(groupMouse, SWT.WRAP);
        labelMouse.setText("The mouse cannot be configured and is always available.");
        labelMouse.setLayoutData(new GridData(GridData.FILL));
    }

    private void createGameControllerSection(Composite groupGameController) {
        // create the composite used to hold all the inner widgets
        Composite c = new Composite(groupGameController, SWT.NONE);
        c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create the layout manager for laying out the inner widgets
        c.setLayout(new FormLayout());

        if(inputDeviceManager.isGameControllerSupportAvailable()) {
            // create the "Connected devices:" label
            Label labelConnectedDevices = new Label(c, SWT.WRAP);
            FormData fd1 = new FormData();
            fd1.top = new FormAttachment(0);
            fd1.left = new FormAttachment(0);
            labelConnectedDevices.setLayoutData(fd1);
            labelConnectedDevices.setText("Connected devices:");

            thrustGauge = new ProgressBar(c, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
            FormData fd5 = new FormData();
            fd5.top = new FormAttachment(labelConnectedDevices, 10);
            fd5.right = new FormAttachment(100, -10);
            fd5.width = SWT.DEFAULT;
            thrustGauge.setLayoutData(fd5);

            // create the list of detected devices
            listConnectedDevices = new List(c, SWT.BORDER);
            FormData fd2 = new FormData();
            fd2.top = new FormAttachment(labelConnectedDevices, 10);
            fd2.left = new FormAttachment(0);
            fd2.right = new FormAttachment(thrustGauge, -30);
            fd2.height = 80;
            listConnectedDevices.setLayoutData(fd2);
            listConnectedDevices.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleSelectionDetectedGameController(e);
                }
            });

            // create the label "Preview:"
            Label labelThrustGauge = new Label(c, SWT.WRAP);
            FormData fd6 = new FormData();
            fd6.left = new FormAttachment(thrustGauge, 0, SWT.CENTER);
            fd6.bottom = new FormAttachment(thrustGauge, -10);
            labelThrustGauge.setLayoutData(fd6);
            labelThrustGauge.setText("Preview:");

            // create the button to start a new detection
            Button buttonDetectGameController = new Button(c, SWT.PUSH);
            FormData fd4 = new FormData();
            fd4.left = new FormAttachment(0);
            fd4.bottom = new FormAttachment(100);
            buttonDetectGameController.setLayoutData(fd4);
            buttonDetectGameController.setText("Detect game controllers");
            buttonDetectGameController.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleClickGameControllerDetection(e);
                }
            });

            // create the details grid
            Composite details = new Composite(c, SWT.NONE);
            FormData fd3 = new FormData();
            fd3.top = new FormAttachment(listConnectedDevices, 10);
            fd3.left = new FormAttachment(0);
            fd3.right = new FormAttachment(thrustGauge, -30);
            fd3.bottom = new FormAttachment(buttonDetectGameController, -10);
            details.setLayoutData(fd3);

            // create the details stuff
            GridLayout l = new GridLayout(2, false);
            l.verticalSpacing = 3;
            l.horizontalSpacing = 20;
            details.setLayout(l);
            Label labelNameDesc = new Label(details, SWT.WRAP);
            labelNameDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING, false, false));
            labelNameDesc.setText("Name:");
            labelName = new Label(details, SWT.WRAP);
            labelName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelName.setText("");
            Label labelAxesDesc = new Label(details, SWT.WRAP);
            labelAxesDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING, false, false));
            labelAxesDesc.setText("Axes:");
            labelAxes = new Label(details, SWT.WRAP);
            labelAxes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelAxes.setText("");
            Label labelButtonsDesc = new Label(details, SWT.WRAP);
            labelButtonsDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING, false, false));
            labelButtonsDesc.setText("Buttons:");
            labelButtons = new Label(details, SWT.WRAP);
            labelButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelButtons.setText("");
            Label labelCapabilitiesDesc = new Label(details, SWT.WRAP);
            labelCapabilitiesDesc
                    .setLayoutData(new GridData(GridData.FILL_HORIZONTAL, GridData.BEGINNING, false, false));
            labelCapabilitiesDesc.setText("Capabilities:");
            labelCapabilities = new Label(details, SWT.WRAP);
            labelCapabilities.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
            labelCapabilities.setText("");

            // update the device list
            handleClickGameControllerDetection(null);
        } else {
            Label labelGameControllerError = new Label(c, SWT.WRAP);
            labelGameControllerError
                    .setText("Game Controller support could not be initialized, please check the logs.");
            labelGameControllerError.setLayoutData(new FormData());
        }
    }

    private void createWiimoteSection(Composite groupWiimote) {
        // create the composite used to hold all the inner widgets
        Composite c = new Composite(groupWiimote, SWT.NONE);
        c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create the layout manager for laying out the inner widgets
        GridLayout l = new GridLayout(1, true);
        l.verticalSpacing = 5;
        c.setLayout(l);

        if(inputDeviceManager.isWiimoteSupportAvailable()) {
            // create the "Connected devices:" label
            Label labelConnectedWiimotes = new Label(c, SWT.WRAP);
            labelConnectedWiimotes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelConnectedWiimotes.setText("Connected devices:");

            listConnectedWiimotes = new org.eclipse.swt.widgets.List(c, SWT.BORDER);
            GridData listLayout = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
            listLayout.widthHint = 100;
            listLayout.heightHint = 50;
            listConnectedWiimotes.setLayoutData(listLayout);
            for(DeviceController d: inputDeviceManager.getInputDevicesByType(WiimoteDevice.class))
                listConnectedWiimotes.add(d.getName());

            // create the label describing the setup process (part 1)
            Label labelWiimoteInfo1 = new Label(c, SWT.WRAP);
            labelWiimoteInfo1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelWiimoteInfo1
                    .setText("Redetecting will drop all existing connections. To redetect, pair all devices via Bluetooth, then hold");

            // create the label used to show the bitmap with the two buttons
            Label labelWiimoteButtons = new Label(c, SWT.NONE);
            labelWiimoteButtons.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false));
            labelWiimoteButtons.setImage(imageManager.get("imageWiimoteButtons"));

            // create the label describing the setup process (part 2)
            Label labelWiimoteInfo2 = new Label(c, SWT.WRAP);
            labelWiimoteInfo2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelWiimoteInfo2.setText("on all devices while clicking the button below.");

            // create the button to start a new detection
            Button buttonDetectWiimotes = new Button(c, SWT.PUSH);
            buttonDetectWiimotes.setLayoutData(new GridData(GridData.FILL, GridData.END, true, true));
            buttonDetectWiimotes.setText("Detect Wii remotes");
            buttonDetectWiimotes.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleClickWiimoteDetection(e);
                }
            });
        } else {
            Label labelWiimoteError = new Label(c, SWT.WRAP);
            labelWiimoteError.setText("Wiimote support could not be initialized, please check the logs.");
            labelWiimoteError.setLayoutData(new GridData(SWT.FILL));
        }
    }

    protected void handleClickWiimoteDetection(SelectionEvent e) {
        // start the redetection
        inputDeviceManager.redetectWiimotes();
        updateDeviceList();

        // update the list
        listConnectedWiimotes.removeAll();
        for(DeviceController d: inputDeviceManager.getInputDevicesByType(WiimoteDevice.class))
            listConnectedWiimotes.add(d.getName());
    }

    protected void handleClickGameControllerDetection(SelectionEvent e) {
        // cancel the timer
        if(thrustPreviewUpdater != null)
            thrustPreviewUpdater.cancel();

        // start the redetection
        inputDeviceManager.redetectGameControllers();
        updateDeviceList();
        gameControllers = inputDeviceManager.getInputDevicesByType(GameControllerDevice.class);

        // update the list
        listConnectedDevices.removeAll();
        for(DeviceController d: gameControllers)
            listConnectedDevices.add(d.getName());
        handleSelectionDetectedGameController(e);
    }

    protected void handleSelectionDetectedGameController(SelectionEvent e) {
        if(thrustPreviewUpdater != null)
            thrustPreviewUpdater.cancel();
        int selectionIndex = listConnectedDevices.getSelectionIndex();
        if(gameControllers != null && selectionIndex >= 0 && selectionIndex <= gameControllers.size()) {
            GameControllerDevice gameController = (GameControllerDevice) gameControllers.get(selectionIndex);
            labelName.setText(gameController.getName());
            labelAxes.setText(String.valueOf(gameController.getNumAxes()));
            labelButtons.setText(String.valueOf(gameController.getNumButtons()));
            StringBuffer capabilities = new StringBuffer();
            for(String capability: gameController.getCapabilities()) {
                if(capabilities.length() > 0)
                    capabilities.append(", ");
                capabilities.append(capability);
            }
            labelCapabilities.setText(capabilities.toString());
            thrustPreviewUpdater = new Timer("thrustPreviewUpdater");
            thrustPreviewUpdater.schedule(new DevicePreviewUpdater(inputDeviceManager, gameController.getId(),
                    thrustGauge), 50, 50);
        } else {
            labelName.setText("");
            labelAxes.setText("");
            labelButtons.setText("");
            labelCapabilities.setText("");
            thrustGauge.setSelection(0);
        }
    }

    @Override
    protected void okPressed() {
        if(thrustPreviewUpdater != null)
            thrustPreviewUpdater.cancel();

        // FIXME: just a hack to make some changes available for testing
        inputDeviceManager.removeInputDevicesByType(KeyboardDevice.class);
        for(Button b: checkboxesKeyboardConfigs) {
            if(b.getSelection())
                inputDeviceManager.addInputDevice(new KeyboardDevice(b.getText()));
        }

        super.okPressed();
    }

    @Override
    protected void fillDeviceList(List deviceList) {
        for(DeviceController dc: inputDeviceManager.getInputDevices())
            deviceList.add(dc.getName());
    }

    /*public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        InputDeviceSettingsDialog dlg = new InputDeviceSettingsDialog(shell);
        dlg.open();
        display.dispose();
    }*/
}

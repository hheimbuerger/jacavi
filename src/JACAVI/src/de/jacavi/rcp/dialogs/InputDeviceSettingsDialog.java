package de.jacavi.rcp.dialogs;

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
import de.jacavi.appl.controller.CarControllerManager;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.impl.GameControllerDevice;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.controller.device.impl.MouseKeyboardDeviceAdapter;
import de.jacavi.appl.controller.device.impl.WiimoteDevice;
import de.jacavi.rcp.Activator;



public class InputDeviceSettingsDialog extends AbstractSettingsDialog {

    private final CarControllerManager carControllerManager;

    private Label labelGameControllerName;

    private Label labelAxes;

    private Label labelButtons;

    private Label labelCapabilities;

    private java.util.List<DeviceController> gameControllers;

    private List listConnectedGameControllers;

    private ProgressBar gameControllerThrustGauge;

    private Timer previewUpdater;

    private List listConnectedWiimotes;

    private ProgressBar wiimoteThrustGauge;

    private java.util.List<DeviceController> wiimotes;

    private ProgressBar keyboardThrustGauge;

    private List listKeyboardLayouts;

    private KeyboardDevice currentKeyboardLayout;

    private Button buttonTestKeyboardLayout;

    private ProgressBar mouseThrustGauge;

    private Button buttonTestMouseLayout;

    private MouseKeyboardDeviceAdapter currentMouse;

    private static class DevicePreviewUpdater extends TimerTask {
        private final UUID deviceID;

        private final ProgressBar progressBar;

        private final CarControllerManager carControllerManager;

        public DevicePreviewUpdater(CarControllerManager inputDeviceManager, UUID deviceID, ProgressBar progressBar) {
            this.carControllerManager = inputDeviceManager;
            this.deviceID = deviceID;
            this.progressBar = progressBar;
        }

        @Override
        public void run() {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if(!progressBar.isDisposed() && carControllerManager.isIdValid(deviceID)) {
                        ControllerSignal signal = carControllerManager.getDevice(deviceID).poll();
                        progressBar.setSelection(signal.getThrust());
                        progressBar.setForeground(signal.isTrigger() ? Display.getDefault().getSystemColor(
                                SWT.COLOR_DARK_MAGENTA) : null);
                    }
                }
            });
        }
    }

    public InputDeviceSettingsDialog(Shell parentShell) {
        super(parentShell);

        carControllerManager = (CarControllerManager) ContextLoader.getBean("carControllerManagerBean");

        // prepare the images
        imageRegistry.put("imageKeyboard", Activator.getImageDescriptor("/images/controller/keyboard_64x64.png"));
        imageRegistry.put("imageMouse", Activator.getImageDescriptor("/images/controller/mouse_64x64.png"));
        imageRegistry.put("imageGameController", Activator
                .getImageDescriptor("/images/controller/game_controller_64x64.png"));
        imageRegistry.put("imageWiimote", Activator.getImageDescriptor("/images/controller/wiimote_64x64.png"));
        imageRegistry
                .put("imageWiimoteButtons", Activator.getImageDescriptor("/images/misc/wiimote_buttons_72x29.png"));
        imageRegistry.put("icon", Activator.getImageDescriptor("/images/actions/configure_controllers_16x16.png"));
    }

    @Override
    public boolean close() {
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }

        return super.close();
    }

    @Override
    protected void setDescriptionTexts() {
        getShell().setText("Input Device Settings");
        setTitle("Configure input devices");
        setMessage("Initialize and configure your input devices.", IMessageProvider.INFORMATION);
    }

    @Override
    protected void createTabItems(CTabFolder tabFolder) {
        createKeyboardSection(prepareTabItem("Keyboard", imageRegistry.get("icon"), imageRegistry.get("imageKeyboard")));
        createMouseSection(prepareTabItem("Mouse", imageRegistry.get("icon"), imageRegistry.get("imageMouse")));
        createGameControllerSection(prepareTabItem("Game Controller", imageRegistry.get("icon"), imageRegistry
                .get("imageGameController")));
        createWiimoteSection(prepareTabItem("Wiimote", imageRegistry.get("icon"), imageRegistry.get("imageWiimote")));
    }

    private void createKeyboardSection(Composite c) {
        // create the layout manager for laying out the inner widgets
        c.setLayout(new FormLayout());

        // create the "Connected devices:" label
        Label labelConnectedDevices = new Label(c, SWT.WRAP);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0, 5);
        fd1.left = new FormAttachment(0, 5);
        labelConnectedDevices.setLayoutData(fd1);
        labelConnectedDevices.setText("Keyboard layouts:");

        keyboardThrustGauge = new ProgressBar(c, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
        FormData fd5 = new FormData();
        fd5.top = new FormAttachment(labelConnectedDevices, 10);
        fd5.right = new FormAttachment(100, -20);
        fd5.width = SWT.DEFAULT;
        keyboardThrustGauge.setLayoutData(fd5);

        // create the list of detected devices
        listKeyboardLayouts = new List(c, SWT.BORDER);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(labelConnectedDevices, 10);
        fd2.left = new FormAttachment(labelConnectedDevices, 0, SWT.LEFT);
        fd2.right = new FormAttachment(keyboardThrustGauge, -30);
        fd2.height = 80;
        listKeyboardLayouts.setLayoutData(fd2);
        listKeyboardLayouts.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                InputDeviceSettingsDialog.this.handleSelectionKeyboardLayout(e);
            }
        });

        // create the label "Preview:"
        Label labelThrustGauge = new Label(c, SWT.WRAP);
        FormData fd6 = new FormData();
        fd6.left = new FormAttachment(keyboardThrustGauge, 0, SWT.CENTER);
        fd6.bottom = new FormAttachment(keyboardThrustGauge, -10);
        labelThrustGauge.setLayoutData(fd6);
        labelThrustGauge.setText("Preview:");

        // create the preview/stop previewing button
        buttonTestKeyboardLayout = new Button(c, SWT.PUSH);
        FormData fd7 = new FormData();
        fd7.left = new FormAttachment(listKeyboardLayouts, 0, SWT.LEFT);
        fd7.top = new FormAttachment(listKeyboardLayouts, 10, SWT.BOTTOM);
        fd7.width = 150;
        buttonTestKeyboardLayout.setLayoutData(fd7);
        buttonTestKeyboardLayout.setText("Preview");
        buttonTestKeyboardLayout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                InputDeviceSettingsDialog.this.handleClickPreviewKeyboardLayout(e);
            }
        });

        // create a placeholder for future controls
        /*        Label labelDEBUGPlaceholder = new Label(c, SWT.WRAP);
                FormData fd8 = new FormData();
                fd8.left = new FormAttachment(buttonTestKeyboardLayout, 0, SWT.LEFT);
                fd8.top = new FormAttachment(buttonTestKeyboardLayout, 10, SWT.BOTTOM);
                labelDEBUGPlaceholder.setLayoutData(fd8);
                labelDEBUGPlaceholder.setText("[---------------------------------]");*/

        // fill the list with the initial layouts
        for(DeviceController d: carControllerManager.getInputDevicesByType(KeyboardDevice.class)) {
            listKeyboardLayouts.add(d.getName());
        }
    }

    private void createMouseSection(Composite c) {
        // create the layout manager for laying out the inner widgets
        c.setLayout(new FormLayout());

        // create the "Connected devices:" label
        Label labelMouse = new Label(c, SWT.WRAP);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0, 5);
        fd1.left = new FormAttachment(0, 5);
        labelMouse.setLayoutData(fd1);
        labelMouse.setText("The mouse cannot be configured and is always available.");

        // create the preview gauge
        mouseThrustGauge = new ProgressBar(c, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
        FormData fd5 = new FormData();
        fd5.top = new FormAttachment(labelMouse, 10, SWT.BOTTOM);
        fd5.right = new FormAttachment(100, -20);
        fd5.width = SWT.DEFAULT;
        mouseThrustGauge.setLayoutData(fd5);

        // create the "Connected devices:" label
        Label labelMouseDescription = new Label(c, SWT.WRAP);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(labelMouse, 10, SWT.BOTTOM);
        fd2.left = new FormAttachment(labelMouse, 0, SWT.LEFT);
        fd2.right = new FormAttachment(mouseThrustGauge, -30, SWT.LEFT);
        labelMouseDescription.setLayoutData(fd2);
        labelMouseDescription
                .setText("Hold the left mouse button while moving the mouse up or down to change the thrust. Hold the right mouse button to activate the lane change trigger.");

        // create the label "Preview:"
        Label labelThrustGauge = new Label(c, SWT.WRAP);
        FormData fd6 = new FormData();
        fd6.left = new FormAttachment(mouseThrustGauge, 0, SWT.CENTER);
        fd6.bottom = new FormAttachment(mouseThrustGauge, -10);
        labelThrustGauge.setLayoutData(fd6);
        labelThrustGauge.setText("Preview:");

        // create the preview/stop previewing button
        buttonTestMouseLayout = new Button(c, SWT.PUSH);
        FormData fd7 = new FormData();
        fd7.left = new FormAttachment(labelMouseDescription, 0, SWT.LEFT);
        fd7.top = new FormAttachment(labelMouseDescription, 10, SWT.BOTTOM);
        fd7.width = 150;
        buttonTestMouseLayout.setLayoutData(fd7);
        buttonTestMouseLayout.setText("Preview");
        buttonTestMouseLayout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                InputDeviceSettingsDialog.this.handleClickPreviewMouse(e, false);
            }
        });
    }

    private void createGameControllerSection(Composite c) {
        // create the layout manager for laying out the inner widgets
        c.setLayout(new FormLayout());

        if(carControllerManager.isGameControllerSupportAvailable()) {
            // create the "Connected devices:" label
            Label labelConnectedDevices = new Label(c, SWT.WRAP);
            FormData fd1 = new FormData();
            fd1.top = new FormAttachment(0, 5);
            fd1.left = new FormAttachment(0, 5);
            labelConnectedDevices.setLayoutData(fd1);
            labelConnectedDevices.setText("Connected devices:");

            gameControllerThrustGauge = new ProgressBar(c, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
            FormData fd5 = new FormData();
            fd5.top = new FormAttachment(labelConnectedDevices, 10);
            fd5.right = new FormAttachment(100, -20);
            fd5.width = SWT.DEFAULT;
            gameControllerThrustGauge.setLayoutData(fd5);

            // create the list of detected devices
            listConnectedGameControllers = new List(c, SWT.BORDER);
            FormData fd2 = new FormData();
            fd2.top = new FormAttachment(labelConnectedDevices, 10);
            fd2.left = new FormAttachment(labelConnectedDevices, 0, SWT.LEFT);
            fd2.right = new FormAttachment(gameControllerThrustGauge, -30);
            fd2.height = 80;
            listConnectedGameControllers.setLayoutData(fd2);
            listConnectedGameControllers.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleSelectionDetectedGameController(e);
                }
            });

            // create the label "Preview:"
            Label labelThrustGauge = new Label(c, SWT.WRAP);
            FormData fd6 = new FormData();
            fd6.left = new FormAttachment(gameControllerThrustGauge, 0, SWT.CENTER);
            fd6.bottom = new FormAttachment(gameControllerThrustGauge, -10);
            labelThrustGauge.setLayoutData(fd6);
            labelThrustGauge.setText("Preview:");

            // create the button to start a new detection
            Button buttonDetectGameController = new Button(c, SWT.PUSH);
            FormData fd4 = new FormData();
            fd4.left = new FormAttachment(labelConnectedDevices, 0, SWT.LEFT);
            fd4.bottom = new FormAttachment(100, -5);
            buttonDetectGameController.setLayoutData(fd4);
            buttonDetectGameController.setText("Redetect game controllers");
            buttonDetectGameController.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleClickGameControllerDetection(e);
                }
            });

            // create the details grid
            Composite details = new Composite(c, SWT.NONE);
            FormData fd3 = new FormData();
            fd3.top = new FormAttachment(listConnectedGameControllers, 10);
            fd3.left = new FormAttachment(labelConnectedDevices, 0, SWT.LEFT);
            fd3.right = new FormAttachment(gameControllerThrustGauge, -30);
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
            labelGameControllerName = new Label(details, SWT.WRAP);
            labelGameControllerName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelGameControllerName.setText("");
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

    private void createWiimoteSection(Composite c) {
        // create the layout manager for laying out the inner widgets
        c.setLayout(new FormLayout());

        if(carControllerManager.isWiimoteSupportAvailable()) {
            // create the "Connected devices:" label
            Label labelConnectedDevices = new Label(c, SWT.WRAP);
            FormData fd1 = new FormData();
            fd1.top = new FormAttachment(0, 5);
            fd1.left = new FormAttachment(0, 5);
            labelConnectedDevices.setLayoutData(fd1);
            labelConnectedDevices.setText("Connected devices:");

            wiimoteThrustGauge = new ProgressBar(c, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);
            FormData fd5 = new FormData();
            fd5.top = new FormAttachment(labelConnectedDevices, 10);
            fd5.right = new FormAttachment(100, -20);
            fd5.width = SWT.DEFAULT;
            wiimoteThrustGauge.setLayoutData(fd5);

            listConnectedWiimotes = new List(c, SWT.BORDER);
            FormData fd2 = new FormData();
            fd2.top = new FormAttachment(labelConnectedDevices, 10);
            fd2.left = new FormAttachment(labelConnectedDevices, 0, SWT.LEFT);
            fd2.right = new FormAttachment(wiimoteThrustGauge, -30);
            fd2.height = 80;
            listConnectedWiimotes.setLayoutData(fd2);
            listConnectedWiimotes.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleSelectionDetectedWiimote(e);
                }
            });

            // create the label "Preview:"
            Label labelThrustGauge = new Label(c, SWT.WRAP);
            FormData fd6 = new FormData();
            fd6.left = new FormAttachment(wiimoteThrustGauge, 0, SWT.CENTER);
            fd6.bottom = new FormAttachment(wiimoteThrustGauge, -10);
            labelThrustGauge.setLayoutData(fd6);
            labelThrustGauge.setText("Preview:");

            // create the button to start a new detection
            Button buttonDetectGameController = new Button(c, SWT.PUSH);
            FormData fd4 = new FormData();
            fd4.left = new FormAttachment(listConnectedWiimotes, 0, SWT.LEFT);
            fd4.bottom = new FormAttachment(100, -5);
            buttonDetectGameController.setLayoutData(fd4);
            buttonDetectGameController.setText("Redetect Wiimotes");
            buttonDetectGameController.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleClickWiimoteDetection(e);
                }
            });

            // create the label describing the setup process (part 1)
            Label labelWiimoteInfo1 = new Label(c, SWT.WRAP);
            FormData fd7 = new FormData();
            fd7.top = new FormAttachment(listConnectedWiimotes, 10);
            fd7.left = new FormAttachment(listConnectedWiimotes, 0, SWT.LEFT);
            fd7.right = new FormAttachment(listConnectedWiimotes, 0, SWT.RIGHT);
            labelWiimoteInfo1.setLayoutData(fd7);
            labelWiimoteInfo1
                    .setText("Redetecting will drop all existing connections. To redetect, pair all devices via Bluetooth, then hold");

            // create the label used to show the bitmap with the two buttons
            Label labelWiimoteButtons = new Label(c, SWT.NONE);
            FormData fd8 = new FormData();
            fd8.top = new FormAttachment(labelWiimoteInfo1, 5);
            fd8.left = new FormAttachment(listConnectedWiimotes, 0, SWT.CENTER);
            labelWiimoteButtons.setLayoutData(fd8);
            labelWiimoteButtons.setImage(imageRegistry.get("imageWiimoteButtons"));

            // create the label describing the setup process (part 2)
            Label labelWiimoteInfo2 = new Label(c, SWT.WRAP);
            FormData fd9 = new FormData();
            fd9.top = new FormAttachment(labelWiimoteButtons, 5);
            fd9.left = new FormAttachment(listConnectedWiimotes, 0, SWT.LEFT);
            fd9.right = new FormAttachment(listConnectedWiimotes, 0, SWT.RIGHT);
            labelWiimoteInfo2.setLayoutData(fd9);
            labelWiimoteInfo2.setText("on all devices while clicking the button below.");

            // update the device list
            handleClickWiimoteDetection(null);
        } else {
            Label labelWiimoteError = new Label(c, SWT.WRAP);
            labelWiimoteError.setText("Wiimote support could not be initialized, please check the logs.");
            labelWiimoteError.setLayoutData(new FormData());
        }
    }

    protected void handleClickGameControllerDetection(SelectionEvent e) {
        // cancel the timer
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }

        // start the redetection
        carControllerManager.redetectGameControllers();
        updateDeviceList();
        gameControllers = carControllerManager.getInputDevicesByType(GameControllerDevice.class);

        // update the list
        listConnectedGameControllers.removeAll();
        for(DeviceController d: gameControllers) {
            listConnectedGameControllers.add(d.getName());
        }
        handleSelectionDetectedGameController(e);
    }

    protected void handleClickWiimoteDetection(SelectionEvent e) {
        // cancel the timer
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }

        // start the redetection
        carControllerManager.redetectWiimotes();
        updateDeviceList();
        wiimotes = carControllerManager.getInputDevicesByType(WiimoteDevice.class);

        // update the list
        listConnectedWiimotes.removeAll();
        for(DeviceController d: carControllerManager.getInputDevicesByType(WiimoteDevice.class)) {
            listConnectedWiimotes.add(d.getName());
        }
        handleSelectionDetectedWiimote(e);
    }

    protected void handleSelectionKeyboardLayout(SelectionEvent e) {
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }
        if(currentKeyboardLayout != null) {
            currentKeyboardLayout.deactivate();
        }
        currentKeyboardLayout = null;

        int selectionIndex = listKeyboardLayouts.getSelectionIndex();
        java.util.List<DeviceController> keyboardDevices = carControllerManager
                .getInputDevicesByType(KeyboardDevice.class);
        if(selectionIndex >= 0 && selectionIndex <= keyboardDevices.size()) {
            buttonTestKeyboardLayout.setEnabled(true);
        } else {
            buttonTestKeyboardLayout.setEnabled(false);
        }
        buttonTestKeyboardLayout.setText("Preview");
        keyboardThrustGauge.setSelection(0);
    }

    protected void handleClickPreviewKeyboardLayout(SelectionEvent e) {
        if(currentKeyboardLayout != null) { // user clicked 'stop previewing'
            if(previewUpdater != null) {
                previewUpdater.cancel();
            }
            if(currentKeyboardLayout != null) {
                currentKeyboardLayout.deactivate();
            }
            currentKeyboardLayout = null;
            keyboardThrustGauge.setSelection(0);
            buttonTestKeyboardLayout.setText("Preview");
        } else { // user clicked 'preview'
            int selectionIndex = listKeyboardLayouts.getSelectionIndex();
            java.util.List<DeviceController> keyboardDevices = carControllerManager
                    .getInputDevicesByType(KeyboardDevice.class);
            if(selectionIndex >= 0 && selectionIndex <= keyboardDevices.size()) {
                currentKeyboardLayout = (KeyboardDevice) keyboardDevices.get(selectionIndex);
                currentKeyboardLayout.activate(null, null);
                previewUpdater = new Timer("previewUpdater");
                previewUpdater.schedule(new DevicePreviewUpdater(carControllerManager, currentKeyboardLayout.getId(),
                        keyboardThrustGauge), 50, 50);
                buttonTestKeyboardLayout.setText("Stop previewing");
            }
        }
    }

    protected void handleClickPreviewMouse(SelectionEvent e, boolean forceStop) {
        if(currentMouse != null || forceStop) { // user clicked 'stop previewing'
            if(previewUpdater != null) {
                previewUpdater.cancel();
            }
            if(currentMouse != null) {
                currentMouse.deactivate();
            }
            currentMouse = null;
            mouseThrustGauge.setSelection(0);
            buttonTestMouseLayout.setText("Preview");
        } else { // user clicked 'preview'
            java.util.List<DeviceController> mouseDevices = carControllerManager
                    .getInputDevicesByType(MouseKeyboardDeviceAdapter.class);
            if(mouseDevices.size() > 0) {
                currentMouse = (MouseKeyboardDeviceAdapter) mouseDevices.get(0);
                currentMouse.activate(null, null);
                previewUpdater = new Timer("previewUpdater");
                previewUpdater.schedule(new DevicePreviewUpdater(carControllerManager, currentMouse.getId(),
                        mouseThrustGauge), 50, 50);
                buttonTestMouseLayout.setText("Stop previewing");
            }
        }
    }

    protected void handleSelectionDetectedGameController(SelectionEvent e) {
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }
        if(listConnectedGameControllers != null) {
            int selectionIndex = listConnectedGameControllers.getSelectionIndex();
            if(gameControllers != null && selectionIndex >= 0 && selectionIndex <= gameControllers.size()) {
                GameControllerDevice gameController = (GameControllerDevice) gameControllers.get(selectionIndex);
                labelGameControllerName.setText(gameController.getName());
                labelAxes.setText(String.valueOf(gameController.getNumAxes()));
                labelButtons.setText(String.valueOf(gameController.getNumButtons()));
                StringBuffer capabilities = new StringBuffer();
                for(String capability: gameController.getCapabilities()) {
                    if(capabilities.length() > 0) {
                        capabilities.append(", ");
                    }
                    capabilities.append(capability);
                }
                labelCapabilities.setText(capabilities.toString());
                previewUpdater = new Timer("previewUpdater");
                previewUpdater.schedule(new DevicePreviewUpdater(carControllerManager, gameController.getId(),
                        gameControllerThrustGauge), 50, 50);
            } else {
                labelGameControllerName.setText("");
                labelAxes.setText("");
                labelButtons.setText("");
                labelCapabilities.setText("");
                gameControllerThrustGauge.setSelection(0);
            }
        }
    }

    protected void handleSelectionDetectedWiimote(SelectionEvent e) {
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }
        if(listConnectedWiimotes != null) {
            int selectionIndex = listConnectedWiimotes.getSelectionIndex();
            if(gameControllers != null && selectionIndex >= 0 && selectionIndex <= wiimotes.size()) {
                WiimoteDevice wiimote = (WiimoteDevice) wiimotes.get(selectionIndex);
                previewUpdater = new Timer("wiimotePreviewUpdater");
                previewUpdater.schedule(new DevicePreviewUpdater(carControllerManager, wiimote.getId(),
                        wiimoteThrustGauge), 50, 50);
            } else {
                wiimoteThrustGauge.setSelection(0);
            }
        }
    }

    @Override
    protected void handleTabSelection(SelectionEvent e) {
        // cancel the timer
        if(previewUpdater != null) {
            previewUpdater.cancel();
        }

        // remove all selections
        listKeyboardLayouts.setSelection(-1);
        if(listConnectedGameControllers != null) {
            listConnectedGameControllers.setSelection(-1);
        }
        if(listConnectedWiimotes != null) {
            listConnectedWiimotes.setSelection(-1);
        }

        // trigger the event handlers
        handleSelectionKeyboardLayout(null);
        handleClickPreviewMouse(null, true);
        handleSelectionDetectedGameController(null);
        handleSelectionDetectedWiimote(null);
    }

    @Override
    protected void fillDeviceList(List deviceList) {
        for(DeviceController dc: carControllerManager.getInputDevices()) {
            deviceList.add(dc.getName());
        }
    }

    @Override
    protected void createLowerSection(Composite parent) {
    // DO NOTHING here
    }
}

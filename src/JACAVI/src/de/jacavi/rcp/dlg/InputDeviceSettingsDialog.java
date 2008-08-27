package de.jacavi.rcp.dlg;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.device.impl.GameControllerDevice;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDevice;
import de.jacavi.appl.controller.device.impl.GameControllerDeviceManager.GameControllerDescriptor;
import de.jacavi.rcp.Activator;



public class InputDeviceSettingsDialog extends AbstractSettingsDialog {

    private final InputDeviceManager inputDeviceManager;

    private final ArrayList<Button> checkboxesKeyboardConfigs = new ArrayList<Button>();

    private List listConnectedWiimotes;

    private List listConnectedGameControllers;

    private GameControllerDescriptor[] gameControllers;

    private Label labelGameControllerName;

    private Label labelGameControllerAxes;

    private Label labelGameControllerButtons;

    private Label labelGameControllerCapabilities;

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
        GridLayout l = new GridLayout(1, true);
        l.verticalSpacing = 5;
        c.setLayout(l);

        if(inputDeviceManager.isGameControllerSupportAvailable()) {
            // create the "Connected devices:" label
            Label labelConnectedWiimotes = new Label(c, SWT.WRAP);
            labelConnectedWiimotes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelConnectedWiimotes.setText("Connected devices:");

            // create the list of detected devices
            listConnectedGameControllers = new org.eclipse.swt.widgets.List(c, SWT.BORDER);
            GridData listLayout = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
            // listLayout.widthHint = 100;
            listLayout.heightHint = 100;
            listConnectedGameControllers.setLayoutData(listLayout);
            listConnectedGameControllers.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleSelectionDetectedGameController(e);
                }
            });
            for(DeviceController d: inputDeviceManager.getInputDevicesByType(GameControllerDevice.class))
                listConnectedGameControllers.add(d.getName());

            labelGameControllerName = new Label(c, SWT.WRAP);
            labelGameControllerName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelGameControllerName.setText("");
            labelGameControllerAxes = new Label(c, SWT.WRAP);
            labelGameControllerAxes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelGameControllerAxes.setText("");
            labelGameControllerButtons = new Label(c, SWT.WRAP);
            labelGameControllerButtons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelGameControllerButtons.setText("");
            labelGameControllerCapabilities = new Label(c, SWT.WRAP);
            labelGameControllerCapabilities.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            labelGameControllerCapabilities.setText("");

            // create the label describing the setup process (part 1)
            Label labelWiimoteInfo1 = new Label(c, SWT.WRAP);
            labelWiimoteInfo1.setLayoutData(new GridData(GridData.FILL, GridData.END, true, true));
            labelWiimoteInfo1.setText("Redetecting will drop all existing connections.");

            // create the button to start a new detection
            Button buttonDetectGameController = new Button(c, SWT.PUSH);
            buttonDetectGameController.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false));
            buttonDetectGameController.setText("Detect game controllers");
            buttonDetectGameController.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDeviceSettingsDialog.this.handleClickGameControllerDetection(e);
                }
            });
        } else {
            Label labelGameControllerError = new Label(c, SWT.WRAP);
            labelGameControllerError
                    .setText("Game Controller support could not be initialized, please check the logs.");
            labelGameControllerError.setLayoutData(new GridData(SWT.FILL));
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
        // start the redetection
        inputDeviceManager.redetectGameControllers();
        updateDeviceList();

        // update the list
        listConnectedGameControllers.removeAll();
        for(DeviceController d: inputDeviceManager.getInputDevicesByType(GameControllerDevice.class))
            listConnectedGameControllers.add(d.getName());
        handleSelectionDetectedGameController(e);
    }

    protected void handleSelectionDetectedGameController(SelectionEvent e) {
        int selectionIndex = listConnectedGameControllers.getSelectionIndex();
        if(gameControllers != null && selectionIndex >= 0 && selectionIndex <= gameControllers.length) {
            labelGameControllerName.setText("Name: " + gameControllers[selectionIndex].name);
            labelGameControllerAxes.setText("Axes: " + gameControllers[selectionIndex].numAxes);
            labelGameControllerButtons.setText("Buttons: " + gameControllers[selectionIndex].numButtons);
            labelGameControllerCapabilities.setText("Capabilities: "
                    + Arrays.toString(gameControllers[selectionIndex].capabilities));
        } else {
            labelGameControllerName.setText("");
            labelGameControllerAxes.setText("");
            labelGameControllerButtons.setText("");
            labelGameControllerCapabilities.setText("");
        }
    }

    @Override
    protected void okPressed() {
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

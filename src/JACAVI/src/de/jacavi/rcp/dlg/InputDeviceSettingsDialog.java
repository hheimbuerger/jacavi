package de.jacavi.rcp.dlg;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDeviceManager;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.util.ExceptionHandler;



public class InputDeviceSettingsDialog extends TitleAreaDialog {

    private final Image imageKeyboard;

    private final Image imageMouse;

    private final Image imageGameController;

    private final Image imageWiimote;

    private final Image imageWiimoteButtons;

    private final Font headingFont;

    private InputDeviceManager inputDeviceManager;

    private List<Button> checkboxesKeyboardConfigs = new ArrayList<Button>();

    private org.eclipse.swt.widgets.List listConnectedWiimotes;

    public InputDeviceSettingsDialog(Shell parentShell) {
        super(parentShell);

        inputDeviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");

        // prepare the images
        imageKeyboard = Activator.getImageDescriptor("/icons/input_devices/keyboard.png").createImage();
        imageMouse = Activator.getImageDescriptor("/icons/input_devices/mouse.png").createImage();
        imageGameController = Activator.getImageDescriptor("/icons/input_devices/game_controller.png").createImage();
        imageWiimote = Activator.getImageDescriptor("/icons/input_devices/wiimote.png").createImage();
        imageWiimoteButtons = Activator.getImageDescriptor("/icons/wiimote_buttons.png").createImage();
        /*imageKeyboard = new Image(Display.getDefault(), "icons/input_devices/keyboard.png");
        imageMouse = new Image(Display.getDefault(), "icons/input_devices/mouse.png");
        imageGameController = new Image(Display.getDefault(), "icons/input_devices/game_controller.png");
        imageWiimote = new Image(Display.getDefault(), "icons/input_devices/wiimote.png");
        imageWiimoteButtons = new Image(Display.getDefault(), "icons/wiimote_buttons.png");*/

        // prepare the font
        headingFont = new Font(Display.getDefault(), "Arial", 11, SWT.BOLD);
    }

    @Override
    public boolean close() {
        imageKeyboard.dispose();
        imageMouse.dispose();
        imageGameController.dispose();
        imageWiimote.dispose();
        imageWiimoteButtons.dispose();
        headingFont.dispose();
        return super.close();
    }

    @Override
    protected Control createContents(Composite parent) {
        getShell().setSize(700, 500);
        return super.createContents(parent);
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
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);

        // prepare the dialog layout
        content.setLayoutData(new GridData(GridData.FILL_BOTH));

        // prepare the composite layout
        GridLayout layout = new GridLayout(7, false);
        layout.marginTop = 10;
        layout.marginRight = 10;
        layout.marginBottom = 10;
        layout.marginLeft = 10;
        content.setLayout(layout);

        // create the per-device composites and the vertical splitters
        Composite groupKeyboard = new Composite(content, SWT.NONE);
        Label separator1 = new Label(content, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_OUT);
        Composite groupMouse = new Composite(content, SWT.NONE);
        Label separator2 = new Label(content, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_OUT);
        Composite groupGameController = new Composite(content, SWT.NONE);
        Label separator3 = new Label(content, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_OUT);
        Composite groupWiimote = new Composite(content, SWT.NONE);

        // set the layout data of the composites
        GridData separatorLayoutData = new GridData(GridData.CENTER, GridData.FILL, false, true);
        separator1.setLayoutData(separatorLayoutData);
        separator2.setLayoutData(separatorLayoutData);
        separator3.setLayoutData(separatorLayoutData);
        GridData compositeLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
        groupKeyboard.setLayoutData(compositeLayoutData);
        groupMouse.setLayoutData(compositeLayoutData);
        groupGameController.setLayoutData(compositeLayoutData);
        groupWiimote.setLayoutData(compositeLayoutData);

        // create the section headers (headline + image)
        createSectionHeader(groupKeyboard, "Keyboard", imageKeyboard);
        createSectionHeader(groupMouse, "Mouse", imageMouse);
        createSectionHeader(groupGameController, "Game Controller", imageGameController);
        createSectionHeader(groupWiimote, "Wiimote", imageWiimote);

        // create the individual sections
        createKeyboardSection(groupKeyboard);
        createMouseSection(groupMouse);
        createGameControllerSection(groupGameController);
        createWiimoteSection(groupWiimote);

        content.pack();

        return content;
    }

    private void createSectionHeader(Composite group, String heading, Image image) {
        GridLayout grid = new GridLayout(1, true);
        grid.verticalSpacing = 5;
        group.setLayout(grid);

        Label labelHeading = new Label(group, SWT.NONE);
        labelHeading.setText(heading);
        labelHeading.setFont(headingFont);
        labelHeading.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, false));

        Label imagebox = new Label(group, SWT.CENTER | SWT.SHADOW_NONE);
        imagebox.setImage(image);
        imagebox.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, false));
    }

    private void createKeyboardSection(Composite groupKeyboard) {
        List<DeviceController> inputDevices = inputDeviceManager.getInputDevicesByType(KeyboardDevice.class);

        for(DeviceController d: inputDevices) {
            Button checkboxKeyboard = new Button(groupKeyboard, SWT.CHECK);
            checkboxKeyboard.setText(d.getName());
            checkboxKeyboard.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
            checkboxKeyboard.setData(d.getId());
            checkboxKeyboard.setSelection(true);
            checkboxesKeyboardConfigs.add(checkboxKeyboard);
        }
    }

    private void createMouseSection(Composite groupMouse) {
        Button checkboxMouse1 = new Button(groupMouse, SWT.CHECK);
        checkboxMouse1.setText("mouse 1 active");
        checkboxMouse1.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
        checkboxMouse1.setEnabled(false);

        Button checkboxMouse2 = new Button(groupMouse, SWT.CHECK);
        checkboxMouse2.setText("mouse 2 active");
        checkboxMouse2.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
        checkboxMouse2.setEnabled(false);
    }

    private void createGameControllerSection(Composite groupGameController) {
        Button checkboxGameController1 = new Button(groupGameController, SWT.CHECK);
        checkboxGameController1.setText("game controller 1 active");
        checkboxGameController1.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
        checkboxGameController1.setEnabled(false);

        Button checkboxGameController2 = new Button(groupGameController, SWT.CHECK);
        checkboxGameController2.setText("game controller 2 active");
        checkboxGameController2.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false));
        checkboxGameController2.setEnabled(false);
    }

    private void createWiimoteSection(Composite groupWiimote) {
        // create the composite used to hold all the inner widgets
        Composite c = new Composite(groupWiimote, SWT.NONE);
        c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // create the layout manager for laying out the inner widgets
        GridLayout l = new GridLayout(1, true);
        l.verticalSpacing = 5;
        c.setLayout(l);

        // create the "Connected devices:" label
        Label labelConnectedWiimotes = new Label(c, SWT.WRAP);
        labelConnectedWiimotes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        labelConnectedWiimotes.setText("Connected devices:");

        listConnectedWiimotes = new org.eclipse.swt.widgets.List(c, SWT.BORDER);
        GridData listLayout = new GridData(SWT.CENTER, SWT.BEGINNING, true, false);
        listLayout.widthHint = 100;
        listLayout.heightHint = 50;
        listConnectedWiimotes.setLayoutData(listLayout);
        updateConnectedWiimotesList(listConnectedWiimotes);

        // create the label describing the setup process (part 1)
        Label labelWiimoteInfo1 = new Label(c, SWT.WRAP);
        labelWiimoteInfo1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        labelWiimoteInfo1
                .setText("Redetecting will drop all existing connections. To redetect, pair all devices via Bluetooth, then hold");

        // create the label used to show the bitmap with the two buttons
        Label labelWiimoteButtons = new Label(c, SWT.NONE);
        labelWiimoteButtons.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, false));
        labelWiimoteButtons.setImage(imageWiimoteButtons);

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
    }

    private void updateConnectedWiimotesList(org.eclipse.swt.widgets.List listConnectedWiimotes) {
        listConnectedWiimotes.removeAll();
        for(DeviceController d: inputDeviceManager.getInputDevicesByType(WiimoteDevice.class))
            listConnectedWiimotes.add(d.toString());
    }

    protected void handleClickWiimoteDetection(SelectionEvent e) {
        // remove previously connected devices from the device manager
        inputDeviceManager.removeInputDevicesByType(WiimoteDevice.class);

        // show the progress dialog while detecting devices
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.setTaskName("Please stand by while available Wiimotes are detected.");

                // run the detection
                WiimoteDeviceManager wiimoteDeviceManager = new WiimoteDeviceManager();
                int numWiimotesConnected = wiimoteDeviceManager.scanForWiimotes();

                // add the detected devices to the device manager
                for(int i = 0; i < numWiimotesConnected; i++)
                    inputDeviceManager.addInputDevice(new WiimoteDevice("Wiimote " + i, wiimoteDeviceManager
                            .getWiimote(i)));

                /*// FIXME: DEBUG
                Thread.sleep(5000);
                inputDeviceManager.addInputDevice(new WiimoteDevice("Wiimote 1", null));*/
            }
        };
        try {
            ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(getShell());
            progressMonitorDialog.run(true, false, op);
        } catch(InvocationTargetException e1) {
            ExceptionHandler.handleException(e1.getCause(), true);
        } catch(InterruptedException e1) {
            ExceptionHandler.handleException(e1, true);
        }

        // update the list of
        updateConnectedWiimotesList(listConnectedWiimotes);
    }

    @Override
    protected void okPressed() {
        inputDeviceManager.removeInputDevicesByType(KeyboardDevice.class);
        for(Button b: checkboxesKeyboardConfigs) {
            if(b.getSelection())
                inputDeviceManager.addInputDevice(new KeyboardDevice(b.getText()));
        }

        super.okPressed();
    }

    /*public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        InputDeviceSettingsDialog dlg = new InputDeviceSettingsDialog(shell);
        dlg.open();
        display.dispose();
    }*/
}

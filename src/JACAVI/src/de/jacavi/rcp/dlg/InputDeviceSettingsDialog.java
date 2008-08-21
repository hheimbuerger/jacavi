package de.jacavi.rcp.dlg;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
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
import de.jacavi.rcp.Activator;



public class InputDeviceSettingsDialog extends TitleAreaDialog {

    private final Image imageKeyboard;

    private final Image imageMouse;

    private final Image imageGameController;

    private final Image imageWiimote;

    private final Font headingFont;

    private InputDeviceManager inputDeviceManager;

    private List<Button> checkboxesKeyboardConfigs = new ArrayList<Button>();

    public InputDeviceSettingsDialog(Shell parentShell) {
        super(parentShell);

        inputDeviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");

        // prepare the images
        imageKeyboard = Activator.getImageDescriptor("/icons/input_devices/keyboard.png").createImage();
        imageMouse = Activator.getImageDescriptor("/icons/input_devices/mouse.png").createImage();
        imageGameController = Activator.getImageDescriptor("/icons/input_devices/game_controller.png").createImage();
        imageWiimote = Activator.getImageDescriptor("/icons/input_devices/wiimote.png").createImage();
        /*imageKeyboard = new Image(Display.getDefault(), "icons/input_devices/keyboard.png");
        imageMouse = new Image(Display.getDefault(), "icons/input_devices/mouse.png");
        imageGameController = new Image(Display.getDefault(), "icons/input_devices/game_controller.png");
        imageWiimote = new Image(Display.getDefault(), "icons/input_devices/wiimote.png");*/

        // prepare the font
        headingFont = new Font(Display.getDefault(), "Arial", 11, SWT.BOLD);
    }

    @Override
    public boolean close() {
        imageKeyboard.dispose();
        imageMouse.dispose();
        imageGameController.dispose();
        imageWiimote.dispose();
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
        GridData compositeLayoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
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
        grid.verticalSpacing = 10;
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
        Button buttonDetectWiimotes = new Button(groupWiimote, SWT.PUSH);
        buttonDetectWiimotes.setText("Detect Wii remotes");
        buttonDetectWiimotes.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
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

package de.jacavi.rcp.dlg;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.rcp.Activator;



public class InputDeviceSettingsDialog extends TitleAreaDialog {

    private final Image imageKeyboard;

    private final Image imageMouse;

    private final Image imageGameController;

    private final Image imageWiimote;

    public InputDeviceSettingsDialog(Shell parentShell) {
        super(parentShell);

        // prepare the images
        imageKeyboard = Activator.getImageDescriptor("/icons/input_devices/keyboard.png").createImage();
        imageMouse = Activator.getImageDescriptor("/icons/input_devices/mouse.png").createImage();
        imageGameController = Activator.getImageDescriptor("/icons/input_devices/game_controller.png").createImage();
        imageWiimote = Activator.getImageDescriptor("/icons/input_devices/keyboard.png").createImage();
    }

    @Override
    public boolean close() {
        imageKeyboard.dispose();
        imageMouse.dispose();
        imageGameController.dispose();
        imageWiimote.dispose();
        return super.close();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);

        // prepare the dialog layout
        content.setLayoutData(new GridData(GridData.FILL_BOTH));

        // prepare the composite layout
        RowLayout layout = new RowLayout(SWT.HORIZONTAL);
        layout.wrap = false;
        layout.pack = true;
        layout.justify = true;
        layout.marginTop = 15;
        layout.marginBottom = 200; // FIXME: just to make it look more like the final version
        content.setLayout(layout);

        // create the canvas and the vertical splitters
        CLabel labelKeyboard = new CLabel(content, SWT.CENTER | SWT.SHADOW_NONE);
        labelKeyboard.setImage(imageKeyboard);
        new Label(content, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_NONE | SWT.BORDER);
        CLabel labelMouse = new CLabel(content, SWT.CENTER | SWT.SHADOW_NONE);
        labelMouse.setImage(imageMouse);
        new Label(content, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_NONE | SWT.BORDER);
        CLabel labelGameController = new CLabel(content, SWT.CENTER | SWT.SHADOW_NONE);
        labelGameController.setImage(imageGameController);
        new Label(content, SWT.SEPARATOR | SWT.VERTICAL | SWT.SHADOW_NONE | SWT.BORDER);
        CLabel labelWiimote = new CLabel(content, SWT.CENTER | SWT.SHADOW_NONE);
        labelWiimote.setImage(imageWiimote);

        return content;
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
    protected void okPressed() {
        super.okPressed();
    }

}

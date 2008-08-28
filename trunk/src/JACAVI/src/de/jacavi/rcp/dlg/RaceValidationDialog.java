package de.jacavi.rcp.dlg;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.appl.racelogic.Race;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.actions.validator.RaceValidator;
import de.jacavi.rcp.actions.validator.ValidatationTaskName;



/**
 * @author Fabian Rohn
 */
public class RaceValidationDialog extends TitleAreaDialog {

    // private static Log log = LogFactory.getLog(PlayerSettingsDialog.class);

    private final Race race;

    private final Image valid;

    private final Image invalid;

    private Button okButton;

    private boolean readyForStart = true;

    public RaceValidationDialog(Shell parentShell, Race race) {
        super(parentShell);
        this.race = race;
        this.valid = Activator.getImageDescriptor("/icons/famfamfam-silk/accept.png").createImage();
        this.invalid = Activator.getImageDescriptor("/icons/famfamfam-silk/exclamation.png").createImage();
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Validate Race Settings");
        setMessage("Congratulations! Validation successfull!", IMessageProvider.INFORMATION);
        return contents;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText("Pre Startup Validation");

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
        group.setText("Validation Results");
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        for(int i = 0; i < RaceValidator.class.getDeclaredMethods().length; i++) {

            CLabel validationTask = new CLabel(group, SWT.NONE);
            validationTask.setText(RaceValidator.class.getDeclaredMethods()[i]
                    .getAnnotation(ValidatationTaskName.class).description());

            try {
                Boolean valid = (Boolean) RaceValidator.class.getDeclaredMethods()[i].invoke(null, race);
                System.out.println(valid);

                CLabel isValidLabel = new CLabel(group, SWT.NONE);
                if(valid) {
                    isValidLabel.setText("VALID");
                    isValidLabel.setImage(this.valid);
                } else {
                    isValidLabel.setText("INVALID");
                    isValidLabel.setImage(this.invalid);
                    readyForStart = false;
                    setErrorMessage("Please fix the following errors in your settings.");
                }

            } catch(IllegalArgumentException e) {
                e.printStackTrace();
            } catch(SecurityException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            } catch(InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return parent;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        okButton = getButton(IDialogConstants.OK_ID);
        okButton.setText("Start Race");
        okButton.setEnabled(readyForStart);
    }
}

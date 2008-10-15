package de.jacavi.rcp.dlg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.appl.racelogic.Player;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.actions.validator.RaceValidator;
import de.jacavi.rcp.actions.validator.ValidatationDesription;
import de.jacavi.rcp.util.ExceptionHandler;



/**
 * @author Fabian Rohn
 */
public class RaceValidationDialog extends TitleAreaDialog {

    // private static Log log = LogFactory.getLog(PlayerSettingsDialog.class);

    private final Image valid;

    private final Image invalid;

    private Button okButton;

    private final RaceValidator validator;

    private boolean readyForStart = true;

    private final java.util.List<Player> players;

    public RaceValidationDialog(Shell parentShell, java.util.List<Player> players) {
        super(parentShell);
        this.players = players;
        this.valid = Activator.getImageDescriptor("/images/famfamfam-silk/accept_16x16.png").createImage();
        this.invalid = Activator.getImageDescriptor("/images/famfamfam-silk/exclamation_16x16.png").createImage();
        this.validator = new RaceValidator();
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
        GridLayout parentlayout = new GridLayout();
        parent.setLayout(parentlayout);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 20;
        layout.numColumns = 3;
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;

        Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
        group.setLayout(layout);
        group.setText("Validation Results");
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        List errorMessagesList = null;
        for(Method validationMethod: RaceValidator.class.getDeclaredMethods()) {

            if(!validationMethod.getName().startsWith("validate")) {
                continue;
            }

            CLabel validationTask = new CLabel(group, SWT.NONE);
            validationTask.setText(validationMethod.getAnnotation(ValidatationDesription.class).value());

            try {
                Boolean valid = (Boolean) validationMethod.invoke(validator, players);

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
                if(errorMessagesList == null) {
                    errorMessagesList = new List(group, SWT.SINGLE | SWT.BORDER);
                    GridData errorsGd = new GridData(GridData.FILL_BOTH);
                    errorsGd.verticalSpan = RaceValidator.class.getDeclaredMethods().length - 1;
                    errorMessagesList.setLayoutData(errorsGd);
                }
            } catch(IllegalArgumentException e) {
                ExceptionHandler.handleException(this, e, true);
            } catch(SecurityException e) {
                ExceptionHandler.handleException(this, e, true);
            } catch(IllegalAccessException e) {
                ExceptionHandler.handleException(this, e, true);
            } catch(InvocationTargetException e) {
                ExceptionHandler.handleException(this, e, true);
            }
        }

        for(String error: validator.getErrorMessages()) {
            errorMessagesList.add(error);
        }
        if(!readyForStart) {
            Display.getCurrent().beep();
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

    @Override
    public boolean close() {
        valid.dispose();
        invalid.dispose();
        return super.close();
    }

}

package de.jacavi.rcp.util.validators;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.jacavi.rcp.dialogs.Validate;



public class ValidationGroup extends Composite {

    private Label innerLabel = null;

    private Color defaultColor = new Color(null, new RGB(251, 108, 105));

    public ValidationGroup(Composite parent, int style) {
        super(parent, style);
        setLayout(new FillLayout());
        innerLabel = new Label(this, SWT.NONE);
        innerLabel.setForeground(defaultColor);
    }

    public boolean isValidationControl(Object o) {
        boolean retVal = false;
        Field f = (Field) o;
        for(Class<?> c: f.getType().getInterfaces()) {
            if(c == ValidationControl.class) {
                retVal = true;
            }
        }
        return retVal;
    }

    @Override
    public void setFont(org.eclipse.swt.graphics.Font font) {
        innerLabel.setFont(font);
    }

    @Override
    public void setForeground(org.eclipse.swt.graphics.Color color) {
        innerLabel.setForeground(color);
        // this.getParent().getChildren();
    }

    public boolean isValid(Object o, String groupName) {
        boolean retVal = true;
        String errorMessage = "";
        for(Field f: o.getClass().getDeclaredFields()) {
            if(f.isAnnotationPresent(Validate.class)) {
                Validate a = f.getAnnotation(Validate.class);
                String group = a.group();
                String error = a.error();
                if(group.equals(groupName) && isValidationControl(f)) {
                    ValidationControl vc = null;
                    try {
                        f.setAccessible(true);
                        vc = (ValidationControl) f.get(o);
                    } catch(IllegalArgumentException e) {
                        de.jacavi.rcp.util.ExceptionHandler.handleException(this, e, false);
                    } catch(IllegalAccessException e) {
                        de.jacavi.rcp.util.ExceptionHandler.handleException(this, e, false);
                    }
                    if(!vc.isValid()) {
                        retVal = false;
                        errorMessage = errorMessage + error + "\n";
                    }
                }
            }
        }

        innerLabel.setText(errorMessage);
        return retVal;
    }

    public void setText(String text) {
        innerLabel.setText(text);
    }
}

package de.jacavi.rcp.widgets.controls.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;



public class RegexValidatedText extends Composite implements ValidationControl {

    private String matchPattern = null;

    private Text innerText = null;

    private boolean isValid = false;

    // #fb6c69
    private Color bgColor = null;

    public RegexValidatedText(Composite parent, int style, String matchPattern) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout());
        this.matchPattern = matchPattern;
        innerText = new Text(this, style);
        bgColor = new Color(this.getDisplay(), new RGB(251, 108, 105));
        innerText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                RegexValidatedText.this.validateText(e);
            }

        });
    }

    public void setText(String text) {
        innerText.setText(text);
    }

    public String getText() {
        return innerText.getText();
    }

    protected void validateText(ModifyEvent event) {

        Text t = (Text) event.widget;
        String input = t.getText();
        if(matchPattern.equals("") || matchPattern == null)
            return;

        Pattern p = Pattern.compile(matchPattern);
        Matcher m = p.matcher(input);

        if(m.matches()) {
            t.setBackground(new Color(t.getDisplay(), new RGB(255, 255, 255)));
            isValid = true;
        } else {
            t.setBackground(bgColor);
            isValid = false;
        }
    }

    @Override
    public boolean isValid() {
        return isValid;
    }
}

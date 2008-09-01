package de.jacavi.rcp.widgets.controls.validators;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;



public class IPV4ValidatedText extends Composite implements ValidationControl {
    private Text innerText = null;

    private boolean isValid = false;

    // #fb6c69
    private Color bgColor = null;

    public IPV4ValidatedText(Composite parent, int style) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout());
        innerText = new Text(this, style);
        bgColor = new Color(null, new RGB(251, 108, 105));

        innerText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                IPV4ValidatedText.this.validateText(e);
            }

        });
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    protected void validateText(ModifyEvent event) {
        isValid = false;
        Text t = (Text) event.widget;
        String input = t.getText();
        StringTokenizer tokenizer = new StringTokenizer(input, ".");

        if(tokenizer.countTokens() == 4) {
            while(tokenizer.hasMoreTokens()) {
                String curToken = tokenizer.nextToken();
                if(curToken.length() > 0) {
                    try {
                        int token = Integer.valueOf(curToken);
                        if(token >= 0 && token <= 255)
                            isValid = true;
                        else
                            isValid = false;
                    } catch(NumberFormatException e) {
                        isValid = false;
                        break;
                    }
                }
            }
        }

        if(isValid)
            t.setBackground(new Color(null, new RGB(255, 255, 255)));
        else
            t.setBackground(bgColor);

    }

    public void setText(String string) {
        innerText.setText(string);
    }

    public String getText() {
        return innerText.getText();
    }

}

package de.jacavi.rcp.util.validators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;



public class RangeValidatedText extends Composite implements ValidationControl {
    private Text innerText = null;

    private boolean isValid = false;

    private int from;

    private int to;

    // #fb6c69
    private Color bgColor = null;

    public RangeValidatedText(Composite parent, int style, int from, int to) {

        super(parent, SWT.NONE);
        setLayout(new FillLayout());
        innerText = new Text(this, style);
        bgColor = new Color(null, new RGB(251, 108, 105));
        this.from = from;
        this.to = to;

        innerText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                RangeValidatedText.this.validateText(e);
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

        try {
            int in = Integer.valueOf(input);
            if(in >= from && in <= to)
                isValid = true;
        } catch(NumberFormatException e) {

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

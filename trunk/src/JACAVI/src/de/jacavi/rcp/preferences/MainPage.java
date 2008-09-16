package de.jacavi.rcp.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jacavi.rcp.Activator;



public class MainPage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String PREF_SHOW_LANES = "showLanes";

    private Button mEnabled;

    // private Text mInterval;

    @Override
    protected final Control createContents(final Composite pParent) {
        final GridLayout layout = new GridLayout(3, true);

        final Composite wrapper = new Composite(pParent, SWT.NONE);
        wrapper.setLayout(layout);

        mEnabled = new Button(wrapper, SWT.CHECK);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 3;
        mEnabled.setLayoutData(gridData);
        mEnabled.setText("Show lanes");

        /*final Label l = new Label(wrapper, SWT.NONE);
        l.setText("Autosave interval (min)");
        l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        mInterval = new Text(wrapper, SWT.BORDER);
        gridData = new GridData(SWT.BEGINNING);
        // CHECKSTYLE:OFF
        gridData.widthHint = 40;
        // CHECKSTYLE:ON
        mInterval.setLayoutData(gridData);*/

        initializeValues();
        return wrapper;
    }

    private void initializeValues() {
        final IPreferenceStore store = Activator.getStore();

        mEnabled.setSelection(store.getBoolean(PREF_SHOW_LANES));
        // mInterval.setText(Integer.toString(store.getInt(AUTOSAVE_INTERVAL)));
    }

    @Override
    public final boolean performOk() {
        final IPreferenceStore store = Activator.getStore();
        store.setValue(PREF_SHOW_LANES, mEnabled.getSelection());

        /*try {
            final int intervalInt = Integer.parseInt(mInterval.getText());
            store.setValue(AUTOSAVE_INTERVAL, intervalInt);
        } catch(final NumberFormatException e) {
            EclipseLog.info("Error parsing autosave period", e, false);
        }*/

        return super.performOk();
    }

    @Override
    protected final void performDefaults() {
        final IPreferenceStore store = Activator.getStore();

        mEnabled.setSelection(store.getDefaultBoolean(PREF_SHOW_LANES));

        // mInterval.setText(Integer.toString(store.getDefaultInt(AUTOSAVE_INTERVAL)));
    }

    @Override
    public void init(IWorkbench workbench) {
    // TODO Auto-generated method stub

    }

}

package de.jacavi.rcp.preferences;

import java.awt.image.AffineTransformOp;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jacavi.rcp.Activator;



public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String TEXT_BICUBIC = "bicubic (highest quality, worst performance)";

    private static final String TEXT_BILINEAR = "bilinear (medium quality, medium performance)";

    private static final String TEXT_NEAREST_NEIGHBOR = "nearest neighbor (low quality, best performance)";

    public static final String PREF_SHOW_LANES = "showLanes";

    public static final String PREF_VIEWPORT_QUALITY = "viewportQuality";

    public static final String PREF_TILE_QUALITY = "tileQuality";

    private Button checkboxShowLanes;

    private Combo comboViewportQuality;

    private Combo comboTileQuality;

    @Override
    protected final Control createContents(final Composite pParent) {
        final GridLayout layout = new GridLayout(2, true);
        layout.verticalSpacing = 10;

        final Composite wrapper = new Composite(pParent, SWT.NONE);
        wrapper.setLayout(layout);

        checkboxShowLanes = new Button(wrapper, SWT.CHECK);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 2;
        checkboxShowLanes.setLayoutData(gridData);
        checkboxShowLanes.setText("Display lanes and checkpoints");

        Label label2 = new Label(wrapper, SWT.NONE);
        label2.setText("Tile rendering quality");
        label2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        comboTileQuality = new Combo(wrapper, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboTileQuality.add(TEXT_BICUBIC);
        comboTileQuality.add(TEXT_BILINEAR);
        comboTileQuality.add(TEXT_NEAREST_NEIGHBOR);
        comboTileQuality.setLayoutData(new GridData(SWT.FILL));

        Label label3 = new Label(wrapper, SWT.WRAP);
        label3
                .setText("Note: The tile rendering quality affects the rendering performance of the cached, off-screen track image. This image is only rerendered when the tile selection changes or the track is modified. It has no effect on the performance of viewport operations. Higher quality settings lead to fewer artifacts on rotated tiles.");
        GridData label3Layout = new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1);
        label3Layout.widthHint = 300;
        label3.setLayoutData(label3Layout);

        Label label1 = new Label(wrapper, SWT.NONE);
        label1.setText("Viewport rendering quality");
        label1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        comboViewportQuality = new Combo(wrapper, SWT.DROP_DOWN | SWT.READ_ONLY);
        comboViewportQuality.add(TEXT_BICUBIC);
        comboViewportQuality.add(TEXT_BILINEAR);
        comboViewportQuality.add(TEXT_NEAREST_NEIGHBOR);
        comboViewportQuality.setLayoutData(new GridData(SWT.FILL));

        Label label4 = new Label(wrapper, SWT.WRAP);
        label4
                .setText("Note: The viewport rendering quality affects the rendering performance of the cached track image to the screen. This happens on every viewport change, i.e. on every pan, rotate and zoom operation and also on every frame during a race. It therefore directly affects the maximum frames per second that can be rendered. Higher quality settings lead to fewer artifacts if the viewport is rotated or zoomed.");
        GridData label4Layout = new GridData(SWT.FILL, SWT.TOP, false, true, 2, 1);
        label4Layout.widthHint = 300;
        label4.setLayoutData(label4Layout);

        initializeValues();
        return wrapper;
    }

    private void initializeValues() {
        final IPreferenceStore store = getPreferenceStore();

        checkboxShowLanes.setSelection(store.getBoolean(PREF_SHOW_LANES));
        restoreScalingAlgorithm(store, PREF_VIEWPORT_QUALITY, comboViewportQuality, false);
        restoreScalingAlgorithm(store, PREF_TILE_QUALITY, comboTileQuality, false);
    }

    @Override
    public final boolean performOk() {
        final IPreferenceStore store = getPreferenceStore();
        store.setValue(PREF_SHOW_LANES, checkboxShowLanes.getSelection());
        storeScalingAlgorithm(store, PREF_VIEWPORT_QUALITY, comboViewportQuality);
        storeScalingAlgorithm(store, PREF_TILE_QUALITY, comboTileQuality);

        return super.performOk();
    }

    @Override
    protected final void performDefaults() {
        final IPreferenceStore store = getPreferenceStore();

        checkboxShowLanes.setSelection(store.getDefaultBoolean(PREF_SHOW_LANES));
        restoreScalingAlgorithm(store, PREF_VIEWPORT_QUALITY, comboViewportQuality, true);
        restoreScalingAlgorithm(store, PREF_TILE_QUALITY, comboTileQuality, true);
    }

    @Override
    public void init(IWorkbench workbench) {}

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return Activator.getStore();
    }

    private void restoreScalingAlgorithm(final IPreferenceStore store, String preference, Combo combobox,
            boolean useDefault) {
        int preferenceValue = useDefault ? store.getDefaultInt(preference) : store.getInt(preference);
        if(preferenceValue == AffineTransformOp.TYPE_BICUBIC)
            combobox.setText(TEXT_BICUBIC);
        else if(preferenceValue == AffineTransformOp.TYPE_BILINEAR)
            combobox.setText(TEXT_BILINEAR);
        else if(preferenceValue == AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
            combobox.setText(TEXT_NEAREST_NEIGHBOR);
    }

    private void storeScalingAlgorithm(final IPreferenceStore store, String preference, Combo combobox) {
        if(combobox.getText().equals(TEXT_BICUBIC))
            store.setValue(preference, AffineTransformOp.TYPE_BICUBIC);
        else if(combobox.getText().equals(TEXT_BILINEAR))
            store.setValue(preference, AffineTransformOp.TYPE_BILINEAR);
        else if(combobox.getText().equals(TEXT_NEAREST_NEIGHBOR))
            store.setValue(preference, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    }

}

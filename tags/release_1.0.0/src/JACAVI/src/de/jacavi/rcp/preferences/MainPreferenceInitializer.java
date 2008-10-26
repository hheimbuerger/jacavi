package de.jacavi.rcp.preferences;

import java.awt.image.AffineTransformOp;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import de.jacavi.rcp.Activator;



public class MainPreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        Activator.getStore().setDefault(MainPreferencePage.PREF_SHOW_LANES, false);
        Activator.getStore().setDefault(MainPreferencePage.PREF_VIEWPORT_QUALITY,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        Activator.getStore().setDefault(MainPreferencePage.PREF_TILE_QUALITY, AffineTransformOp.TYPE_BICUBIC);
    }

}

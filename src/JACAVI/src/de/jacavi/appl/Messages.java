package de.jacavi.appl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;



public class Messages {
    private static final String BUNDLE_NAME = "i18n.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {}

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch(MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String[] getStringArray(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key).split(",");
        } catch(MissingResourceException e) {
            return new String[] { "!" + key + "!" };
        }
    }
}
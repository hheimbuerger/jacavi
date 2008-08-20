package de.jacavi.helper;

public class Helper {

    /**
     * @return boolean
     */
    public static boolean isWindowsOs() {
        String osName = System.getProperty("os.name");
        if(osName.toLowerCase().contains("windows"))
            return true;
        else
            return false;
    }
}

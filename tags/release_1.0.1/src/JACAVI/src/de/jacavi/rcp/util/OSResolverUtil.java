package de.jacavi.rcp.util;




/**
 * @author Florian Roth
 */
public class OSResolverUtil {

    /**
     * Checkout if it is a Windows OS
     * 
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

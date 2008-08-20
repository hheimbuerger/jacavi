package de.jacavi.rcp.util;

import java.util.ArrayList;
import java.util.List;

import de.jacavi.appl.ContextLoader;
import de.jacavi.hal.CarreraLibraryType;



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

    /**
     * Get all the technologies supportetd by the current OS. Technologies not supported by Windows are configured in
     * configurationBeans.xml under noWindowsHALTechnologiesBean
     * 
     * @return List<String> of supported HAL Technologies
     */
    @SuppressWarnings("unchecked")
    public static List<String> getTechnologiesByOS() {

        List<String> retList = new ArrayList<String>();

        for(CarreraLibraryType type: CarreraLibraryType.values()) {
            retList.add(type.toString());
        }
        // get the config Bean
        Object noWindowsTechBean = ContextLoader.getBean("noWindowsHALTechnologies");
        List<String> noWindowsTechList = null;
        // make cast a bit more save
        if(noWindowsTechBean instanceof List) {
            noWindowsTechList = (List<String>) noWindowsTechBean;
        }

        if(isWindowsOs())
            retList.removeAll(noWindowsTechList);

        return retList;
    }
}

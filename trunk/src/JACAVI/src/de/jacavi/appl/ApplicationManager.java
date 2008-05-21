package de.jacavi.appl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import de.jacavi.appl.racelogic.Race;



/**
 * @author Fabian Rohn
 */
public class ApplicationManager implements InitializingBean {
    private static Log log = LogFactory.getLog(ApplicationManager.class);

    private Race race;

    public ApplicationManager() {
        race = (Race) ContextLoader.getBean("race");
    }

    public void afterPropertiesSet() throws Exception {
        if(race == null)
            log.error("Initialization failed");
        else
            log.info("Initialization successfull");
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Race getRace() {
        return race;
    }
}

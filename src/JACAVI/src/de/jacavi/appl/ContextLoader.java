package de.jacavi.appl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * Initialisation of Spring-Contextfiles.
 * 
 * @author Fabian Rohn
 */
public class ContextLoader {
    private static final Log log = LogFactory.getLog(ContextLoader.class);

    private static ApplicationContext factory = null;

    static {
        log.info("Initializing JaCaVi Spring Context");
        factory = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static Object getBean(String beanName) {
        return factory.getBean(beanName);
    }

}
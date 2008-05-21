package de.jacavi.appl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;



/**
 * Initialisation of Spring-Contextfiles.
 * 
 * @author Fabian Rohn
 */
public class ContextLoader {
    private static final Log log = LogFactory.getLog(ContextLoader.class);

    private static XmlBeanFactory factory = null;

    public static Object getBean(String beanName) {
        if(factory == null) {
            log.info("Initializing JACAVI Spring Context");
            factory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
        }
        return factory.getBean(beanName);
    }

}
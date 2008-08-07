package de.jacavi.rcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;



/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	
    private static Log log = LogFactory.getLog(Application.class);

    public Object start(IApplicationContext context) {

        log.info("JACAVI Application starting up...");
        Display display = PlatformUI.createDisplay();
        try {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if(returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally {
            display.dispose();
            log.info("JACAVI Application shut down!");
        }
    }

    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if(workbench == null)
            return;
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if(!display.isDisposed())
                    workbench.close();
            }
        });
    }

}

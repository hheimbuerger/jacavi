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

        log.info("JaCaVi Application starting up...");
        Display display = PlatformUI.createDisplay();

        // pre-load the native wiiuse library, as it will later on be loaded from native code, effectively circumventing
        // the OSGi class loader -- loading it here means it will also be in the process and the call from the native
        // code will succeed
        log.info("Preloading native wiiuse library.");
        try {
        	System.loadLibrary("wiiuse");
        } catch(UnsatisfiedLinkError e) {
        	log.warn("Preloading the native wiiuse library failed! This most likely indicates an error on Windows and Linux platforms and is expected behaviour on all other platforms (as no wiiuse library is available for those).", e);
        }

        try {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if(returnCode == PlatformUI.RETURN_RESTART) {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        } finally {
            display.dispose();
            log.info("JaCaVi Application shut down!");
        }
    }

    public void stop() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if(workbench == null) {
            return;
        }
        final Display display = workbench.getDisplay();
        display.syncExec(new Runnable() {
            public void run() {
                if(!display.isDisposed()) {
                    workbench.close();
                }
            }
        });
    }

}

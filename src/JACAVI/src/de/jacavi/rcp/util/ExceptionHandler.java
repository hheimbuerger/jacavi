package de.jacavi.rcp.util;

import org.apache.commons.logging.Log;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;



/**
 * @author Fabian Rohn
 *  <p>
 *  This Handler writes an occured Exception into a logger and shows an ErrorDialog (optional)
 */
public class ExceptionHandler {
    /**
     * @param t
     *      the thrown exception
     *      <p>
     * @param logger
     *      the class specific Apache logger <br>
     *      often created by <code>LogFactory.getLog(YourClass.class)</code>
     *      <p>
     * @param message
     *      the message wich is filled into the logging message and shown in the ErrorDialog
     *      <p>
     * @param showErrorDialog
     *      value if an ErrorDialog may be shown
     *      <p>
     * @param title
     *      the title, shown in the Dialog shell<br>
     *      if <code>showErrorDialog==false</code> this parameter will be ignored (set to null)
     *      <p>
     * @param status
     *      the status of the dialog (Warning,Error,...)<br>
     *      if <code>showErrorDialog==false</code> this parameter will be ignored (set to null)
     */
    public static void handleException(Throwable t, Log logger, String message, boolean showErrorDialog, String title,
            IStatus status) {
        logger.error(message, t);

        if(showErrorDialog) {
            ErrorDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message,
                    status);
        }
    }
}

package de.jacavi.rcp.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;



/**
 * @author Fabian Rohn
 *         <p>
 *         This Handler writes an occured Exception into a logger and shows an ErrorDialog (optional)
 */
public class ExceptionHandler {

    /**
     * Call this to handle all kinds of exceptions
     * 
     * @param occurrence
     *            The object where the exception occured for better log reading
     *            <p>
     * @param message
     *            An extra message to show if an exceptionoccures
     *            <p>
     * @param t
     *            The thrown exception
     *            <p>
     * @param showErrorDialog
     *            If true show an error dialogue otherwise show not
     *            <p>
     */
    public static void handleException(Object occurrence, String message, Throwable t, boolean showErrorDialog) {

        Log log = LogFactory.getLog(occurrence.getClass());
        log.error(message, t);

        if(showErrorDialog)
            StatusManager.getManager().handle(new Status(IStatus.ERROR, "JACAVI", message, t), StatusManager.SHOW);
        else
            StatusManager.getManager().handle(new Status(IStatus.ERROR, "JACAVI", message, t), StatusManager.NONE);

    }

    /**
     * Call this to handle all kinds of exceptions
     * 
     * @param occurrence
     *            The object where the exception occured for better log reading
     *            <p>
     * @param t
     *            The thrown exception
     *            <p>
     * @param showErrorDialog
     *            If true show an error dialogue otherwise show not
     *            <p>
     */
    public static void handleException(Object occurrence, Throwable t, boolean showErrorDialog) {
        handleException(occurrence, "Error", t, showErrorDialog);
    }

}

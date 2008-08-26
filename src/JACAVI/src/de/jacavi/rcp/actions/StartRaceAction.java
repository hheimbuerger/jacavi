package de.jacavi.rcp.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.jacavi.appl.racelogic.Race;
import de.jacavi.rcp.perspectives.RacePerspective;



/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class StartRaceAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    private Race race;

    /**
     * The constructor.
     */
    public StartRaceAction() {}

    /**
     * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
     * UI.
     * 
     * @see IWorkbenchWindowActionDelegate#run
     */
    public void run(IAction action) {
        // MessageDialog.openInformation(
        // window.getShell(),
        // "Starting Race",
        // "Now Race is starting...");
        // ProgressMonitorDialog dialog = new ProgressMonitorDialog(window
        // .getShell());
        // try {
        // dialog.run(true, true, new IRunnableWithProgress() {
        // @Override
        // public void run(IProgressMonitor monitor) {
        // monitor.beginTask("Initializing Player...",
        // IProgressMonitor.UNKNOWN);
        // for (int i = 0; i < 10; i++) {
        // if (monitor.isCanceled())
        // return;
        // monitor.subTask("Init Player " + i);
        // sleep(1000);
        // }
        // monitor.done();
        // }
        // });
        // } catch (InvocationTargetException e) {
        // e.printStackTrace();
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        try {
            PlatformUI.getWorkbench().showPerspective(RacePerspective.ID, window);
        } catch(WorkbenchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
     * this can only happen after the delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {}

    /**
     * We can use this method to dispose of any system resources we previously allocated.
     * 
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {}

    /**
     * We will cache window object in order to be able to provide parent shell for the message dialog.
     * 
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    // private void sleep(Integer waitTime) {
    // try {
    // Thread.sleep(waitTime);
    // } catch(Throwable t) {
    // System.out.println("Wait time interrupted");
    // }
    // }

    public void setRace(Race race) {
        this.race = race;
    }

    public Race getRace() {
        return race;
    }
}
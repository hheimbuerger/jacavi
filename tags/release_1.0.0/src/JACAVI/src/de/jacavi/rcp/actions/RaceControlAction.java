package de.jacavi.rcp.actions;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.racelogic.RaceEngine;



public abstract class RaceControlAction implements IWorkbenchWindowActionDelegate {

    protected IWorkbenchWindow window;

    protected final RaceEngine raceEngine;

    protected final List<Player> players;

    @SuppressWarnings("unchecked")
    public RaceControlAction() {
        raceEngine = (RaceEngine) ContextLoader.getBean("raceEngineBean");
        players = (List<Player>) ContextLoader.getBean("playersBean");
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

}

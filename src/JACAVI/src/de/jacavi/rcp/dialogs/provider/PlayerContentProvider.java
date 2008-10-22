package de.jacavi.rcp.dialogs.provider;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.jacavi.appl.racelogic.Player;



public class PlayerContentProvider implements IStructuredContentProvider {

    /**
     * Gets the elements for the table
     */
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object arg0) {
        // Returns all the players in the specified team
        return ((ArrayList<Player>) arg0).toArray();
    }

    /**
     * Disposes any resources
     */
    public void dispose() {
    // We don't create any resources, so we don't dispose any
    }

    /**
     * Called when the input changes
     */
    public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
        arg0.refresh();
    }
}
package de.jacavi.rcp.dlg.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.jacavi.rcp.dlg.PlayerTableModel;

public class PlayerContentProvider implements IStructuredContentProvider {

	/**
	 * Gets the elements for the table
	 * 
	 */
	public Object[] getElements(Object arg0) {
		// Returns all the players in the specified team
		return ((PlayerTableModel) arg0).getPlayer().toArray();
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
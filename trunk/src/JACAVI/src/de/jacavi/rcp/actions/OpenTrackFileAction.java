package de.jacavi.rcp.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class OpenTrackFileAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;		
	}

	@Override
	public void run(IAction action) {
		String[] filterExt = { "*.jacavi" };
		FileDialog dlg = new FileDialog(window.getShell(),SWT.MULTI);
		dlg.setFilterExtensions(filterExt);
		File f = new File(dlg.open());
		System.out.println(f.getAbsolutePath());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

}

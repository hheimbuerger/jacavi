package de.jacavi.rcp.util;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author rofa
 * 
 * Class that resolves Views or Editors by args
 */
public class PartFromIDResolver {
	public static IViewPart resolveView(String partID) {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(partID);
	}

	public static IEditorPart resolveEditor(IEditorInput editorInput) {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findEditor(editorInput);
	}
}

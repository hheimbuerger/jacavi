package de.jacavi.rcp.util;

import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.WorkbenchErrorHandler;
import org.eclipse.ui.statushandlers.WorkbenchStatusDialogManager;



public class JacaviErrorHandler extends WorkbenchErrorHandler {

    @Override
    public void handle(StatusAdapter statusAdapter, int style) {
        super.handle(statusAdapter, style);
    }

    @Override
    protected void configureStatusDialog(WorkbenchStatusDialogManager statusDialog) {
        statusDialog.enableDefaultSupportArea(true);
        // here you can set other providers
        // statusDialog.setDetailsAreaProvider(new DetailsAreaProvider());
        // statusDialog.setStatusListLabelProvider(labelProvider);
        // statusDialog.setSupportAreaProvider(provider)
    }
}

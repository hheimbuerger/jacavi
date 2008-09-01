package de.jacavi.rcp.dlg.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jacavi.appl.racelogic.Player;



public class PlayerLabelProvider implements ITableLabelProvider {

    public static String[] COLUMNNNAMES = { "Player Name", "Controller", "Connector" };

    public Image getColumnImage(Object element, int columnIndex) {
        // TODO here we could fill each column with a 16x16 px image (e.g. a small wiimote or a 42 logo)
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        Player p = (Player) element;

        String result = "";
        switch(columnIndex) {
            case 0:
                if(p.getName() != null)
                    result = p.getName();
                break;
            case 1:
                if(p.getController() != null)
                    result = p.getController().toString();
                break;
            case 2:
                if(p.getSlotCarSystemConnector() != null)
                    result = p.getSlotCarSystemConnector().toString();
                break;
            case 3:
                // if (p.getColor() != null)
                // result = p.getColor().toString();
                break;
            default:
                result = "UNKNOWN";
                break;
        }
        return result;
    }

    public void addListener(ILabelProviderListener listener) {
    // TODO Auto-generated method stub

    }

    public void dispose() {
    // TODO Auto-generated method stub
    }

    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {

    }

}

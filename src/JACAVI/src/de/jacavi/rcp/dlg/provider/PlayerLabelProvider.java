package de.jacavi.rcp.dlg.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.jacavi.appl.racelogic.Player;
import de.jacavi.hal.analogue.AnalogueDriveConnector;
import de.jacavi.hal.bluerider.BlueriderConnector;
import de.jacavi.hal.lib42.Lib42DriveConnector;
import de.jacavi.rcp.Activator;



public class PlayerLabelProvider implements ITableLabelProvider {

    public static String[] COLUMNNNAMES = { "Player Name", "Controller", "Connector" };

    private final Map<String, Image> usedImages = new HashMap<String, Image>();

    public PlayerLabelProvider() {
        usedImages.put("analog", Activator.getImageDescriptor("/icons/connectors/analogue(16X16).png").createImage());
        usedImages.put("bluerider", Activator.getImageDescriptor("/icons/connectors/bluerider(16X16).png")
                .createImage());
        usedImages.put("digital", Activator.getImageDescriptor("/icons/connectors/lib42(16X16).png").createImage());
    }

    public Image getColumnImage(Object element, int columnIndex) {
        Player player = (Player) element;

        switch(columnIndex) {
            case 0:

                break;
            case 1:

                break;
            case 2:
                if(player.getSlotCarSystemConnector() != null) {
                    if(player.getSlotCarSystemConnector().getDriveConnector() instanceof AnalogueDriveConnector) {
                        return usedImages.get("analog");
                    }
                    if(player.getSlotCarSystemConnector().getDriveConnector() instanceof Lib42DriveConnector) {
                        return usedImages.get("digital");
                    }
                    if(player.getSlotCarSystemConnector().getDriveConnector() instanceof BlueriderConnector) {
                        return usedImages.get("bluerider");
                    }
                }
                break;

            default:
                break;
        }
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

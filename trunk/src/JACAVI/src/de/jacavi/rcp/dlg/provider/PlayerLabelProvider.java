package de.jacavi.rcp.dlg.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import de.jacavi.appl.valueobjects.Player;

public class PlayerLabelProvider implements ITableLabelProvider {

	public static String[] COLUMNNNAMES = { "Player Name", "Controller",
			"Protocol", "Color" };

	public Image getColumnImage(Object element, int columnIndex) {
		Player p = (Player) element;

		if (columnIndex == 3 && p.getColor()!=null) {
			Image image = new Image(null, 15, 15);
			GC gc = new GC(image);
			gc.setBackground(p.getColor());
			gc.fillRectangle(0, 0, 15, 15);
			gc.dispose();
			return image;
		} else
			return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Player p = (Player) element;

		String result = "";
		switch (columnIndex) {
		case 0:
			if (p.getName() != null)
				result = p.getName();
			break;
		case 1:
			if (p.getController() != null)
				result = p.getController().getClass().getSimpleName();
			break;
		case 2:
			if (p.getProtocol() != null)
				result = p.getProtocol();
			break;
		case 3:
//			if (p.getColor() != null)
//				result = p.getColor().toString();
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

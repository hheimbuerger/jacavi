package de.jacavi.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.Activator;
import de.jacavi.valueobjects.Player;

public class LapView extends ViewPart {
	public static final String ID = "JACAVI.lapView";

	private ArrayList<Player> players;

	public LapView() {
		players = new ArrayList<Player>();
		players
				.add(new Player("1", "Player 1", "Device", "Wiimote", "Carrera"));
		players.add(new Player("2", "Player 2", "Script", "", "Blutooth"));
	}

	@Override
	public Image getTitleImage() {
		return Activator.getImageDescriptor("/icons/clock-16x16.png")
				.createImage();
	}

	public void createPartControl(Composite parent) {

		// final Text text = new Text(parent, SWT.BORDER);
		// text.setBounds(25, 240, 220, 25);

		Table table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		String[] titles = { "Name", "Input", "Device", "Protcol",
				"Current Lap", "Last Lap", "Best Lap", "Rounds" };

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[i]);
		}

		for (int i = 0; i < players.size(); i++) {
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(0, players.get(i).getName());
			item.setText(1, players.get(i).getInput());
			item.setText(2, players.get(i).getDevice());
			item.setText(3, players.get(i).getProtocol());
			item.setText(4, "0:00");
			item.setText(5, "0:00");
			item.setText(6, "0:00");
			item.setText(7, "0");
		}

		for (int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}

		table.setBounds(25, 25, 220, 200);

		// table.addListener(SWT.Selection, new Listener() {
		// public void handleEvent(Event event) {
		// if (event.detail == SWT.CHECK) {
		// text.setText("You checked " + event.item);
		// } else {
		// text.setText("You selected " + event.item);
		// }
		// }
		// });

	}

	public void setFocus() {
	}
}
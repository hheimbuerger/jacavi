package de.jacavi.rcp.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.Messages;
import de.jacavi.appl.valueobjects.Player;



public class LapView extends ViewPart {
    public static final String ID = "JACAVI.lapView"; //$NON-NLS-1$

    private final ArrayList<Player> players;

    @SuppressWarnings("unchecked")
    public LapView() {
        players = (ArrayList<Player>) ContextLoader.getBean("playersBean"); //$NON-NLS-1$
    }

    @Override
    public void createPartControl(Composite parent) {

        final Text text = new Text(parent, SWT.BORDER);
        text.setBounds(25, 240, 220, 25);

        Table table = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        String[] titles = Messages.getStringArray("LapView.columns");

        for(int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NULL);
            column.setText(titles[i]);
        }

        for(int i = 0; i < players.size(); i++) {
            TableItem item = new TableItem(table, SWT.NULL);
            item.setText(0, players.get(i).getName());
            item.setText(1, players.get(i).getController().getClass().getSimpleName());
            item.setText(2, players.get(i).getProtocol());
            item.setText(3, "0:00"); //$NON-NLS-1$
            item.setText(4, "0:00"); //$NON-NLS-1$
            item.setText(5, "0:00"); //$NON-NLS-1$
            item.setText(6, "0"); //$NON-NLS-1$
        }

        for(int loopIndex = 0; loopIndex < titles.length; loopIndex++) {
            table.getColumn(loopIndex).pack();
        }

        table.setBounds(25, 25, 220, 200);

        table.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if(event.detail == SWT.CHECK) {
                    text.setText("You checked " + event.item); //$NON-NLS-1$
                } else {
                    text.setText("You selected " + event.item); //$NON-NLS-1$
                }
            }
        });

    }

    @Override
    public void setFocus() {}
}
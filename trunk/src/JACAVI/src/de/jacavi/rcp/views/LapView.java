package de.jacavi.rcp.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.Messages;
import de.jacavi.appl.racelogic.LapCompletionListener;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.racelogic.RaceStatisticsManager;
import de.jacavi.rcp.dlg.provider.PlayerContentProvider;

public class LapView extends ViewPart implements LapCompletionListener,
		IPerspectiveListener {
	public static final String ID = "JACAVI.lapView"; //$NON-NLS-1$

	private final ArrayList<Player> players;
	private TableViewer tableViewer;

	@SuppressWarnings("unchecked")
	public LapView() {
		players = (ArrayList<Player>) ContextLoader.getBean("playersBean");
		addListenerObject(this);
		((RaceStatisticsManager) ContextLoader
				.getBean("statisticsRegistryBean")).addListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {

		final Text text = new Text(parent, SWT.BORDER);
		text.setBounds(25, 240, 220, 25);

		String[] colNames = Messages.getStringArray("LapView.columns");
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION);
		// // Set up the table
		final Table playerTable = tableViewer.getTable();
		playerTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		playerTable.setHeaderVisible(true);
		playerTable.setLinesVisible(true);
		tableViewer.setColumnProperties(colNames);
		tableViewer.setLabelProvider(new LapLabelProvider());
		tableViewer.setContentProvider(new PlayerContentProvider());
		tableViewer.setInput(players);

		TableColumn tc = null;
		for (String colName : colNames) {
			tc = new TableColumn(playerTable, SWT.LEFT);
			tc.setText(colName);
			tc.setWidth(100);
		}
		tableViewer.refresh();

		//
		playerTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					text.setText("You checked " + event.item); //$NON-NLS-1$
				} else {
					text.setText("You selected " + event.item); //$NON-NLS-1$
				}
				;
			}
		});

	}

	@Override
	public void setFocus() {
	}

	private class LapLabelProvider implements ITableLabelProvider {

		private final SimpleDateFormat formatter;

		public LapLabelProvider() {
			formatter = new SimpleDateFormat("mm:ss:SSS");
		}

		@Override
		public Image getColumnImage(Object arg0, int arg1) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Player p = (Player) element;

			String result = "";
			switch (columnIndex) {
			case 0:
				if (p.getName() != null) {
					result = p.getName();
				}
				break;
			case 1:
				result = formatter.format(new Date(p.getRaceStatistic()
						.getLastLap()));
				break;
			case 2:
				result = formatter.format(new Date(p.getRaceStatistic()
						.getBestLap()));
				break;
			case 3:
				result = p.getPosition().lap + "";
				break;
			default:
				result = "UNKNOWN";
				break;
			}
			return result;
		}

		@Override
		public void addListener(ILabelProviderListener arg0) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object arg0, String arg1) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener arg0) {
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.jacavi.rcp.views.LapCompletionListener#onLapCompleted(de.jacavi.appl
	 * .racelogic.Player)
	 */
	public void onLapCompleted(final Player p) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				tableViewer.refresh(p);
			}
		});
	}

	@Override
	public void perspectiveActivated(IWorkbenchPage arg0,
			IPerspectiveDescriptor arg1) {
		tableViewer.refresh();
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage arg0,
			IPerspectiveDescriptor arg1, String arg2) {
	}

}
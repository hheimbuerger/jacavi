package de.jacavi.rcp.dlg;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.rcp.dlg.provider.PlayerContentProvider;
import de.jacavi.rcp.dlg.provider.PlayerLabelProvider;



/**
 * @author Fabian Rohn
 */
public class PlayerOverviewDialog extends TitleAreaDialog {

    private static TableViewer tableViewer;

    private final ArrayList<Player> model;

    @SuppressWarnings("unchecked")
    public PlayerOverviewDialog(Shell parentShell) {
        super(parentShell);
        model = (ArrayList<Player>) ContextLoader.getBean("playersBean");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        setTitle("Player Overview");
        setMessage("Please configure the number of players and its settings", IMessageProvider.INFORMATION);
        return contents;
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        parent.getShell().setText("Player Overview");

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
        GridData gdInputs = new GridData();
        gdInputs.widthHint = 100;
        GridLayout layout = new GridLayout();
        layout.marginHeight = 20;
        layout.numColumns = 2;
        layout.verticalSpacing = 10;
        layout.horizontalSpacing = 10;

        GridLayout parentlayout = new GridLayout();
        parentlayout.numColumns = 1;
        parent.setLayout(parentlayout);

        tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        // // Set up the table
        final Table playerTable = tableViewer.getTable();
        playerTable.setLayoutData(new GridData(GridData.FILL_BOTH));

        playerTable.setHeaderVisible(true);
        playerTable.setLinesVisible(true);
        tableViewer.setColumnProperties(PlayerLabelProvider.COLUMNNNAMES);
        tableViewer.setLabelProvider(new PlayerLabelProvider());
        tableViewer.setContentProvider(new PlayerContentProvider());
        tableViewer.setInput(model);

        String[] colNames = PlayerLabelProvider.COLUMNNNAMES;
        TableColumn tc = null;
        for(String colName: colNames) {
            tc = new TableColumn(playerTable, SWT.LEFT);
            tc.setText(colName);
            tc.setWidth(100);
        }
        playerTable.getColumns()[2].setWidth(280);

        playerTable.addSelectionListener(new SelectionAdapter() {});
        playerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Player player = (Player) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                openPlayerSettingsDialog(parent, player);
            }
        });

        tableViewer.refresh();

        // Add the buttons
        createButtons(parent);

        return parent;
    }

    private void createButtons(final Composite parent) {

        // Create and configure the "Add" button
        Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
        add.setText("Add");

        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;
        add.setLayoutData(gridData);
        add.addSelectionListener(new SelectionAdapter() {

            // Add a task to the ExampleTaskList and refresh the view
            @Override
            public void widgetSelected(SelectionEvent e) {
                Player newPlayer = new Player();
                model.add(newPlayer);
                openPlayerSettingsDialog(parent, newPlayer);
                tableViewer.refresh();
            }
        });

        // Create and configure the "Delete" button
        Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
        delete.setText("Delete");
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;
        delete.setLayoutData(gridData);

        delete.addSelectionListener(new SelectionAdapter() {

            // Remove the selection and refresh the view
            @Override
            public void widgetSelected(SelectionEvent e) {
                Player player = (Player) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                if(player != null) {
                    model.remove(player);
                    tableViewer.refresh();
                }
            }
        });

        // Create and configure the "Add" button
        Button edit = new Button(parent, SWT.PUSH | SWT.CENTER);
        edit.setText("Edit");

        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 80;

        edit.setLayoutData(gridData);
        edit.addSelectionListener(new SelectionAdapter() {

            // Add a task to the ExampleTaskList and refresh the view
            @Override
            public void widgetSelected(SelectionEvent e) {
                Player player = (Player) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
                if(player != null) {
                    openPlayerSettingsDialog(parent, player);
                }
            }
        });

    }

    protected static void openPlayerSettingsDialog(Composite parent, Player player) {
        PlayerSettingsDialog dlg = new PlayerSettingsDialog(parent.getShell(), player);
        if(dlg.open() == Window.OK) {
            tableViewer.refresh();
        }
    }

    @Override
    public boolean close() {
        tableViewer.getTable().dispose();
        return super.close();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }

    public ArrayList<Player> getPlayer() {
        return model;
    }
}

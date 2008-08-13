package de.jacavi.rcp.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TilesetRepository.TileSet;



public class CreateTrackPageOne extends WizardPage {
    private Text trackNameInputField;

    private Combo comboTileSet;

    private Composite container;

    public CreateTrackPageOne() {
        super("First Page");
        setTitle("New Track");
        setDescription("Create a new Carrera track");
    }

    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        Label label1 = new Label(container, SWT.NONE);
        label1.setText("Put here the name of the new track: ");

        trackNameInputField = new Text(container, SWT.BORDER | SWT.SINGLE);
        trackNameInputField.setText(Track.FILE_EXTENSION);
        trackNameInputField.setFocus();
        trackNameInputField.addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {}

            public void keyReleased(KeyEvent e) {
                checkComplete();
            }

        });
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        trackNameInputField.setLayoutData(gd);

        Label label2 = new Label(container, SWT.NONE);
        label2.setText("Kind of slot car track: ");

        comboTileSet = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
        String[] tileSets = new String[TileSet.values().length];
        for(int i = 0; i < TileSet.values().length; i++) {
            tileSets[i] = TileSet.values()[i].name();
        }
        comboTileSet.setItems(tileSets);
        comboTileSet.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkComplete();
            }
        });

        // Required to avoid an error in the system
        setControl(container);
        setPageComplete(false);

    }

    public String getText1() {
        return trackNameInputField.getText();
    }

    public TileSet getTileSet() {
        return TileSet.valueOf(comboTileSet.getText());
    }

    // You need to overwrite this method otherwise you receive a
    // AssertionFailedException
    // This method should always return the top widget of the application
    @Override
    public Control getControl() {
        return container;
    }

    private void checkComplete() {
        String input = trackNameInputField.getText();
        if(input.endsWith(Track.FILE_EXTENSION) && input.length() > Track.FILE_EXTENSION.length()
                && !comboTileSet.getText().isEmpty()) {
            setPageComplete(true);
        } else {
            setPageComplete(false);
        }
    }
}

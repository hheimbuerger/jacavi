package de.jacavi.rcp.dlg;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Fabian Rohn
 *
 */
public class PlayerSettingsDialog extends TitleAreaDialog {

	private static final String[] inputs = new String[] { "--Select Input--",
			"Device", "Script" };
	private static final String[] devices = new String[] { "--Select Device--",
			"Keyboard", "Joystick", "Wiimote" };
	private static final String[] protocols = new String[] {
			"--Select Protocol--", "Carrera", "HTWG", "Bluetooth" };


	public PlayerSettingsDialog(Shell parentShell) {
		super(parentShell);
	}


	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		setTitle("Player Settings");
		setMessage(
				"Please set Player specific options",
				IMessageProvider.INFORMATION);
		return contents;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Player Settings");
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
		GridData gdInputs = new GridData();
		gdInputs.widthHint = 100;
		GridLayout layout = new GridLayout();
		layout.marginHeight = 20;
		layout.numColumns = 2;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		
		GridLayout parentlayout = new GridLayout();
		parentlayout.numColumns = 2;
		parent.setLayout(parentlayout);

		// Player 1
		Group player1group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		player1group.setLayout(layout);
		player1group.setText("Player 1 Settings");
		player1group.setLayoutData(gd);

		CLabel lPlayer1 = new CLabel(player1group, SWT.NONE);
		lPlayer1.setText("Name:");

		Text player1 = new Text(player1group, SWT.BORDER);
		player1.setLayoutData(gdInputs);

		CLabel labelInput1 = new CLabel(player1group, SWT.NONE);
		labelInput1.setText("Input:");

		Combo comboInput1 = new Combo(player1group, SWT.BORDER | SWT.READ_ONLY);
		comboInput1.setLayoutData(gdInputs);
		comboInput1.setItems(inputs);
		comboInput1.select(0);

		CLabel labelDevice1 = new CLabel(player1group, SWT.NONE);
		labelDevice1.setText("Devices:");

		Combo devicesCombo1 = new Combo(player1group, SWT.BORDER
				| SWT.DROP_DOWN | SWT.READ_ONLY);
		devicesCombo1.setLayoutData(gdInputs);
		devicesCombo1.setItems(devices);
		devicesCombo1.select(0);

		CLabel labelProtocol1 = new CLabel(player1group, SWT.NONE);
		labelProtocol1.setText("Protocol:");

		Combo protocolsCombo1 = new Combo(player1group, SWT.BORDER
				| SWT.DROP_DOWN | SWT.READ_ONLY);
		protocolsCombo1.setLayoutData(gdInputs);
		protocolsCombo1.setItems(protocols);
		protocolsCombo1.select(0);

		// Player 2
		Group player2group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		player2group.setLayout(layout);
		player2group.setText("Player 2 Settings");
		player2group.setLayoutData(gd);

		CLabel lPlayer2 = new CLabel(player2group, SWT.NONE);
		lPlayer2.setText("Name:");

		Text player2 = new Text(player2group, SWT.BORDER);
		player2.setLayoutData(gdInputs);

		CLabel labelInput2 = new CLabel(player2group, SWT.NONE);
		labelInput2.setText("Input:");

		Combo comboInput2 = new Combo(player2group, SWT.BORDER | SWT.READ_ONLY);
		comboInput2.setLayoutData(gdInputs);
		comboInput2.setItems(inputs);
		comboInput2.select(0);

		CLabel labelDevice2 = new CLabel(player2group, SWT.NONE);
		labelDevice2.setText("Devices:");

		Combo devicesCombo2 = new Combo(player2group, SWT.BORDER
				| SWT.DROP_DOWN | SWT.READ_ONLY);
		devicesCombo2.setLayoutData(gdInputs);
		devicesCombo2.setItems(devices);
		devicesCombo2.select(0);

		CLabel labelProtocol2 = new CLabel(player2group, SWT.NONE);
		labelProtocol2.setText("Protocol:");

		Combo protocolsCombo2 = new Combo(player2group, SWT.BORDER
				| SWT.DROP_DOWN | SWT.READ_ONLY);
		protocolsCombo2.setLayoutData(gdInputs);
		protocolsCombo2.setItems(protocols);
		protocolsCombo2.select(0);

		return parent;
	}
	

}

package de.jacavi.rcp.dlg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.jacavi.rcp.Activator;
import de.jacavi.rcp.util.ExceptionHandler;



public class AgentSettingsDialog extends TitleAreaDialog {

    private Tree treeAgents;

    private Text textSourceCode;

    private final Font sourceCodeFont;

    private final ImageRegistry imageRegistry;

    public AgentSettingsDialog(Shell parentShell) {
        super(parentShell);
        imageRegistry = new ImageRegistry();
        imageRegistry.put("python", Activator.getImageDescriptor("/icons/agent_types/python.png"));
        imageRegistry.put("groovy", Activator.getImageDescriptor("/icons/agent_types/groovy.png"));
        sourceCodeFont = new Font(Display.getDefault(), "Courier New", 10, SWT.NONE);
    }

    @Override
    public boolean close() {
        imageRegistry.dispose();
        sourceCodeFont.dispose();
        return super.close();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        c.setLayoutData(new GridData(GridData.FILL_BOTH));

        FormLayout layout = new FormLayout();
        layout.marginTop = 10;
        layout.marginRight = 10;
        layout.marginBottom = 10;
        layout.marginLeft = 10;
        c.setLayout(layout);

        Button buttonRefresh = new Button(c, SWT.PUSH);
        FormData fd2 = new FormData();
        fd2.left = new FormAttachment(0);
        fd2.width = 200;
        fd2.bottom = new FormAttachment(100);
        buttonRefresh.setLayoutData(fd2);
        buttonRefresh.setText("Refresh");
        buttonRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AgentSettingsDialog.this.retrieveAvailableAgents();
            }
        });

        treeAgents = new Tree(c, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(0);
        fd1.left = new FormAttachment(buttonRefresh, 0, SWT.LEFT);
        fd1.right = new FormAttachment(buttonRefresh, 0, SWT.RIGHT);
        fd1.bottom = new FormAttachment(buttonRefresh, -10, SWT.TOP);
        treeAgents.setLayoutData(fd1);
        treeAgents.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AgentSettingsDialog.this.handleAgentSelection(e);
            }
        });

        textSourceCode = new Text(c, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        FormData fd3 = new FormData();
        fd3.top = new FormAttachment(treeAgents, 0, SWT.TOP);
        fd3.left = new FormAttachment(treeAgents, 10, SWT.RIGHT);
        fd3.right = new FormAttachment(100);
        fd3.bottom = new FormAttachment(buttonRefresh, 0, SWT.BOTTOM);
        textSourceCode.setLayoutData(fd3);
        textSourceCode.setFont(sourceCodeFont);

        retrieveAvailableAgents();

        return c;
    }

    protected void handleAgentSelection(SelectionEvent e) {
        if(e != null && e.item instanceof TreeItem) {
            TreeItem selectedItem = (TreeItem) e.item;
            File agentFile = (File) selectedItem.getData();

            StringBuilder contents = new StringBuilder();

            try {
                // use buffering, reading one line at a time
                // FileReader always assumes default encoding is OK!
                BufferedReader input = new BufferedReader(new FileReader(agentFile));
                try {
                    String line = null; // not declared within while loop
                    /*
                    * readLine is a bit quirky :
                    * it returns the content of a line MINUS the newline.
                    * it returns null only for the END of the stream.
                    * it returns an empty String if two newlines appear in a row.
                    */
                    while((line = input.readLine()) != null) {
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }
                } finally {
                    input.close();
                }
            } catch(IOException ex) {
                ExceptionHandler.handleException(ex, true);
            }

            textSourceCode.setText(contents.toString());
        } else {
            textSourceCode.setText("");
        }
    }

    private void retrieveAvailableAgents() {
        // remove all current entries and remove the selection
        treeAgents.removeAll();
        handleAgentSelection(null);

        // add the new entries
        File agentsDir = new File("agents/");
        for(File agentFile: agentsDir.listFiles()) {
            if(agentFile.getName().endsWith(".py") || agentFile.getName().endsWith(".groovy")) {
                TreeItem item = new TreeItem(treeAgents, SWT.NONE);
                item.setImage(agentFile.getName().endsWith(".py") ? imageRegistry.get("python") : imageRegistry
                        .get("groovy"));
                item.setText(agentFile.getName());
                item.setData(agentFile);
            }
        }
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        getShell().setText("Driving Agent Settings");
        setTitle("Review available driving agents");
        setMessage("Review the driving agents you have installed.", IMessageProvider.INFORMATION);
        return super.createButtonBar(parent);
    }

    @Override
    protected Control createContents(Composite parent) {
        getShell().setSize(800, 600);
        return super.createContents(parent);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        // handle the click on the 'Close' button
        if(buttonId == IDialogConstants.CLOSE_ID)
            close();

        super.buttonPressed(buttonId);
    }
}

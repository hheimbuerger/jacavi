package de.jacavi.rcp.dlg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarControllerManager;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.agent.DrivingAgentController;
import de.jacavi.appl.controller.agent.DrivingAgentController.ScriptExecutionException;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.util.ExceptionHandler;



public class AgentSettingsDialog extends TitleAreaDialog {
    private final Font sourceCodeFont;

    private final Font resultsFont;

    private final ImageRegistry imageRegistry;

    private final CarControllerManager carControllerManager;

    private Tree treeAgents;

    private Text textSourceCode;

    private Text textResults;

    private ToolItem toolItemSave;

    private ToolItem toolItemDryRun;

    private boolean editReady = false;

    private boolean dirty = false;

    private DrivingAgentController openScript;

    public AgentSettingsDialog(Shell parentShell) {
        super(parentShell);

        carControllerManager = (CarControllerManager) ContextLoader.getBean("carControllerManagerBean");

        imageRegistry = new ImageRegistry();
        imageRegistry.put("python", Activator.getImageDescriptor("/icons/agent_types/python.png"));
        imageRegistry.put("groovy", Activator.getImageDescriptor("/icons/agent_types/groovy.png"));
        imageRegistry.put("famfamfam_disk", Activator.getImageDescriptor("/icons/famfamfam-silk/disk.png"));
        imageRegistry.put("famfamfam_arrow_refresh", Activator
                .getImageDescriptor("/icons/famfamfam-silk/arrow_refresh.png"));
        imageRegistry.put("famfamfam_flag_green", Activator.getImageDescriptor("/icons/famfamfam-silk/flag_green.png"));
        sourceCodeFont = new Font(Display.getDefault(), "Courier New", 10, SWT.NONE);
        resultsFont = new Font(Display.getDefault(), "Courier New", 8, SWT.BOLD);
    }

    @Override
    public boolean close() {
        if(dirty)
            if(!MessageDialog.openQuestion(getParentShell(), "Unsaved modifications",
                    "The current script has unsaved modifications, do you want to proceed without saving these?")) {
                return false;
            }

        imageRegistry.dispose();
        sourceCodeFont.dispose();
        return super.close();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create the composite holding all components
        final Composite c = new Composite(parent, SWT.NONE);
        c.setLayoutData(new GridData(GridData.FILL_BOTH));

        // create the layout manager
        FormLayout layout = new FormLayout();
        layout.marginTop = 10;
        layout.marginRight = 10;
        layout.marginBottom = 10;
        layout.marginLeft = 10;
        c.setLayout(layout);

        // create the toolbar
        ToolBar toolbar = new ToolBar(c, SWT.HORIZONTAL | SWT.FLAT);
        FormData fd6 = new FormData();
        fd6.top = new FormAttachment(0);
        fd6.left = new FormAttachment(0);
        toolbar.setLayoutData(fd6);

        // add the 'refresh' button to the toolbar
        ToolItem toolItemRefresh = new ToolItem(toolbar, SWT.NONE);
        toolItemRefresh.setText("Refresh");
        toolItemRefresh.setToolTipText("Updates the list of available agents.");
        toolItemRefresh.setImage(imageRegistry.get("famfamfam_arrow_refresh"));
        toolItemRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AgentSettingsDialog.this.retrieveAvailableAgents();
            }
        });

        // add a separator
        new ToolItem(toolbar, SWT.SEPARATOR);

        // add the 'save' button to the toolbar
        toolItemSave = new ToolItem(toolbar, SWT.NONE);
        toolItemSave.setText("Save");
        toolItemSave.setToolTipText("Saves the modifications to disk. (Ctrl-S)");
        toolItemSave.setImage(imageRegistry.get("famfamfam_disk"));
        toolItemSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AgentSettingsDialog.this.handleSaveClick(e);
            }
        });
        toolItemSave.setEnabled(false);

        // add the 'dry run' button to the toolbar
        toolItemDryRun = new ToolItem(toolbar, SWT.NONE);
        toolItemDryRun.setText("Dry run");
        toolItemDryRun.setToolTipText("Runs the agent once with null parameters -- not supported by all agents. (F5)");
        toolItemDryRun.setImage(imageRegistry.get("famfamfam_flag_green"));
        toolItemDryRun.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AgentSettingsDialog.this.handleTestRunClick(e);
            }
        });
        toolItemDryRun.setEnabled(false);

        // create the tree listing the agents
        treeAgents = new Tree(c, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        FormData fd1 = new FormData();
        fd1.top = new FormAttachment(toolbar, 10, SWT.BOTTOM);
        fd1.left = new FormAttachment(0);
        fd1.width = 200;
        fd1.bottom = new FormAttachment(100);
        treeAgents.setLayoutData(fd1);
        treeAgents.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AgentSettingsDialog.this.handleAgentSelection(e);
            }
        });

        // create the text field showing the source code
        textSourceCode = new Text(c, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        FormData fd2 = new FormData();
        fd2.top = new FormAttachment(treeAgents, 0, SWT.TOP);
        fd2.left = new FormAttachment(treeAgents, 10, SWT.RIGHT);
        fd2.right = new FormAttachment(100);
        fd2.bottom = new FormAttachment(70);
        textSourceCode.setLayoutData(fd2);
        textSourceCode.setFont(sourceCodeFont);
        textSourceCode.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if(editReady)
                    setDirty(true);
            }
        });
        textSourceCode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(((e.stateMask & SWT.CTRL) == SWT.CTRL) && (e.keyCode == 's')) {
                    if(toolItemSave.getEnabled())
                        AgentSettingsDialog.this.handleSaveClick(null);
                    e.doit = false;
                } else if(e.keyCode == SWT.F5) {
                    if(toolItemDryRun.getEnabled())
                        AgentSettingsDialog.this.handleTestRunClick(null);
                    e.doit = false;
                }
            }
        });

        // create the text field holding the compilation/dry run results
        textResults = new Text(c, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER | SWT.READ_ONLY);
        FormData fd3 = new FormData();
        fd3.top = new FormAttachment(textSourceCode, 10, SWT.BOTTOM);
        fd3.left = new FormAttachment(textSourceCode, 0, SWT.LEFT);
        fd3.right = new FormAttachment(textSourceCode, 0, SWT.RIGHT);
        fd3.bottom = new FormAttachment(100);
        textResults.setLayoutData(fd3);
        textResults.setFont(resultsFont);

        // fill the tree with all known agents
        retrieveAvailableAgents();

        return c;
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
        getShell().setSize(800, 700);
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

    private void setDirty(boolean dirty) {
        this.dirty = dirty;
        toolItemSave.setEnabled(dirty);
    }

    protected void handleAgentSelection(SelectionEvent e) {
        if(e != null && e.item instanceof TreeItem) {
            if(dirty)
                if(MessageDialog.openQuestion(getParentShell(), "Unsaved modifications",
                        "The current script has unsaved modifications, do you want to save before proceeding?"))
                    handleSaveClick(e);

            TreeItem selectedItem = (TreeItem) e.item;
            openScript = (DrivingAgentController) selectedItem.getData();
            File agentFile = openScript.getAgentFile();

            StringBuilder contents = new StringBuilder();

            try {
                BufferedReader input = new BufferedReader(new FileReader(agentFile));
                try {
                    String line = null;
                    while((line = input.readLine()) != null) {
                        contents.append(line);
                        contents.append(System.getProperty("line.separator"));
                    }
                } finally {
                    input.close();
                }

                editReady = false;

                textSourceCode.setText(contents.toString());
                toolItemDryRun.setEnabled(true);
                setDirty(false);
                textResults.setText("");
                editReady = true;
                return;
            } catch(IOException ex) {
                ExceptionHandler.handleException(ex, true);
            }
        }

        editReady = false;
        openScript = null;
        textSourceCode.setText("");
        textResults.setText("");
        toolItemDryRun.setEnabled(false);
        setDirty(false);
    }

    protected void handleSaveClick(SelectionEvent e) {
        if(openScript != null) {
            File agentFile = openScript.getAgentFile();
            try {
                FileWriter fileWriter = new FileWriter(agentFile);
                fileWriter.write(textSourceCode.getText());
                fileWriter.close();
                setDirty(false);
            } catch(IOException exc) {
                ExceptionHandler.handleException(exc, true);
            }
        }
    }

    protected void handleTestRunClick(SelectionEvent e) {
        if(openScript != null) {
            try {
                ControllerSignal signal = openScript.dryRun();
                textResults.setText("Script completed successfully and returned: ControllerSignal(" + signal.getSpeed()
                        + ", " + signal.isTrigger() + ")");
            } catch(ScriptExecutionException exc) {
                textResults.setText(exc.getMessage());
            }
        }
    }

    private void retrieveAvailableAgents() {
        carControllerManager.redetectAgents();
        refreshAgentList();
    }

    private void refreshAgentList() {
        // remove all current entries and remove the selection
        treeAgents.removeAll();
        handleAgentSelection(null);

        // add the new entries
        for(DrivingAgentController dac: carControllerManager.getDrivingAgents()) {
            TreeItem item = new TreeItem(treeAgents, SWT.NONE);
            switch(dac.getScriptType()) {
                case PYTHON:
                    item.setImage(imageRegistry.get("python"));
                    break;
                case GROOVY:
                    item.setImage(imageRegistry.get("groovy"));
                    break;
                default:
                    throw new RuntimeException("Unexpected script type.");
            }
            item.setText(dac.getName());
            item.setData(dac);
        }
    }

}

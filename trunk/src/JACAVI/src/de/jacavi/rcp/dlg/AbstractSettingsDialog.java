package de.jacavi.rcp.dlg;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;



public abstract class AbstractSettingsDialog extends TitleAreaDialog {

    protected static class TabDescriptor {
        public String title;

        public Image icon;

        public Image image;

        public TabDescriptor(Image icon, Image image, String title) {
            this.icon = icon;
            this.image = image;
            this.title = title;
        }
    }

    private final Color colorDarkWhite;

    private final Color colorLightBlue;

    protected final Font headingFont;

    protected Map<String, Image> imageManager = new HashMap<String, Image>();

    private CTabFolder tabFolder;

    private List listDevices;

    public AbstractSettingsDialog(Shell parentShell) {
        super(parentShell);

        // prepare the font
        Display display = Display.getDefault();
        headingFont = new Font(display, "Arial", 11, SWT.BOLD);

        // prepare the colors
        colorDarkWhite = new Color(display, 242, 244, 247);
        colorLightBlue = new Color(display, 157, 167, 195);
    }

    @Override
    public boolean close() {
        for(Image i: imageManager.values())
            i.dispose();
        headingFont.dispose();
        colorDarkWhite.dispose();
        colorLightBlue.dispose();
        return super.close();
    }

    @Override
    protected Control createContents(Composite parent) {
        getShell().setSize(500, 700);
        return super.createContents(parent);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        setDescriptionTexts();
        return super.createButtonBar(parent);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);

        // prepare the dialog layout
        content.setLayoutData(new GridData(GridData.FILL_BOTH));

        // prepare the composite layout
        GridLayout layout = new GridLayout(1, false);
        layout.marginTop = 10;
        layout.marginRight = 10;
        layout.marginBottom = 10;
        layout.marginLeft = 10;
        layout.verticalSpacing = 10;
        content.setLayout(layout);

        // create the tab folder
        tabFolder = new CTabFolder(content, SWT.TOP | SWT.BORDER);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        tabFolder.setTabHeight(22);
        tabFolder.setSimple(false);
        tabFolder.setSelectionBackground(new Color[] { colorDarkWhite, colorLightBlue }, new int[] { 50 }, true);

        // add a selection listener to the tab folder which immediately passes the focus on to the inner control of the
        // tab -- this is necessary to avoid an ugly focus marker on windows (the tab title is underlined)
        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if(e.item != null)
                    ((CTabItem) e.item).getControl().setFocus();
            }
        });

        // create the composite holding the device list at the bottom
        Composite bottomSectionComposite = new Composite(content, SWT.NONE);
        bottomSectionComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        FormLayout bottomSectionLayout = new FormLayout();
        bottomSectionComposite.setLayout(bottomSectionLayout);

        // add the label
        Label labelDeviceList = new Label(bottomSectionComposite, SWT.WRAP);
        labelDeviceList.setText("List of configured devices:");
        FormData labelFormData = new FormData();
        labelFormData.top = new FormAttachment(0, 10);
        labelFormData.left = new FormAttachment(0);
        labelDeviceList.setLayoutData(labelFormData);

        // add the device list
        listDevices = new List(bottomSectionComposite, SWT.BORDER);
        FormData listFormData = new FormData();
        listFormData.top = new FormAttachment(labelDeviceList, 5); // 5 pixels from the label
        listFormData.height = 80; // fixed height of 80 pixels
        listFormData.right = new FormAttachment(90, 0); // to 90% width
        listFormData.bottom = new FormAttachment(100); // to 100% height
        listFormData.left = new FormAttachment(10, 0); // from 10% width
        listDevices.setLayoutData(listFormData);

        // tell the subclass to create the tab items now
        createTabItems(tabFolder);

        // refresh the device list
        updateDeviceList();

        return content;
    }

    protected Composite prepareTabItem(String title, Image icon, Image image) {
        // create the corresponding tab item
        CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
        tab.setText(title);
        tab.setImage(icon);

        // create the inner composite and set it on the tab item
        Composite tabControl = new Composite(tabFolder, SWT.NONE);
        tab.setControl(tabControl);

        // create the layout manager for the inner composite
        GridLayout grid = new GridLayout(1, true);
        grid.verticalSpacing = 5;
        tabControl.setLayout(grid);

        // create the heading
        Label labelHeading = new Label(tabControl, SWT.NONE);
        labelHeading.setText(title);
        labelHeading.setFont(headingFont);
        labelHeading.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, false));

        // create the image box
        Label imagebox = new Label(tabControl, SWT.CENTER | SWT.SHADOW_NONE);
        imagebox.setImage(image);
        imagebox.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, true, false));

        // create the separator
        Label separator = new Label(tabControl, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_OUT);
        separator.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        // create the content composite that will be used by the subclass to insert its widgets
        Composite content = new Composite(tabControl, SWT.NONE);
        content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        // return that content composite
        return content;
    }

    protected final void updateDeviceList() {
        listDevices.removeAll();
        fillDeviceList(listDevices);
    }

    abstract protected void setDescriptionTexts();

    abstract protected void createTabItems(CTabFolder tabFolder);

    abstract protected void fillDeviceList(List deviceList);
}

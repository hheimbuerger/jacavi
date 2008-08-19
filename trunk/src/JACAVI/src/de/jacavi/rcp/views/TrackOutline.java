package de.jacavi.rcp.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.rcp.editors.TrackDesigner;



public class TrackOutline extends ViewPart implements IPartListener2, IPropertyListener {

    private static Log log = LogFactory.getLog(TileExplorer.class);

    public static final String ID = "JACAVI.trackOutline";

    private Track currentTrack;

    private TableViewer listViewer;

    public TrackOutline() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        window.getActivePage().addPartListener(this);
        this.addListenerObject(this);
    }

    @Override
    public void createPartControl(Composite parent) {

        listViewer = new TableViewer(parent);
        listViewer.getTable().setLinesVisible(true);

        // the following provokes that only one single column is painted. Otherwise two columns are painted as default
        TableColumn singleColumn = new TableColumn(listViewer.getTable(), SWT.NONE);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableColumnLayout.setColumnData(singleColumn, new ColumnWeightData(100));
        parent.setLayout(tableColumnLayout);

        listViewer.setContentProvider(new ArrayContentProvider());
        listViewer.setLabelProvider(new LabelProvider() {
            @Override
            public Image getImage(Object element) {
                return null;
            }

            @Override
            public String getText(Object element) {
                return ((TrackSection) element).getName();
            }
        });

        if(currentTrack != null) {
            listViewer.setInput(currentTrack.getSections());
        }

        // listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        // public void selectionChanged(SelectionChangedEvent event) {
        // IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        // StringBuffer sb = new StringBuffer("Selection - ");
        // sb.append("tatal " + selection.size() + " items selected: ");
        // for(Iterator iterator = selection.iterator(); iterator.hasNext();) {
        // sb.append(iterator.next() + ", ");
        // }
        // System.out.println(sb);
        // }
        // });

        // listViewer.addFilter(new ViewerFilter() {
        // @Override
        // public boolean select(Viewer viewer, Object parentElement, Object element) {
        //
        // if(((Language) element).isObjectOriented)
        // return true;
        // else
        // return false;
        // }
        // });
        //
        // listViewer.setSorter(new ViewerSorter() {
        // @Override
        // public int compare(Viewer viewer, Object e1, Object e2) {
        // return ((Language) e1).genre.compareTo(((Language) e2).genre);
        // }
        //
        // });

    }

    @Override
    public void setFocus() {}

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            log.debug(partRef.getPartName() + " activated");
            TrackDesigner activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            refresh(activeEditor);
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            TrackDesigner activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            activeEditor.addPropertyListener(this);
        }
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {}

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {}

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {}

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {}

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {}

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {}

    @Override
    public void propertyChanged(Object source, int propId) {
        if(source instanceof TrackDesigner && TrackDesigner.PROP_TRACK_CHANGED == propId) {
            TrackDesigner sourceDesigner = (TrackDesigner) source;
            refresh(sourceDesigner);
        }
    }

    private void refresh(TrackDesigner source) {
        currentTrack = source.getCurrentTrack();
        listViewer.setInput(currentTrack.getSections());
        listViewer.refresh();
        log.debug("TableViewer inside the TrackOutlineView refreshed");
    }
}

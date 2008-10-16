package de.jacavi.rcp.views;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.track.Tile;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.rcp.editors.TrackDesigner;



public class TrackOutline extends ViewPart implements IPartListener2, IPropertyListener {

    private static Log log = LogFactory.getLog(TrackOutline.class);

    public static final String ID = "JACAVI.trackOutline";

    private TrackDesigner activeEditor;

    private Track currentTrack;

    private TableViewer tilesTableViewer;

    private final ImageRegistry imageRegistry;

    public TrackOutline() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        window.getPartService().addPartListener(this);
        imageRegistry = new ImageRegistry();
        // this.addListenerObject(this);
    }

    @Override
    public void createPartControl(Composite parent) {

        tilesTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
        tilesTableViewer.getTable().setLinesVisible(true);

        // the following provokes that only one single column is painted. Otherwise two columns are painted as default
        TableColumn singleColumn = new TableColumn(tilesTableViewer.getTable(), SWT.NONE);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableColumnLayout.setColumnData(singleColumn, new ColumnWeightData(100));
        parent.setLayout(tableColumnLayout);

        tilesTableViewer.setContentProvider(new ArrayContentProvider());
        tilesTableViewer.setLabelProvider(new LabelProvider() {
            @Override
            public Image getImage(Object element) {
                Tile tile = ((TrackSection) element).getTile();
                Image scaledImage = imageRegistry.get(currentTrack.getTileset().getId() + "#" + tile.getId());
                if(scaledImage == null) {
                    Image image;
                    try {
                        image = new Image(Display.getDefault(), new FileInputStream(tile.getBitmapFile()));
                    } catch(FileNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                    scaledImage = new Image(Display.getDefault(), image.getImageData().scaledTo(16, 16));
                    imageRegistry.put(currentTrack.getTileset().getId() + "#" + tile.getId(), scaledImage);
                    image.dispose();
                }
                return scaledImage;
            }

            @Override
            public String getText(Object element) {
                return ((TrackSection) element).getName();
            }
        });

        if(currentTrack != null) {
            tilesTableViewer.setInput(currentTrack.getSections());
        }

        tilesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @SuppressWarnings("unchecked")
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                selection.iterator().toString();

                if(event.getSource() instanceof TableViewer) {
                    TableViewer source = (TableViewer) event.getSource();
                    LinkedList<TrackSection> sectionList = (LinkedList<TrackSection>) source.getInput();
                    for(int i = 0; i < sectionList.size(); i++) {
                        if(sectionList.get(i) == selection.getFirstElement()) {
                            activeEditor.getTrackWidget().setSelectedTile(i);
                        }

                    }
                }
            }
        });

    }

    @Override
    public void setFocus() {
        tilesTableViewer.getTable().setFocus();
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            log.debug(partRef.getPartName() + " activated");
            activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            refresh(activeEditor);
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            activeEditor.addPropertyListener(this);
        }
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            if(activeEditor == null) {
                log.debug(partRef.getPartName() + " closed");
                clear();
            }
        }
    }

    private void clear() {
        tilesTableViewer.getTable().clearAll();
        tilesTableViewer.getTable().setEnabled(false);
    }

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
        // handles PROP_TRACK_CHANGED and PROP_SELECTION_CHANGE
        if(source instanceof TrackDesigner) {
            TrackDesigner sourceDesigner = (TrackDesigner) source;
            if(TrackDesigner.PROP_TRACK_CHANGED == propId) {
                refresh(sourceDesigner);
            }

            // if no section is selection, set the selection to null(none)
            int selected = sourceDesigner.getTrackWidget().getSelectedTile();
            if(selected == -1) {
                tilesTableViewer.setSelection(null);
            } else {
                tilesTableViewer.getTable().select(selected);
            }
        }
    }

    private void refresh(TrackDesigner source) {
        currentTrack = source.getTrackWidget().getTrack();
        tilesTableViewer.setInput(currentTrack.getSections());
        tilesTableViewer.refresh();
        if(!tilesTableViewer.getTable().isEnabled()) {
            tilesTableViewer.getTable().setEnabled(true);
        }
        log.debug("TableViewer inside the TrackOutlineView refreshed");
    }

    /**
     * Dispose handler that is invoked when the view is closed.
     * <p>
     * Used to dispose the loaded images, because each of them is managing an operating system resource.
     */
    @Override
    public void dispose() {
        super.dispose();
        imageRegistry.dispose();
        tilesTableViewer.getTable().dispose();
    }
}

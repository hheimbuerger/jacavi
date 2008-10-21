package de.jacavi.rcp;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import de.jacavi.rcp.perspectives.EditorPerspective;
import de.jacavi.rcp.perspectives.RacePerspective;



public class ApplicationActionBarAdvisor extends ActionBarAdvisor implements IPerspectiveListener {

    private MenuManager fileMenu;

    private MenuManager trackMenu;

    private GroupMarker otherGroup;

    private MenuManager infoMenu;

    private IWorkbenchAction saveTrackAction;

    private IWorkbenchAction newTrackAction;

    private IWorkbenchAction preferencesAction;

    private IWorkbenchAction aboutAction;

    private IWorkbenchAction exitAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(final IWorkbenchWindow window) {
        window.addPerspectiveListener(this);
        newTrackAction = ActionFactory.NEW.create(window);
        newTrackAction.setText("New");
        register(newTrackAction);

        saveTrackAction = ActionFactory.SAVE.create(window);
        saveTrackAction.setText("Save");
        register(saveTrackAction);

        preferencesAction = ActionFactory.PREFERENCES.create(window);
        preferencesAction.setImageDescriptor(Activator.getImageDescriptor("/images/actions/preferences_16x16.png"));
        register(preferencesAction);

        aboutAction = ActionFactory.ABOUT.create(window);
        aboutAction.setImageDescriptor(Activator.getImageDescriptor("/images/icon/jacavi_icon_16x16.png"));
        register(aboutAction);

        exitAction = ActionFactory.QUIT.create(window);
        exitAction.setImageDescriptor(Activator.getImageDescriptor("/images/actions/exit_16x16.png"));
        register(exitAction);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        fileMenu = new MenuManager("&File", "fileMenu");
        menuBar.add(fileMenu);
        fileMenu.add(preferencesAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        trackMenu = new MenuManager("&Track", "trackMenu");
        menuBar.add(trackMenu);
        trackMenu.add(newTrackAction);
        trackMenu.add(new GroupMarker("trackGroup"));
        trackMenu.add(new Separator());
        trackMenu.add(saveTrackAction);

        // Group marker for other Actions, see plugin.xml
        otherGroup = new GroupMarker("other");
        menuBar.add(otherGroup);

        infoMenu = new MenuManager("&Help");
        menuBar.add(infoMenu);
        infoMenu.add(aboutAction);
    }

    @Override
    public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
        String labelShell = ApplicationWorkbenchWindowAdvisor.applicationTitle + " - " + perspective.getLabel();
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText(labelShell);
        if(perspective.getId().equals(RacePerspective.ID)) {
            saveTrackAction.setEnabled(false);
            newTrackAction.setEnabled(false);
            trackMenu.setVisible(false);
        } else if(perspective.getId().equals(EditorPerspective.ID)) {
            saveTrackAction.setEnabled(true);
            newTrackAction.setEnabled(true);
            trackMenu.setVisible(true);
        }
    }

    @Override
    public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}

}

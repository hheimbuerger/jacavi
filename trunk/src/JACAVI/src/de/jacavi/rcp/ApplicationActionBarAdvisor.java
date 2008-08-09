package de.jacavi.rcp;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;


public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IWorkbenchAction exitAction;
	private IWorkbenchAction saveTrackAction;
	private IWorkbenchAction newTrackAction;
	private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		newTrackAction = ActionFactory.NEW.create(window);
		newTrackAction.setText("New");
		register(newTrackAction);

		saveTrackAction = ActionFactory.SAVE.create(window);
		saveTrackAction.setText("Save");
		register(saveTrackAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File");
		menuBar.add(fileMenu);
		fileMenu.add(exitAction);
		

		MenuManager trackMenu = new MenuManager("&Track","trackMenu");
		menuBar.add(trackMenu);
		trackMenu.add(newTrackAction);
		trackMenu.add(new GroupMarker("trackGroup"));
		trackMenu.add(new Separator());
		trackMenu.add(saveTrackAction);

		menuBar.add(new GroupMarker("other"));

		MenuManager infoMenu = new MenuManager("&Help");
		menuBar.add(infoMenu);
		infoMenu.add(aboutAction);
	}

//	protected void fillCoolBar(ICoolBarManager coolBar) {
//		// This will add a new toolbar to the application
//		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
//		coolBar.add(new ToolBarContributionItem(toolbar, "main"));
//		// Add the entry to the toolbar
//		 toolbar.add(Factory.iSaveAction);
//	}

}

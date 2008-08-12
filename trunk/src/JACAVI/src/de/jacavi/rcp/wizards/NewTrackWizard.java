package de.jacavi.rcp.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TilesetRepository.TileSet;
import de.jacavi.rcp.actions.OpenTrackDesignerAction;
import de.jacavi.rcp.wizards.pages.CreateTrackPageOne;



public class NewTrackWizard extends Wizard implements INewWizard {

    @SuppressWarnings("unused")
    private IWorkbench workbench;

    @SuppressWarnings("unused")
    private IStructuredSelection selection;

    private CreateTrackPageOne one;

    public NewTrackWizard() {
        super();
        setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages() {
        one = new CreateTrackPageOne();
        addPage(one);
    }

    @Override
    public boolean performFinish() {
        System.out.println(one.getText1());
        Track track = new Track(TileSet.DEBUG);
        track.setTrackName(one.getText1());
        new OpenTrackDesignerAction(track).run();
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }

}

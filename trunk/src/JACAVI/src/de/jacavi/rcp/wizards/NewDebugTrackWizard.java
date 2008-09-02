package de.jacavi.rcp.wizards;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.TilesetRepository;



public class NewDebugTrackWizard extends NewTrackWizard {

    public NewDebugTrackWizard() {
        super(((TilesetRepository) ContextLoader.getBean("tilesetRepositoryBean")).getTileset("debug"));
    }
}

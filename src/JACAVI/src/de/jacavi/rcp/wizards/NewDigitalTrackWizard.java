package de.jacavi.rcp.wizards;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.TilesetRepository;



public class NewDigitalTrackWizard extends NewTrackWizard {

    public NewDigitalTrackWizard() {
        super(((TilesetRepository) ContextLoader.getBean("tilesetRepositoryBean")).getTileset("digital"));
    }
}

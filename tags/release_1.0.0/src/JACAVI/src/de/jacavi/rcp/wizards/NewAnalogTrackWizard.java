package de.jacavi.rcp.wizards;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.TilesetRepository;



public class NewAnalogTrackWizard extends NewTrackWizard {

    public NewAnalogTrackWizard() {
        super(((TilesetRepository) ContextLoader.getBean("tilesetRepositoryBean")).getTileset("analogue"));
    }
}

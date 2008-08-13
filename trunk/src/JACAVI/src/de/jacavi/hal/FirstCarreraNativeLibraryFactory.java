package de.jacavi.hal;

import de.jacavi.appl.ContextLoader;



public class FirstCarreraNativeLibraryFactory implements CarreraNativeLibraryFactory {

    @Override
    public TechnologyController initialiseCarreraLib(CarreraLibraryType type) {

        TechnologyController halController = null;

        switch(type) {
            case lib42:

                halController = (TechnologyController) ContextLoader.getBean("lib42technologyBean");

                break;

            case bluerider:
                // TODO
                break;
        }

        return halController;
    }

}

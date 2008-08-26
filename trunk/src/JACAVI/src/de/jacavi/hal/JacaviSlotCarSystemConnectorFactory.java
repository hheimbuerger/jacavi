package de.jacavi.hal;

import de.jacavi.hal.lib42.Lib42ConnectorAdapter;



public class JacaviSlotCarSystemConnectorFactory implements SlotCarSystemConnectorFactory {

    @Override
    public SlotCarSystemConnector initialiseCarreraLib(SlotCarSystemType type) {

        SlotCarSystemConnector halController = null;

        switch(type) {
            case lib42:

                halController = new Lib42ConnectorAdapter();
                break;

            case bluerider:
                // TODO
                break;
        }

        return halController;
    }

}

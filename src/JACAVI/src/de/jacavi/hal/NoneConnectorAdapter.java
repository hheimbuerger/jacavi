package de.jacavi.hal;

public class NoneConnectorAdapter implements SlotCarSystemConnector {

    @Override
    public void fullBreak(int carID) {
    // TODO Auto-generated method stub

    }

    @Override
    public int getSpeed(int carID) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSwitch(int carID) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSpeed(int carID, int speed) {
    // TODO Auto-generated method stub

    }

    @Override
    public int toggleSwitch(int carID) {
        // TODO Auto-generated method stub
        return 0;
    }
    /**
     * @author Florian Roth
     */
}

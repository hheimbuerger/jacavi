package de.jacavi.appl.controller.device;

import java.util.UUID;

import de.jacavi.appl.controller.CarController;



public abstract class DeviceController extends CarController {
    protected DeviceController(String name) {
        super(name);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * use this to adapt the needed speed (Range 0-100)
     * 
     * @return int normalised speed
     */
    abstract public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal);

}

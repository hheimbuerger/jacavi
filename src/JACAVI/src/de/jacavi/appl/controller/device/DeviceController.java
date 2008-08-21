package de.jacavi.appl.controller.device;

import java.util.UUID;

import de.jacavi.appl.controller.CarController;



public abstract class DeviceController implements CarController {
    private UUID id;

    private String name;

    protected DeviceController(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

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

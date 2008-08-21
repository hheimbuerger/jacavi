package de.jacavi.appl.controller.device;

import de.jacavi.appl.controller.CarController;



public interface DeviceController extends CarController {
    /**
     * use this to adapt the needed speed (Range 0-100)
     * 
     * @return int normalised speed
     */
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal);

}

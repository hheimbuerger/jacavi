package de.jacavi.appl.controller.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import de.jacavi.appl.controller.device.impl.KeyboardDevice;



public class InputDeviceManager {

    private final Map<UUID, DeviceController> inputDevices = new TreeMap<UUID, DeviceController>();

    public InputDeviceManager() {
        // TODO: should be read from configuration, just inserting some for testing here
        KeyboardDevice k1 = new KeyboardDevice("Keyboard 1");
        KeyboardDevice k2 = new KeyboardDevice("Keyboard 2");
        inputDevices.put(k1.getId(), k1);
        inputDevices.put(k2.getId(), k2);
    }

    public void addInputDevice(DeviceController controller) {
        inputDevices.put(controller.getId(), controller);

        // FIXME: Initializing input devices here for testing!
        controller.initialize();
    }

    public Collection<DeviceController> getInputDevices() {
        return inputDevices.values();
    }

    public List<DeviceController> getInputDevicesByType(Class<?> type) {
        List<DeviceController> result = new ArrayList<DeviceController>();
        for(DeviceController dc: inputDevices.values())
            if(dc.getClass() == type)
                result.add(dc);
        return result;
    }

    public void removeInputDevice(UUID id) {
        inputDevices.remove(id);
    }

    public void removeInputDevicesByType(Class<?> type) {
        List<UUID> toRemove = new ArrayList<UUID>();
        for(UUID id: inputDevices.keySet())
            if(inputDevices.get(id).getClass() == type)
                toRemove.add(id);
        for(UUID id: toRemove)
            inputDevices.remove(id);
    }
}

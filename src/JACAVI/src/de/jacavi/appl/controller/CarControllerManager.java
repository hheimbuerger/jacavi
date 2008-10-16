package de.jacavi.appl.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.agent.DrivingAgentController;
import de.jacavi.appl.controller.agent.ScriptController;
import de.jacavi.appl.controller.agent.impl.GroovyScriptController;
import de.jacavi.appl.controller.agent.impl.JythonScriptController;
import de.jacavi.appl.controller.agent.impl.XmlRpcController;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.impl.GameControllerDevice;
import de.jacavi.appl.controller.device.impl.GameControllerDeviceManager;
import de.jacavi.appl.controller.device.impl.KeyboardDevice;
import de.jacavi.appl.controller.device.impl.KeyboardLayout;
import de.jacavi.appl.controller.device.impl.MouseKeyboardDeviceAdapter;
import de.jacavi.appl.controller.device.impl.WiimoteDevice;
import de.jacavi.appl.controller.device.impl.WiimoteDeviceManager;
import de.jacavi.appl.controller.device.impl.GameControllerDeviceManager.GameControllerDescriptor;
import de.jacavi.appl.racelogic.Player;



public class CarControllerManager {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(CarControllerManager.class);

    private final Map<UUID, CarController> controllers = new TreeMap<UUID, CarController>();

    private GameControllerDeviceManager gameControllerDeviceManager;

    private WiimoteDeviceManager wiimoteDeviceManager;

    private final File agentsPath;

    public CarControllerManager(String agentsPath) {
        // TODO: should be read from configuration, just inserting some for testing here
        KeyboardDevice k1 = new KeyboardDevice("Keyboard (up/down/ctrl/del)", new KeyboardLayout(SWT.ARROW_UP,
                SWT.ARROW_DOWN, SWT.CONTROL, 'y', 'x', SWT.DEL));
        KeyboardDevice k2 = new KeyboardDevice("Keyboard (w/s/shift/q)", new KeyboardLayout('w', 's', SWT.SHIFT, 'e',
                'd', 'q'));
        controllers.put(k1.getId(), k1);
        controllers.put(k2.getId(), k2);

        // a mouse is assumed to be always available
        MouseKeyboardDeviceAdapter m1 = new MouseKeyboardDeviceAdapter("Mouse");
        controllers.put(m1.getId(), m1);

        // we also initialize two static XML-RPC controllers
        XmlRpcController xmlRpcController1 = new XmlRpcController("XML-RPC, port 8080", 8080);
        controllers.put(xmlRpcController1.getId(), xmlRpcController1);
        XmlRpcController xmlRpcController2 = new XmlRpcController("XML-RPC, port 8081", 8081);
        controllers.put(xmlRpcController2.getId(), xmlRpcController2);

        // game controllers have to be detected
        try {
            gameControllerDeviceManager = new GameControllerDeviceManager();
            redetectGameControllers();
        } catch(UnsatisfiedLinkError e) {
            logger
                    .warn(
                            "GameControllerDeviceManager could not be loaded, game controller support won't be available in this session",
                            e);
            gameControllerDeviceManager = null;
        }

        // and so do Wiimotes
        try {
            wiimoteDeviceManager = new WiimoteDeviceManager();
            redetectWiimotes();
        } catch(UnsatisfiedLinkError e) {
            logger.warn("WiimoteDeviceManager could not be loaded, Wiimote support won't be available in this session",
                    e);
            wiimoteDeviceManager = null;
        }

        // make sure the base path for the agents exists
        this.agentsPath = new File(agentsPath);
        if(!this.agentsPath.exists())
            try {
                throw new IllegalArgumentException("The specified base path for the agents does not exist: "
                        + this.agentsPath.getCanonicalPath());
            } catch(IOException e) {
                throw new IllegalArgumentException(e);
            }

        // also find out which driving agents are installed
        redetectAgents();
    }

    /**
     * Adds a new controller to the internal list.
     * 
     * @param controller
     *            the controller to add
     */
    public void addController(CarController controller) {
        controllers.put(controller.getId(), controller);
    }

    /**
     * Returns a list of all connected input devices, sorted by name in alphabetical order.
     * 
     * @return a sorted list of connected input devices
     */
    public List<DeviceController> getInputDevices() {
        List<DeviceController> result = new ArrayList<DeviceController>();
        for(CarController dc: controllers.values()) {
            if(dc instanceof DeviceController) {
                result.add((DeviceController) dc);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Returns a list of all unused (not assigned to any existing player) DeviceControllers if player==null if
     * player==null you will get all unused devices if player!=null you will get all+ the players one
     * 
     * @param player
     *            the except player
     * @return a sorted list of configured unused input devices
     */
    @SuppressWarnings("unchecked")
    public List<DeviceController> getUnusedDevices(Player player) {
        List<DeviceController> result = getInputDevices();
        List<Player> players = (List<Player>) ContextLoader.getBean("playersBean");
        for(Player p: players) {
            if(p != player) {
                if(p.getController() != null && p.getController() instanceof DeviceController) {
                    result.remove(p.getController());
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Returns a list of all driving agents, sorted by name in alphabetical order.
     * 
     * @return a sorted list of driving agents
     */
    public List<DrivingAgentController> getDrivingAgents() {
        List<DrivingAgentController> result = new ArrayList<DrivingAgentController>();
        for(CarController dc: controllers.values()) {
            if(dc instanceof DrivingAgentController) {
                result.add((DrivingAgentController) dc);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Returns a list of all script-based driving agents, sorted by name in alphabetical order.
     * 
     * @return a sorted list of script-based driving agents
     */
    public List<ScriptController> getDrivingScripts() {
        List<ScriptController> result = new ArrayList<ScriptController>();
        for(CarController dc: controllers.values()) {
            if(dc instanceof ScriptController) {
                result.add((ScriptController) dc);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Returns a sorted list of all connected input devices of a certain type (XyzDevice).
     * 
     * @param type
     *            the class of devices to return
     * @return a sorted list of all connected input devices of the given type
     */
    public List<DeviceController> getInputDevicesByType(Class<?> type) {
        List<DeviceController> result = new ArrayList<DeviceController>();
        for(CarController dc: controllers.values()) {
            if(dc instanceof DeviceController) {
                for(Class<?> superclass = dc.getClass(); superclass != Object.class; superclass = superclass
                        .getSuperclass()) {
                    if(superclass == type) {
                        result.add((DeviceController) dc);
                        break;
                    }
                }
            }
        }

        Collections.sort(result);
        return result;
    }

    /**
     * Removes an input device from the internal list after deactivating it.
     * 
     * @param id
     *            the ID of the device to remove
     */
    public void removeInputDevice(UUID id) {
        if(controllers.containsKey(id)) {
            controllers.get(id).cleanup();
            controllers.remove(id);
        } else {
            throw new IllegalArgumentException("tried removing an ID that does not exist!");
        }
    }

    /**
     * Removes all input devices of a given type.
     * 
     * @param type
     *            the class of input devices to remove (XyzDevice)
     */
    public void removeControllersByType(Class<?> type) {
        List<UUID> toRemove = new ArrayList<UUID>();
        for(UUID id: controllers.keySet()) {
            // walk the class hierarchy up and check if controller.get(id) is an instance of type
            for(Class<?> superclass = controllers.get(id).getClass(); superclass != Object.class; superclass = superclass
                    .getSuperclass()) {
                if(superclass == type) {
                    toRemove.add(id);
                    break;
                }
            }
        }
        for(UUID id: toRemove) {
            removeInputDevice(id);
        }
    }

    /**
     * Returns true if the given ID is valid (corresponds to an available input device), false otherwise.
     * 
     * @param id
     *            the ID to check
     * @return true if the given ID is valid (corresponds to an available input device), false otherwise
     */
    public boolean isIdValid(UUID id) {
        return controllers.containsKey(id);
    }

    /**
     * Determines if support for game controllers is available.
     * 
     * @return true if the game controller library could be loaded, false otherwise
     */
    public boolean isGameControllerSupportAvailable() {
        return gameControllerDeviceManager != null;
    }

    /**
     * Determines if support for Wiimotes is available.
     * 
     * @return true if the Wiimote library could be loaded, false otherwise
     */
    public boolean isWiimoteSupportAvailable() {
        return wiimoteDeviceManager != null;
    }

    /**
     * Redetects all available game controllers.
     * <p>
     * FIXME: Currently, previously connected devices are always thrown away. So devices that are still connected are
     * given new IDs.
     */
    public void redetectGameControllers() {
        // run the detection
        removeControllersByType(GameControllerDevice.class);
        GameControllerDescriptor[] gameControllers = gameControllerDeviceManager.scanForGameControllers();

        // add the detected devices to the device manager
        if(gameControllers != null) {
            for(GameControllerDescriptor gameController: gameControllers) {
                addController(new GameControllerDevice(gameController.name, gameControllerDeviceManager
                        .getGameController(gameController), gameController));
            }
        }
    }

    /**
     * Redetects all available Wiimotes.
     * <p>
     * FIXME: Currently, previously connected devices are always thrown away. So devices that are still connected are
     * given new IDs.
     */
    public void redetectWiimotes() {
        // run the detection
        removeControllersByType(WiimoteDevice.class);
        int numWiimotesConnected = wiimoteDeviceManager.scanForWiimotes();

        // add the detected devices to the device manager
        for(int i = 1; i <= numWiimotesConnected; i++) {
            addController(new WiimoteDevice("Wiimote " + i, wiimoteDeviceManager.getWiimote(i)));
        }
    }

    /**
     * Redetects all available agents (script files).
     */
    public void redetectAgents() {
        removeControllersByType(ScriptController.class);

        for(File agentFile: agentsPath.listFiles()) {
            if(agentFile.getName().endsWith(".py")) {
                addController(new JythonScriptController(agentFile.getName().substring(0,
                        agentFile.getName().length() - 3)
                        + " (Jython)", agentFile));
            } else if(agentFile.getName().endsWith(".groovy")) {
                addController(new GroovyScriptController(agentFile.getName().substring(0,
                        agentFile.getName().length() - 7)
                        + " (Groovy)", agentFile));
            }
        }
    }

    public CarController getDevice(UUID id) {
        return controllers.get(id);
    }

}

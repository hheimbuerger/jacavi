package de.jacavi.rcp.actions.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.eclipse.ui.PlatformUI;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.CarControllerManager;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Tileset;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.ConnectorConfigurationManager;
import de.jacavi.hal.SlotCarDriveConnector;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.hal.analogue.AnalogueDriveConnector;
import de.jacavi.hal.bluerider.BlueriderDriveConnector;
import de.jacavi.hal.lib42.Lib42DriveConnector;
import de.jacavi.rcp.editors.TrackDesigner;



public class RaceValidator {

    private final List<String> errorMessages;

    public RaceValidator() {
        errorMessages = new ArrayList<String>();
    }

    @ValidationDescription("Validating number of players...")
    public boolean validateNumberOfPlayer(List<Player> players) {
        boolean valid = true;

        // determine the current track from the active editor
        TrackDesigner activeEditor = (TrackDesigner) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().getActiveEditor();

        if(activeEditor == null) {
            errorMessages.add("Invalid track! Please create a new or open an existing track.");
            return false;
        }
        if(activeEditor.isDirty()) {
            errorMessages.add("Invalid track! Please save your track before starting a race on it.");
            return false;
        }

        Track activeTrack = activeEditor.getTrack();

        if(players.size() == 0) {
            valid = false;
            errorMessages.add("No players available! Please check your player settings.");
        }
        if(players.size() > activeTrack.getStartingPoints().size()) {
            valid = false;
            errorMessages.add("The active track has starting points for only " + activeTrack.getStartingPoints().size()
                    + " players. Remove some players or add additional starting points to the track.");
        }

        return valid;
    }

    @ValidationDescription("Validating player names...")
    public boolean validatePlayerNames(List<Player> players) {
        boolean valid = true;
        if(players.size() == 0) {
            return false;
        }

        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getName() == null || players.get(i).getName().trim().length() == 0) {
                errorMessages.add("Player no. " + (i + 1) + " has an invalid name! Please check your player settings.");
                valid = false;
            }
        }
        return valid;
    }

    @ValidationDescription("Validating player controllers...")
    public boolean validatePlayerController(List<Player> players) {
        CarControllerManager deviceManager = (CarControllerManager) ContextLoader.getBean("carControllerManagerBean");
        boolean valid = true;
        if(players.size() == 0) {
            return false;
        }

        for(int i = 0; i < players.size(); i++) {
            CarController carController = players.get(i).getController();
            if(carController == null || carController.getId() == null
                    || !deviceManager.isIdValid(carController.getId())) {
                errorMessages.add("Player no. " + (i + 1)
                        + " has an invalid controller! Please check your player and input device or driving agents settings.");
                valid = false;
            }
        }
        // check that every controller is only used by one Player
        for(int i = 0; i < players.size(); i++) {

            if(players.get(i).getController() == null) {
                continue;
            }

            UUID curUUID = players.get(i).getController().getId();
            int cntEqual = 0;
            for(Player p2: players) {
                if(p2.getController() != null) {
                    if(p2.getController().getId().equals(curUUID)) {
                        cntEqual++;
                    }
                }
            }
            if(cntEqual > 1) {
                errorMessages.add("Player no. " + (i + 1) + " does not have a unique car controller.");
                valid = false;
            }

        }
        return valid;
    }

    /**
     * Validates each players connector that its:
     * <ul>
     * <li>not null</li>
     * <li>an valid uuid</li>
     * <li>the right combination of current focused track type and connector</li>
     * </ul>
     * <p>
     * It is also validated, if no editor is open.
     * <p>
     * Available Track types are analogue,digital and debug.
     * <p>
     * Lib42 can only be combined with digital. Analogue and Bluerider can only be combined with analogue. Simulation
     * can be combined with every track. All connectors can be combined with debug.
     * 
     * @param players
     * @return true if its valid otherwise false
     */
    @ValidationDescription("Validating player connectors...")
    public boolean validatePlayersConnectorAgainstTrack(List<Player> players) {
        boolean valid = true;
        if(players.size() == 0) {
            return false;
        }

        // determine the current track from the active editor
        TrackDesigner activeEditor = (TrackDesigner) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().getActiveEditor();

        if(activeEditor == null) {
            return false;
        }

        Track activeTrack = activeEditor.getTrack();
        /*
         * available track types are analogue,digital and debug
         */
        String currentTrackType = activeTrack.getTileset().getId();

        // get the ConnectorCOnfigurationManager
        ConnectorConfigurationManager connectionManager = (ConnectorConfigurationManager) ContextLoader
                .getBean("connectorManager");
        for(int i = 0; i < players.size(); i++) {
            // get the connector
            SlotCarSystemConnector systemConnector = players.get(i).getSlotCarSystemConnector();

            // check if the id of the connector is valid and if its not null
            if(systemConnector == null) {
                valid = false;
                errorMessages.add("Player no. " + (i + 1) + " has no connector selected.");
                continue;
            }
            if(!connectionManager.isIdValid(systemConnector.getId())) {
                valid = false;
                errorMessages
                        .add("Player no. "
                                + (i + 1)
                                + " has an invalid connector. The previously selected connector was modified and no longer valid.");
                continue;
            }

            // get the drive connector
            SlotCarDriveConnector driveConnector = systemConnector.getDriveConnector();

            // get the interfaces of the DriveConnector
            List<Class<?>> interfaces = Arrays.asList(driveConnector.getClass().getInterfaces());

            /*
             * on digital track only lib42 on analogue track all except lib42 on
             * debug track all? SimulatedDriveConnector on all?
             */
            if(currentTrackType.equals("digital")) {
                if(interfaces.contains(BlueriderDriveConnector.class)) {
                    valid = false;
                    errorMessages.add("Player no. " + (i + 1)
                            + " has an invalid connector. Can not combine BlueriderConnector with digital track.");
                } else if(interfaces.contains(AnalogueDriveConnector.class)) {
                    valid = false;
                    errorMessages.add("Player no. " + (i + 1)
                            + " has an invalid connector. Can not combine AnalogueConnector with digital track.");
                }

            } else if(currentTrackType.equals("analogue")) {
                if(interfaces.contains(Lib42DriveConnector.class)) {
                    valid = false;
                    errorMessages.add("Player no. " + (i + 1)
                            + " has an invalid connector. Can not combine Lib42Connector with analogue track.");
                }
            }

        }

        // check that every connector is only used by one Player
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getSlotCarSystemConnector() == null) {
                continue;
            }

            UUID curUUID = players.get(i).getSlotCarSystemConnector().getId();
            int cntEqual = 0;
            for(Player p2: players) {
                if(p2.getSlotCarSystemConnector() != null) {
                    if(p2.getSlotCarSystemConnector().getId().equals(curUUID)) {
                        cntEqual++;
                    }
                }
            }
            if(cntEqual > 1) {
                errorMessages.add("Player no. " + (i + 1) + " does not have a unique connector.");
                valid = false;
            }
        }
        return valid;
    }

    @ValidationDescription("Validating cars and track...")
    public boolean validateCarsTrack(List<Player> players) {
        boolean valid = true;

        // determine the current track from the active editor
        TrackDesigner activeEditor = (TrackDesigner) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().getActiveEditor();

        if(activeEditor == null) {
            return false;
        }

        Track activeTrack = activeEditor.getTrack();
        /*
         * available track types are analogue,digital and debug
         */
        String currentTrackType = activeTrack.getTileset().getId();

        for(int i = 0; i < players.size(); i++) {
            List<String> tilesetIDs = new ArrayList<String>();
            for(Tileset t: players.get(i).getCar().getTilesets()) {
                tilesetIDs.add(t.getId());
            }

            if(currentTrackType.equals("analogue")) {
                if(!tilesetIDs.contains("analogue")) {
                    valid = false;
                    errorMessages.add("Player no. " + (i + 1) + " has an incompatible car for this track.");
                }
            } else if(currentTrackType.equals("digital")) {
                if(!tilesetIDs.contains("digital")) {
                    valid = false;
                    errorMessages.add("Player no. " + (i + 1) + " has an incompatible car for this track.");
                }
            }
        }
        return valid;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

}

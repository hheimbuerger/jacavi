package de.jacavi.rcp.actions.validator;

import java.util.ArrayList;
import java.util.List;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.script.DrivingAgentController;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.hal.SlotCarSystemDriveConnector;



public class RaceValidator {

    private final List<String> errorMessages;

    public RaceValidator() {
        errorMessages = new ArrayList<String>();
    }

    @ValidatationTaskName(description = "Validating Number of Player...")
    public boolean validateNumberOfPlayer(List<Player> players) {
        if(players.size() != 0) {
            return true;
        } else {
            errorMessages.add("No Players available! Please check your player settings.");
            return false;
        }
    }

    @ValidatationTaskName(description = "Validating Player Names...")
    public boolean validatePlayerNames(List<Player> players) {
        boolean valid = true;
        if(players.size() == 0)
            return false;

        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getName() == null || players.get(i).getName().trim().length() == 0) {
                errorMessages.add("Player No. " + (i + 1) + " has an invalid name! Please check your player settings.");
                valid = false;
            }
        }
        return valid;
    }

    @ValidatationTaskName(description = "Validating Player Controller...")
    public boolean validatePlayerController(List<Player> players) {
        InputDeviceManager deviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");
        boolean valid = true;
        if(players.size() == 0)
            return false;

        for(int i = 0; i < players.size(); i++) {
            CarController carController = players.get(i).getController();
            if(carController instanceof DrivingAgentController) {
                continue;
            } else if(carController == null || carController.getId() == null
                    || !deviceManager.isIdValid(carController.getId())) {
                errorMessages.add("Player No. " + (i + 1)
                        + " has an invalid controller! Please check your player and input device settings.");
                valid = false;
            }
        }
        return valid;
    }

    @ValidatationTaskName(description = "Validating Player Technologies...")
    public boolean validatePlayerTechnologies(List<Player> players) {
        boolean valid = true;
        if(players.size() == 0)
            return false;

        for(int i = 0; i < players.size(); i++) {
            SlotCarSystemDriveConnector systemConnector = players.get(i).getSlotCarSystemConnector();
            if(systemConnector == null) {// TODO check also an ID ???
                errorMessages.add("Player No. " + (i + 1)
                        + " has an invalid connector! Please check your player and connector settings.");
                valid = false;
            }
        }
        return valid;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}

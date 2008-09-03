package de.jacavi.rcp.actions.validator;

import java.util.ArrayList;
import java.util.List;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.controller.script.DrivingAgentController;
import de.jacavi.appl.racelogic.Race;
import de.jacavi.hal.SlotCarSystemDriveConnector;



public class RaceValidator {

    private final List<String> errorMessages;

    public RaceValidator() {
        errorMessages = new ArrayList<String>();
    }

    @ValidatationTaskName(description = "Validating Number of Player...")
    public boolean validateNumberOfPlayer(Race race) {
        if(race.getPlayers().size() != 0) {
            return true;
        } else {
            errorMessages.add("No Players available! Please check your player settings.");
            return false;
        }
    }

    @ValidatationTaskName(description = "Validating Player Names...")
    public boolean validatePlayerNames(Race race) {
        boolean valid = true;
        if(race.getPlayers().size() == 0)
            return false;

        for(int i = 0; i < race.getPlayers().size(); i++) {
            if(race.getPlayers().get(i).getName() == null || race.getPlayers().get(i).getName().trim().length() == 0) {
                errorMessages.add("Player No. " + (i + 1) + " has an invalid name! Please check your player settings.");
                valid = false;
            }
        }
        return valid;
    }

    @ValidatationTaskName(description = "Validating Player Controller...")
    public boolean validatePlayerController(Race race) {
        InputDeviceManager deviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");
        boolean valid = true;
        if(race.getPlayers().size() == 0)
            return false;

        for(int i = 0; i < race.getPlayers().size(); i++) {
            CarController carController = race.getPlayers().get(i).getController();
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
    public boolean validatePlayerTechnologies(Race race) {
        boolean valid = true;
        if(race.getPlayers().size() == 0)
            return false;

        for(int i = 0; i < race.getPlayers().size(); i++) {
            SlotCarSystemDriveConnector systemConnector = race.getPlayers().get(i).getSlotCarSystemConnector();
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

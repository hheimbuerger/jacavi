package de.jacavi.rcp.actions.validator;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.device.InputDeviceManager;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.racelogic.Race;
import de.jacavi.hal.SlotCarSystemConnector;



public class RaceValidator {

    @ValidatationTaskName(description = "Validating Number of Player...")
    public static boolean validateNumberOfPlayer(Race race) {
        return race.getPlayers().size() != 0;
    }

    @ValidatationTaskName(description = "Validating Player Names...")
    public static boolean validatePlayerNames(Race race) {
        boolean valid = false;
        for(Player player: race.getPlayers()) {
            if(player.getName() != null || player.getName().trim().length() != 0)
                valid = true;
        }
        return valid;
    }

    @ValidatationTaskName(description = "Validating Player Devices...")
    public static boolean validatePlayerDevices(Race race) {
        InputDeviceManager deviceManager = (InputDeviceManager) ContextLoader.getBean("inputDeviceManagerBean");
        boolean valid = false;
        for(Player player: race.getPlayers()) {
            CarController carController = player.getController();
            if(carController != null) {
                if(carController.getId() != null || deviceManager.isIdValid(carController.getId())) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    @ValidatationTaskName(description = "Validating Player Technologies...")
    public static boolean validatePlayerTechnologies(Race race) {
        boolean valid = false;
        for(Player player: race.getPlayers()) {
            SlotCarSystemConnector systemConnector = player.getTechnologyController();
            if(systemConnector != null)// TODO check also an ID ???
                valid = true;
        }
        return valid;
    }
}

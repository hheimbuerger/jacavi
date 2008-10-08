package de.jacavi.appl.racelogic.tda;

import java.util.Arrays;
import java.util.List;

import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.SlotCarDriveConnector;
import de.jacavi.hal.analogue.AnalogueDriveConnector;
import de.jacavi.hal.bluerider.BlueriderDriveConnector;
import de.jacavi.hal.lib42.Lib42DriveConnector;
import de.jacavi.hal.simulation.SimulationDriveConnector;



/**
 * TDA injection factory
 */
public class TDAInjectorFactory {

    /**
     * Injects the specific TDA to the player by checking players DriveConnector
     * <p>
     * 
     * @param player
     *            The player who gets the specific TDA injected
     */
    public void initializeTDA(Player player, Track currentTrack, int raceTimerInterval) {
        SlotCarDriveConnector driveConnector = player.getSlotCarSystemConnector().getDriveConnector();
        List<Class<?>> interfaces = Arrays.asList(driveConnector.getClass().getInterfaces());
        if(interfaces.contains(Lib42DriveConnector.class)) {
            player.setTda(new Lib42TDA(player, currentTrack, raceTimerInterval));
        } else if(interfaces.contains(AnalogueDriveConnector.class)) {
            player.setTda(new AnalogueTDA(player, currentTrack, raceTimerInterval));
        } else if(interfaces.contains(BlueriderDriveConnector.class)) {
            player.setTda(new SimulationTDA(player, currentTrack, raceTimerInterval));
        } else if(interfaces.contains(SimulationDriveConnector.class)) {
            player.setTda(new SimulationTDA(player, currentTrack, raceTimerInterval));
        }
    }
}

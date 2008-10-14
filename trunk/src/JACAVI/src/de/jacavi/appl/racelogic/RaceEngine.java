package de.jacavi.appl.racelogic;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.tda.TDAInjectorFactory;
import de.jacavi.appl.racelogic.tda.TrackDataApproximator;
import de.jacavi.appl.track.StartingPoint;
import de.jacavi.appl.track.Statistics;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.rcp.util.Check;
import de.jacavi.rcp.views.RaceView;



/**
 * @author fro
 */
public class RaceEngine {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(RaceEngine.class);

    private RaceView raceView = null;

    private Timer raceTimer = null;

    private int raceTimerInterval = 0;

    boolean isTimerRunning = false;

    private Track track;

    private List<Player> players;

    private RaceEngine() {}

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setRaceTimerInterval(int raceTimerInterval) {
        this.raceTimerInterval = raceTimerInterval;
    }

    /**
     * Start the RaceTimerTask
     * <p>
     * 
     * @param activeTrack
     *            The focused Track the race should start at
     * @param raceView
     *            The RaceView the race is shown in
     */
    public void startRace(Track activeTrack, RaceView raceView) {
        Check.Require(activeTrack != null, "activeTrack may not be null");
        Check.Require(raceView != null, "raceView may not be null");
        ((RaceStatisticsManager) ContextLoader.getBean("statisticsRegistryBean")).init();
        logger.debug("###RaceTimerInterval " + raceTimerInterval);

        if(!isTimerRunning) {
            this.track = activeTrack;
            this.raceView = raceView;

            // get the tda injector factory
            TDAInjectorFactory tdaInjector = (TDAInjectorFactory) ContextLoader.getBean("tdaInjectorFactory");

            // TODO: assign all cars to their starting position
            int i = 0;
            StartingPoint[] startingPoints = track.getStartingPoints();
            for(Player player: players) {
                // reset players positions
                player.getPosition().reset(startingPoints[i++]);
                // inject each player with an specific tda and give activeTrack
                tdaInjector.initializeTDA(player, track, raceTimerInterval);
                // prepare devices
                player.getController().activate();
            }

            // set running flag
            isTimerRunning = true;

            // create new timer feed with RaceTimerTask and start it
            raceTimer = new Timer();
            raceTimer.schedule(new RaceTimerTask(), 0, raceTimerInterval);

        } else {
            logger.error("RaceEngine.startRace() was invoked but timer was already running. Race was *not* started!");
        }
    }

    /**
     * Stop the RaceTimerTask
     */
    public void stopRace() {
        if(isTimerRunning) {
            raceTimer.cancel();
            isTimerRunning = false;
            // disorganize devices
            for(Player player: players) {
                // deactivate all controllers
                player.getController().deactivate();
                // all cars stop when the game stops
                player.getSlotCarSystemConnector().getDriveConnector().fullBreak();
                player.setRaceStatistic(new Statistics());
                player.getPosition().lap = 0;
            }

        } else {
            logger.error("RaceEngine.stopRace() was invoked but timer was not running. Race was *not* stopped!");
        }
    }

    /**
     * Inner Class
     * <p>
     * Represents the RaceTimer polls the InputDevices gets FeedbackSignals and gives the ControllerSignal to the
     * SlotCarSystemConnectors, the TDA fror approximation and indicates an refresh of the RaceView. This is done every
     * Gametick.
     * <p>
     * <p>
     * You can configurate the raceTimerInterval (time until the next gametick) in jacavi.configuration.
     */
    class RaceTimerTask extends TimerTask {

        // gametick counter
        int gametick = 0;

        @Override
        public void run() {
            for(int i = 0; i < players.size(); i++) {

                Player player = players.get(i);
                // get the players CarController
                CarController carController = player.getController();
                // get players hal connector
                SlotCarSystemConnector slotCarSystemConnector = player.getSlotCarSystemConnector();
                // get players controller signal
                ControllerSignal controllerSignal = carController.poll();
                // get the tda
                TrackDataApproximator tda = player.getTda();

                // dont give any signals if the car is not on track
                if(player.getPosition().isOnTrack) {

                    // change track
                    if(controllerSignal.isTrigger() && !slotCarSystemConnector.getSwitch()) {
                        slotCarSystemConnector.toggleSwitch();
                    } else if(!controllerSignal.isTrigger() && slotCarSystemConnector.getSwitch()) {
                        slotCarSystemConnector.toggleSwitch();
                    }

                    // switch front Light
                    if(controllerSignal.isSwitchFrontLight()) {
                        slotCarSystemConnector.switchFrontLight();
                        controllerSignal.setSwitchFrontLight(false);
                    }
                    // switch back light
                    if(controllerSignal.isSwitchBackLight()) {
                        slotCarSystemConnector.switchBackLight();
                        controllerSignal.setSwitchBackLight(false);
                    }
                    // set new speed
                    slotCarSystemConnector.setThrust(controllerSignal.getThrust());
                    // get the hal feedback signal
                    FeedbackSignal feedbackSignal = slotCarSystemConnector.pollFeedback();
                    // invoke the TDA
                    tda.updatePosition(player.getPosition(), gametick, player.getCar(), controllerSignal,
                            feedbackSignal);

                } else {
                    // full break on connector
                    slotCarSystemConnector.fullBreak();
                    if(controllerSignal.isReset()) {
                        // reset the controller
                        player.getController().reset();
                        // reset the tda
                        tda.reset();
                        // reset the car to starting position
                        player.getPosition().reset(track.getStartingPoints()[i]);
                    }
                }
                // repaint the RaceView
                raceView.repaint();
            }
            gametick++;
        }
    }
}
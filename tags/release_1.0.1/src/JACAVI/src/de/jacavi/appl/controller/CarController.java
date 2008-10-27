package de.jacavi.appl.controller;

import java.util.List;
import java.util.UUID;

import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;



public abstract class CarController implements Comparable<CarController> {
    protected final UUID id;

    protected final String name;

    protected CarController(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    abstract public ControllerSignal poll();

    public void cleanup() {
        deactivate();
    }

    @Override
    public int compareTo(CarController o) {
        return name.compareTo(o.getName());
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
     * Prepare for the race that's going to start soon.
     * <p>
     * May also be called for a preview from the InputDeviceSettingsDialog.
     */
    public void activate(Track track, List<Player> players) {}

    /**
     * Do what needs to be done after the race has finished.
     * <p>
     * Must have no effect if called multiple times. The default implementation of {@link cleanup()} calls this for good
     * measure.
     */
    public void deactivate() {}

    /**
     * Reset resets the state of a Controller without deactivating its listeners. Currently it is called in the
     * RaceEngine if ControllerSignal.isReset() to make sure the Controller has the same state as a freshly activated
     * one.
     * <p>
     * We need the reset because some Controllers save their thrust in state.
     */
    public void reset() {}

    /**
     * Returns the last signal that was poll()ed.
     * <p>
     * The difference to poll() is that this should not actually communicate in any way with the hardware. It's
     * primarily used by the TrackWidget to draw the thrust gauges.
     * <p>
     * The default implementation just returns the result of poll() with null arguments. When in doubt, overwrite it!
     * <p>
     * IMPORTANT NOTE: This might actually be called *before* the first poll()!
     */
    public ControllerSignal getLastSignal() {
        return poll();
    }

}

package de.jacavi.appl.controller;

import java.util.UUID;



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
     * In comparison to the initialize method this one may be called when the race perspective has opened and the race
     * has started
     */
    public void activate() {}

    /**
     * Do what needs to be done after the race has finished.
     * <p>
     * Must have no effect if called multiple times. The default implementation of {@link cleanup()} calls this for good
     * measure.
     */
    public void deactivate() {}

    public void reset() {}
}

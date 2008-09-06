package de.jacavi.appl.controller;

import java.util.UUID;



public abstract class CarController implements Comparable<CarController> {
    protected final UUID id;

    protected final String name;

    protected CarController(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    abstract public boolean initialize();

    abstract public ControllerSignal poll();

    abstract public void cleanup();

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
    public void preRace() {}

    /**
     * Do what needs to be done after the race has finished.
     */
    public void postRace() {}
}

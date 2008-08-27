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
}

package de.jacavi.appl.controller;




public interface CarController {
    public boolean initialize();

    public ControllerSignal poll();

    public void cleanup();
}

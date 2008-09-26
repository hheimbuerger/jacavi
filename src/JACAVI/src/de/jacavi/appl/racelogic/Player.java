package de.jacavi.appl.racelogic;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.racelogic.tda.TrackDataApproximator;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.hal.SlotCarSystemConnector;



public class Player {

    private String name;

    private CarController carController = null;

    private SlotCarSystemConnector slotCarSystemConnector = null;

    private TrackDataApproximator tda = null;

    private final CarPosition position;

    private Car car;

    public Player() {
        this.name = "New Player";
        this.position = new CarPosition();
    }

    public void setController(CarController controller) {
        this.carController = controller;
    }

    public CarController getController() {
        return carController;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public CarPosition getPosition() {
        return position;
    }

    public SlotCarSystemConnector getSlotCarSystemConnector() {
        return slotCarSystemConnector;
    }

    public void setSlotCarSystemConnector(SlotCarSystemConnector slotCarSystemConnector) {
        this.slotCarSystemConnector = slotCarSystemConnector;
    }

    public TrackDataApproximator getTda() {
        return tda;
    }

    public void setTda(TrackDataApproximator tda) {
        this.tda = tda;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return name;
    }
}

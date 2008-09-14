package de.jacavi.appl.racelogic;

import org.eclipse.swt.graphics.Color;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.racelogic.tda.DebugTDA;
import de.jacavi.appl.racelogic.tda.TrackDataApproximator;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.hal.SlotCarSystemConnector;



public class Player {

    private int id;

    private String name;

    private CarController carController;

    private SlotCarSystemConnector slotCarSystemConnector;

    private final TrackDataApproximator tda;

    private final CarPosition position;

    private Color color;

    private Car car;

    public Player() {
        this(0, "Player", null);
    }

    public Player(int id, String name, CarController controller) {
        this.id = id;
        this.name = name;
        this.carController = controller;
        this.position = new CarPosition();
        this.tda = new DebugTDA(); // FIXME: needs to be set based on slotCarSystemController
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

}

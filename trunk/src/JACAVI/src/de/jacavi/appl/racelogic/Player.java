package de.jacavi.appl.racelogic;

import org.eclipse.swt.graphics.Color;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.racelogic.tda.DebugTDA;
import de.jacavi.appl.racelogic.tda.TrackDataApproximator;
import de.jacavi.hal.SlotCarSystemConnector;



public class Player {

    private int id;

    private String name;

    private CarController carController;

    private SlotCarSystemConnector slotCarSystemConnector;

    private TrackDataApproximator tda;

    private int position; // including current track

    private Color color;

    public Player() {
        this.name = "Player";
        this.tda = new DebugTDA(null); // FIXME: needs to be set based on slotCarSystemController
    }

    public Player(int id, String name, CarController controller) {
        super();
        this.id = id;
        this.name = name;
        this.carController = controller;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

}
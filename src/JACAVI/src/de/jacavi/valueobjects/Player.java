package de.jacavi.appl.valueobjects;

import java.io.Serializable;

import de.jacavi.appl.controller.CarController;



public class Player implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7275413800602182772L;

    private String id;

    private String name;

    private CarController controller;

    private String protocol;

    public Player() {
    // TODO Auto-generated constructor stub
    }

    public Player(String id, String name, CarController controller, String protocol) {
        super();
        this.id = id;
        this.name = name;
        this.controller = controller;
        this.protocol = protocol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setController(CarController controller) {
        this.controller = controller;
    }

    public CarController getController() {
        return controller;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

}

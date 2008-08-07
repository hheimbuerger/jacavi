package de.jacavi.appl.valueobjects;

import java.io.Serializable;

import org.eclipse.swt.graphics.Color;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.script.impl.Script;



public class Player implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7275413800602182772L;

    private int id;

    private String name;

    private CarController controller;

    private String protocol;
    
    private Color color;

    public Player() {
    	this.name = "Player";
    }

    public Player(int id, String name, CarController controller, String protocol) {
        super();
        this.id = id;
        this.name = name;
        this.controller = controller;
        this.protocol = protocol;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
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

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}

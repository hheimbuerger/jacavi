package de.jacavi.valueobjects;

import java.io.Serializable;

public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7275413800602182772L;

	private String id;
	private String name;
	private String input;
	private String device;
	private String protocol;


	public Player(String id, String name, String input, String device,
			String protocol) {
		super();
		this.id = id;
		this.name = name;
		this.input = input;
		this.device = device;
		this.protocol = protocol;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

}

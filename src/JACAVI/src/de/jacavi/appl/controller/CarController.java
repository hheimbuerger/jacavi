package de.jacavi.appl.controller;

import org.eclipse.swt.widgets.Composite;



public interface CarController {
    public boolean initialize(Composite guiElement);

    public ControllerSignal poll();

    public void cleanup();
}

package de.jacavi.hal;

public interface PacecarController {

    //Handle Pace Car
    //TODO: implementation
    void activatePacecar();
    void deactivatePacecar();
    void pacecar2box();
    void setPacecarSwitch( int value );
    int togglePacecarSwitch();
    void setPcPitstop( int pitstop );
    int isPacecarActive();
    int getPcSwitch();
    
}

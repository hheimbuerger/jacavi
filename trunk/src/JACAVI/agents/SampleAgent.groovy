// SampleAgent:
//   A very simple Groovy driving agent that alternates
//   between a faster and a slower mode every 100 gameticks.


import java.util.List
import de.jacavi.appl.controller.agent.DrivingAgent
import de.jacavi.appl.controller.ControllerSignal
import de.jacavi.appl.track.CarPosition
import de.jacavi.appl.track.Track
import de.jacavi.appl.racelogic.Player

public class SampleAgent implements DrivingAgent {
	private int counter = 0

    public ControllerSignal poll(CarPosition you, Player[] others, Track track) {
        counter++
        if(((counter / 100) as int) % 2 == 1) {
            return new ControllerSignal(35, true)
        } else {
            return new ControllerSignal(55, false)
        }
    }
}

import de.jacavi.appl.controller.agent.DrivingAgent
import de.jacavi.appl.controller.ControllerSignal

public class SampleAgent implements DrivingAgent {
	private int counter = 0

    public ControllerSignal poll() {
        counter++
        if(((counter / 100) as int) % 2 == 1) {
            return new ControllerSignal(35, true)
        } else {
            return new ControllerSignal(55, false)
        }
    }
}

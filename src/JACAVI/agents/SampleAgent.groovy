import de.jacavi.appl.controller.agent.DrivingAgent
import de.jacavi.appl.controller.ControllerSignal

public class SampleAgent implements DrivingAgent {
	private int counter = 0

    public ControllerSignal poll() {
        counter++
        if(((counter / 300) as int) % 2 == 1) {
            return new ControllerSignal(45, false)
        } else {
            return new ControllerSignal(55, false)
        }
    }
}

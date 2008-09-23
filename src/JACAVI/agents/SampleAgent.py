#import de.jacavi.appl.controller.agent.DrivingAgent
#import de.jacavi.appl.controller.ControllerSignal

class MyAgent(DrivingAgent):
    def __init__(self):
        self.counter = 0

    def poll(self):
        self.counter += 1
        if self.counter / 300 % 2:
            return ControllerSignal(45, False)
        else:
            return ControllerSignal(55, False)

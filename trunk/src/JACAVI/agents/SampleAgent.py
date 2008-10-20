# SampleAgent:
#   A very simple Jython driving agent that alternates
#   between a faster and a slower mode every 100 gameticks.

class MyAgent(DrivingAgent):
    def __init__(self):
        self.counter = 0

    def poll(self, you, others, track):
        #self.counter += 1
        #if self.counter / 100 % 2:
        #    return ControllerSignal(35, True)
        #else:
        #    return ControllerSignal(55, False)
        return ControllerSignal(35, False)

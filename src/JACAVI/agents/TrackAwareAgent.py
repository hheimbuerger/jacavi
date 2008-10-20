# TrackAwareAgent:
#   A Jython driving agent that detects the tile he's
#   currently on and goes 60% thrust on straight tiles
#   and 30% thrust on curvy tiles.

class MyAgent(DrivingAgent):
    def __init__(self):
        self.counter = 0

    def poll(self, you, others, track):
        if you and track: # during a race

            # determine the section I'm on
            section = you.trackSectionIndex

            # which tile is that?
            tile = track.getSections().get(section).getTile()

            # if the entry-to-exit angle is 0, full speed
            if tile.getEntryToExitAngle().angle == 0:
                return ControllerSignal(60, False)

            # if it's a turn tile, slow down
            else:
                return ControllerSignal(30, False)

        else: # for the dry run
            return ControllerSignal(10, True)

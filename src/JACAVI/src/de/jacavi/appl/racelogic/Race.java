package de.jacavi.appl.racelogic;

import java.util.List;

import de.jacavi.appl.track.Track;



public class Race {

    private List<Player> players;

    private Track track;

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

}

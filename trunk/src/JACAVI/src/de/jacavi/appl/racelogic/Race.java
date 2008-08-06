package de.jacavi.appl.racelogic;

import java.util.Collection;

import de.jacavi.appl.valueobjects.Player;
import de.jacavi.track.Track;



public class Race {

    private Collection<Player> players;

    private Track track;

    public void setPlayers(Collection<Player> players) {
        this.players = players;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

}

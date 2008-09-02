package de.jacavi.hal;

public class FeedbackSignal {

    private String lastCheckpoint;

    private Gforce gforce = null;

    public FeedbackSignal(Gforce gforce, String lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
        this.gforce = gforce;
    }

    public Gforce getGforce() {
        return gforce;
    }

    public String getLastCheckpoint() {
        return lastCheckpoint;
    }

    public void setLastCheckpoint(String lastCheckpoint) {
        this.lastCheckpoint = lastCheckpoint;
    }

    public void setGforce(Gforce gforce) {
        this.gforce = gforce;
    }

}

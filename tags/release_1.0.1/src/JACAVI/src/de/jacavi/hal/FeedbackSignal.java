package de.jacavi.hal;

/**
 * An checkpoint etc Feedback from hal. FeedbackSignal with checkpintID=0 is the null checkpoint and should not existent
 * on hal
 */
public class FeedbackSignal {

    private String checkpointID;

    private Gforce gforce = null;

    public FeedbackSignal(Gforce gforce, String lastCheckpoint) {
        this.checkpointID = lastCheckpoint;
        this.gforce = gforce;
    }

    public Gforce getGforce() {
        return gforce;
    }

    public String getCheckpoint() {
        return checkpointID;
    }

    public void setCheckpoint(String lastCheckpoint) {
        this.checkpointID = lastCheckpoint;
    }

    public void setGforce(Gforce gforce) {
        this.gforce = gforce;
    }

    @Override
    public boolean equals(Object obj) {
        return(obj instanceof FeedbackSignal && checkpointID.equals(((FeedbackSignal) obj).getCheckpoint()));
    }
}

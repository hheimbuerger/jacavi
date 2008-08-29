package de.jacavi.hal;

public class FeedbackSignal {

    private final String lastCheckpoint;

    private final String accelerationData;

    public FeedbackSignal(String accelerationData, String lastCheckpoint) {
        this.accelerationData = accelerationData;
        this.lastCheckpoint = lastCheckpoint;
    }

    public String getLastCheckpoint() {
        return lastCheckpoint;
    }

    public String getAccelerationData() {
        return accelerationData;
    }

}

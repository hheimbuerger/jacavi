package de.jacavi.hal;

/**
 * Feedback interface.
 */
public interface SlotCarFeedbackConnector {
    FeedbackSignal pollFeedback();

    void resetSignal();
}

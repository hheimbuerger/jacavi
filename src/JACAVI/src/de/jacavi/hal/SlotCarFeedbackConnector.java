package de.jacavi.hal;

/**
 * Currently this is only a marker interface for the backchannels of the diffrent SlotCarSystems
 * 
 * @author fro
 */
public interface SlotCarFeedbackConnector {
    FeedbackSignal pollFeedback();
}

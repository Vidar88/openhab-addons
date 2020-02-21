package org.openhab.binding.spacetrack.internal.handler.detection;

import org.hipparchus.ode.events.Action;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.events.handlers.RecordAndContinue;
import org.orekit.time.AbsoluteDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VisibilityHandler implements EventHandler<ElevationDetector> {

    /** Observed events. */
    private final List<Event<ElevationDetector>> events = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(VisibilityHandler.class);
    private AbsoluteDate initialDate;

    public VisibilityHandler(AbsoluteDate initialDate) {
        this.initialDate = initialDate;
    }

    /**
     * Get the events passed to this handler.
     *
     * <p> Note the returned list of events is in the order the events were
     * passed to this handler by calling eventOccurred. This may or may not be chronological order.
     *
     * <p> Also not that this method returns a view of the internal collection
     * used to store events and calling any of this handler's methods may modify
     * both the underlying collection and the returned view. If a snapshot of
     * the events up to a certain point is needed create a copy of the returned
     * collection.
     *
     * @return the events observed by the handler in the order they were
     * observed.
     */
    public List<Event<ElevationDetector>> getEvents() {
        return Collections.unmodifiableList(this.events);
    }

    /** Clear all stored events. */
    public void clear() {
        this.events.clear();
    }

    @Override
    public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector,
                                final boolean increasing) {
        events.add(new Event<ElevationDetector>(detector, s, increasing));
        return Action.CONTINUE;
    }

    public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
        return oldState;
    }

    /** A single event detected during propagation. */
    public static class Event<T> {

        /** The observed state. */
        private final SpacecraftState state;
        /** The detector. */
        private final T detector;
        /** The sign of the derivative of the g function. */
        private final boolean increasing;

        /**
         * Create a new event.
         *
         * @param detector   of the event.
         * @param state      of the event.
         * @param increasing if the g function is increasing.
         */
        private Event(final T detector,
                      final SpacecraftState state,
                      final boolean increasing) {
            this.detector = detector;
            this.state = state;
            this.increasing = increasing;
        }

        /**
         * Get the detector.
         *
         * @return the detector that found the event.
         * @see EventHandler#eventOccurred(SpacecraftState, EventDetector,
         * boolean)
         */
        public T getDetector() {
            return detector;
        }

        /**
         * Is the g() function increasing?
         *
         * @return if the sign of the derivative of the g function is positive
         * (true) or negative (false).
         * @see EventHandler#eventOccurred(SpacecraftState, EventDetector,
         * boolean)
         */
        public boolean isIncreasing() {
            return increasing;
        }

        /**
         * Get the spacecraft's state at the event.
         *
         * @return the satellite's state when the event was triggered.
         * @see EventHandler#eventOccurred(SpacecraftState, EventDetector,
         * boolean)
         */
        public SpacecraftState getState() {
            return state;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "state=" + state +
                    ", increasing=" + increasing +
                    ", detector=" + detector +
                    '}';
        }
    }

}

package org.normal.normalDebugger.common.resolver;

import org.bukkit.event.Event;
import org.normal.normalDebugger.common.event.EventCause;

/**
 * Represents a strategy for resolving the cause of a specific {@link Event}.
 *
 * <p>This interface is intended to be implemented by classes that can analyze
 * a specific type of event and extract a meaningful {@link EventCause} from it.</p>
 *
 * <p>Resolvers can be registered via a registry (e.g., {@code CauseResolverRegistry})
 * and prioritized based on their {@link #priority()} value.</p>
 *
 * @param <E> the specific type of {@link Event} this resolver supports
 */
public interface EventCauseResolver<E extends Event> {

    /**
     * Determines whether this resolver supports handling the given event.
     *
     * @param event the event to check
     * @return true if this resolver can handle the event; false otherwise
     */
    boolean matches(Event event);

    /**
     * Resolves the cause of the provided event.
     *
     * <p>This method is only called if {@link #matches(Event)} returned true.</p>
     *
     * @param event the event to resolve
     * @return the resolved {@link EventCause}, or null if no cause could be determined
     */
    EventCause resolve(E event);


    /**
     * Defines the priority of this resolver relative to others.
     *
     * <p>Resolvers with higher priority are considered before those with lower priority.</p>
     *
     * @return the resolver's priority (default is 0)
     */
    default int priority() {
        return 0; }
}

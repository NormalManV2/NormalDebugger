package org.normal.normalDebugger.event;

import org.normal.normalDebugger.common.event.EventCause;

import java.util.Map;

public record TracedEvent(
        String eventName,
        String eventClass,
        String pluginSource,
        long timestamp,
        boolean cancelled,
        EventCause cause,
        Map<String, Object> metadata
) {}

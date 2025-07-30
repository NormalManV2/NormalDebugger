package org.normal.normalDebugger.common.event;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record EventCause(
        @Nullable Object actor,
        @Nullable Object target,
        @Nullable Object source,
        @NotNull String causeCategory,
        @NotNull String description
) {}

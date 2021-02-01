package com.vincentmet.customquests.api;

import java.util.UUID;

public interface IProgressProvider{
	boolean isSubtaskCompleted(UUID uuid, int questId, int taskId, int subTaskId);
	boolean hasEntry(UUID uuid, int questId, int taskId, int subTaskId);
}

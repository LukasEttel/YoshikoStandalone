package de.hhu.ba.yoshikoWrapper.cytoUtil;

import java.util.Map;

import org.cytoscape.work.TaskObserver;

import de.hhu.ba.yoshikoWrapper.core.CyCore;

public class CommandExecutor{

	public static void executeCommand(String namespace, String command,
            Map<String, Object> args, TaskObserver observer) {
		;
		CyCore.dialogTaskManager.execute(
				CyCore.commandExecutorTaskFactory.createTaskIterator(namespace, command, args, observer)
				);

	}

}

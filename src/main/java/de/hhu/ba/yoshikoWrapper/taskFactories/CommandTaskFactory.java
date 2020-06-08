package de.hhu.ba.yoshikoWrapper.taskFactories;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import de.hhu.ba.yoshikoWrapper.core.ParameterSet;
import de.hhu.ba.yoshikoWrapper.tasks.*;

public class CommandTaskFactory implements TaskFactory{

	private final YoshikoCommand command;

	/**
	 * Default constructor
	 */
	public CommandTaskFactory(YoshikoCommand command) {
		super();
		this.command = command;
	}

	@Override
	public TaskIterator createTaskIterator() {
		//We simply switch between the possible commands
		if (command == YoshikoCommand.CREATE_CLUSTER_VIEW) {
			return new TaskIterator(
					new CreateClusterViewsTask(
							null
							)
					);
		}
		else if (command == YoshikoCommand.CREATE_META_GRAPH) {
			return null;
		}

		else if (command == YoshikoCommand.GET_SOLUTIONS) {
			return new TaskIterator(
					new GetSolutionsTask()
					);
		}
		else if (command == YoshikoCommand.GET_CLUSTERS) {
			return new TaskIterator(
					new GetClustersTask()
					);
		}
		else if (command == YoshikoCommand.PERFORM_ALGORITHM) {
			return new TaskIterator(
					new AlgorithmTask(null, CyCore.mainPanel.parameterSet));
		}
		else return null; //TODO: Might be useful to generate an error/ throw an exception here as this should never be invoked
	}


	public boolean isReady () { return true; } //TODO: Think about when that would actually make sense / prevent launching of multiple tasks
}

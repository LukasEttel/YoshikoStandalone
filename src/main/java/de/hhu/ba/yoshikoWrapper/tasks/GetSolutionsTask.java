package de.hhu.ba.yoshikoWrapper.tasks;

import java.util.Collection;

import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import de.hhu.ba.yoshikoWrapper.core.ResultList;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoResult;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoSolution;

/**
 * Basic task that retrieves solutions associated with a run (result)
 * This is meant to be invoked from cmd / CyRest exclusively to receive solution IDs
 * @author Philipp Spohr, Dec 12, 2017
 *
 */
public class GetSolutionsTask implements ObservableTask {

	/**
	 * The result ID for the result/run for which the solutions are to be retrieved
	 */
	@Tunable(description="The result ID for which the solutions should be displayed", context="nogui")
	public int resultID = -1;

	/**
	 * The solutions as a collection in case the task was successful
	 */
	private Collection<YoshikoSolution> solutions;

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		YoshikoResult result = ResultList.get(resultID);
		if (result == null) {
			throw new Exception("No result with ID: "+resultID+" was found!"); //TODO: Localization
		}
		solutions = result.getSolutions();
	}

	@Override
	public void cancel() {} //There is nothing here that remains in an instable state as we are simply reading / retrieving data

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		if (type.equals(String.class)) {
			String ret = "";
			for (YoshikoSolution s: solutions) {
				ret+="Solution[ID="+s.getId()+"]: "+s.getClusters().size()+" clusters\n";
			}
			return (R) ret;
		}
		return null;
	}


}

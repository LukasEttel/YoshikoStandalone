package de.hhu.ba.yoshikoWrapper.tasks;

import java.util.Collection;

import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import de.hhu.ba.yoshikoWrapper.core.ResultList;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoResult;

/**
 * A task that retrieves the clusters associated with a given solution in a given result. This is meant to be used via CyRest / command line functionality exclusively
 *
 * @author Philipp Spohr, Dec 12, 2017
 *
 */
public class GetClustersTask implements ObservableTask {


	/**
	 * The ID of the result for which the clusters are to be retrieved
	 */
	@Tunable(description="The result ID for which the solutions should be displayed", context="nogui")
	public int resultID = -1;

	/**
	 * The ID of the solution for which the clusters are to be retrieved
	 */
	@Tunable(description="The solution ID for which the solutions should be displayed", context="nogui")
	public long solutionID = -1;


	/**
	 * The clusters as a collection when they are retrieved
	 */
	private Collection<YoshikoCluster> clusters;

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		YoshikoResult result = ResultList.get(resultID);
		if (result == null) {
			throw new Exception("No result with ID: "+resultID+" was found!"); //TODO: Localization
		}
		if (result.getSolution(solutionID)== null) {
			throw new Exception("No solution with ID: "+solutionID+" was found!"); //TODO: Localization
		}
		clusters = result.getSolution(solutionID).getClusters();
	}

	@Override
	public void cancel() {}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		if (type.equals(String.class)) {
			String ret = "";
			for (YoshikoCluster c: clusters) {
				ret+="Cluster[ID="+c.getID()+"]: "+c.getSize()+" nodes\n";
			}
			return (R) ret;
		}
		return null;
	}

}

package de.hhu.ba.yoshikoWrapper.taskFactories;

/**
 * Describes commands for the Yoshiko App that can be invoked via REST/CyREST
 * @author Philipp Spohr, Nov 30, 2017
 *
 */
public enum YoshikoCommand {
	/**
	 * Creates a CV for a given Cluster found in a result
	 */
	CREATE_CLUSTER_VIEW,
	CREATE_META_GRAPH,
	PERFORM_ALGORITHM,
	GET_SOLUTIONS,
	GET_CLUSTERS;

	@Override
	public String toString() {
		if (this==PERFORM_ALGORITHM) {
			return "cluster"; //TODO: Dynamic
		}
		else if (this==CREATE_CLUSTER_VIEW) {
			return "createcvs";
		}
		else if (this==GET_CLUSTERS) {
			return "clusters"; //TODO: maybe smarter names for the commands
		}
		else if (this==GET_SOLUTIONS) {
			return "solutions";
		}
		return "null";
	}
}



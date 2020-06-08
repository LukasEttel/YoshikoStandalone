package de.hhu.ba.yoshikoWrapper.cytoUtil;

import java.util.List;

import de.hhu.ba.yoshikoWrapper.core.NetworkParsingException;
import de.hhu.ba.yoshikoWrapper.core.ParameterSet;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
//import org.slf4j.Logger;

//import de.hhu.ba.yoshikoWrapper.logging.YoshikoLogger;


public class GraphAnalyzer {

	//private static Logger logger = YoshikoLogger.getInstance().getLogger();

	/**
	 * Simple helper function, checks if a network contains multiple edges between a pair of nodes
	 * @param net The CyNetwork that is to be analyzed
	 * @return <b>true</b> if a pair of nodes exists that is connected with more than one edge, <b>false</b> otherwise
	 */
	public static boolean isMultiGraph(CyNetwork net) {
		//TODO: Better algorithm?


		int n = net.getNodeCount();
		List<CyEdge> edges = net.getEdgeList();

		//Easiest check: Check if the graph contains more edges than a complete graph
		if (edges.size() > (n*(n-1))/2) {
			return true;
		}

		for (int i = 0; i < edges.size()-1; i++) {
			for (int j=i+1; j<edges.size();j++) {
				if (connectSameNodes(edges.get(i),edges.get(j))){
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks whether two edges connect the same pair of nodes
	 * This function is symmetric
	 * @param e1 An arbitrary CyEdge
	 * @param e2 An arbitrary CyEdge
	 * @return <b>true</b> if the edges connect the same pair of nodes, <b>false</b> otherwise
	 */
	private static boolean connectSameNodes(CyEdge e1, CyEdge e2) {
		if (//Treating all edges as undirected here
				(e1.getSource() == e2.getTarget() && e1.getTarget() == e2.getSource()) ||
				(e1.getSource() == e2.getSource() && e1.getTarget() == e2.getTarget())
				) {
			return true;
		}
		return false;
	}

//	public static boolean isDirected(CyNetwork net) {
//		for (CyEdge e: net.getEdgeList()) {
//			if (e.isDirected()) {
//				return true;
//			}
//		}
//		return false;
//	}

	/**
	 * Generates a fitting bitmask by choosing the reduction rules that appear to be the best choice based on current research
	 * @param containsRealValues
	 * @param heuristic
	 * @return The bitmask as a String
	 */
	public static String suggestReductionRules(boolean containsRealValues, boolean heuristic) {

		//TODO: Maybe also choose SNR Factor?
		//TODO: More research, identify good rules

		boolean useCRule = false,useCCRule= false,useACRule= false,useHERule= false,usePDRRule= false,useSNRule = false;
		String ret = "";

		//First of all: We don't choose any rules when in heuristic mode
		if (!heuristic) {
			//We check if the graph contains real weights
			if (!containsRealValues) {
				useCRule = true;
				useCCRule = true;
				useACRule = true;
				useHERule = true;
			}
			usePDRRule = true;
			useSNRule = true;
		}

		ret += (useCRule ? "1" : "0");
		ret += (useCCRule ? "1" : "0");
		ret += (useACRule ? "1" : "0");
		ret += (useHERule ? "1" : "0");
		ret += (usePDRRule ? "1" : "0");
		ret += (useSNRule ? "1" : "0");

		//TODO: Spend some thoughts on logging, make a coherent design decision on logging
		//logger.info("Suggesting the following reduction-rules bitmask: "+ret);
		//System.out.println("Suggesting the following reduction-rules bitmask: "+ret);

		return ret;
	}

	/**
	 * Simple helper function that checks if a WCE instance contains real values (as opposed to only integers)
	 * @param net The network from which the WCE instance is to be derived
	 * @param weightColumn The CyColumn from which - if != null - the edge weights are taken
	 * @param permanentColumn The CyColumn from which - if != null - we derive if edges are permanent
	 * @param forbiddenColumn The CyColumn from which - if != null - we derive if edges are forbidden
	 * @param defaultInsertionCost The insertion cost used for non-existing edges or ones with no mapping associated
	 * @param defaultDeletionCost The deletion cost for edges with no mapping associated
	 * @return true if the WCE instance is real-valued, false otherwise
	 */
	public static boolean containsRealValues(
			CyNetwork net,
			CyColumn weightColumn,
			CyColumn permanentColumn,
			CyColumn forbiddenColumn,
			double defaultInsertionCost,
			double defaultDeletionCost)
	{
		//Simple checks: Deletion and Insertion Costs
		if (defaultInsertionCost % 1 != 0 || defaultDeletionCost % 1 != 0) {
			return true;
		}

		if (net != null){
			//Fetch edges
			List<CyEdge> edges = net.getEdgeList();

			//Loop over edges
			for (CyEdge e : edges) {

				//Fetch entry and check if it exists
				CyRow edgeEntry = net.getRow(e);

				//Check if there is a weight column defined, else skip
				if (weightColumn != null){
					//Check if the column contains an entry for the respective edge
					//It is possible, that there are missing entries
					if (edgeEntry.get(weightColumn.getName(), weightColumn.getType()) != null){
						if (weightColumn.getType() == Double.class) {
							double weight = edgeEntry.get(weightColumn.getName(), Double.class);
							if (weight%1!=0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static double recommendThreshold(CyNetwork net, ParameterSet parameterSet) throws NetworkParsingException{
		double threshold = parameterSet.threshold;
		double maxEdgeWeight = Double.NEGATIVE_INFINITY;
		double minEdgeWeight = Double.POSITIVE_INFINITY;

		for (CyEdge edge : net.getEdgeList()){
			double weight;
			weight = extractValue(edge,net, parameterSet);
			weight -= threshold;


			if (weight < minEdgeWeight){
				minEdgeWeight = weight;
			}
			if (weight > maxEdgeWeight){
				maxEdgeWeight = weight;
			}
		}

		if (minEdgeWeight == maxEdgeWeight){
			return 0;
		}

		if (minEdgeWeight >= 0){
			return (maxEdgeWeight+minEdgeWeight)/2;
		}
		return 0;
	}

	private static double extractValue(CyEdge edge, CyNetwork network,ParameterSet parameterSet) throws NetworkParsingException {
		CyColumn weightColumn = parameterSet.getWeightColumn();

		//Parse editing costs
		double weight = parameterSet.defaultDeletionCost;

		//Fetch entry and check if it exists
		CyRow edgeEntry = network.getRow(edge);

		//Check if the column contains an entry for the respective edge
		//It is possible, that there are missing entries
		if (parameterSet.weightColumn != null){
			if (edgeEntry.get(weightColumn.getName(), weightColumn.getType()) != null){
				if (weightColumn.getType() == Integer.class) {
					weight = 1.0*edgeEntry.get(weightColumn.getName(), Integer.class);
				}
				else if (weightColumn.getType() == Double.class) {
					weight = edgeEntry.get(weightColumn.getName(), Double.class);
				}
			}
		}


		CyColumn permanentColumn = parameterSet.getPermanentColumn();
		CyColumn forbiddenColumn = parameterSet.getForbiddenColumn();

		//Parse Forbidden/Permanent markers
		boolean forbidden = false;
		boolean permanent = false;

		if (permanentColumn != null) {
			//Additional check as it is not required to have a value in every row
			if (edgeEntry.get(permanentColumn.getName(), Boolean.class) != null) {
				permanent =  (boolean)edgeEntry.get(permanentColumn.getName(), Boolean.class);
			}
		}
		if (forbiddenColumn != null) {
			//Additional check as it is not required to have a value in every row
			if (edgeEntry.get(forbiddenColumn.getName(), Boolean.class) != null) {
				forbidden =  (boolean)edgeEntry.get(forbiddenColumn.getName(), Boolean.class);
			}
		}

		//Check for edges that are forbidden AND permanent -> Throw exception
		if (forbidden && permanent) {
			throw new NetworkParsingException("dualInfinityError");
		}


		if(permanent){
			return Double.POSITIVE_INFINITY;
		}else if (forbidden){
			return Double.NEGATIVE_INFINITY;
		}

		return weight;
	}
}
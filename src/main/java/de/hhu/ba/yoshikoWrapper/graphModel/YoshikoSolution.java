/*******************************************************************************
 * Copyright (C) 2018 Philipp Spohr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package de.hhu.ba.yoshikoWrapper.graphModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;


import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.slf4j.Logger;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.logging.YoshikoLogger;

/**
 * Represents a solution found by the Yoshiko algorithm.
 * @author Philipp Spohr, Dec 12, 2017
 *
 */
public class YoshikoSolution {

	/**A reference to the result (and thus the Yoshiko run) to which this solution belongs
	 * TODO: I hate this codestyle wise maybe replace in the long run
	 */
	private final YoshikoResult result;

	/**
	 * The CyNetwork representing the meta-graph for this solution if such a graph exists.
	 */
	private CyNetwork metaGraph;

	private HashMap<YoshikoCluster, CyNode> metaGraphMap;


	private HashMap<Long,YoshikoCluster> clusters;

	private final long id;

	private Logger logger = YoshikoLogger.getInstance().getLogger();

	private final double clusteringCost;


	public YoshikoSolution(YoshikoResult yoshikoResult, long id, double clusteringCost) {
		clusters = new HashMap<Long,YoshikoCluster>();
		this.result = yoshikoResult;
		this.id = id;
		this.clusteringCost = clusteringCost;
	}

	public void delete() {
		for (YoshikoCluster c: clusters.values()) {
			c.delete();
		}
		if (this.metaGraph != null) {
			CyCore.networkManager.destroyNetwork(metaGraph);
		}
	}

	public void highlightInMetaGraph(YoshikoCluster yoshikoCluster) {
		try {
			List<CyRow> allRows = metaGraph.getDefaultNodeTable().getAllRows();
			for (CyRow r: allRows) {
				r.set("selected", false);
			}

			metaGraph.getRow(metaGraphMap.get(yoshikoCluster)).set("selected", true);
		}
		catch (Exception e) {
			logger.warn("The graph doesn't exist anymore, can't highlight nodes!");
		}

	}

	//_____________GETTER / SETTER ________________//

	public Collection<YoshikoCluster> getClusters() {
		return clusters.values();
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	public CyNetwork getOriginalGraph() {
		return result.getOriginalGraph();
	}

	public void setMetaGraph(CyNetwork metaGraph, HashMap<YoshikoCluster, CyNode> map) {
		this.metaGraph = metaGraph;
		this.metaGraphMap = map;
	}

	public CyNetwork getMetaGraph() {
		return metaGraph;
	}

	public void addCluster(YoshikoCluster cluster) {
		clusters.put(cluster.getID(), cluster);
	}

	public double getClusteringCost(){
		return clusteringCost;
	}


}

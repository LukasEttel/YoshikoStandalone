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

import java.awt.Image;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngine;
import org.slf4j.Logger;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.cytoUtil.StyleManager;
import de.hhu.ba.yoshikoWrapper.logging.YoshikoLogger;

import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NETWORK_HEIGHT;
import static org.cytoscape.view.presentation.property.BasicVisualLexicon.NETWORK_WIDTH;

public class YoshikoCluster {

	/**
	 * The internal id used to uniquely identify the cluster
	 */
	private final long id;

	/**
	 * All nodes associated with this cluster
	 */
	private ArrayList<CyNode> nodes;

	private Image img;

	private CySubNetwork subnet;


	//SYMBOLIC LINKS
	private final YoshikoSolution solution;
	private Logger logger = YoshikoLogger.getInstance().getLogger();

	//STATICS

	public static Comparator<YoshikoCluster> lessThanComparator = new Comparator<YoshikoCluster>() {

		@Override
		public int compare(YoshikoCluster o1, YoshikoCluster o2) {
			if (o1.getSize() == o2.getSize()) {
				return 0;
			}
			else if(o1.getSize() < o2.getSize()) {
				return 1;
			}
			return -1;
		}

	};


	public YoshikoCluster(YoshikoSolution solution, long id) {
		this.id = id;
		this.solution = solution;
		this.nodes = new ArrayList<CyNode>();
	}

	/**
	 * Generates a subgraph for this clusters by choosing the nodes and induced edges from the original graph if such a graph doesn't exist yet.
	 * @return
	 */
	public CySubNetwork getSubNetwork() {

		if (subnet == null) {
			CyRootNetwork originalGraphAsRoot =
					CyCore.rootNetworkManager.getRootNetwork(solution.getOriginalGraph());

			//Create nested graph and clusters subnet
			ArrayList<CyEdge> inducedEdges = new ArrayList<CyEdge>();
			for (CyNode n: nodes) {
				//Sadly Cytoscape doesnt provide a comfort function here
				List<CyEdge> adjacentEdges = solution.getOriginalGraph().getAdjacentEdgeList(n, CyEdge.Type.ANY);

				for (CyEdge e: adjacentEdges) {
					if (nodes.contains(e.getSource()) && nodes.contains(e.getTarget())) {
						inducedEdges.add(e);
					}
				}
			}

			subnet = originalGraphAsRoot.addSubNetwork(nodes ,inducedEdges);
			subnet.getRow(subnet).set(CyNetwork.NAME, LocalizationManager.get("clusters")+" "+(id+1));
		}

		return subnet;
	}

	public void highlight() {
		if (CyCore.cy.getCurrentNetwork() == solution.getOriginalGraph()) {
			highlightInOriginalGraph();
		}
		else if (CyCore.cy.getCurrentNetwork() == solution.getMetaGraph()) {
			solution.highlightInMetaGraph(this);
		}
	}

	/**
	 * Attempt to select the nodes belonging to this clusters in the original Graph given that they still exist
	 */
	private void highlightInOriginalGraph() {
		try {
			List<CyRow> allRows = solution.getOriginalGraph().getDefaultNodeTable().getAllRows();
			for (CyRow r: allRows) {
				r.set("selected", false);
			}
			//Select nodes corresponding to the clusters
			for (CyNode n : nodes) {
				solution.getOriginalGraph().getRow(n).set("selected", true);
			}
		}
		catch (Exception e) {
			logger.warn("The graph doesn't exist anymore, can't highlight nodes!");
		}
	}

	public void generateClusterIcon(int width, int height,JLabel label) throws InterruptedException {
		final CyNetworkView view = CyCore.networkViewFactory.createNetworkView(getSubNetwork());

		//layout clusters
		final CyLayoutAlgorithm layout = CyCore.layoutAlgorithmManager.getDefaultLayout();
		CyCore.runAndWait(layout.createTaskIterator(
			view,
			layout.getDefaultLayoutContext(),
			CyLayoutAlgorithm.ALL_NODE_VIEWS,
			null
			)
		);

		view.setVisualProperty(NETWORK_WIDTH, (double) width);
		view.setVisualProperty(NETWORK_HEIGHT, (double) height);
		view.fitContent();

		StyleManager.style(view, CyCore.visualMappingManager.getCurrentVisualStyle());

		RenderingEngine<CyNetwork> renderingEngine = CyCore.renderingEngineFactory.createRenderingEngine(label, view);

		img = renderingEngine.createImage(width,height);
		label.setIcon(new ImageIcon(img));

		renderingEngine.dispose();
		view.dispose();
	}

	public void delete() {
		if (subnet != null) {
			if (CyCore.networkManager.networkExists(subnet.getSUID())) {
				CyCore.networkManager.destroyNetwork(subnet);
			}
		}
	}

	public int getSize() {
		return nodes.size();
	}

	public long getID() {
		return id;
	}

	public void addNode(CyNode node) {
		nodes.add(node);
	}

	public String getNodeName(CyNode n) {
		return solution.getOriginalGraph().getRow(n).get("name", String.class);
	}


}

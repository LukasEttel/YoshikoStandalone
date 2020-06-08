package de.hhu.ba.yoshikoWrapper.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.cytoUtil.StyleManager;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoSolution;

//TODO: Should also not be possible / throw an exception if the result was invalidated (might currently just be disabled via GUI)

public class CreateMetaGraphTask extends AbstractTask{

	private final YoshikoSolution solution;

	private boolean isTerminated;

	/**
	 * This internal list is used to track which networks have already been initialized.
	 * This is used to remove those views upon cancellation.
	 */
	private ArrayList<CySubNetwork> initializedSubNetworks;

	public CreateMetaGraphTask(YoshikoSolution s) {
		this.solution = s;

		this.isTerminated = false;
		this.initializedSubNetworks = new ArrayList<CySubNetwork>();
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		taskMonitor.setTitle(LocalizationManager.get("task_cmg"));

		CyNetwork metaGraph = CyCore.networkFactory.createNetwork();

		metaGraph.getRow(metaGraph).set(CyNetwork.NAME, LocalizationManager.get("metaGraph"));

		metaGraph.getDefaultNodeTable().createColumn("clusterSize", Integer.class, false);
		metaGraph.getDefaultEdgeTable().createColumn("edgeStrength", Integer.class, false);

		CyLayoutAlgorithm layout = CyCore.layoutAlgorithmManager.getDefaultLayout();


		//Map Cluster to Nodes for further reference
		HashMap<YoshikoCluster,CyNode> map = new HashMap<YoshikoCluster,CyNode>();



		//Add nodes
		for (YoshikoCluster c: solution.getClusters()) {

			if(isTerminated) {
				throw new Exception("Terminated by user!");
			}

			CyNode clusterNode = metaGraph.addNode();
			CySubNetwork subnet = c.getSubNetwork();
			//Store reference
			this.initializedSubNetworks.add(subnet);

			taskMonitor.setStatusMessage(LocalizationManager.get("status_createCV")+" "+(c.getID()+1));
			CyCore.networkManager.addNetwork(subnet,false);
			//Create network view and register it
			CyNetworkView subnetView = CyCore.networkViewFactory.createNetworkView(subnet);

			//Apply layout
			CyCore.runAndWait(

				layout.createTaskIterator(
						subnetView,
						layout.getDefaultLayoutContext(),
						CyLayoutAlgorithm.ALL_NODE_VIEWS,
						null
				)
			);

			StyleManager.style(subnetView,CyCore.visualMappingManager.getCurrentVisualStyle());

			CyCore.networkViewManager.addNetworkView(subnetView,false);

			//Link clusters node in meta graph and subnet graph to use Cytoscape Nested Network feature
			clusterNode.setNetworkPointer(subnet);
			//Set node attributes
			metaGraph.getRow(clusterNode).set("name", LocalizationManager.get("clusters")+" "+(c.getID()+1));
			metaGraph.getRow(clusterNode).set(StyleManager.CLUSTERSIZE_COLUMN_NAME,c.getSize());
			map.put(c, clusterNode);
		}

		//EDGE_PROCESSING
		//We are checking if any edges in the original graph connect clusters, if so we count them

		taskMonitor.setStatusMessage(LocalizationManager.get("metaGraph_edges"));

		Iterator<YoshikoCluster> it1 = solution.getClusters().iterator();

		while (it1.hasNext()) {
			YoshikoCluster c1 = it1.next();
			Iterator<YoshikoCluster> it2 = solution.getClusters().iterator();
			while(it2.hasNext()) {
				YoshikoCluster c2 = it2.next();
				System.out.println("Debug: Processing edges between cluster "+c1.getID()+" and "+c2.getID());

				if(isTerminated) {
					throw new Exception("Terminated by user!");
				}

				if (
						metaGraph.containsEdge(map.get(c1),map.get(c2))
						|| c1 == c2
						) {
					continue;
				}
				//TODO: Improve running time here?
				for (CyNode c1n : c1.getSubNetwork().getNodeList()) {
					for (CyNode c2n : c2.getSubNetwork().getNodeList()) {
						if (solution.getOriginalGraph().containsEdge(c1n, c2n) || solution.getOriginalGraph().containsEdge(c2n, c1n)) {
							System.out.println("Debug: Found relevant edge from "+c1n.getSUID()+ " to "+c2n.getSUID());
							if (!metaGraph.containsEdge(map.get(c1), map.get(c2))){
								System.out.println("We create a new edge for it");
								CyEdge edge = metaGraph.addEdge(map.get(c1), map.get(c2), false);
								metaGraph.getRow(edge).set(StyleManager.EDGESTRENGTH_COLUMN_NAME,1);
							}
							else {
								CyEdge edge = metaGraph.getConnectingEdgeList(map.get(c1), map.get(c2), Type.ANY).get(0);
								metaGraph.getRow(edge).set(StyleManager.EDGESTRENGTH_COLUMN_NAME,
								metaGraph.getRow(edge).get(StyleManager.EDGESTRENGTH_COLUMN_NAME, Integer.class)+1
										);
							}
						}
					}
				}
			}
		}

		solution.setMetaGraph(metaGraph,map);

		CyNetworkView view = CyCore.networkViewFactory.createNetworkView(metaGraph);

		//Layout and style solution
		CyCore.runAndWait(
			layout.createTaskIterator(
					view,
					layout.getDefaultLayoutContext(),
					CyLayoutAlgorithm.ALL_NODE_VIEWS,
					null
			)
		);

		StyleManager.styleWithMapping(view, CyCore.visualMappingManager.getCurrentVisualStyle());


		CyCore.networkManager.addNetwork(metaGraph,false);
		CyCore.networkViewManager.addNetworkView(
			view
		);

		view.updateView();

		CyCore.cy.setCurrentNetworkView(view);

	}

	@Override
	public void cancel() {
		//Set killswitch
		isTerminated = true;
		//Remove generated ClusterViews
		for (CySubNetwork v: initializedSubNetworks) {
			CyCore.networkManager.destroyNetwork(v);
		}
	}

}

package de.hhu.ba.yoshikoWrapper.tasks;

import java.util.ArrayList;

import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.cytoUtil.StyleManager;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;

public class CreateClusterViewsTask implements Task {

	@Tunable
	public ArrayList<YoshikoCluster> clusters; //TODO: Make Tunable, reference by ID?

	/**
	 * Killswitch, used to determine if the algorithm has been terminated.
	 * We can use this to cancel at a given point (so we don't need to finish the entire task)
	 */
	private boolean isTerminated;

	/**
	 * Used to fall back upon cancellation
	 */
	private CyNetworkView previousView;

	/**
	 * This internal list is used to track which networks have already been initialized.
	 * This is used to remove those views upon cancellation.
	 */
	private ArrayList<CySubNetwork> initializedSubNetworks;

	public CreateClusterViewsTask(ArrayList<YoshikoCluster> clusters) {
		this.clusters = clusters;

		this.previousView = CyCore.cy.getCurrentNetworkView();
		this.isTerminated = false;
		this.initializedSubNetworks = new ArrayList<CySubNetwork>();
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		taskMonitor.setTitle(LocalizationManager.get("task_ccv"));

		CyLayoutAlgorithm layout = CyCore.layoutAlgorithmManager.getDefaultLayout();
		for (YoshikoCluster c : clusters) {
			if (this.isTerminated) {
				throw new Exception("Terminated by user!"); //TODO: Localize
			}
			CySubNetwork subnet = c.getSubNetwork();
			initializedSubNetworks.add(subnet);
			taskMonitor.setStatusMessage(LocalizationManager.get("status_createCV")+" "+(c.getID()+1));
			CyCore.networkManager.addNetwork(subnet,false);
			//Create network view and register it
			CyNetworkView subnetView = CyCore.networkViewFactory.createNetworkView(subnet);
			//layout clusters
			CyCore.dialogTaskManager.execute(
				layout.createTaskIterator(
						subnetView,
						layout.getDefaultLayoutContext(),
						CyLayoutAlgorithm.ALL_NODE_VIEWS,
						null
				),
				new TaskObserver() {

					@Override
					public void taskFinished(ObservableTask task) {}

					@Override
					public void allFinished(FinishStatus finishStatus) {
						StyleManager.style(subnetView,CyCore.visualMappingManager.getCurrentVisualStyle());
						CyCore.networkViewManager.addNetworkView(subnetView);
					}
				}
			);
		}
	}

	@Override
	public void cancel() {
		this.isTerminated = true;
		//Remove generated ClusterViews
		for (CySubNetwork v: initializedSubNetworks) {
			CyCore.networkManager.destroyNetwork(v);
		}
		CyCore.cy.setCurrentNetworkView(previousView);
	}

}

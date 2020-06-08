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
package de.hhu.ba.yoshikoWrapper.swing.components;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.Group;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


import org.cytoscape.work.TaskIterator;


import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoSolution;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;
import de.hhu.ba.yoshikoWrapper.tasks.CreateClusterViewsTask;
import de.hhu.ba.yoshikoWrapper.tasks.CreateMetaGraphTask;
/**
 * Swing Component, that represents one solution of a Yoshiko result and displays it
 *
 */
@SuppressWarnings("serial")
public class SolutionTab extends JPanel {

	//SWING COMPONENTS
	private final JLabel clusteringCosts;
	private final JLabel clusterCount;
	private final JCheckBox hideSingles;
	private final JScrollPane scrollPane;
	private final ClusterViewList clusterViewList;
	private final JButton createClusterView;
	private final JButton createMetaGraph;

	//SWING LAYOUT OBJECTS
	private final GroupLayout layout;
	private final GroupLayout scrollLayout;
	private final Group horizontalGroup_scrollLayout;
	private final Group verticalGroup_scrollLayout;

	//SYMBOLIC LINKS

	private final YoshikoSolution solution;

	/**
	 * Basic constructor, creates a new SolutionTab representing a given YoshikoSolution.
	 * @param s The solution that is to be displayed by this tab
	 * @throws Exception
	 */
	public SolutionTab(YoshikoSolution s) throws Exception {

		//Save abstract solution object for reference
		this.solution = s;

		hideSingles = new JCheckBox(LocalizationManager.get("hideSingles"));
		//We check this option as default //TODO: We might save that to config?
		hideSingles.setSelected(true);

		hideSingles.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						clusterViewList.toggleSingleVisibility(false);
					}
					else if (e.getStateChange() == ItemEvent.DESELECTED) {
						clusterViewList.toggleSingleVisibility(true);
					}
				}
				catch(Exception ex) { //TODO: Specify which type of exceptions can actually occur and process accordingly
					ex.printStackTrace();
				}
			}
		});

		//Declaration of Swing Components
		clusterViewList = new ClusterViewList();

		//Build CV list
		List<YoshikoCluster> list = new ArrayList<YoshikoCluster>(solution.getClusters());
		list.sort(YoshikoCluster.lessThanComparator);
		for (YoshikoCluster c: list) {
			ClusterView clusterView = new ClusterView(c);
			clusterViewList.add(clusterView);
		}

		//Apply default visibility (Hide singles)
		clusterViewList.toggleSingleVisibility(false);

		scrollPane = new JScrollPane(clusterViewList);

		clusteringCosts = new JLabel("Clustering-Cost: "+s.getClusteringCost());

		clusterCount = new JLabel(LocalizationManager.get("clusterFound")+" "+s.getClusters().size());

		createClusterView = new JButton(LocalizationManager.get("createClusterView"));
		createMetaGraph = new JButton(LocalizationManager.get("createMetaGraph"));

		//Configuration of Swing Components

		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		createClusterView.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<YoshikoCluster> selectedClusters = clusterViewList.getSelectedClusters();
				if (selectedClusters.isEmpty()) {
					JOptionPane.showMessageDialog(null,
							LocalizationManager.get("noClustersSelected"),
							LocalizationManager.get("optionpane_title"),
							JOptionPane.INFORMATION_MESSAGE
					);
					return;
				}
				CyCore.dialogTaskManager.execute(
						new TaskIterator(1,
							new CreateClusterViewsTask(
								selectedClusters
							)
						)
				);
			}

		});

		createMetaGraph.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CyCore.dialogTaskManager.execute(
						new TaskIterator(1,
								new CreateMetaGraphTask(solution)
						)
				);
			}

		});

		//Add configured Swing Components to self

		SwingUtil.addAll(this, clusteringCosts, clusterCount,scrollPane,createClusterView,createMetaGraph);

		//Layout

		scrollLayout = new GroupLayout(clusterViewList);

		horizontalGroup_scrollLayout = scrollLayout.createParallelGroup();
		verticalGroup_scrollLayout = scrollLayout.createSequentialGroup();

		scrollLayout.setHorizontalGroup(horizontalGroup_scrollLayout);
		scrollLayout.setVerticalGroup(verticalGroup_scrollLayout);

		scrollLayout.setAutoCreateContainerGaps(true);
		scrollLayout.setAutoCreateGaps(true);

		for (ClusterView v: clusterViewList.getClusterViews()) {
			horizontalGroup_scrollLayout.addComponent(v);
			verticalGroup_scrollLayout.addComponent(v);
		}

		clusterViewList.setLayout(scrollLayout);

		//Define main layout for solution tab

		layout = new GroupLayout(this);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER,true)
				.addComponent(createClusterView,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
				.addComponent(createMetaGraph,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
				.addGap(8)
				.addComponent(clusteringCosts,DEFAULT_SIZE,DEFAULT_SIZE,Short.MAX_VALUE)
				.addGap(8)
				.addComponent(clusterCount,DEFAULT_SIZE,DEFAULT_SIZE,Short.MAX_VALUE)
				.addComponent(hideSingles,DEFAULT_SIZE,DEFAULT_SIZE,Short.MAX_VALUE)
				.addGap(8)
				.addComponent(scrollPane,PREFERRED_SIZE,PREFERRED_SIZE,Short.MAX_VALUE)
				.addGap(4)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(createClusterView,DEFAULT_SIZE,DEFAULT_SIZE,DEFAULT_SIZE)
				.addComponent(createMetaGraph,DEFAULT_SIZE,DEFAULT_SIZE,DEFAULT_SIZE)
				.addGap(8)
				.addComponent(clusteringCosts,DEFAULT_SIZE,DEFAULT_SIZE,DEFAULT_SIZE)
				.addGap(8)
				.addComponent(clusterCount,DEFAULT_SIZE,DEFAULT_SIZE,DEFAULT_SIZE)
				.addComponent(hideSingles,DEFAULT_SIZE,DEFAULT_SIZE,DEFAULT_SIZE)
				.addGap(4)
				.addComponent(scrollPane,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
				.addGap(4)
		);

		this.setLayout(layout);
	}

	/**
	 * Simply disables the CCV and CMG graph to visually highlight the fact that those tasks are no longer possible
	 */
	public void invalidateResult() {
		createClusterView.setEnabled(false);
		createMetaGraph.setEnabled(false);
	}


}

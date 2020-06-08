/*******************************************************************************
 * Copyright (C) 2017 Philipp Spohr
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
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Component;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;

import de.hhu.ba.yoshikoWrapper.taskFactories.CommandTaskFactory;
import de.hhu.ba.yoshikoWrapper.taskFactories.YoshikoCommand;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.util.swing.BasicCollapsiblePanel;
import org.cytoscape.work.TaskIterator;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.core.ParameterSet;
import de.hhu.ba.yoshikoWrapper.cytoUtil.GraphAnalyzer;
import de.hhu.ba.yoshikoWrapper.swing.GraphicsLoader;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

/**This class describes the Swing Panel that the user interacts with in cytoscape
 *
 */
@SuppressWarnings("serial")
public class MainPanel extends JPanel implements CytoPanelComponent {

	//SWING COMPONENTS

	private final YoshikoHeader header;

	private final JCheckBox showAdvancedOptions;

	private final ArrayList<JComponent> advancedOptions;

	private final JScrollPane scrollPane;
	private final JPanel scrollableContent;

	//Those don't work really as collapsible panels and cause glitches which forces the workaround of wrapping them
	//Probably someone used getClass() == instead of instanceof so extending BasicCollapsiblePanel is sadly not possible
	private final BasicCollapsiblePanel ecPanelWrapper;
	private final EditCostPanel ecPanel;

	private final BasicCollapsiblePanel opWrapper;
	private final OperationModePanel opModePanel;

	private final JButton runButton;

	public ParameterSet parameterSet;

	/**
	 * Main constructor, creates a new Panel and initializes subcomponents
	 */
	public MainPanel() {

		//Initialize Swing components
		header = new YoshikoHeader();

		showAdvancedOptions = new JCheckBox(LocalizationManager.get("showAdvanced"));

		scrollableContent = new JPanel();
		scrollPane = new JScrollPane(scrollableContent);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		ecPanel = new EditCostPanel();
		ecPanelWrapper = new BasicCollapsiblePanel(LocalizationManager.get("editingCostPanel"));
		ecPanelWrapper.add(ecPanel);
		ecPanelWrapper.setCollapsed(false);

		opModePanel = new OperationModePanel();
		opWrapper = new BasicCollapsiblePanel(LocalizationManager.get("operationMode"));
		opWrapper.add(opModePanel);
		opWrapper.setCollapsed(false);

		runButton = new JButton(LocalizationManager.get("run"));
		runButton.addActionListener(buttonListener);

		//Add components to the scrollable part of the main panel
		SwingUtil.addAll(scrollableContent,
				ecPanelWrapper,
				opWrapper
		);

		//Add the 'fixed' components to the main panel
		SwingUtil.addAll(this,
				header,
				scrollPane,
				runButton
		);

		//Manage all advanced components separately to enable toggling
		advancedOptions = new ArrayList<JComponent>();
		advancedOptions.addAll(
				Arrays.asList(
						opWrapper
						)
		);

		showAdvancedOptions(true);

		applyLayout();
	}

	private void applyLayout() {
		//Layout
		GroupLayout layout = new GroupLayout(this);
		GroupLayout scrollLayout = new GroupLayout(scrollableContent);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		scrollLayout.setAutoCreateGaps(true);
		scrollLayout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING,true)
				.addGroup(layout.createSequentialGroup()
					.addComponent(header,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
					//"SPRING" Functionality ; eats all the space that is available
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, DEFAULT_SIZE, Short.MAX_VALUE)
				)
				.addGap(8)
				.addComponent(scrollPane,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
				.addComponent(runButton,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
		);

		scrollLayout.setHorizontalGroup(scrollLayout.createParallelGroup(Alignment.LEADING,true)
				.addComponent(ecPanelWrapper,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
				.addComponent(opWrapper,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(header,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
				)
				.addGap(8)
				.addComponent(scrollPane,DEFAULT_SIZE,DEFAULT_SIZE,DEFAULT_SIZE)
				.addComponent(runButton,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
		);

		scrollLayout.setVerticalGroup(scrollLayout.createSequentialGroup()
				.addComponent(ecPanelWrapper,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
				.addComponent(opWrapper,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
		);

		scrollableContent.setLayout(scrollLayout);

		this.setLayout(layout);
	}


	private void showAdvancedOptions(boolean show) {
		for (JComponent j : advancedOptions) {
			j.setVisible(show);
		}
		setPreferredSize(getPreferredSize()); // <<< Swing at its finest
		revalidate();
	}

	/**
	 * ButtonListener for the "Run" Button
	 * Handles calling the algorithm and fetching/passing the arguments from swing components
	 */
	private ActionListener buttonListener = new ActionListener() {

		private final TaskIterator iterator = new TaskIterator();
		@Override
		public void actionPerformed(ActionEvent e) {

			CyNetwork networkToBeProcessed = CyCore.cy.getCurrentNetwork();

			parameterSet = fetchParameters(networkToBeProcessed);

			//WARNINGS FOR FEATURES THAT ARE NOT RESEARCHED
			//TODO: (Obviously) research!
			if (GraphAnalyzer.isMultiGraph(networkToBeProcessed)) {
				JOptionPane.showMessageDialog(
						null,
						LocalizationManager.get("multiGraphWarning"),
						LocalizationManager.get("optionpane_title"),
						JOptionPane.WARNING_MESSAGE
				);
			}

			try {
				double recommendedThreshold = GraphAnalyzer.recommendThreshold(networkToBeProcessed, parameterSet);
				if (!(recommendedThreshold==0)) {
					showMessageDialog(null, "The provided graph only consists of edges with \n positive weight and will likely not yield a good result. \n Adjusting the threshold might be necessary. \n"+recommendedThreshold+" might be a good Threshold" , "Threshold-Warning",JOptionPane.WARNING_MESSAGE);
				}
			}catch (Exception exception){
				exception.printStackTrace();
			}
			//SWING BLACK MAGIC
			Window noWindow = null;
			JDialog statusWindow = new JDialog(noWindow);
			JDialog popupLevel = new JDialog(statusWindow);
			popupLevel.setAlwaysOnTop(true);
			popupLevel.setModalityType(ModalityType.APPLICATION_MODAL);
			popupLevel.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
			statusWindow.setModalityType(ModalityType.MODELESS);
			statusWindow.setAlwaysOnTop(false);

			CyCore.statusWindow = statusWindow;
			//SWING BLACK MAGIC



			CommandTaskFactory commandTaskFactory = new CommandTaskFactory(YoshikoCommand.PERFORM_ALGORITHM);
			TaskIterator taskIterator = commandTaskFactory.createTaskIterator();

			try {
				CyCore.runAndWait(taskIterator);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	};

	/**
	 * Fetches all the parameters from the various swing components and packs them into a neat abstract wrapper class
	 * @return The currently selected parameter wrapped in a ParameterSet
	 */
	public ParameterSet fetchParameters(CyNetwork net) {
		ParameterSet ret = new ParameterSet();
		ret.net = net;
		ret.timeLimit = opModePanel.getTimeLimit();
		ret.setWeightColumn(ecPanel.getWeightColumn());
		ret.setPermanentColumn(ecPanel.getPermanentColumn());
		ret.setForbiddenColumn(ecPanel.getForbiddenColumn());
		ret.threshold = ecPanel.getThreshold();
		ret.defaultInsertionCost = ecPanel.getDefaultInsertionCost();
		ret.defaultDeletionCost = ecPanel.getDefaultDeletionCost();
		ret.useHeuristic = opModePanel.useHeuristic();
		ret.useTriangleCuts = opModePanel.useTriangleCuts();
		ret.usePartitionCuts = opModePanel.usePartitionCuts();
		ret.solCount = opModePanel.getSolCount();
		ret.disableMultiThreading = opModePanel.isMultiThreadingDisabled();
		ret.clusterCount = opModePanel.getClusterCount();
		return ret;
	}


	//GETTER / SETTER

	public Component getComponent() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.application.swing.CytoPanelComponent#getCytoPanelName()
	 */
	public CytoPanelName getCytoPanelName() {
		//By convention most plugins that provide a "toolbox"-like interface use the WEST orientation
		return CytoPanelName.WEST;
	}

	public String getTitle() {
		return "Yoshiko";
	}

	public Icon getIcon() {
		return GraphicsLoader.getLogo(16);
	}
}

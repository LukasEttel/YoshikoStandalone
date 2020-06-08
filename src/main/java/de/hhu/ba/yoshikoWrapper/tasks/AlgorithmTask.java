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
package de.hhu.ba.yoshikoWrapper.tasks;

import java.awt.Window;
import java.util.List;
import java.util.Properties;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.cytoUtil.GraphAnalyzer;
import de.hhu.ba.yoshikoWrapper.yoshikoAlgorithm.YoshikoAlgoritmController;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyColumn;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ContainsTunables;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;

import de.hhu.ba.yoshikoWrapper.core.ParameterSet;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoResult;
import de.hhu.ba.yoshikoWrapper.logging.YoshikoLogger;
import de.hhu.ba.yoshikoWrapper.swing.components.ResultPanel;

import javax.swing.*;


public class AlgorithmTask extends AbstractTask {

	//Constants
	private static final String SOLUTION_COLUMN_PREFIX = "yoshikoSolution_"; //TODO: Make customizable?

	//Symbolic links
	private static Logger logger = YoshikoLogger.getInstance().getLogger();


	private final Window statusWindow; //TODO: Make tunable?

	//Parameters

	/**The ParameterSet specifying how the algorithm is to be performed
	 *
	 */
	@ContainsTunables
	public ParameterSet parameterSet = null;

	private ResultPanel resultPanel;

	private YoshikoResult result;


	/**
	 * Default constructor, creates a new AlgorithmTask
	 * @param statusWindow The Window in which the status-bar is to be shown, can be null
	// * @param net The network that is to be clustered
	 * @param parameterSet The parameter set specifying the clustering mode
	 */
	public AlgorithmTask(
			Window statusWindow,
			ParameterSet parameterSet
			)
	{
		this.statusWindow = statusWindow;
		this.parameterSet = parameterSet;
	}

	@Override
	public void run(final TaskMonitor taskMonitor) throws Exception {
		//TODO: Improve setProgress values
		taskMonitor.setTitle("yoshTask");

		//Check current network
		if (parameterSet.net == null) {
			logger.warn("CoreAlgorithm called on a net that is NULL!");
			throw new Exception("CoreAlgorithm called on a net that is NULL!"); //TODO: Localize
		}

		result = new YoshikoResult(parameterSet.net);

		//We identify the columns if they exist from their given names
		CyColumn weightColumn = parameterSet.getWeightColumn();
		CyColumn permanentColumn = parameterSet.getPermanentColumn();
		CyColumn forbiddenColumn = parameterSet.getForbiddenColumn();

		System.out.print(parameterSet.toString()); //TODO: Move to debug logger


		YoshikoAlgoritmController yoshikoAlgoritmController = new YoshikoAlgoritmController(parameterSet, taskMonitor, result);
		yoshikoAlgoritmController.controllAlgorithm();
		taskMonitor.setProgress(1);

		//Generate solutionsPanel
		resultPanel = new ResultPanel(result);
		//Show solution panel
		CyCore.registrar.registerService(resultPanel, CytoPanelComponent.class, new Properties());
		//Focus solution panel
		CytoPanel eastPanel = CyCore.swing.getCytoPanel(CytoPanelName.EAST);
		eastPanel.setSelectedIndex(eastPanel.indexOfComponent(resultPanel));
		//Show (might be invisible)
		eastPanel.setState(CytoPanelState.DOCK);
		//Blackest magic, attempts to scale the result panel to a good size
		SwingUtilities.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						eastPanel.getThisComponent().setMinimumSize(eastPanel.getThisComponent().getPreferredSize());
						eastPanel.getThisComponent().revalidate();
					}
				}
			);
		//eastPanel.getThisComponent().revalidate();
	}


/*
	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		//We return the id of the result so we can work with the result from CMD
		if (type.equals(String.class)) {
			return (R) ("Result generated with ID: "+result.getID());
		}
		return null;
	}
*//*
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		//In order to validate the arguments for this task we simply check the ParameterSet
		return parameterSet.getValidationState(errMsg);
	}*/
}
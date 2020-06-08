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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static javax.swing.GroupLayout.DEFAULT_SIZE;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

import javax.swing.GroupLayout.Alignment;

@SuppressWarnings("serial")
public class OperationModePanel extends JPanel{

	//SWING COMPONENTS
	private final JCheckBox useClusterCount;
	private final ClusterCountChooser ccChooser;
	private final JRadioButton useHeuristic;
	private final JRadioButton useILP;
	private final TimeLimitSetter timeLimitSetter;
	private final SolutionNumberChooser solutionNumberChooser;
	private final JCheckBox useTriangleCutsBox;
	private final JCheckBox usePartitionCutsBox;
	private final JCheckBox disableMultiThreading;

	private final ButtonGroup heuristicGroup;

	public OperationModePanel() {

		useClusterCount = new JCheckBox(LocalizationManager.get("useClusterCount"));
		ccChooser = new ClusterCountChooser();
		ccChooser.setEnabled(false);

		useClusterCount.addActionListener(ccSwitch);

		heuristicGroup = new ButtonGroup();

		useILP = new JRadioButton("Use Integer Linear Programming");
		useHeuristic = new JRadioButton("Use Heuristic");

		//As a default option we use the heuristic -> faster and usually good @Bachelor's Thesis //TODO: Save this as config?
		useHeuristic.setSelected(true);

		heuristicGroup.add(useILP);
		heuristicGroup.add(useHeuristic);

		solutionNumberChooser = new SolutionNumberChooser();

		timeLimitSetter = new TimeLimitSetter();

		useTriangleCutsBox = new JCheckBox("Use Triangle Cuts");
		usePartitionCutsBox = new JCheckBox("Use Partition Cuts");

		disableMultiThreading = new JCheckBox(LocalizationManager.get("disableMultiThreading"));
		disableMultiThreading.setSelected(true);

		//Link time limit option to ILP
		useILP.addActionListener(ilpHeuristicSwitch);
		useHeuristic.addActionListener(ilpHeuristicSwitch);

		SwingUtil.addAll(this,
				useClusterCount,
				ccChooser
		);

		//We set all components to enabled/disabled according to mode
		ilpHeuristicSwitch();

		//Layout
		GroupLayout layout = new GroupLayout(this);

		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING,true)
				.addComponent(useClusterCount, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(ccChooser, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(useClusterCount)
				.addComponent(ccChooser)
		);

		this.setLayout(layout);

	}

	private void ilpHeuristicSwitch() {
		if (useILP.isSelected()) {
			timeLimitSetter.setEnabled(true);
			useTriangleCutsBox.setEnabled(true);
			usePartitionCutsBox.setEnabled(true);
			solutionNumberChooser.setEnabled(true);
			disableMultiThreading.setEnabled(true);
		}
		else {
			timeLimitSetter.setEnabled(false);
			useTriangleCutsBox.setEnabled(false);
			usePartitionCutsBox.setEnabled(false);
			solutionNumberChooser.setEnabled(false);
			disableMultiThreading.setEnabled(false);
		}
	}

	private ActionListener ilpHeuristicSwitch = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ilpHeuristicSwitch();
		}
	};

	private ActionListener ccSwitch = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			ccChooser.setEnabled(useClusterCount.isSelected());
		}

	};
	//SETTER GETTER

	public int getTimeLimit() {
		return timeLimitSetter.getTimeLimit();
	}

	public boolean useTriangleCuts() {
		return useTriangleCutsBox.isSelected();
	}

	public boolean usePartitionCuts() {
		return usePartitionCutsBox.isSelected();
	}

	public boolean useHeuristic() {
		return useHeuristic.isSelected();
	}

	public boolean isMultiThreadingDisabled() {
		return disableMultiThreading.isSelected();
	}

	public int getSolCount() {
		return solutionNumberChooser.getSolCount();
	}

	public int getClusterCount() {
		return useClusterCount.isSelected() ? ccChooser.getClusterCount() : -1;
	}
}

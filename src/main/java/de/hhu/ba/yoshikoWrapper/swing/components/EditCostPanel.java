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


import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.cytoscape.model.CyColumn;

import javax.swing.GroupLayout.Alignment;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

@SuppressWarnings("serial") //Will never be serialized
public class EditCostPanel extends JPanel {

	//SWING COMPONENTS
	private final ColumnMapper columnMapper;

	private final DoubleInputField  icField;
	private final DoubleInputField dcField;
	private final DoubleInputField threshholdField;
	
	private final JSeparator separator;

	private final JLabel icLabel;
	private final JLabel dcLabel;
	private final JLabel threshholdLable;

	//private final HelpButton helpButton;

	public EditCostPanel() {

		//Initialize components
		//helpButton = new HelpButton();

		columnMapper = new ColumnMapper();

		icField = new DoubleInputField(Double.NEGATIVE_INFINITY,0);
		dcField = new DoubleInputField(0,Double.POSITIVE_INFINITY);
		threshholdField = new DoubleInputField(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		icField.setText("-1.0");
		icField.setToolTipText(LocalizationManager.get("icTooltip"));
		dcField.setText("1.0");
		threshholdField.setText("0.0");
		
		separator = new JSeparator(JSeparator.HORIZONTAL);

		icLabel = new JLabel(LocalizationManager.get("defaultInsertion"));
		dcLabel = new JLabel("Default deletion cost:");
		threshholdLable = new JLabel("Threshold for weight:");

		//Add components
		SwingUtil.addAll(this,columnMapper,separator,icLabel,icField,dcLabel,dcField, threshholdLable, threshholdField);
		//SwingUtil.addAll(helpButton);



		//Layout
		GroupLayout layout = new GroupLayout(this);

		layout.setHorizontalGroup(layout.createParallelGroup()
				//.addComponent(helpButton,Alignment.TRAILING)
				.addComponent(columnMapper)
				.addComponent(separator)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(icLabel)
								.addComponent(dcLabel)
								.addComponent(threshholdLable)
						)
						.addGap(4)
						.addGroup(layout.createParallelGroup(Alignment.LEADING)
								.addComponent(icField)
								.addComponent(dcField)
								.addComponent(threshholdField)
						)
				)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				//.addComponent(helpButton)
				.addComponent(columnMapper)
				.addComponent(separator)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(icLabel)
						.addGap(4)
						.addComponent(icField)
				)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(dcLabel)
						.addGap(4)
						.addComponent(dcField)
				)
				.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(threshholdLable)
						.addGap(4)
						.addComponent(threshholdField)
				)
		);

		this.setLayout(layout);

	}
	//SETTER / GETTER

	public double getDefaultInsertionCost() {
		return icField.getValueAsDouble();
	}

	public double getDefaultDeletionCost() {
		return dcField.getValueAsDouble();
	}

	public double getThreshold() {return threshholdField.getValueAsDouble();}

	public ColumnMapper getColumnMapper() {
		return columnMapper;
	}

	public CyColumn getWeightColumn() {
		return columnMapper.getEditingCostColumn() != null ? columnMapper.getEditingCostColumn() : null;
	}
	
	public CyColumn getPermanentColumn() {
		return columnMapper.getPermanentColumn() != null ? columnMapper.getPermanentColumn() : null;	}

	public CyColumn getForbiddenColumn() {
		return columnMapper.getForbiddenColumn() != null ? columnMapper.getForbiddenColumn(): null;	}

}

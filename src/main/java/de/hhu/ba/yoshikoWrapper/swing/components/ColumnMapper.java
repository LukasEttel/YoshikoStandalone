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

import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;

import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.session.events.SessionLoadedEvent;
import org.cytoscape.session.events.SessionLoadedListener;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.swing.EnableWhenSelectedListener;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

/**
 *
 * @author Philipp Spohr, Nov 30, 2017
 *
 */
@SuppressWarnings("serial") //will never be serialized
public class ColumnMapper extends JPanel
implements //everything
ColumnCreatedListener,
ColumnDeletedListener,
SessionLoadedListener,
SetCurrentNetworkListener

{

	//Swing components
	private final JComboBox<CyColumn> weightMapper;
	private final JComboBox<CyColumn> permanentMapper;
	private final JComboBox<CyColumn> forbiddenMapper;

	private final JCheckBox useMappingCost;
	private final JCheckBox useMappingPerm;
	private final JCheckBox useMappingForb;

	public ColumnMapper() {

		//Listener for Network-Changes -> Updating possible columns for mapping
		CyCore.registrar.registerService(this,ColumnCreatedListener.class,new Properties());
		CyCore.registrar.registerService(this,ColumnDeletedListener.class,new Properties());
		CyCore.registrar.registerService(this,SessionLoadedListener.class,new Properties());
		CyCore.registrar.registerService(this,SetCurrentNetworkListener.class,new Properties());

		//SWING COMPONENTS

		//Combo-Boxes that map to the CyColumns
		weightMapper = new JComboBox<CyColumn>();
		permanentMapper = new JComboBox<CyColumn>();
		forbiddenMapper = new JComboBox<CyColumn>();

		//Should only be enabled if the option is checked
		weightMapper.setEnabled(false);
		permanentMapper.setEnabled(false);
		forbiddenMapper.setEnabled(false);

		useMappingCost = new JCheckBox("Map modification costs");
		useMappingPerm = new JCheckBox("Map edges as permanent");
		useMappingForb = new JCheckBox("Map edges as forbidden");

		useMappingCost.addActionListener(
			new EnableWhenSelectedListener(useMappingCost, weightMapper)
		);
		useMappingPerm.addActionListener(
			new EnableWhenSelectedListener(useMappingPerm, permanentMapper)
			);
		useMappingForb.addActionListener(
			new EnableWhenSelectedListener(useMappingForb, forbiddenMapper)
		);


		SwingUtil.addAll(this,useMappingCost,weightMapper);
		SwingUtil.addAll(this,useMappingPerm,permanentMapper);
		SwingUtil.addAll(this,useMappingForb,forbiddenMapper);

		//Layout
		GroupLayout layout = new GroupLayout(this);

		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(useMappingCost)
					.addComponent(useMappingPerm)
					.addComponent(useMappingForb)
			)
			.addGroup(layout.createParallelGroup(Alignment.LEADING)
					.addComponent(weightMapper)
					.addComponent(permanentMapper)
					.addComponent(forbiddenMapper)
			)

		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(useMappingCost)
						.addComponent(weightMapper)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(useMappingPerm)
						.addComponent(permanentMapper)
				)
				.addGroup(layout.createParallelGroup()
						.addComponent(useMappingForb)
						.addComponent(forbiddenMapper)
				)
		);

		this.setLayout(layout);

		//Initial call to get table values
		updateValues();

	}

	private void updateValues() {

		CyNetwork net = CyCore.cy.getCurrentNetwork();

		if (net != null) { //Check if a network is loaded

			//Clear entries
			weightMapper.removeAllItems();
			for (CyColumn c : net.getDefaultEdgeTable().getColumns()){
				//Only add columns with numeric values
				if (c.getType() == Integer.class || c.getType() == Double.class) {
					weightMapper.addItem(c);
				}
			}
			boolean enable = (weightMapper.getItemCount() > 0) ? true : false;
			useMappingCost.setEnabled(enable);
			if (!useMappingCost.isEnabled()) {
				useMappingCost.setSelected(false);
				weightMapper.setEnabled(false);
			}

			forbiddenMapper.removeAllItems();
			for (CyColumn c : net.getDefaultEdgeTable().getColumns()){
				//Only add columns with boolean values
				if (c.getType() == Boolean.class) {
					forbiddenMapper.addItem(c);
				}
			}
			enable = (forbiddenMapper.getItemCount() > 0) ? true : false;
			useMappingForb.setEnabled(enable);
			if (!useMappingForb.isEnabled()) {
				useMappingForb.setSelected(false);
				forbiddenMapper.setEnabled(false);
			}

			permanentMapper.removeAllItems();
			for (CyColumn c : net.getDefaultEdgeTable().getColumns()){
				//Only add columns with boolean values
				if (c.getType() == Boolean.class) {
					permanentMapper.addItem(c);
				}
			}
			enable = (permanentMapper.getItemCount() > 0) ? true : false;
			useMappingPerm.setEnabled(enable);
			if (!useMappingPerm.isEnabled()) {
				useMappingPerm.setSelected(false);
				permanentMapper.setEnabled(false);
			}

		}
	}


	//LISTENER__IMPL//

	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
			updateValues();
	}

	@Override
	public void handleEvent(SessionLoadedEvent e) {
		updateValues();
	}

	@Override
	public void handleEvent(ColumnDeletedEvent e) {
		try {
			if (
					e.getSource() == CyCore.cy.getCurrentNetwork().getDefaultEdgeTable() ||
					e.getSource() == CyCore.cy.getCurrentNetwork().getDefaultNetworkTable()
				)
			{
				updateValues();
			}
		}
		catch (Exception ex) {
			//TODO:
		}

	}

	@Override
	public void handleEvent(ColumnCreatedEvent e) {
		try {
			if (
					e.getSource() == CyCore.cy.getCurrentNetwork().getDefaultEdgeTable() ||
					e.getSource() == CyCore.cy.getCurrentNetwork().getDefaultNetworkTable()
				)
			{
				updateValues();
			}
		}
		catch (Exception ex) {
			//TODO:
		}

	}

	public CyColumn getEditingCostColumn() {
		if (useMappingCost.isSelected()) {
			return weightMapper.getItemAt(weightMapper.getSelectedIndex());
		}
		return null;
	}

	public CyColumn getForbiddenColumn() {
		if (useMappingForb.isSelected()) {
			return forbiddenMapper.getItemAt(forbiddenMapper.getSelectedIndex());
		}
		return null;
	}

	public CyColumn getPermanentColumn() {
		if (useMappingPerm.isSelected()) {
			return permanentMapper.getItemAt(permanentMapper.getSelectedIndex());
		}
		return null;
	}



}

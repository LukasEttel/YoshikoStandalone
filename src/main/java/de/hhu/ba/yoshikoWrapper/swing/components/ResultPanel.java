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

import static javax.swing.GroupLayout.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.ChangeListener;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.event.AbstractCyEvent;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.AboutToRemoveEdgesEvent;
import org.cytoscape.model.events.AboutToRemoveEdgesListener;
import org.cytoscape.model.events.AboutToRemoveNodesEvent;
import org.cytoscape.model.events.AboutToRemoveNodesListener;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.util.swing.BasicCollapsiblePanel;
import org.cytoscape.view.model.CyNetworkView;

import de.hhu.ba.yoshikoWrapper.core.CyCore;
import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoResult;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoSolution;
import de.hhu.ba.yoshikoWrapper.swing.GraphicsLoader;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

/**Swing component that contains ALL solutions that are found during one run.
 * Conforms to CY 3.5 by being a CytoPanelComponent which is the norm for "result" panels
 */
@SuppressWarnings("serial")//Will never be serialized
public class ResultPanel extends JPanel implements CytoPanelComponent,
//Implements listeners to invalidate results when the graph the result was generated for changes
AboutToRemoveEdgesListener,
AboutToRemoveNodesListener,
AddedEdgesListener,
AddedNodesListener,
NetworkAboutToBeDestroyedListener
{

	//MACRO

	private final JTabbedPane tabbedPane;

	private final JButton destroyButton;

	private BasicCollapsiblePanel marker;
	private final JLabel invalidLabel;

	private final ArrayList<SolutionTab> solutionTabs;
	private final YoshikoResult result;

	private boolean isValid;

	//SWING LAYOUT

	private ParallelGroup horizontalGroup;
	private SequentialGroup verticalGroup;

	public ResultPanel(YoshikoResult result) throws Exception {

		this.result = result;

		this.isValid = true;

		//Init subcomponents
		invalidLabel = new JLabel();
		invalidLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tabbedPane = new JTabbedPane();

		solutionTabs = new ArrayList<SolutionTab>();
		for (YoshikoSolution s : result.getSolutions()) {
			SolutionTab tab = new SolutionTab(s);
			tabbedPane.add(
					LocalizationManager.get("solution")+" "+(s.getId()+1),
					tab
			);
			//Save reference
			solutionTabs.add(tab);
		}

		marker = new BasicCollapsiblePanel(LocalizationManager.get("solutionMarker"));
		marker.setLayout(new BoxLayout(marker,BoxLayout.Y_AXIS));

		this.add(marker);

		destroyButton = new JButton(LocalizationManager.get("discardSolution"));
		destroyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deleteResult();
			}

		});

		SwingUtil.addAll(this,invalidLabel,tabbedPane,destroyButton);

		//Layout
		GroupLayout layout = new GroupLayout(this);

		horizontalGroup = layout.createParallelGroup(Alignment.CENTER,true);
		verticalGroup = layout.createSequentialGroup();

		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);

		horizontalGroup
			.addComponent(marker,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
			.addComponent(invalidLabel,DEFAULT_SIZE, PREFERRED_SIZE,PREFERRED_SIZE)
			.addComponent(tabbedPane,PREFERRED_SIZE, PREFERRED_SIZE,Short.MAX_VALUE)
			.addComponent(destroyButton,DEFAULT_SIZE, DEFAULT_SIZE,PREFERRED_SIZE);
		verticalGroup
			.addComponent(marker,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
			.addComponent(invalidLabel,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
			.addComponent(tabbedPane,DEFAULT_SIZE, DEFAULT_SIZE,DEFAULT_SIZE)
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(destroyButton,DEFAULT_SIZE, DEFAULT_SIZE,PREFERRED_SIZE);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		this.setLayout(layout);

		registerAllListeners();

	}

	private void registerAllListeners() {
		CyCore.registrar.registerService(this, AboutToRemoveEdgesListener.class, new Properties());
		CyCore.registrar.registerService(this, AboutToRemoveNodesListener.class, new Properties());
		CyCore.registrar.registerService(this, AddedEdgesListener.class, new Properties());
		CyCore.registrar.registerService(this, AddedNodesListener.class, new Properties());
		CyCore.registrar.registerService(this, NetworkAboutToBeDestroyedListener.class, new Properties());
	}

	public void deleteResult() {

		int dialogResult = JOptionPane.showConfirmDialog (
				null,
				LocalizationManager.get("deleteSolution"),
				LocalizationManager.get("warning"),
				JOptionPane.YES_NO_OPTION
		);
		if (dialogResult != JOptionPane.YES_OPTION) {
			return;
		}

		//Restore view
		if (result.getOriginalGraph() != null) {
			Collection<CyNetworkView> viewsToReturn = CyCore.networkViewManager.getNetworkViews(result.getOriginalGraph());
			if (!viewsToReturn.isEmpty()) {
				CyCore.cy.setCurrentNetworkView(viewsToReturn.iterator().next());
			}
		}

		CyCore.registrar.unregisterService(this,CytoPanelComponent.class);
		result.delete();
		super.setVisible(false);

	}

	private void invalidateResult() {
		if (isValid) {
			isValid = false;
		}
		invalidLabel.setText(LocalizationManager.get("invalidated"));
		invalidLabel.setForeground(Color.RED);
		for (SolutionTab t: solutionTabs) {
			t.invalidateResult();
		}
	}


	private void invalidateIfRelevant(AbstractCyEvent<CyNetwork> e) {
		if (e.getSource() == result.getOriginalGraph()) {
			invalidateResult();
		}
	}

	//______________SETTER/GETTER_______________//

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	@Override
	public String getTitle() {
		return LocalizationManager.get("resultsPanelTitle");
	}

	@Override
	public Icon getIcon() {
		return GraphicsLoader.getSolvedLogo(16);
	}

	public YoshikoResult getResult() {
		return result;
	}


	//LISTENER METHODS

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		if (e.getNetwork() == result.getOriginalGraph()) {
			invalidate();
		}
	}
	@Override
	public void handleEvent(AddedNodesEvent e) {
		invalidateIfRelevant(e);
	}

	@Override
	public void handleEvent(AddedEdgesEvent e) {
		invalidateIfRelevant(e);
	}

	@Override
	public void handleEvent(AboutToRemoveNodesEvent e) {
		invalidateIfRelevant(e);
	}

	@Override
	public void handleEvent(AboutToRemoveEdgesEvent e) {
		invalidateIfRelevant(e);
	}
}

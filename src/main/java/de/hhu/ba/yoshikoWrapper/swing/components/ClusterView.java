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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;

import org.cytoscape.model.CyNode;
import org.cytoscape.util.swing.BasicCollapsiblePanel;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.swing.GraphicsLoader;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

@SuppressWarnings("serial")
public class ClusterView extends JPanel {

	//MACRO / STATIC
	private final static int CLUSTER_ICON_SIZE = 128;

	private static final Border regularBorder = BorderFactory.createLineBorder(Color.GRAY,3);
	private static final Border highlightBorder = BorderFactory.createLineBorder(GraphicsLoader.yoshikoGreen,3);
	private static final Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE,3);

	//SWING COMPONENTS

	private final JLabel title;
	private final JLabel clusterSize;
	private final JLabel icon;
	private final BasicCollapsiblePanel nodeList;

	//INTERNAL

	private boolean isSelected;

	//SYMBOLIC LINKS

	private final YoshikoCluster cluster;

	public ClusterView(YoshikoCluster c) throws Exception {

		this.cluster = c;

		this.isSelected = false;

		//Swing init

		title = new JLabel(LocalizationManager.get("clusters")+" "+(c.getID()+1));
		clusterSize = new JLabel(LocalizationManager.get("clusterSize")+" "+c.getSize());
		icon = new JLabel();
		//icon.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		cluster.generateClusterIcon(CLUSTER_ICON_SIZE, CLUSTER_ICON_SIZE,icon);

		SwingUtil.addAll(this,title,clusterSize,icon);

		nodeList = new BasicCollapsiblePanel(LocalizationManager.get("nodes"));

		//Loop over nodes in the clusters and add them to the view
		for (CyNode n : c.getSubNetwork().getNodeList()) {
			nodeList.add(
				new JLabel(
					c.getNodeName(n)
				)
			);
		}

		this.add(nodeList);

		this.addMouseListener(mouseListener);
		this.setBorder(regularBorder);

		GroupLayout layout = new GroupLayout(this);

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING,true)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(Alignment.LEADING,true)
								.addComponent(title,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
								.addComponent(clusterSize,DEFAULT_SIZE,DEFAULT_SIZE,PREFERRED_SIZE)
						)
						.addGap(12)
						.addComponent(icon)
					)
				.addComponent(nodeList)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(Alignment.CENTER,true)
						.addGroup(layout.createSequentialGroup()
								.addComponent(title)
								.addComponent(clusterSize)
						)
						.addGap(12)
						.addComponent(icon)
					)
				.addComponent(nodeList)
		);

		this.setLayout(layout);

	}

	private MouseListener mouseListener = new MouseListener() {

		@Override
		public void mouseClicked(MouseEvent e) {
			isSelected = !isSelected;
			setBorder(isSelected ? selectedBorder : regularBorder);
		}

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			cluster.highlight();
			setBorder(isSelected ? selectedBorder : highlightBorder);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setBorder(isSelected ? selectedBorder : regularBorder);
		}


	};

	//____________SETTER  GETTER ________________//

	public boolean isSelected() {
		return isSelected;
	}

	public YoshikoCluster getCluster() {
		return cluster;
	}
}

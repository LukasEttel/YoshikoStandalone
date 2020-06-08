package de.hhu.ba.yoshikoWrapper.swing.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import org.slf4j.Logger;

import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoCluster;
import de.hhu.ba.yoshikoWrapper.logging.YoshikoLogger;

@SuppressWarnings("serial")
public class ClusterViewList extends JPanel implements Scrollable {

	private static final Logger logger = YoshikoLogger.getInstance().getLogger();

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return this.getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 16;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 16;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public ClusterView add(Component c) throws NullPointerException{
		//Simple type check
		if (c instanceof ClusterView) {
			return (ClusterView) super.add(c);
		}
		logger.error("Attempted to add a component to ClusterViewList that is not a ClusterView");
		return null;
	}

	public ArrayList<ClusterView> getClusterViews() {
		ArrayList<ClusterView> ret = new ArrayList<ClusterView>();
		for (Component c : this.getComponents()) {
			if (c instanceof ClusterView) {
				ret.add((ClusterView)c);
			}
		}
		return ret;
	}

	public ArrayList<YoshikoCluster> getSelectedClusters() {
		ArrayList<YoshikoCluster> ret = new ArrayList<YoshikoCluster>();
		ArrayList<ClusterView> cv = getClusterViews();
		for (ClusterView v : cv) {
			if (v.isSelected()) {
				ret.add(v.getCluster());
			}
		}
		return ret;
	}

	public void toggleSingleVisibility(boolean showSingles) {
		for (ClusterView cv : getClusterViews()) {
			if (cv.getCluster().getSize() > 1 || showSingles) {
				cv.setVisible(true);
			}
			else {
				cv.setVisible(false);
			}
		}

	}

}

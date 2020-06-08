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
package de.hhu.ba.yoshikoWrapper.cytoUtil;

import java.util.List;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;

import de.hhu.ba.yoshikoWrapper.core.CyCore;

public class StyleManager {

	public static final String CLUSTERSIZE_COLUMN_NAME = "clusterSize";
	public static final String EDGESTRENGTH_COLUMN_NAME = "edgeStrength";

	public static final double MIN_EDGE_SIZE = 1.0;
	public static final double MAX_EDGE_SIZE = 10.0;

	public static final double MIN_NODE_SIZE = 5.0;
	public static final double MAX_NODE_SIZE = 30.0;



	public static void style(CyNetworkView view, VisualStyle style) {
		CyCore.visualMappingManager.setVisualStyle(style,view);
		style.apply(view);
		view.updateView();
	}

	public static void styleWithMapping(CyNetworkView view, VisualStyle style) {

		style = CyCore.visualStyleFactory.createVisualStyle(style);
		CyCore.visualMappingManager.setVisualStyle(style,view);


		//Define style for solution
		ContinuousMapping<Integer, Double> contMapNodes = (ContinuousMapping<Integer, Double>)CyCore.continuousMappingFactory.createVisualMappingFunction(
				CLUSTERSIZE_COLUMN_NAME,
				Integer.class,
				BasicVisualLexicon.NODE_SIZE
			);

		ContinuousMapping<Integer, Double> contMapEdges = (ContinuousMapping<Integer, Double>)CyCore.continuousMappingFactory.createVisualMappingFunction(
				EDGESTRENGTH_COLUMN_NAME,
				Integer.class,
				BasicVisualLexicon.EDGE_WIDTH
			);

		//Calculate size range and scale accordingly
		List<Integer> sizes = view.getModel().getDefaultNodeTable().getColumn(CLUSTERSIZE_COLUMN_NAME).getValues(Integer.class);
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (Integer i : sizes) {
			if (i < min) {
				min = i;
			}
			if (i > max) {
				max = i;
			}
		}
		contMapNodes.addPoint(min, new BoundaryRangeValues<Double>(MIN_NODE_SIZE,MIN_NODE_SIZE,MIN_NODE_SIZE));
		contMapNodes.addPoint(max, new BoundaryRangeValues<Double>(MAX_NODE_SIZE,MAX_NODE_SIZE,MAX_NODE_SIZE));

		sizes = view.getModel().getDefaultEdgeTable().getColumn(EDGESTRENGTH_COLUMN_NAME).getValues(Integer.class);
		min = Integer.MAX_VALUE;
		max = Integer.MIN_VALUE;
		for (Integer i : sizes) {
			if (i < min) {
				min = i;
			}
			if (i > max) {
				max = i;
			}
		}

		contMapEdges.addPoint(min, new BoundaryRangeValues<Double>(MIN_EDGE_SIZE,MIN_EDGE_SIZE,MIN_EDGE_SIZE));
		contMapEdges.addPoint(max, new BoundaryRangeValues<Double>(MAX_EDGE_SIZE,MAX_EDGE_SIZE,MAX_EDGE_SIZE));

		style.addVisualMappingFunction(contMapNodes);
		style.addVisualMappingFunction(contMapEdges);

		style.apply(view);
		view.updateView();

	}

}

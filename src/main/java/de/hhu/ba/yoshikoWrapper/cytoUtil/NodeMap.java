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

import java.util.HashMap;
import java.util.Map.Entry;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

public class NodeMap {
	
	private HashMap<CyNode,Long> nodeMap;
	
	
	public NodeMap(CyNetwork net){
		nodeMap = new HashMap<CyNode,Long> ();
		long index = 0;
		for (CyNode n :net.getNodeList()) {
			nodeMap.put(n, index);
			index++;
		}
		
	}


	public long get(CyNode key) {
		return nodeMap.get(key);
	}


	public CyNode indexOf(long nodeID) {
	    for (Entry<CyNode, Long> entry : nodeMap.entrySet()) {
	        if (nodeID ==  entry.getValue()) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}

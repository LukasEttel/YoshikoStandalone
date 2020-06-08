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
package de.hhu.ba.yoshikoWrapper.graphModel;

import java.util.Collection;
import java.util.HashMap;

import org.cytoscape.model.CyNetwork;

import de.hhu.ba.yoshikoWrapper.core.ResultList;


/**Basic data class that represents a CES instance internally. By using this class the C++ resources can be freed upon retrieval.
 *
 */
public class YoshikoResult{

	private CyNetwork originalGraph;

	private HashMap<Long,YoshikoSolution> solutions;


	private int id;

	public YoshikoResult(CyNetwork net) {
		solutions = new HashMap<Long,YoshikoSolution>();
		this.originalGraph = net;

		ResultList.add(this);
	}

	public void delete() {
		for (YoshikoSolution s: solutions.values()) {
			s.delete();
		}
		ResultList.remove(this.id);
	}

	//___________SETTER GETTER_____________//

	/**
	 * @return the flags associated with this result
	 */

	public void addSolution(YoshikoSolution solution) {
		solutions.put(solution.getId(),solution);
	}

	public Collection<YoshikoSolution> getSolutions() {
		return solutions.values();
	}

	public CyNetwork getOriginalGraph() {
		return originalGraph;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getID() {
		return id;
	}

	public YoshikoSolution getSolution(long solutionID) {
		return solutions.get(solutionID);
	}

}

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

package de.hhu.ba.yoshikoWrapper.core;


import java.io.IOException;
import java.util.ArrayList;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.util.ListSingleSelection;

public class ParameterSet implements TunableValidator
{

	@Tunable(description="Network to analyze for clusters", context="nogui")
	public CyNetwork net = CyCore.cy.getCurrentNetwork();

	@Tunable(description="Time Limit for the ILP mode", context="nogui")
	public int timeLimit = -1;
	
	//COLUMN-MAPPINGS

	@Tunable(description="A column in the edge table containing weights", context="nogui")
	public ListSingleSelection<CyColumn> weightColumn = null;
	@Tunable(description="A column containing boolean entries for edges that are to be treated as permanent",context="nogui")
	public ListSingleSelection<CyColumn> permanentColumn = null;
	@Tunable(description="A column containing boolean entries for edges that are to be treated as forbidden",context="nogui")
	public ListSingleSelection<CyColumn> forbiddenColumn = null;

	@Tunable(description="The default insertion cost that is to be used for non-existing edges",context="nogui")
	public double defaultInsertionCost = -1;
	@Tunable(description="The default deletion cost that is to be used for edges without an associated weight",context="nogui")
	public double defaultDeletionCost = 1;
	@Tunable(description="The threshold to tackle the problem of a completly poitive graph", context="nogui")
	public double threshold = 0;

	@Tunable(description="A bitmask representing which reduction rules should be used",context="nogui")
	public String reductionRulesBitMask = "000000";
	@Tunable(description="A value controlling the resolution of the SNR reduction rule. Higher values mean a longer running time but possibly better reduction.",context="nogui")
	public double snrMultFactor = 1.0;

	@Tunable(description="Alternative Callback for CPLEX, might be faster on certain instances",context="nogui")
	public boolean useTriangleCuts = false;
	@Tunable(description="Alternative Callback for CPLEX, might be faster on large instances",context="nogui")
	public boolean usePartitionCuts = false;

	@Tunable(description="Uses a heuristic instead of ILP to solve WCE, significantly faster",context="nogui")
	public boolean useHeuristic = true;

	@Tunable(description="The maximum number of (optimal) solutions that is to be calculated",context="nogui")
	public int solCount = 1;

	@Tunable(description="Disable multithreading to keep the system responsive",context="nogui")
	public boolean disableMultiThreading;

	@Tunable(description="Automatically choose an appopriate set of reduction rules (overrides a given bitmask)",context="nogui")
	/**Describes whether auto configuration of the reduction rules is to be used. Overrides the bit mask.**/
	public boolean suggestReduction = true;

	@Tunable(description="Determines the number of clusters that are to be generated. -1 generates the optimal amount of clusters in the sense of WCE",context="nogui")
	public int clusterCount = -1;


	//ugly passing of variables
	public boolean containsOnlyPositiveEdges;
	public double recomendetTreshold;
	/**
	 * Default constructor, initializes the column mappings to provide a selection of fitting columns
	 */
	public ParameterSet() {

		ArrayList<CyColumn> numericColumns = new ArrayList<CyColumn>();
		
		//Only numeric columns are relevant for weight mapping
		for (CyColumn col : net.getDefaultEdgeTable().getColumns()) {
			if (Number.class.isAssignableFrom(col.getType())) {
				numericColumns.add(col);
			}
		}
		weightColumn = new ListSingleSelection<CyColumn>(numericColumns);
		
		ArrayList<CyColumn> booleanColumns = new ArrayList<CyColumn>();
		
		//Only boolean columns are relevant for forbidden/permanent mapping
		for (CyColumn col : net.getDefaultEdgeTable().getColumns()) {
			if (col.getType() == Boolean.class) {
				booleanColumns.add(col);
			}
		}
		forbiddenColumn = new ListSingleSelection<CyColumn>(booleanColumns);
		permanentColumn = new ListSingleSelection<CyColumn>(booleanColumns);
		
		//Don't select any columns by default
		weightColumn.setSelectedValue(null);
		forbiddenColumn.setSelectedValue(null);
		permanentColumn.setSelectedValue(null);

	}

	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		System.out.println("DEBUG: Running VALIDATION of tunables"); //TODO: Move to logger (if it would work)
		try {
			if (!checkBitmask(reductionRulesBitMask)) {
				errMsg.append("The Bitmask provided is invalid! Needs to be six bit binary (example: 011001)\n");
				return ValidationState.INVALID;
			}
			//Checks for cluster count number
			if ((clusterCount < 0 && clusterCount != -1)||(clusterCount == 0)) {
				errMsg.append("Invalid cluster count number!");
				return ValidationState.INVALID;
			}
			return ValidationState.OK;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ValidationState.INVALID;
	}

	/**
	 * Helper function that verifies a String and checks if it represents a 6-bit bitmask
	 * @param mask
	 * @return
	 */
	private boolean checkBitmask(String mask) {
		if (mask.length() != 6) {
			return false;
		}
		for (byte c : mask.getBytes()) {
			if (((char)c)!='0'&&((char)c)!='0') {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString(){
		String ret = "[ParameterSet]:\n";
		ret += "Target Cluster Count: "+clusterCount+"\n";
		ret += "Weight mapped to column: "+(weightColumn.getSelectedValue() != null ? weightColumn.getSelectedValue().getName() : "[NONE]")+"\n";
		ret += "Permanent edges mapped to column: "+(permanentColumn.getSelectedValue() != null ? permanentColumn.getSelectedValue().getName() : "[NONE]")+"\n";
		ret += "Forbidden edges mapped to column: "+(forbiddenColumn.getSelectedValue() != null ? forbiddenColumn.getSelectedValue().getName() : "[NONE]")+"\n";
		//TODO
		return ret;
	}
	
	//SETTER & GETTER
	
	//TODO: Code Redundancy, might be smart to insert the three columns into some sort of super structure and then remove the redundant setters/getters, pass column type as argument

	public void setWeightColumn(CyColumn column) {
		weightColumn.setSelectedValue(column);
	}
	
	public void setPermanentColumn(CyColumn column) {
		permanentColumn.setSelectedValue(column);
	}
	
	public void setForbiddenColumn(CyColumn column) {
		forbiddenColumn.setSelectedValue(column);
	}

	public CyColumn getWeightColumn() {
		return weightColumn.getSelectedValue();
	}
	
	public CyColumn getPermanentColumn() {
		return permanentColumn.getSelectedValue();
	}
	
	public CyColumn getForbiddenColumn() {
		return forbiddenColumn.getSelectedValue();
	}

}

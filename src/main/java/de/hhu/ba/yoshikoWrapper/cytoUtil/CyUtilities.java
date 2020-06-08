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


/**
 * Helper functions that simplify the Cytoscape API that haven't found a home in any other class
 *
 */
public class CyUtilities {

	/**
	 * Selects the tab containing the CyBrowser in the result panel if such a tab exists
	 */
	public static void highlightCyBrowser() {
		//We retrieve the result panel
		//CytoPanel resultPanel = CyCore.swing.getCytoPanel(CytoPanelName.EAST);
		
		//VARIANT 1: FETCH BY TITLE OF TAB
		
//		for (int i= 0; i< resultPanel.getCytoPanelComponentCount(); i++) {
//			Component component = resultPanel.getComponentAt(i);
//			if (component instanceof CytoPanelComponent2) {
//				CytoPanelComponent2 cmp = (CytoPanelComponent2) component;
//				if (cmp.getTitle().contains("YoshikoInfo")) {
//					resultPanel.setSelectedIndex(i);
//					return;
//				}
//			}
//			
//		}
		
		//VARIANT 2: FETCH BY CLASS OF TAB COMPONENT
		
//		int index = resultPanel.indexOfComponent("edu.ucsf.rbvi.cyBrowser.internal.view.ResultsPanelBrowser");
//		if (index != -1) {
//			resultPanel.setSelectedIndex(index);
//		}
//		else {
//			//TODO: Output something?
//			System.err.println("Failed to identify CyBrowser tab");
//		}
	}

}

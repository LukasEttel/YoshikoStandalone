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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

@SuppressWarnings("serial")
public class ClusterCountChooser extends JPanel {
	private final IntegerInputField numSolutionsSetter;
	private final JLabel label;

	public ClusterCountChooser() {
		//Swing Component init
		numSolutionsSetter = new IntegerInputField(1,Integer.MAX_VALUE);
		numSolutionsSetter.setValue(2);
		label = new JLabel(LocalizationManager.get("nrClusters"));
		
		SwingUtil.addAll(this,label, numSolutionsSetter);
		
		BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
		this.setLayout(layout);
	}

	@Override
	public void setEnabled(boolean enabled) {
		numSolutionsSetter.setEnabled(enabled);
	}

	public int getClusterCount() {
		return numSolutionsSetter.getValueAsInt();
	}
}

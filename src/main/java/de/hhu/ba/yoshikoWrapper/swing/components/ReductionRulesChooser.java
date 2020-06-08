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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

@SuppressWarnings("serial")
public class ReductionRulesChooser extends JPanel{

	private final JCheckBox useAutoConfig;

	private final JSeparator separator;

	private final JCheckBox useCRule;
	private final JCheckBox useCCRule;
	private final JCheckBox useACRule;
	private final JCheckBox useHERule;
	private final JCheckBox usePDRRule;
	private final JCheckBox useSNRule;

	private final DoubleInputField multFactor;

	private final JPanel SNPanel;


	public ReductionRulesChooser() {

		//Initialize subcomponents
		useAutoConfig = new JCheckBox(LocalizationManager.get("autoConfig"));
		useAutoConfig.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				enableRuleSelection(e.getStateChange()== ItemEvent.DESELECTED ? true : false);
			}

		});
		separator = new JSeparator(SwingConstants.HORIZONTAL);
		//TODO: Localization
		useCRule = new JCheckBox("Use Clique Rule");
		useCCRule = new JCheckBox("Use Critical-Clique Rule");
		useACRule = new JCheckBox("Use Almost-Clique Rule");
		useHERule = new JCheckBox("Use Heavy-Edge 3 in 1 Rule");
		usePDRRule = new JCheckBox("Use Parameter Dependent Reduction Rule");
		useSNRule = new JCheckBox("Use Similar Neighborhood Rule");

		multFactor = new DoubleInputField(1, Double.POSITIVE_INFINITY);
		multFactor.setText("1.0");

		SNPanel = new JPanel();
		SNPanel.setLayout(new BoxLayout(SNPanel,BoxLayout.X_AXIS));

		SwingUtil.addAll(SNPanel,new JLabel(LocalizationManager.get("multFactor")),multFactor);

		enableRuleSelection(false);
		useAutoConfig.setSelected(true);

		useSNRule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (useSNRule.isSelected()) {
					multFactor.setEnabled(true);
				}
				else {
					multFactor.setEnabled(false);
				}
			}
		});


		SwingUtil.addAll(this,
				useAutoConfig,
				separator,
				useCRule,
				useCCRule,
				useACRule,
				useHERule,
				usePDRRule,
				useSNRule,
				SNPanel
				);

		//Layout
		GroupLayout layout = new GroupLayout(this);

		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING,true)
			.addComponent(useAutoConfig, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(separator, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useCRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useCCRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useACRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useHERule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(usePDRRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useSNRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(SNPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(useAutoConfig, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(separator, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useCRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useCCRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useACRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useHERule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(usePDRRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(useSNRule, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(SNPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
		);

		this.setLayout(layout);

	}


	protected void enableRuleSelection(boolean enable) {
		useCRule.setEnabled(enable);
		useCCRule.setEnabled(enable);
		useACRule.setEnabled(enable);
		useHERule.setEnabled(enable);
		usePDRRule.setEnabled(enable);
		useSNRule.setEnabled(enable);
		multFactor.setEnabled(enable);
	}


	/** Creates a 6 Bit bitmask representing the currently selected choice of reduction rules.
	 * @return
	 */
	public String getBitMask() {
		String ret = "";
		ret += (useCRule.isSelected() ? "1" : "0");
		ret += (useCCRule.isSelected() ? "1" : "0");
		ret += (useACRule.isSelected() ? "1" : "0");
		ret += (useHERule.isSelected() ? "1" : "0");
		ret += (usePDRRule.isSelected() ? "1" : "0");
		ret += (useSNRule.isSelected() ? "1" : "0");
		return ret;
	}

	public boolean useAutoConfig() {
		return useAutoConfig.isSelected();
	}

	public double getMultFactor() {
		if (useSNRule.isSelected()) {
			return multFactor.getValueAsDouble();
		}
		return 1.0d;
	}
}

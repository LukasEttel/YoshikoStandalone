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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.swing.SwingUtil;

@SuppressWarnings("serial")
public class TimeLimitSetter extends JPanel{

	private JCheckBox checkBox;
	private IntegerInputField numberField;
	
	public TimeLimitSetter() {
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		checkBox = new JCheckBox(LocalizationManager.get("timeLimitILP"));
		numberField = new IntegerInputField();
		numberField.setText("30");
		checkBox.addActionListener(
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					numberField.setEnabled(checkBox.isSelected());
				}
				
			}
		);
		this.checkBox.setSelected(true);
		setEnabled(true);
		//REGISTER COMPONENTS
		SwingUtil.addAll(this,checkBox,numberField);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (enabled) {
			this.checkBox.setEnabled(enabled);
			this.numberField.setEnabled(this.checkBox.isSelected());
		}
		else {
			this.checkBox.setEnabled(enabled);
			this.numberField.setEnabled(enabled);
		}
	}
	
	public int getTimeLimit() {
		if (!checkBox.isSelected()) {
			return -1;
		}
		return numberField.getValueAsInt();
	}

}

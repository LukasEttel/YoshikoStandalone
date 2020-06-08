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

import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;

import de.hhu.ba.yoshikoWrapper.swing.FormatHelper;

/**
 * Provides a more strict input field that only accepts integers
 */
@SuppressWarnings("serial")
public class IntegerInputField extends JFormattedTextField{

	private final NumberFormatter formatter;

	public IntegerInputField(int minValue, int maxValue) {
		super();
		formatter = FormatHelper.getIntegerFormatter(minValue,maxValue);
		this.setFormatter(formatter);
		this.setColumns(8);
	}

	public IntegerInputField() {
		super();
		formatter = FormatHelper.getIntegerFormatter();
		this.setFormatter(formatter);
		this.setColumns(8);
	}

	public int getValueAsInt() {
		return (int) getValue();
	}
}

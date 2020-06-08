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
package de.hhu.ba.yoshikoWrapper.swing;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.text.NumberFormatter;

/**
 * Serves as a Factory for various formatters that might be helpful for restricted input fields.
 * @author Philipp Spohr, Dec 12, 2017
 *
 */
public class FormatHelper {

	/**Returns a basic integer input formatter accepting only positive integers
	 * Equivalent to getIntegerFormatter(0,Integer.MAX_VALUE)
	 * @return The NumberFormatter object
	 */
	public static NumberFormatter getIntegerFormatter() {
		return getIntegerFormatter(0, Integer.MAX_VALUE);
	}

	/**
	 * Returns a basic integer input formatter accepting only integers in the given range
	 * @param minValue The smallest, accepted integer
	 * @param maxValue The largest, accepted integer
	 * @return A NumberFormatter object accepting only integers in the given range
	 */
	public static NumberFormatter getIntegerFormatter(int minValue, int maxValue) {
		NumberFormat format = NumberFormat.getInstance();
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(minValue);
	    formatter.setMaximum(maxValue);
	    applySharedAttributes(formatter);

	    return formatter;
	}

	public static NumberFormatter getDoubleFormatter(double minValue, double maxValue) {
		NumberFormat format = DecimalFormat.getInstance();
		format.setMaximumFractionDigits(Integer.MAX_VALUE);
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Double.class);
	    formatter.setMinimum(minValue);
	    formatter.setMaximum(maxValue);
	    applySharedAttributes(formatter);

	    return formatter;
	}

	private static void applySharedAttributes(NumberFormatter formatter) {

	    formatter.setCommitsOnValidEdit(true);
	    formatter.setAllowsInvalid(true);
	}

}

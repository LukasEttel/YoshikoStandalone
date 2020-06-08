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
package de.hhu.ba.yoshikoWrapper.swing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;


import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;
import de.hhu.ba.yoshikoWrapper.cytoUtil.CommandExecutor;
import de.hhu.ba.yoshikoWrapper.help.HelpLinks;
import de.hhu.ba.yoshikoWrapper.swing.GraphicsLoader;

@SuppressWarnings("serial")
public class HelpButton extends JButton{

	/**
	 * The final size (width and height) of the help button in pixel, declared here for better code maintainability
	 */
	private static final int SIZE = 16;

	/**
	 * The default icon for the help button that should be shown if the button is not highlighted
	 */
	private static final ImageIcon defaultIcon = GraphicsLoader.getInfoIcon(SIZE);

	/**
	 * The icon for the help button that should be shown if the button is highlighted
	 */
	private static final ImageIcon hlIcon = GraphicsLoader.getInfoIconHL(SIZE);


	/**
	 * Default Constructor, creates a JButton that opens the help section in CyBrowser
	 */
	public HelpButton() {

		super(defaultIcon);

		//Design/Style
		setToolTipText(LocalizationManager.get("tooltip_helpButton"));
		setBorder(BorderFactory.createEmptyBorder());

		//Add main functionality
		addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CommandExecutor.executeCommand("cybrowser", "dialog",HelpLinks.mainInfo , null);
					//Workaround to instantly select/highlight the CyBrowser tab
					//CyUtilities.highlightCyBrowser();
				}
			}
		);

		//Add mouse listener (cosmetic only)
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {
				setIcon(hlIcon);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setIcon(defaultIcon);
			}
		});
	}
}

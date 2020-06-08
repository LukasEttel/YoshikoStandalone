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

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.hhu.ba.yoshikoWrapper.core.LocalizationManager;

public class GraphicsLoader {

	//TODO: Organize Images in HashMap for better readability / flexibility

	private static BufferedImage yoshikoLogo;
	private static BufferedImage yoshikoLogo_solved;
	private static BufferedImage yoshikoText;
	private static BufferedImage infoIcon;
	private static BufferedImage infoIcon_highlighted;

	/**
	 * The fixed Yoshiko styled color, for quick reference
	 */
	public final static Color yoshikoGreen = new Color(0,128,0);

	private final static ClassLoader classLoader = GraphicsLoader.class.getClassLoader();

	private final static HashMap<Locale,BufferedImage> flags;
	static {
		flags = new HashMap<Locale,BufferedImage>();
		try {
			flags.put(LocalizationManager.usEnglish, ImageIO.read(classLoader.getResource("graphics/flags/enUS.png")));
			flags.put(LocalizationManager.german, ImageIO.read(classLoader.getResource("graphics/flags/deDE.png")));
			flags.put(LocalizationManager.serbocroatianLatin, ImageIO.read(classLoader.getResource("graphics/flags/hrHR.png")));
			flags.put(LocalizationManager.modernGreek, ImageIO.read(classLoader.getResource("graphics/flags/elEL.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ImageIcon getFlag(Locale lcl, int width, int height) {
		return new ImageIcon(flags.get(lcl).getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getInfoIconHL(int size) {
		if (infoIcon_highlighted == null) {
			try {
				infoIcon_highlighted = ImageIO.read(
						classLoader.getResource("graphics/InfoHighlighted.png")
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ImageIcon(infoIcon_highlighted.getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getInfoIcon(int size) {
		if (infoIcon == null) {
			try {
				infoIcon = ImageIO.read(
						classLoader.getResource("graphics/Info.png")
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ImageIcon(infoIcon.getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getLogo(int size) {
		if (yoshikoLogo == null) {
			try {
				yoshikoLogo = ImageIO.read(
						classLoader.getResource("graphics/YoshikoLogo.png")
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ImageIcon(yoshikoLogo.getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}

	public static ImageIcon getSolvedLogo(int size) {
		if (yoshikoLogo_solved == null) {
			try {
				yoshikoLogo_solved = ImageIO.read(
						classLoader.getResource("graphics/YoshikoSolved.png")
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ImageIcon(yoshikoLogo_solved.getScaledInstance(size, size, Image.SCALE_SMOOTH));
	}


	public static ImageIcon getText(int height) {
		//Default dimension is 258x48
		if (yoshikoText == null) {
			try {
				yoshikoText = ImageIO.read(
						classLoader.getResource("graphics/YoshikoText.png")
				);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ImageIcon(yoshikoText.getScaledInstance((int)(height*258/48), height, Image.SCALE_SMOOTH));
	}
}

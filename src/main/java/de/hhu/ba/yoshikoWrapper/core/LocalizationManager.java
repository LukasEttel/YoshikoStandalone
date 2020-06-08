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
package de.hhu.ba.yoshikoWrapper.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/** Static collection of functions and states representing the current locale and providing access to localized strings
 *  This class should never be initialized
 */
public class LocalizationManager {

	/**
	 * Default constructor, hidden to prevent invocation
	 */
	private LocalizationManager() {};

	//Locales

	static public final Locale usEnglish = new Locale("en","US");
	static public final Locale german = new Locale("de","DE");
	static public final Locale serbocroatianLatin = new Locale("hr","HR");
	static public final Locale modernGreek = new Locale("el","EL");

	//Map

    static private final HashMap<String, Locale> locales;
    static
    {
        locales = new HashMap<String, Locale>();
        locales.put("enUS", usEnglish);
        locales.put("deDE", german);
        locales.put("hrHR", serbocroatianLatin);
        locales.put("elEL", modernGreek);
    }


	static private ResourceBundle currentBundle;

	static private Locale currentLocale = usEnglish;


	private static void updateBundle() {
		currentBundle = ResourceBundle.getBundle("YoshikoStrings",currentLocale);
		CyCore.cm.getProperties().setProperty(
				"locale",
				currentLocale.getLanguage()+currentLocale.getCountry()
				);
	}

	//https://en.wikipedia.org/wiki/Regional_Indicator_Symbol -> Right now there are not enough fonts support this
//	public static  String localeToUnicodeFlag(Locale locale) {
//		String countryCode = locale.getCountry();
//		int firstLetter = Character.codePointAt(countryCode, 0)-0x41+0x1F1E6;
//		int secondLetter = Character.codePointAt(countryCode, 1)-0x41+0x1F1E6;
//		return new String(Character.toChars(firstLetter))+new String(Character.toChars(secondLetter));
//	}


	//SETTER/GETTER methods

	static public Collection<Locale> getLocales(){
		return locales.values();
	}

	/**
	 * Returns the localized string corresponding to the given key.
	 * This is the main method that should be used for accessing strings and is preferable to any use of constant strings.
	 * @param key The key for the string (refer to YoshikoStrings.properties for all possible keys)
	 * @return The localized string
	 */
	static public String get(String key) {
		if (currentBundle == null) {
			currentBundle = ResourceBundle.getBundle("YoshikoStrings",currentLocale);
		}
		return currentBundle.getString(key);
	}

	static public void switchLanguage(Locale lcl) {
		currentLocale = lcl;
		updateBundle();
	}

	public static void switchLanguage(String key) {
		//System.out.println("DEBUG: SWITCHING TO: "+key);
		currentLocale = locales.get(key);
		updateBundle();
	}

	public static Locale getCurrentLocale() {
		return currentLocale;
	}
}

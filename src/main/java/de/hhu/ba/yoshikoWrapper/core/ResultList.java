package de.hhu.ba.yoshikoWrapper.core;

import java.util.HashMap;

import de.hhu.ba.yoshikoWrapper.graphModel.YoshikoResult;

/**
 * Simple wrapper for a HashMap that manages unique IDs
 * @author Philipp Spohr, Dec 11, 2017
 *
 */
public class ResultList {

	private static HashMap<Integer,YoshikoResult> map = new HashMap<Integer,YoshikoResult>();

	public static void add(YoshikoResult yoshikoResult) {
		int i = 0;
		while (true) {
			if (!map.containsKey(i)) {
				map.put(i, yoshikoResult);
				yoshikoResult.setID(i);
				return;
			}
			i++;
		}
	}

	public static void remove(int resultID) {
		map.remove(resultID);
	}

	public static YoshikoResult get(int resultID) {
		if (map.containsKey(resultID)) {
			return map.get(resultID);
		}
		return null;
	}


}

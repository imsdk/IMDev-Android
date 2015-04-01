package com.imsdk.imdeveloper.ui.a1common;

import java.util.HashMap;
import java.util.Locale;

import com.imsdk.imdeveloper.ui.view.sortlistview.CharacterParser;

public class DataCommon {
	private static HashMap<String, String> sSortLetterHashMap = new HashMap<String, String>();

	public static String getSortLetter(String s) {
		if (s == null) {
			return "#";
		}

		s = s.trim();

		if (s.length() == 0) {
			return "#";
		}

		String sortLetter = sSortLetterHashMap.get(s);

		if (sortLetter != null) {
			return sortLetter;
		}

		String pinyin = CharacterParser.getInstance().getSelling(s);

		sortLetter = pinyin.substring(0, 1).toUpperCase(Locale.getDefault());

		// 正则表达式，判断首字母是否是英文字母
		if (sortLetter.matches("[A-Z]")) {
			return sortLetter;
		} else {
			return "#";
		}
	}
}

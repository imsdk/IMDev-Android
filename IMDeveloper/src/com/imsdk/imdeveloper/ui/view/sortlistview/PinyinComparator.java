package com.imsdk.imdeveloper.ui.view.sortlistview;

import java.util.Comparator;

import com.imsdk.imdeveloper.ui.a1common.DataCommon;

public class PinyinComparator implements Comparator<String> {
	public int compare(String customUserID1, String customUserID2) {
		String sortLetter1 = DataCommon.getSortLetter(customUserID1);
		String sortLetter2 = DataCommon.getSortLetter(customUserID2);

		if (sortLetter1.equals("@") || sortLetter2.equals("#")) {
			return -1;
		} else if (sortLetter1.equals("#") || sortLetter2.equals("@")) {
			return 1;
		} else {
			return sortLetter1.compareTo(sortLetter2);
		}
	}
}

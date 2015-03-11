package com.imsdk.imdeveloper.view.sortlistview;

import java.util.Comparator;

import com.imsdk.imdeveloper.bean.Message;
import com.imsdk.imdeveloper.bean.User;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<Message> {

	public int compare(Message o1, Message o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}

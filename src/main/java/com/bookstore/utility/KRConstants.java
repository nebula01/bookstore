package com.bookstore.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KRConstants {

	public final static String KR = "KR";

	public final static Map<String, String> mapOfKRStates = new HashMap<String, String>() {
		{
			put("", "");
		}
	};

	// map 키
	public final static List<String> listOfKRStatesCode = new ArrayList<>(mapOfKRStates.keySet());
	
	// map 밸류 
	public final static List<String> listOfKRStatesName = new ArrayList<>(mapOfKRStates.values());
}

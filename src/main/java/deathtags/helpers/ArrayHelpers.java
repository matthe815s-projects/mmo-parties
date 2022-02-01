package deathtags.helpers;

import java.util.ArrayList;
import java.util.List;

public class ArrayHelpers {
	/**
	 * Find all of the related values to a provided variable.
	 * @param first
	 * @param array
	 * @return
	 */
	public static List<String> FindClosestToValue(String first, String[] array)
	{
		List<String> results = new ArrayList<String>();
		
		for (int i=0;i<array.length;i++) {
			if (array[i].startsWith(first))
				results.add(array[i]);
		}
		
		return results;
	}
}

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
	public static void main(String[]args) {
		/*
		 * Just a test file to call certain functions and check if they work.
		 */
		
		
		Integer[]val = {10,1,12};
		ArrayIndexComparator comparator = new ArrayIndexComparator(val);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		System.out.println(Arrays.toString(indexes));
	}
}

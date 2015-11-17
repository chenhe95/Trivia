import java.util.ArrayList;
import java.util.HashMap;

public class Test {
	public static void main(String[] a) {
                HashMap<Integer, ArrayList<Integer>> mapTest = new HashMap<>();
		ArrayList<Integer> intlist = new ArrayList<Integer>();
                mapTest.put(0, intlist);
		intlist.add(3213);
		intlist.add(5213);
		intlist.add(4213);
		intlist.add(3, 1111);
		System.out.println(mapTest.toString());
	}
}

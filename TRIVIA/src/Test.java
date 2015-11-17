import java.util.ArrayList;

public class Test {
	public static void main(String[] a) {
		ArrayList<Integer> intlist = new ArrayList<Integer>();
		intlist.add(3213);
		intlist.add(5213);
		intlist.add(4213);
		intlist.add(3, 1111);
		System.out.println(intlist.toString());
	}
}

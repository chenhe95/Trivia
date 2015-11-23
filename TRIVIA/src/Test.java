import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Test {
	public static void main(String[] a) {
                HashMap<Integer, ArrayList<Integer>> mapTest = new HashMap<>();
		ArrayList<Integer> intlist = new ArrayList<Integer>();
		mapTest.put(0, intlist);
		TreeMap<Integer, Integer> xxxx = new TreeMap<>();
                xxxx.put(32, 55);
                xxxx.put(1515, 23);
                xxxx.put(0, 123124);
                
          System.out.println(new ArrayList<Integer>(xxxx.values()).get(0));    
          System.out.println(new ArrayList<Integer>(xxxx.keySet()).get(0)); 
          System.out.println(new ArrayList<Integer>(xxxx.values()).get(1)); 
          System.out.println(new ArrayList<Integer>(xxxx.keySet()).get(1)); 
          System.out.println(new ArrayList<Integer>(xxxx.values()).get(2));
          System.out.println(new ArrayList<Integer>(xxxx.keySet()).get(2)); 
         // System.out.println(new ArrayList<Integer>(xxxx.keySet()).get(0));  
        
		intlist.add(3213);
		intlist.add(5213);
		intlist.add(4213);
		intlist.add(3, 1111);
		//System.out.println(mapTest.toString());
	}
}

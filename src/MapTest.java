import java.util.LinkedHashMap;
import java.util.Map;

public class MapTest {

	private static final Map<String, String> map = new LinkedHashMap<String, String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// company_cd

		// request_type: inquiry

		// request_type: order
		// transaction_type 
		// transaction_type_detail
		// action_code
		// account_section

		// merged both transaction and transaction_type together.

		// ===========================================================
		// code, tp, tpd, ac, rt, as | mode
		// 2400,  ?,   ?,  ?,  ?,  ? | NONE
		// 2400,  5,   5,  ?,  ?,  ? | COPY
		// 2400,  ?,   ?,  0,  ?,  L | API
		// 2400,  5,   5,  1,  ?,  L | API

		// 1st record
		map.put("N:2400:I", "L:NONE");
		map.put("N:2400:?", "N:2400:??");
		map.put("N:2400:??", "N:2400:???,N:2400:??0");
		map.put("N:2400:???", "N:2400:????,N:2400:???0");
		map.put("N:2400:????", "L:NONE");

		// 2nd record
		map.put("N:2400:5", "N:2400:55");
		map.put("N:2400:55", "N:2400:55?,N:2400:551");
		map.put("N:2400:55?", "N:2400:55??");
		map.put("N:2400:55??", "L:COPY");

		// 3rd record
		map.put("N:2400:??0", "N:2400:??0L,N:2400:??0?");
		map.put("N:2400:??0?", "L:COPY");
		map.put("N:2400:??0L", "L:API");
		
		// 4th record
		map.put("N:2400:551", "N:2400:551?,N:2400:551L");
		map.put("N:2400:551?", "L:NONE");
		map.put("N:2400:551L", "L:API");

		System.out.println("=================");
		System.out.println(search("2400", "5", "5", "?", "0"));
		System.out.println("=================");
		System.out.println(search("2400", "?", "?", "?", "?"));
		System.out.println("=================");
		System.out.println(search("2400", "5", "5", "1", "L"));

/*
		add("2400", "5", "5", "0", "L", "API");
		add("2400", "5", "5", "1", "L", "COPY");
*/		
//		map.entrySet().stream().forEach(p -> System.out.println(p.getKey() + " ==> " + p.getValue()));
	}
	
	public static void add(String code, String tp, String tpd, String ac, String as, String mode) {
		
		String [] keys = { tp, tpd, ac, as };
		
		String tmp = "N:" + code + ":";
		String prev = null;
		
		for (int i = 0; i < keys.length; i++) {
			
			prev = new String(tmp);
			tmp += keys[i];
			
			String val = map.get(tmp);
			
			if (val != null) {
				if (val.indexOf(tmp + keys[i + 1]) < 0) {
					val += "," + tmp + keys[i + 1];
					map.put(tmp,  val);
				}
			}
			else {
				if (i < keys.length - 1)
					val = tmp + "?" + "," + tmp + keys[i + 1];
				else
					val = "L:" + mode;
			}
			
			map.put(tmp,  val);
		}
	}
	
	public static String search(String code, String tp, String tpd, String ac, String as) {
		
		String[] keys = { tpd, ac, as };
		
		return _search("N:" + code + ":" + tp, 0, keys);
	}
	
	public static String _search(String prefix, int index, String [] tokens) {	
		
		if (index > tokens.length)
			return null;
		
		String val = map.get(prefix);
		
		if (val != null && val.startsWith("L:")) {
			return val;
		}
		else 
		if (index < tokens.length && val != null) {
		
			String [] children = val.split("[,]+");
		
			for (String child : children) {
			
				if (child.endsWith(tokens[index]) || child.endsWith("?")) {	
					val = _search(child, index + 1, tokens);
				}
			}
		}
		
		return val;
	}
}

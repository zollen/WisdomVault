import java.util.HashMap;
import java.util.Map;

public class MapTest {

	private static final Map<String, String> map = new HashMap<String, String>();

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
		print("2400", "5", "5", "?", "0");
		System.out.println("=================");
		print("2400", "?", "?", "?", "?");
		System.out.println("=================");
		print("2400", "5", "5", "1", "L");

	}

	public static void print(String code, String tp, String tpd, String ac, String as) {

		String[] keys = new String[4];
		keys[0] = tp;
		keys[1] = tpd;
		keys[2] = ac;
		keys[3] = as;

		String tmp = "N:" + code + ":";
		String val = null;
		String prev = null;

		for (int i = 0; i < 4; i++) {

			prev = new String(tmp);
			tmp += keys[i];

			val = map.get(tmp);
			if (val == null) {
				tmp = prev + "?";
				val = map.get(tmp);
			}

			if (val != null && val.startsWith("N:")) {
				String [] children = val.split("[,]+");
				
				for (String child : children) {
			//		System.out.println(child + " <==> " + keys[i + 1]);
					
					if (child.endsWith(keys[i + 1])) {
						val = child;
						break;
					}
				}
			}
		}
		
		System.out.println(tmp + " ===> " + val);
	}

}

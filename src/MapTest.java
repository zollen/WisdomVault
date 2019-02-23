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

		register("2400", "*", "*", "*", "*", "NONE", "COPY");
		register("2400", "5", "5", "0", "L", "NONE", "API");
		register("2400", "5", "5", "1", "*", "NONE", "NONE");
		register("2400", "5", "5", "1", "L", "NONE", "COPY");
		
		
		System.out.println(search("2400", "5", "8", "3", "K"));

		map.entrySet().stream().forEach(p -> System.out.println(p.getKey() + " ==> " + p.getValue()));
	}
	
	public static void register(String code, String tp, String tpd, String ac, String as, String empty, String mode) {
		
		String[] keys = { tpd, ac, as };
		
		_register("N:" + code + ":" + tp, "N:" + code + ":" + tp, 0, keys, empty, mode);
	}
	
	public static void _register(String head, String prefix, int index, String [] tokens, String empty, String mode) {
		
		if (index == tokens.length) {
			
			String tmp = head;
			for (String token : tokens)
				tmp += token;
			
			if (prefix.equals(tmp))
				map.put(prefix, "L:" + mode);
			else
				map.put(prefix, "L:" + empty);
			
			return;
		}
		
		String val = map.get(prefix);
		
		if (val == null) {
			
			map.put(prefix, prefix + "*");
			_register(head, prefix + "*", index + 1, tokens, empty, mode);
			
			if (!"*".equals(tokens[index])) {
				map.put(prefix, prefix + "*" + "," + prefix + tokens[index]);
				_register(head, prefix + tokens[index], index + 1, tokens, empty, mode);
			}		
		}
		else {
			
			if (val.indexOf(prefix + tokens[index]) < 0)
				map.put(prefix, val + "," + prefix + tokens[index]);
			
			_register(head, prefix + tokens[index], index + 1, tokens, empty, mode);	
		}
	}
	
	public static String search(String code, String tp, String tpd, String ac, String as) {
		
		String[] keys = { tpd, ac, as };
		
		return _search("N:" + code + ":", "N:" + code + ":" + tp, 0, keys);
	}
	
	public static String _search(String head, String prefix, int index, String [] tokens) {	
		
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
			
				if (child.endsWith(tokens[index]) || child.endsWith("*")) {	
					val = _search(head, child, index + 1, tokens);
				}
			}
		}
		else {
			val = map.get(head + "****");
		}
		
		return val;
	}
}

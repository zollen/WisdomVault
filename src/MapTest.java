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

		register("2400", "*", "*", "*", "*", "COPY");
		register("2400", "5", "5", "0", "L", "API");
		register("2400", "5", "5", "1", "*", "NONE");
		register("2400", "5", "5", "1", "L", "COPY");
		
		System.out.println(search("2400", "5", "5", "0", "L"));
		System.out.println(search("2400", "5", "5", "0", "K"));
		System.out.println(search("2400", "5", "5", "1", "K"));
		System.out.println(search("2400", "5", "5", "1", "L"));

	//	map.entrySet().stream().forEach(p -> System.out.println(p.getKey() + " ==> " + p.getValue()));
	}
	
	public static void register(String code, String tp, String tpd, String ac, String as, String mode) {
		
		String[] keys = { code, tp, tpd, ac, as };
		
		_register("N:" + code + ":", 1, keys, mode);
	}
	
	public static void _register(String prefix, int index, String [] tokens, String mode) {
		
		if (index == tokens.length) {
			
			StringBuilder builder = new StringBuilder();
			builder.append("N:" + tokens[0] + ":");
			for (int i = 1; i < tokens.length; i++)
				builder.append(tokens[i]);
			
			if (prefix.equals(builder.toString())) {
				map.put(prefix, "L:" + mode);
			}
			else {
				String val = null;
				String vv = "N:" + tokens[0] + ":";
				for (int i = tokens.length - 2; i >= 1; i--) {
					
					builder = new StringBuilder();
					builder.append(vv);
					
					for (int j = 1; j <= i; j++)
						builder.append(tokens[j]);
					
					for (int j = tokens.length - 1; j > i; j--)
						builder.append("*");
					
					val = map.get(builder.toString());
					if (val != null)
						break;
				}
				
				if (val == null) {
					builder = new StringBuilder();
					for (int i = 1; i < tokens.length; i++)
						builder.append("*");
					val = map.get("N:" + tokens[0] + ":" + builder.toString());
				}
				
				if (val != null)
					map.put(prefix, val);
				
			}
			
			return;
		}
		
		String val = map.get(prefix);
		
		if (val == null) {
			
			map.put(prefix, prefix + "*");
			_register(prefix + "*", index + 1, tokens, mode);
			
			if (!"*".equals(tokens[index])) {
				map.put(prefix, prefix + "*" + "," + prefix + tokens[index]);
				_register(prefix + tokens[index], index + 1, tokens, mode);
			}		
		}
		else {
			
			if (val.indexOf(prefix + tokens[index]) < 0)
				map.put(prefix, val + "," + prefix + tokens[index]);
			
			_register(prefix + tokens[index], index + 1, tokens, mode);	
		}
	}
	
	public static String search(String code, String tp, String tpd, String ac, String as) {
		
		String[] keys = { tp, tpd, ac, as };
		
		return _search("N:" + code + ":", 0, keys);
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
			
				if (child.endsWith(tokens[index]) || child.endsWith("*")) {	
					val = _search(child, index + 1, tokens);
				}
			}
		}
		else {
			if (val == null)
				val = "L:NONE";		
		}
		
		return val;
	}
}

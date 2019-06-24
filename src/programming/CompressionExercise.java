package programming;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class CompressionExercise {
/*
	private static List<String> paths = new ArrayList<String>();
*/
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String xml = "<?xml version=\"1.0\" ?><OrdSet Version=\"29\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"tfs\" xsi:schemaLocation=\"tfs tfs.xsd\"><Msg><MsgCreate><Date>20190115</Date><Time>102813</Time><Src><SrcType>D</SrcType><DlrCode>9190</DlrCode></Src><Hello/><Target><TargetType>F</TargetType><MgmtCode>MFC</MgmtCode></Target></MsgCreate><MsgType><OrdReq><ActnCode>NEW</ActnCode><DamnMe></DamnMe><SrcID>42131330250</SrcID><RepCode>1231</RepCode><OrdDtl><AcctLookup><MgmtCode>MFC</MgmtCode><FundAcctID>11</FundAcctID><AcctDesig>1</AcctDesig></AcctLookup><TrxnDtl><Buy><TrxnTyp>5</TrxnTyp><FundID>027</FundID><Amt><AmtType>A</AmtType></Amt><GNRdmtn>G</GNRdmtn><SettlMethd>1</SettlMethd><SettlSrc>D</SettlSrc></Buy></TrxnDtl></OrdDtl></OrdReq></MsgType></Msg></OrdSet>";
/*
		InputSource in = new InputSource(new StringReader(xml));
		XPathExpression xp = XPathFactory.newInstance().newXPath().compile("/");
		Node root = (Node) xp.evaluate(in, XPathConstants.NODE);

		scan(root);

		String test = paths.stream().collect(Collectors.joining("\n"));
		System.out.println(test);
*/
		String test = xml;
		System.out.println("INPUTS: " + test.length());
		System.out.println("Deflator: " + encode64(deflater(test)).length());
		System.out.println("GZIP: " + encode64(gzip(test)).length());
		System.out.println("BZIP2: " + encode64(compress(test, CompressorStreamFactory.BZIP2)).length());
		System.out.println("LZ4_BLOCK: " + encode64(compress(test, CompressorStreamFactory.LZ4_BLOCK)).length());
		System.out.println("LZ4_FRAMED: " + encode64(compress(test, CompressorStreamFactory.LZ4_FRAMED)).length());
	
	}

	public static String gzip(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	public static String deflater(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}

		byte[] output = new byte[str.length()];
		Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
		compresser.setInput(str.getBytes());
		compresser.finish();

		int compressedDataLength = compresser.deflate(output);
		compresser.end();

		byte[] actual = new byte[compressedDataLength];

		for (int i = 0; i < compressedDataLength; i++)
			actual[i] = output[i];

		return new String(actual);
	}
	
	public static String compress(String str, String method) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CompressorOutputStream cos = new CompressorStreamFactory()
		         .createCompressorOutputStream(method, out);
		cos.write(str.getBytes());
		cos.close();
		return out.toString("ISO-8859-1");
	}
	
	public static String encode64(String str) {

		return Base64.getEncoder().encodeToString(str.getBytes());
	}
/*
	public static void scan(Node parent) {
		scan(parent.getFirstChild(), new StringBuilder());
	}

	public static void scan(Node parent, StringBuilder builder) {

		if (parent != null) {

			boolean leave = false;

			switch (parent.getNodeType()) {
			case Node.TEXT_NODE: {
				builder.append("/");
				builder.append(parent.getNodeValue());
				leave = true;
			}
				break;
			default:
				if (parent.getChildNodes().getLength() <= 0) {
					builder.append("/");
					builder.append(parent.getNodeName());
					leave = true;
				}
			}

			if (leave) {
				paths.add(builder.toString());
				return;
			}
		}

		for (int i = 0; parent != null && i < parent.getChildNodes().getLength(); i++) {

			StringBuilder tmp = new StringBuilder(builder);

			tmp.append("/");
			tmp.append(parent.getNodeName());

			scan(parent.getChildNodes().item(i), tmp);
		}
	}
*/
}

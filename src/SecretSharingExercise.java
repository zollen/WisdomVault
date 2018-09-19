import java.util.Base64;
import java.util.Random;

public class SecretSharingExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		{
			// single byte encryption & decryption
			
			byte input = 0b00110100;
			byte key1 = 0b01011011;
			byte key2 = 0b00010111;

			System.out.println("RESULT: " + (int) input);
			System.out.println("RESULT: " + (int) key1);
			System.out.println("RESULT: " + (int) key2);

			System.out.println("RESULT: " + (int) (input ^ key1 ^ key2));

			System.out.println("RESULT: " + (int) (input ^ key1 ^ key2 ^ key1 ^ key2));
		}

		{
			// message encryption and decryption

			String message = "This is a great learning experience!!!";
			byte [] msg = message.getBytes();
			byte [] key1 = new byte[msg.length];
			byte [] key2 = new byte[msg.length];
			byte [] key3 = new byte[msg.length];
			byte[] encrypted = new byte[msg.length];

			Random rand = new Random(System.nanoTime());
			rand.nextBytes(key1);
			rand.nextBytes(key2);
			rand.nextBytes(key3);

			// encryption
			for (int i = 0; i < msg.length; i++) {
				encrypted[i] = (byte) (msg[i] ^ key1[i] ^ key2[i] ^ key3[i]);
			}

			System.out.println("Encrypted: " + new String(Base64.getEncoder().encode(encrypted)));

			// decryption
			byte[] decrypted = new byte[msg.length];
			for (int i = 0; i < msg.length; i++) {
				decrypted[i] = (byte) (encrypted[i] ^ key2[i] ^ key1[i] ^ key3[i]);
			}

			System.out.println("Decrypted: " + new String(decrypted));
		}
	}

}

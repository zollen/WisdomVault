package programming;
import java.util.Base64;
import java.util.Random;

public class SecretSharingExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		{
			// single byte encryption & decryption
			
			byte input = 0b00110100;
			byte salt1 = 0b01011011;
			byte salt2 = 0b00010111;

			System.out.println("RESULT: " + (int) input);
			System.out.println("RESULT: " + (int) salt1);
			System.out.println("RESULT: " + (int) salt2);

			System.out.println("RESULT: " + (int) (input ^ salt1 ^ salt2));

			System.out.println("RESULT: " + (int) (input ^ salt1 ^ salt2 ^ salt1 ^ salt2));
		}

		{
			// message encryption and decryption

			String message = "This is a great learning experience!!!";
			byte [] msg = message.getBytes();
			byte [] salt1 = new byte[msg.length];
			byte [] salt2 = new byte[msg.length];
			byte [] salt3 = new byte[msg.length];
			byte[] encrypted = new byte[msg.length];

			Random rand = new Random(System.nanoTime());
			rand.nextBytes(salt1);
			rand.nextBytes(salt2);
			rand.nextBytes(salt3);

			// encryption
			for (int i = 0; i < msg.length; i++) {
				encrypted[i] = (byte) (msg[i] ^ salt1[i] ^ salt2[i] ^ salt3[i]);
			}

			System.out.println("Encrypted: " + new String(Base64.getEncoder().encode(encrypted)));

			// decryption
			byte[] decrypted = new byte[msg.length];
			for (int i = 0; i < msg.length; i++) {
				decrypted[i] = (byte) (encrypted[i] ^ salt2[i] ^ salt1[i] ^ salt3[i]);
			}

			System.out.println("Decrypted: " + new String(decrypted));
		}
	}

}

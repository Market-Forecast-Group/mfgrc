package com.mfg.utils.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import com.mfg.utils.U;

/**
 * This class models a very simple protocol for exchanging bytes with another
 * source.
 * 
 * The string or bytes are sent using a simple codification, text or binary.
 * Internally the string are sent always using utf-8. The binary data is encoded
 * using base64 data and can be compressed or not.
 * 
 * Blobs may be signed or not, depending on the parameters.
 * 
 * All the read methods are blocking, otherwise noted.
 */
public final class VSTcp {

	/**
	 * This is the normal end of line in network.
	 */
	private static final char EOL[] = new char[] { '\r', '\n' };

	/**
	 * This enumeration will list the compression policy of the protocol. Only
	 * binary messages are compressed, of course. That is either binary messages
	 * or string messages which are sent binary encoded.
	 */
	public enum COMPRESSION_POLICY {
		ALWAYS, NEVER, ON_THRESHOLD
	}

	/**
	 * This is the regular expression used in output to massage the string
	 */
	private static Pattern _regexOutput = Pattern.compile("%");

	/**
	 * This is the regular expression in input to massage the string.
	 */
	private static Pattern _regexInput = Pattern.compile("%%");

	private Socket _sock = null;

	private BufferedReader _reader;
	private BufferedWriter _writer;

	/**
	 * initializes the class with a socket.
	 * 
	 * <p>
	 * The socket could be <i>client</i> or <i>server</i>. The class simply gets
	 * a writer and a reader from this socket and initializes them to the
	 * correct encoding.
	 * 
	 * <p>
	 * At time of writing the messages are sent only in text form, but we may
	 * change that later, as the need arises (for example if we need to
	 * send/receive many amount of data it will be better to compress them
	 * first).
	 * 
	 * @param aSocket
	 *            an already connected socket.
	 * @throws IOException
	 *             if something goes wrong
	 */
	public void init(Socket aSocket) throws IOException {

		_sock = aSocket;

		// I assume that the server talks text.
		// We always use utf-8 as the default encoding.
		_writer = new BufferedWriter(new OutputStreamWriter(
				_sock.getOutputStream(), Charset.forName("UTF-8")));

		_reader = new BufferedReader(new InputStreamReader(
				_sock.getInputStream(), Charset.forName("UTF-8")));
	}

	/**
	 * The simple connect function. If we are already connected it does nothing.
	 * 
	 * After the connection you cannot change the underlying method of
	 * transferring, either text or binary.
	 * 
	 * This is the "client" connect; if I am receiving a socket server side I
	 * must give another handshaking.
	 * 
	 * @return 0 on success, -1 in case of error, -2 if the socket is already
	 *         open, close it first.
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public void connect(String host, int port) throws IOException {
		if (_sock != null) {
			throw new RuntimeException("very strange sock != null");
		}

		init(new Socket(host, port));

	}

	public void disconnect() {
		if (_sock != null) {
			try {
				_writer.close();
				_reader.close();
				_sock.close();
			} catch (IOException e) {
				U.debug_var(939205,
						"ignoring exception while closing the socket ",
						this._sock.getInetAddress().toString());
			}
			_writer = null;
			_reader = null;
			_sock = null;
		}
	}

	/**
	 * Sends the string on the socket. The string is encoded in UTF-8 and sent
	 * on the wire. The implementation is free to add some new-lines at the end,
	 * just to make it more human-readable.
	 * 
	 * @return 0 on success
	 */
	public int sendString(String s) {
		try {
			_sendStringImpl(s);
		} catch (IOException | NullPointerException ex) {
			// the null pointer exception can mean that we have already
			// disconnected the socket.
			return -1;
		}
		return 0;
	}

	/**
	 * @return the next string which is in the socket. That is the next text
	 *         message. There is no correspondence between the string and the
	 *         line. A "string message" can span multiple lines. And could also
	 *         have been binary encoded. It's up to the implementation to define
	 *         this. returns null in case of error
	 */
	public String readString() {
		try {
			return _readStringImpl();
		} catch (IOException e) {
			return null;
		}
	}

	private void _sendStringImpl(String s) throws IOException {

		// long before = System.currentTimeMillis();

		// byte sb[] = s.getBytes(Charset.forName("UTF-8"));
		String modified = _regexOutput.matcher(s).replaceAll("%%");

		_writer.write(modified);
		_writer.write(EOL);
		_writer.write("%");
		_writer.write(EOL);
		_writer.flush();

		// long now = System.currentTimeMillis();
		// System.out.println("[ciekwklo] Sent string had " + s.length() +
		// " code points in " + (now-before)+ " msecs");
	}

	/**
	 * This is the opposite of the writeString. It assumes that the format is
	 * always text. Of course this may change
	 */
	private String _readStringImpl() throws IOException {
		StringBuilder sb = new StringBuilder();

		for (;;) {
			String line = _reader.readLine();
			if (line == null) {
				// Ok, the end is arrived, I give what I have received so far.
				break;
			}

			if (line.equals("%")) {
				// a single percentage sign, I have finished
				break;
			}
			// I have to massage the string...
			if (sb.length() != 0) {
				sb.append(EOL); // this is not the first line
			}
			String massaged = _regexInput.matcher(line).replaceAll("%");
			sb.append(massaged);
		}

		String answer = sb.toString();
		return answer;
	}

	/**
	 * Just a test method
	 * 
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		VSTcp vstcp = new VSTcp();
		vstcp.connect("localhost", 9011);

		int i = 0;

		while (true) {
			String s = _get_random_string();
			// System.out.println("->>[");
			// System.out.println(s);
			// System.out.println("]");

			int res = vstcp.sendString(s);
			String answer;
			if (res == 0) {
				answer = vstcp.readString();
			} else {
				System.out.println("cannot send s " + s);
				break;
			}

			if (answer == null) {
				System.out.println("cannot read");
				break;
			}

			// System.out.println("<--[");
			// System.out.println(answer);
			// System.out.println("]");

			if (!s.equals(answer)) {
				System.out.println("error sen " + s + " l " + s.length());
				System.out.println("error rec " + answer + " l "
						+ answer.length());
				break;
			}
			System.out.println("Ok @ " + i++);

			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	// private static final char[] allowableChars = new char[]{'*', 'e', 'b',
	// '1', '5', '2', 'b', 'Z', '%'};
	// private static final char[] allowableChars = new char[]{'e', '%', 'Ã ',
	// 'Ãˆ',};
	private static final char[] allowableChars = new char[] { 'Ã', 'Ã', '{',
			'$', '.', 'Â', '\ufeb9', '\u062f', '\ua806', '\ua58e', '\u0000',
			'\u20ac', '\n', '%', 'e' };

	// '\u10330', '\u10331'

	@SuppressWarnings("unused")
	private static final int[] otherChars = new int[] { 0x10330, 0x10331,
			0x10332 };

	private static String _get_random_string() {
		int length = (int) (Math.random() * 1023) + 1;

		StringBuilder sb = new StringBuilder();

		// for (int i = 0 ; i < length ; ++ i){
		// if (Math.random() < 0.8){
		// sb.append(allowableChars[(int)(Math.random() *
		// allowableChars.length)]);
		// } else {
		// char[] chars = Character.toChars(otherChars[(int)(Math.random() *
		// otherChars.length)]);
		// assert(chars.length == 2);
		// sb.append(chars);
		// }
		// }

		for (int i = 0; i < length; ++i) {
			sb.append(allowableChars[(int) (Math.random() * allowableChars.length)]);
		}

		String res = sb.toString();

		assert (res.codePointCount(0, res.length()) == length) : " l " + length
				+ " strlen " + res.codePointCount(0, res.length()) + " str "
				+ res;
		return res;
	}

}

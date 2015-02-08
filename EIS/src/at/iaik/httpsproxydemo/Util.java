package at.iaik.httpsproxydemo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Util {

	public static byte[] readAll(InputStream inputStream) {
		// creating stream for holding data
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		// creating buffer
		byte[] data = new byte[1024];
		try {
			while (true) {
				// checking if data is available to be read, available delivers
				// the number of readable bytes
				int available = inputStream.available();

				// if no data is available and data has been read (finished
				// reading for now) returning result
				// Note: read is blocking! (i.e. if no data is present this
				// blocks the current thread): if no
				// further data is available for reading and we have not read
				// anything yet (nothing is sent at
				// moment) then we perform the read and sleep
				if (available == 0 && bOut.size() > 0) {
					bOut.close();
					return bOut.toByteArray();
				}

				if (available == 0 && bOut.size() == 0) {
					System.out.println("Going to sleep, cya");
				}

				// reading data (or go to sleep)
				int count = inputStream.read(data);

				// if stream is invalid returning null
				if (count == -1) {
					bOut.close();
					return null;
				}

				// writing data to buffer
				bOut.write(Arrays.copyOfRange(data, 0, count));
				bOut.flush();
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String findHost(byte[] data) {
		String request = new String(data);
		StringTokenizer tokenizer = new StringTokenizer(request, "\r\n");
		while (tokenizer.hasMoreElements()) {
			String current = (String) tokenizer.nextElement();
			if (current.contains("Host")) {
				System.out.println("found host: " + current);
				String[] elements = current.split(":");
				String hotfix = elements[1].substring(1);
				return hotfix;
			}

		}

		return null;
	}
	
	public static String findProperty(byte[] data, String property) {
		String request = new String(data);
		StringTokenizer tokenizer = new StringTokenizer(request, "\r\n");
		while (tokenizer.hasMoreElements()) {
			String current = (String) tokenizer.nextElement();
			if (current.contains(property)) {
				System.out.println("found " + property + ": " + current);
				String[] elements = current.split(" ");
				return elements[1];
			}
		}
		return null;
	}

}

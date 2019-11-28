package org.alicebot.ab.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Enumeration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

public class NetworkUtils {

	public static String localIPAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String ipAddress = inetAddress.getHostAddress().toString();
						int p = ipAddress.indexOf("%");
						if (p > 0)
							ipAddress = ipAddress.substring(0, p);
						System.out.println("--> localIPAddress = " + ipAddress);
						return ipAddress;
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return "127.0.0.1";
	}

	public static String responseContent(String url) throws Exception {
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.setURI(new URI(url));
		InputStream is = defaultHttpClient.execute((HttpUriRequest) request).getEntity().getContent();
		BufferedReader inb = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder("");

		String NL = System.getProperty("line.separator");
		String line;
		while ((line = inb.readLine()) != null) {
			sb.append(line).append(NL);
		}
		inb.close();
		return sb.toString();
	}

	public static String spec(String host, String botid, String custid, String input) {
		String spec = "";
		if (custid.equals("0")) {
			spec = String.format("%s?botid=%s&input=%s", new Object[] { "http://" + host + "/pandora/talk-xml", botid, URLEncoder.encode(input) });
		} else {
			spec = String.format("%s?botid=%s&custid=%s&input=%s", new Object[] { "http://" + host + "/pandora/talk-xml", botid, custid, URLEncoder.encode(input) });
		}
		return spec;
	}

}

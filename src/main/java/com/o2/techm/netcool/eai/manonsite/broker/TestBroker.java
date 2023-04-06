/*
 * Created on 16-Apr-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.techm.netcool.eai.manonsite.broker; 
import javax.net.ssl.*;
import java.security.KeyStore; 
import java.security.NoSuchAlgorithmException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestBroker {
	
	/**
	 * 
	 */
	public TestBroker() 
	{
		URL url=null;
		try {
			url = new URL("https://heathrow.cellnet.co.uk:62946/ManOnSite3c");
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		final String keyfile = "C:\\apps\\eclipse3.0.1\\workspace\\manonsite\\certs\\heathrow.p12";
		final String password = "o2fm";
		//final String script = "vvtlistener1/SMSOA_xmlrpc_listener";
		final int port = 62946;
		final String host = "heathrow.cellnet.co.uk";
		
		final String MSISDN = "447XXXXXXXXX";
		final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodCall>" +
		"<methodName>sendsimplemessage</methodName><params><param><value><struct>" +
		"<member><name>Type</name><value>NOR</value></member><member>" +
		"<name>Message</name><value><array><data><value><struct><member>" +
		"<name>text</name><value>Hello World</value></member></struct></value>" +
		"</data></array></value></member><member><name>MSISDN</name><value><array>"+
		"<data><value><struct><member><name>num</name><value>" + MSISDN +
		"</value></member></struct></value></data></array></value></member>" +
		"</struct></value></param></params></methodCall>";
		SSLSocketFactory factory = null;
		SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("SSL");
		
			//	 new line
			java.security.Security.addProvider(
					new com.sun.net.ssl.internal.ssl.Provider());
			
			KeyManagerFactory kmf;
			KeyStore ks;
			
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("PKCS12");
			ks.load(new FileInputStream(keyfile),password.toCharArray());
			kmf.init(ks,password.toCharArray());
			ctx.init(kmf.getKeyManagers(),null,null);
			factory = ctx.getSocketFactory();	
			
			System.out.println("Loaded digital cert");
		
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setSSLSocketFactory(ctx.getSocketFactory());
			connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		    connection.setDoOutput(true);
		    connection.setDoInput(true);
		    /* could also use the Socket option*/
			/*SSLSocket socket = (SSLSocket)factory.createSocket(host,port);
			socket.startHandshake();
			PrintWriter out = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())));
			*/
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			//	 the request - replace this with the XML Post request
			out.println("POST "+ " HTTP/1.0");
			out.println("User-Agent: Java/1.4.2_08 (ManOnSite Processor)");
			//out.println("Content-Type: text/xml");
			//out.println("Content-length: "+xml.length());
			out.println();
			//out.println(xml);
			out.flush();
			if (out.checkError())
			{
				System.out.println("IO ERROR");
				System.exit(0);
			}
			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection/*socket*/.getInputStream()));
			String line;
			while ((line=in.readLine())!=null)
			{
				System.out.println(line);
			}
			in.close();
			out.close();
			/*socket.close();*/
			connection.disconnect();
		}catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		 
		catch (Exception e) {
			e.printStackTrace(); 
			System.err.println(e.toString());			
		}
	}
	
	public void test() {
		
		KeyStore keyStoreKeys;
		KeyManagerFactory keyMgrFactory;
		SSLContext sslContext;
		
		try {			
			//Load Key Store
			keyStoreKeys = KeyStore.getInstance("JKS");
			
			keyStoreKeys.load(new FileInputStream("C:\\apps\\eclipse3.0.1\\workspace\\manonsite\\certs\\mos3.keystore"),"changeit".toCharArray());
			
			//Get Key Manager
			keyMgrFactory = KeyManagerFactory.getInstance("SunX509");
			keyMgrFactory.init(keyStoreKeys, "changeit".toCharArray());
			
			//Set SSL Context Type
			sslContext = SSLContext.getInstance("SSL");
			
			//Set SSL Context
			sslContext.init(keyMgrFactory.getKeyManagers(), null, null);
			
			URL url = new URL("https://heathrow.cellnet.co.uk:62946");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
			
			connection.setRequestMethod("POST");
			connection.setRequestProperty(
					"Content-type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			
			String data = URLEncoder.encode("",	"UTF-8");
			out.print(data);
			out.flush();
			out.close();
			
			InputStreamReader inReader = new
			InputStreamReader(connection.getInputStream());
			BufferedReader aReader = new BufferedReader(inReader);
			String aLine;
			while ((aLine = aReader.readLine()) != null)
				System.out.println(aLine);
			aReader.close();
			connection.disconnect(); 	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		System.out.println("Testing connection");
		TestBroker test1 = new TestBroker();
		/*System.out.println("Test .......");
		test1.test();*/
	}
}

	

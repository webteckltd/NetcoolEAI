package com.o2.techm.netcool.eai.manonsite.inms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.techm.netcool.eai.manonsite.common.Configuration;

public class INMSConnection
{
	private HttpsURLConnection httpConnection=null;
	private String keyfile;
	private String password;
	private static final String ENDPOINT = Configuration.getValue(Configuration.INMS_URL);
	private static final String FUNCTIONCALL = "?&function=alarm&siteRef="; 
	private static final String HEADER_PREFIX = "SMSHeader:";
	private static final String FOOTER = "## End."; 
	public static final String INMSFAILED="INMSWEB FAILURE";
	private static final Logger log = LoggerFactory.getLogger(INMSConnection.class);
	
	public INMSConnection() 
	{
		password = Configuration.getValue(Configuration.INMS_PASSWORD );
		keyfile = Configuration.getValue(Configuration.INMS_CERTIFICATE );
	}
	
	
	protected String[]  sentHttpApacheCommanRequest(String funtnUrl)throws IOException{
		try
		{
			log.debug("Calliing INMS web  with Request Payload   = " + funtnUrl + " for URL  = " + ENDPOINT);
			String inms_resposne;
			StringBuffer strBuffer =  new StringBuffer();
			String[] data ;
			
			
			DefaultHttpClient httpclient = getSSlHttpClient();
			HttpPost httpPost = new HttpPost(ENDPOINT);
			StringEntity s = new StringEntity(funtnUrl, "UTF-8");
			httpPost.setEntity(s);
			
			String usrAgt = "Java/" + System.getProperty("java.runtime.version") + " (ManOnSite Processor)";
		    httpPost.addHeader("User-Agent", usrAgt);
		    httpPost.addHeader("Content-type", "application/x-www-form-urlencoded");
				
			log.debug("executing request" + httpPost.getRequestLine());
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
				
			log.debug("Status Line recived from INMS web   = " + response.getStatusLine() );
			if (entity != null ) {
				inms_resposne = EntityUtils.toString(response.getEntity());
				log.debug(" RAW respopsnse recived from INMD web  =  " + inms_resposne);
				
				data = inms_resposne.split(System.lineSeparator());
				for (int i = 0; i < data.length; i++) {
					String line = data[i];
					if (line.toUpperCase().startsWith("SMSBODY")|| line.toUpperCase().startsWith("NCOBODY") || line.toUpperCase().startsWith("STATUS")){
						{	
							strBuffer.append(line);
							strBuffer.append(System.lineSeparator());
						}
					}
				}
				data  = strBuffer.toString().split(System.lineSeparator());	
				
				
			entity.consumeContent();
			}else{
				log.error(" INNS web sends Null Response Entity");
				throw new IOException(" Exception while trying to reach INMS web " );
			}
			
			httpclient.getConnectionManager().shutdown();
			log.debug("INMS Send Request  = " + funtnUrl +" Completed sucessfully ");
			return data;
		}
		catch (Exception e)
		{
			// TODO add a write retry value
			log.error("IOException caught: " + e.getMessage() ,e);
			throw new IOException(" Exception while trying to reach INMS web " , e);
		}	
	}

	public String[] getCellSiteAlarms(String cellSite) throws IOException
	{		
		String[] result = sentHttpApacheCommanRequest(FUNCTIONCALL+cellSite);
		return result;
	}

	public String[] getRequest(String srcFunc ,String cellSite) throws IOException
	{

		String funcName = new StringBuffer("?&function=").append(srcFunc).append("&siteRef=").append(cellSite).toString();
		String[] result = sentHttpApacheCommanRequest(funcName);
		return result;
	}
	public String[] getRequest(String srcFunc ,String cellSite, String extras) throws IOException
	{		
		String funcName = new StringBuffer("?&function=").append(srcFunc).append("&siteRef=").append(cellSite).append(extras).toString();
		String[] result = sentHttpApacheCommanRequest(funcName);
		return result;
	}

	
	private DefaultHttpClient getSSlHttpClient() throws Exception {
		DefaultHttpClient httpclient = null;
		try {

		    //SSLContext ctx = SSLContext.getInstance("TLS");
			SSLContext ctx = SSLContext.getInstance("TLSv1.2");
			ctx.init(getKeyManagers(), getTrustManagers(), new SecureRandom());
			
			org.apache.http.conn.ssl.SSLSocketFactory sf = new org.apache.http.conn.ssl.SSLSocketFactory(ctx, new StrictHostnameVerifier());
			
			httpclient = new DefaultHttpClient();
			ClientConnectionManager manager = httpclient.getConnectionManager();
			manager.getSchemeRegistry().register(new Scheme("https", 443, sf));
			log.debug("SSL context is configured  with key and trust stores");
			return httpclient;
		} catch (Exception e) {
			log.error("Error while creating SSL context " , e);
			e.printStackTrace();
			throw new Exception("Error while creating SSL context " , e);
		}
	}

	public TrustManager[] getTrustManagers() throws Exception {
		try {
			KeyStore jkstrustStore = KeyStore.getInstance("JKS");
			jkstrustStore.load(new FileInputStream(new File(keyfile)), password.toCharArray());
			TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmfactory.init(jkstrustStore);
			return tmfactory.getTrustManagers();
		} catch (Exception e) {
			log.error("Error while creating Trust Manager " , e);
			e.printStackTrace();
			throw new Exception("Error while creating Trust Manager " , e);
		}
	}

	public KeyManager[] getKeyManagers() throws Exception {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(new File(keyfile)), password.toCharArray());
			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(keyStore, password.toCharArray());
			return kmfactory.getKeyManagers();
		} catch (Exception e) {
			log.error("Error while creating Key Manager " , e);
			e.printStackTrace();
			throw new Exception("Error while creating Key Manager " , e);
		}
	}
	
	
	
}

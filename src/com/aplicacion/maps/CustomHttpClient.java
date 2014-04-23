package com.aplicacion.maps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class CustomHttpClient {
	
	private static final int HTTP_TIMEOUT = 30*1000;
	
	private static HttpClient mHttpClient;

	private static HttpClient getHttpClient() {
		
		if (mHttpClient == null)
		{
			mHttpClient = new DefaultHttpClient();
			final HttpParams par = mHttpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(par, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(par, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(par, HTTP_TIMEOUT);
		}
		
		return mHttpClient;
	}
	
	
	public static String executeHttpPost(String url, ArrayList<NameValuePair> postValores) throws Exception{
	
		BufferedReader in = null;
		
		try{
			
		
			HttpClient cliente = getHttpClient();
			HttpPost post = new HttpPost(url);
		
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postValores);
			post.setEntity(formEntity);
		
			HttpResponse respuesta = cliente.execute(post);
		
			in = new BufferedReader(new InputStreamReader(respuesta.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String linea = "";
		    //String NL = System.getProperty("line.separator");
			while ((linea = in.readLine()) != null)
			{
				//sb.append(linea+NL);
				sb.append(linea);
			}
			in.close();
			String resultado = sb.toString();
						
			return resultado;
		}
		finally {
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		
	}

}


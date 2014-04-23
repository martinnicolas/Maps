package com.aplicacion.maps;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.aplicacion.maps.CustomHttpClient;
import com.aplicacion.maps.MainActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//import android.location.Criteria;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends android.support.v4.app.FragmentActivity implements LocationListener{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Obtengo una instancia del manejador de Ubicaciones
		LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
		//Determino el mejor proveedor para obtener mi ubicación
		/*Criteria req = new Criteria();
		req.setAccuracy(Criteria.ACCURACY_FINE);
		req.setAltitudeRequired(true);		
		//Obtengo el mejor proveedor
		String mejorProveedor = locManager.getBestProvider(req, false);*/		
		
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		mostrarPosicion(location);
		enviarPosicion(location);		
	}

	@Override
	public void onProviderDisabled(String provider) {
		mostrarMensaje("El proveedor esta deshabilitado");		
	}

	@Override
	public void onProviderEnabled(String provider) {
		mostrarMensaje("El proveedor esta habilitado");		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		mostrarMensaje("Estado del proveedor: "+status);		
	}
	
	/**
	 * Muestra una ubicación en el mapa de GoogleMaps
	 * 
	 * @param location
	 */
	private void mostrarPosicion(Location location) {
		
		//Obtengo el mapa
		GoogleMap map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
		
		//Centro el mapa en la ubicacion del aparato, con un zoom de 15
		LatLng ubicacion = new LatLng(location.getLatitude(),location.getLongitude());
		CameraPosition camPos = new CameraPosition.Builder().target(ubicacion).zoom(15).build();
		CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
		map.animateCamera(camUpdate);
				
		//Agrego un marcador en la posición del aparato, para mostrar la ubicación en el mapa
		map.clear();
		map.addMarker(new MarkerOptions().position(ubicacion).title("YO (MARTÍN"));
		
    }
	
	/**
	 * Envía una ubicación al servidor
	 * 
	 * @param location
	 */
	private void enviarPosicion(Location location){
		String latitud = Double.valueOf(location.getLatitude()).toString();
		String longitud = Double.valueOf(location.getLongitude()).toString();
    	
    	new EnBackground().execute(latitud,longitud);
	}
	
	/**
	 * Muestra un mensaje en pantalla
	 * 
	 * @param mensaje
	 */
	private void mostrarMensaje(CharSequence mensaje){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, mensaje, duration);
		toast.show();	
	}	

	/**
	 * Clase para la ejecución de tareas asíncronas (En segundo plano)
	 * 
	 * @author Admin-PC
	 */
	class EnBackground extends AsyncTask<String, String, String>{
		
		private String latitud;
	    private String longitud;
	    	
	    @Override
	    protected void onPreExecute() {}
	    	
		@Override
		protected String doInBackground(String... params) {
			latitud = params[0];
			longitud= params[1];
				
			ArrayList<NameValuePair> postValores = new ArrayList<NameValuePair>();
		    postValores.add(new BasicNameValuePair("latitud",latitud));
		    postValores.add(new BasicNameValuePair("longitud",longitud));
				
		    String respuesta = null;
		    String res = null;
		    try {	    		
		    	respuesta = CustomHttpClient.executeHttpPost("http://www.geopos.meximas.com/posicion.php",postValores);
				res = respuesta.toString();
				//res.replaceAll("\\s+","");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}
			
		@Override
		protected void onPostExecute(String result) {}
		
	}
	
}

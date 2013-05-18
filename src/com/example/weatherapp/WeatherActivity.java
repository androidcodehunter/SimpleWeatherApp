package com.example.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.weatherapp.data.City;
import com.example.weatherapp.tools.XmlParser;

public class WeatherActivity extends Activity {

	private String weatherText = "";

	private HashMap<String, String> cityPair;
	private ArrayAdapter<String> arrayAdapterSpinnerCityName;

	private HttpResponse response = null;

	private Button btnWeather;
	private WebView webViewWeather;
	private Spinner spinnerCityName;
	private ProgressDialog httpGetProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		btnWeather = (Button) findViewById(R.id.button_get_weather);
		webViewWeather = (WebView) findViewById(R.id.webView_weather);

		spinnerCityName = (Spinner) findViewById(R.id.spinner_city_name);

		cityPair = new HashMap<String, String>();

		createCityWoeidPair();

		arrayAdapterSpinnerCityName = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new ArrayList<String>(
						cityPair.keySet()));

		spinnerCityName.setAdapter(arrayAdapterSpinnerCityName);

		btnWeather.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				httpGetProgressDialog = new ProgressDialog(WeatherActivity.this)
						.show(WeatherActivity.this, "Please wait...",
								"Fetching weather data for "
										+ spinnerCityName.getSelectedItem()
												.toString());

				new Thread() {
					@Override
					public void run() {
						super.run();

						try {
							weatherText = parseXmlString(convertInputStreamToString(convertResponseToInputStream(getWeatherRss(spinnerCityName
									.getSelectedItem().toString()))));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						runOnUiThread(changeWeatherText);
					}
				}.start();
			}
		});
	}

	private HttpResponse getWeatherRss(String cityname) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(
					"http://weather.yahooapis.com/forecastrss?w="
							+ cityPair.get(cityname) + "&u=c"));
			response = client.execute(request);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	private String parseXmlString(String xmlString) {
		return XmlParser.parse(xmlString);
	}

	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		if (inputStream != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(
						inputStream, "UTF-8"), 1024);
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				inputStream.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	private InputStream convertResponseToInputStream(HttpResponse response)
			throws IOException {
		return response.getEntity().getContent();
	}

	private Runnable changeWeatherText = new Runnable() {

		@Override
		public void run() {
			
			if(httpGetProgressDialog.isShowing() && httpGetProgressDialog != null)
				httpGetProgressDialog.hide();
			
			webViewWeather.loadData(weatherText, "text/html", "utf-8");
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_weather, menu);
		return true;
	}

	private void createCityWoeidPair() {
		City dhaka = new City("Dhaka", "22502221");
		City kathmondu = new City("Kathmondu", "2269179");

		cityPair.put(dhaka.getName(), dhaka.getWOEID());
		cityPair.put(kathmondu.getName(), kathmondu.getWOEID());
	}
}

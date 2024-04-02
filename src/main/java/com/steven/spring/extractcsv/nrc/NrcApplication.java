package com.steven.spring.extractcsv.nrc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class NrcApplication {

	public static void main(String[] args) {
		SpringApplication.run(NrcApplication.class, args);

		// Endpoint URL to retrieve the CSV data
		String endpointUrlCSV = "http://localhost:8080/download";

		// Optional query parameters
		int startRow = 2; // Start downloading from the second row. 0th row would be the column titles.
		//Setting start to anything >0 may remove column titles from appearing in JSON response
		int count = 5;   // Download only 5 rows

		// Build the URL with query parameters
		StringBuilder urlBuilder = new StringBuilder(endpointUrlCSV);
		urlBuilder.append("?start=").append(startRow);
		urlBuilder.append("&count=").append(count);

		String urlendpointUrlCSVString = urlBuilder.toString();

		System.out.println("Perform the HTTP GET request");
		try {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(new URI(urlendpointUrlCSVString))
					.build();

			HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			// If response was successful, print out the CSV response
			if (httpResponse.statusCode() == 200) {
				System.out.println(httpResponse.body());
			} else {
				System.err.println("Error. Status code: " + httpResponse.statusCode());
			}
		} catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Perform the HTTP GET request with \"Accept\" header set to \"application/json\"");
		try {
			HttpClient httpClient = HttpClient.newHttpClient();
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(new URI(urlendpointUrlCSVString))
					.header("Accept", "application/json")
					.build();

			HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			System.out.println("Response Body:");
			System.out.println(httpResponse.body());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
    }
}

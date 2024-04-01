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
		// Endpoint URL where the CSV file is hosted
		String endpointUrl = "http://localhost:8080/download";

		// Optional query parameters
		int startRow = 2; // Example: Start downloading from the second row
		int count = 10;   // Example: Download only 10 rows

		// Build the URL with query parameters
		StringBuilder urlBuilder = new StringBuilder(endpointUrl);
		urlBuilder.append("?start=").append(startRow);
		urlBuilder.append("&count=").append(count);

		String url = urlBuilder.toString();

		// Perform the HTTP GET request
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(url))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Check if the request was successful
			if (response.statusCode() == 200) {
				// Print the CSV content
				System.out.println(response.body());
			} else {
				System.err.println("Failed to download CSV. Status code: " + response.statusCode());
			}
		} catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Break================");
		
		// Perform the HTTP GET request with "Accept" header set to "application/json"
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(url))
					.header("Accept", "application/json")
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Print the response body
			System.out.println("Response Body:");
			System.out.println(response.body());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
		}


    }

}

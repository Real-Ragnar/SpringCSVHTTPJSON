package com.steven.spring.extractcsv.nrc.rest;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


@RestController
public class NRCRestController {

    private static final String CSV_FILE_PATH = "55262d.csv";

    @GetMapping(value = "/download")
    public ResponseEntity<?> downloadCSV(
            @RequestParam(value = "start", required = false) Integer start,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestHeader(value = "Accept", required = false) String acceptHeader) {

        // Load CSV file from resources
        ClassPathResource resource = new ClassPathResource(CSV_FILE_PATH);

        if (acceptHeader != null && acceptHeader.equals("application/json")) {
            // If "Accept" header is application/json, return JSON response
            return ResponseEntity.ok().body(generateJsonResponse(resource, start, count));
        } else {
            // Otherwise, return CSV content
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(generateCSVResponse(resource, start, count));
        }
    }

    private String generateCSVResponse(ClassPathResource resource, Integer start, Integer count) {
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            // Skip lines if start parameter is provided
            if (start != null) {
                for (int i = 0; i < start; i++) {
                    reader.readLine();
                }
            }

            // Read CSV lines and collect them into a single string
            String csvContent = reader.lines()
                    .limit(count != null ? count : Long.MAX_VALUE) // Limit number of lines to the specified count value, otherwise set to largest value
                    .collect(Collectors.joining("\n"));

            return csvContent;

        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading CSV file", e);
        }
    }

    private String generateJsonResponse(ClassPathResource resource, Integer start, Integer count) {
        // Parse CSV content and convert it to JSON
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            // Read the first row which is the header row to get column names
            String[] headers = reader.readNext();
            if (headers == null) {
                // Return an empty JSON object if the CSV file is empty
                return new JSONObject().toString();
            }
            // Skip lines if start parameter is provided
            for (int i = 0; i < start; i++) {
                if (reader.readNext() == null) {
                    // Return an empty JSON object if start parameter exceeds the number of rows
                    return new JSONObject().toString();
                }
            }

            // Read CSV lines and convert them to JSON Array to be displayed
            JSONArray csvRowsJsonArray = new JSONArray();
            String[] csvRowLine;
            int lineCount = 0;
            while ((csvRowLine = reader.readNext()) != null && (count == null || lineCount < count)) {
                JSONObject csvRowJsonObject = new JSONObject(); // Created for each row, and each data value is assigned to its corresponding column title.
                for (int i = 0; i < headers.length; i++) {
                    // Format each row entry to contain the column title
                    csvRowJsonObject.put(headers[i], csvRowLine[i]);
                }
                csvRowsJsonArray.put(csvRowJsonObject);
                lineCount++;
            }

            // Create a JSON object to hold both column titles and data
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("Observation Data", csvRowsJsonArray); // Add CSV data

            return jsonResult.toString(2); // Indent JSON for readability
        } catch (IOException e) {
            throw new RuntimeException("Error while reading CSV file", e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
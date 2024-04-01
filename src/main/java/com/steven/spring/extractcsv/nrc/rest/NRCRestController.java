package com.steven.spring.extractcsv.nrc.rest;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.stream.Collectors;


@RestController
public class NRCRestController {

    private static final String CSV_FILE_PATH = "55262.csv";

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
                    .limit(count != null ? count : Long.MAX_VALUE) // Limit the number of lines if count parameter is provided
                    .collect(Collectors.joining("\n"));

            return csvContent;

        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading CSV file", e);
        }
    }

    private String generateJsonResponse(ClassPathResource resource, Integer start, Integer count) {
        // Parse CSV content and convert it to JSON
        try (CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            // Skip lines if start parameter is provided
            for (int i = 0; i < start; i++) {
                reader.readNext();
            }

            // Read CSV lines and convert them to JSON
            JSONArray jsonArray = new JSONArray();
            String[] line;
            int lineCount = 0;
            while ((line = reader.readNext()) != null && (count == null || lineCount < count)) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < line.length; i++) {
                    jsonObject.put("column" + (i + 1), line[i]);
                }
                jsonArray.put(jsonObject);
                lineCount++;
            }

            return jsonArray.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading CSV file", e);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
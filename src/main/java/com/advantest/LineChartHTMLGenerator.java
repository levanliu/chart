/*******************************************************************************
 * Copyright (c) 2024 Advantest. All rights reserved.
 *
 * Contributors:
 *     Advantest - initial API and implementation
 *******************************************************************************/
package com.advantest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LineChartHTMLGenerator {
    public static void main(String[] args) {
        LineChartHTMLGenerator generator = new LineChartHTMLGenerator();
        generator.generateLineChart();
    }

    public void generateLineChart() {
        // Read JSON data from a file
        String jsonData = readJsonDataFromFile("src/main/resources/mv_perf_test_json_report");
        if (jsonData == null) {
            System.out.println("Error reading JSON data from file");
            return;
        }

        // Read HTML template file
        String htmlTemplate = readHtmlTemplateFromFile("src/main/resources/templates/line_chart_template.html.html");
        if (htmlTemplate == null) {
            System.out.println("Error reading HTML template from file");
            return;
        }

        // Generate the modified HTML content
        List<String[]> modifiedContent = generateModifiedHTML(htmlTemplate, jsonData);

        // Write the modified HTML content to a new file
        boolean success = writeModifiedContentToFile(modifiedContent, "src/main/resources/templates/mv_perf_test_line_chart.html");
        if (success) {
            System.out.println("HTML file generated successfully!");
        } else {
            System.out.println("Error writing modified HTML content to file");
        }
    }

    private String readJsonDataFromFile(String filePath) {
        StringBuilder jsonData = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonData.toString();
    }

    private String readHtmlTemplateFromFile(String filePath) {
        StringBuilder htmlContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return htmlContent.toString();
    }

    private List<String[]> generateModifiedHTML(String template, String jsonData) {
        StringBuilder modifiedContent = new StringBuilder(template);
        List<String> value;
        List<String[]> tempData = new ArrayList<>();

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonData, JsonArray.class);

        for (JsonElement element : jsonArray) {
            value = new ArrayList<>();
            JsonObject jsonObj = element.getAsJsonObject();

            JsonObject additionalData = jsonObj.getAsJsonObject("AdditionalData");
            JsonElement pin = additionalData.get("pin");
            JsonElement level = additionalData.get("level");
            JsonElement timing = additionalData.get("timing");
            JsonElement action = additionalData.get("action");

            String testCase = "pin_" + pin.getAsString() + "_level_" + level.getAsString() + "_timing_" + timing.getAsString() + "_action_" + action.getAsString();

            JsonObject moreData = jsonObj.getAsJsonObject("MoreData");
            Map<String, JsonElement> moreDataMap = moreData.asMap();

            value.add(testCase);

            for (Map.Entry<String, JsonElement> entry : moreDataMap.entrySet()) {
                value.add(entry.getKey());
                value.add(entry.getValue().getAsString());
            }
            tempData.add(value.toArray(new String[0]));
        }
        return tempData;
    }

    private boolean writeModifiedContentToFile(List<String[]> tempData, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            StringBuilder modifiedContent = new StringBuilder();
    
            // Append the modified content to the StringBuilder
            for (String[] rowData : tempData) {
                modifiedContent.append("[");
                for (int i = 0; i < rowData.length; i++) {
                    modifiedContent.append("'").append(rowData[i]).append("'");
                    if (i != rowData.length - 1) {
                        modifiedContent.append(", ");
                    }
                }
                modifiedContent.append("],\n");
            }
    
            // Write the modified content to the file
            writer.write(modifiedContent.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}

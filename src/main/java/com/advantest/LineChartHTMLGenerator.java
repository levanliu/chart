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
        String htmlTemplate = readHtmlTemplateFromFile("src/main/resources/templates/line_chart_template.html");
        if (htmlTemplate == null) {
            System.out.println("Error reading HTML template from file");
            return;
        }

        // Generate the modified HTML content
        String modifiedContent = generateModifiedHTML(htmlTemplate, jsonData);

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

    private String generateModifiedHTML(String template, String jsonData) {
        StringBuilder modifiedContent = new StringBuilder(template);
        List<String[]> tempData = new ArrayList<>();

        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(jsonData, JsonArray.class);

        StringBuilder jsonDataBuilder = new StringBuilder("var tempData = [\n");

        // Generate the header row
        StringBuilder headerRow = new StringBuilder();
        headerRow.append("['testCase'");
        JsonObject firstObj = jsonArray.get(0).getAsJsonObject();
        JsonObject moreDataHeader = firstObj.getAsJsonObject("MoreData");
        for (Map.Entry<String, JsonElement> entry : moreDataHeader.entrySet()) {
            headerRow.append(", '").append(entry.getKey()).append("'");
        }
        headerRow.append("],\n");
        jsonDataBuilder.append(headerRow);

        for (JsonElement element : jsonArray) {
            JsonObject jsonObj = element.getAsJsonObject();

            JsonObject additionalData = jsonObj.getAsJsonObject("AdditionalData");
            JsonElement pin = additionalData.get("pin");
            JsonElement level = additionalData.get("level");
            JsonElement timing = additionalData.get("timing");
            JsonElement action = additionalData.get("action");

            String testCase = "pin_" + pin.getAsString() + "_level_" + level.getAsString() + "_timing_" + timing.getAsString() + "_action_" + action.getAsString();

            JsonObject moreData = jsonObj.getAsJsonObject("MoreData");
            List<String> row = new ArrayList<>();
            row.add(testCase);
            for (Map.Entry<String, JsonElement> entry : moreData.entrySet()) {
                row.add(entry.getValue().getAsString());
            }
            tempData.add(row.toArray(new String[0]));

            StringBuilder rowJsonData = new StringBuilder();
            rowJsonData.append("[");

            for (int i = 0; i < row.size(); i++) {
                if (i == 0) {
                    rowJsonData.append("'").append(row.get(i)).append("'");
                } else {
                    rowJsonData.append(row.get(i));
                }
                if (i != row.size() - 1) {
                    rowJsonData.append(", ");
                }
            }
            rowJsonData.append("],");
            jsonDataBuilder.append(rowJsonData.toString()).append("\n");
        }

        jsonDataBuilder.deleteCharAt(jsonDataBuilder.length() - 1); // Remove the last comma
        jsonDataBuilder.append("\n];");

        // Replace the var tempData assignment in the HTML template with the generated JSON string
        modifiedContent.replace(modifiedContent.indexOf("var tempData = ["), modifiedContent.indexOf("];") + 2, jsonDataBuilder.toString());

        return modifiedContent.toString();
    }

    private boolean writeModifiedContentToFile(String modifiedContent, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
          // Write the modified content to the file
            writer.write(modifiedContent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

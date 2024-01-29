package com.advantest;

import java.io.*;

public class HTMLModifier {
    public static void main(String[] args) {
        // Specify the path of the HTML file
        String filePath = "path/to/your/html/file.html";

        // Read the contents of the HTML file
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Modify the content as needed
        String modifiedContent = content.toString(); // Modify this line to update the content

        // Write the modified content back to the HTML file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(modifiedContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

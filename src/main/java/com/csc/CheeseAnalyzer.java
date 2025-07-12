package com.csc;

import java.io.*;
import java.util.*;

public class CheeseAnalyzer {

    public static void analyzeCheeseData(String inputFileName) {
        int pasteurizedCount = 0;
        int rawCount = 0;
        int organicHighMoistureCount = 0;
        Map<String, Integer> milkTypeCount = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            String header = br.readLine();
            if (header == null) {
                System.out.println("Empty CSV file.");
                return;
            }

            String[] columns = header.split(",");
            int milkTreatmentIdx = findIndex(columns, "MilkTreatmentTypeEn");
            int organicIdx = findIndex(columns, "Organic");
            int moistureIdx = findIndex(columns, "MoisturePercent");
            int milkTypeIdx = findIndex(columns, "MilkTypeEn");

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = splitCSVLine(line);

                if (tokens.length <= Math.max(Math.max(milkTreatmentIdx, organicIdx), Math.max(moistureIdx, milkTypeIdx))) {
                    continue;
                }

                String milkTreatment = tokens[milkTreatmentIdx].trim();
                if (milkTreatment.equalsIgnoreCase("Pasteurized")) {
                    pasteurizedCount++;
                } else if (milkTreatment.equalsIgnoreCase("Raw Milk")) {
                    rawCount++;
                }

                String organicStr = tokens[organicIdx].trim();
                String moistureStr = tokens[moistureIdx].trim();
                if (!organicStr.isEmpty() && !moistureStr.isEmpty()) {
                    try {
                        int organicVal = Integer.parseInt(organicStr);
                        double moistureVal = Double.parseDouble(moistureStr);
                        if (organicVal == 1 && moistureVal > 41.0) {
                            organicHighMoistureCount++;
                        }
                    } catch (NumberFormatException ignored) {}
                }

                String milkType = tokens[milkTypeIdx].trim();
                if (!milkType.isEmpty()) {
                    milkType = milkType.toLowerCase();
                    milkTypeCount.put(milkType, milkTypeCount.getOrDefault(milkType, 0) + 1);
                }
            }

            String mostCommonMilkType = "N/A";
            int maxCount = 0;
            for (Map.Entry<String, Integer> entry : milkTypeCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostCommonMilkType = entry.getKey();
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
                bw.write("Number of cheeses using pasteurized milk: " + pasteurizedCount + "\n");
                bw.write("Number of cheeses using raw milk: " + rawCount + "\n");
                bw.write("Number of organic cheeses with moisture > 41.0%: " + organicHighMoistureCount + "\n");
                bw.write("Most common milk type used in Canadian cheeses: " + mostCommonMilkType + "\n");
            }

            System.out.println("Analysis completed. Results written to output.txt.");

        } catch (IOException e) {
            System.out.println("Error reading or writing file: " + e.getMessage());
        }
    }

    private static int findIndex(String[] columns, String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private static String[] splitCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    public static void main(String[] args) {
        analyzeCheeseData("cheese_data.csv"); // adjust if your file has a different name
    }
}
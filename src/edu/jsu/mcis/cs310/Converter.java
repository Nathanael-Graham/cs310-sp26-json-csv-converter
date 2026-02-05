package edu.jsu.mcis.cs310;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Iterator;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        try {
        
            // INSERT YOUR CODE HERE
             CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> allRows = reader.readAll();
        
            Iterator<String[]> iterator = allRows.iterator();
        
            if (!iterator.hasNext()) {
                return "{}";
            }
            
            String[] headings = iterator.next();
            JsonArray colHeadings = new JsonArray();

            int h = 0;
            while (h < headings.length) {
                colHeadings.add(headings[h]);
                h++;
            }

            JsonArray prodNums = new JsonArray();
            JsonArray data = new JsonArray();

            while (iterator.hasNext()) {
                String[] row = iterator.next();
                prodNums.add(row[0]);

                JsonArray dataRow = new JsonArray();

                int c = 1;
                while (c < row.length) {
                    if (c == 2 || c == 3){
                        dataRow.add(Integer.valueOf(row[c]));
                    }
                    else {
                        dataRow.add(row[c]);
                    }
                    c++;
                }
                data.add(dataRow);
            }
            JsonObject root = new JsonObject();
            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);

            result = Jsoner.serialize(root);   
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // INSERT YOUR CODE HERE
            JsonObject root = (JsonObject) Jsoner.deserialize(jsonString);
            
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray data = (JsonArray) root.get("Data");
            
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);
            
            Iterator<Object> headingIterator = colHeadings.iterator();
            String[] headerRow = new String[colHeadings.size()];
            
            int headerIndex = 0;
            while (headingIterator.hasNext()){
                Object heading = headingIterator.next();
                headerRow[headerIndex] = heading.toString();
                headerIndex++;
            }
            
            csvWriter.writeNext(headerRow);
            Iterator<Object> prodIterator = prodNums.iterator();
            Iterator<Object> dataIterator = data.iterator();
            
            while (prodIterator.hasNext() && dataIterator.hasNext()){
                Object prod = prodIterator.next();
                Object rowObject = dataIterator.next();
                
                JsonArray dataRow = (JsonArray) rowObject;
                
                String[] csvRow = new String[dataRow.size() + 1];
                csvRow[0] = prod.toString();
                
                Iterator<Object> rowIterator = dataRow.iterator();
                
                int i = 1;
                while (rowIterator.hasNext()){
                    Object value = rowIterator.next();
                    if (i == 3 && value instanceof Number){
                        csvRow[i] = String.format("%02d", ((Number) value).intValue());
                    }
                    else{
                        csvRow[i] = value.toString();
                    }
                    i++;
                }
                csvWriter.writeNext(csvRow);
            }
            csvWriter.close();
            result = writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}

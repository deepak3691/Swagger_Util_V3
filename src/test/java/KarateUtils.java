import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KarateUtils {

    public static String resourceName;
    public static String filename;
    public static FileInputStream fis = null;
    public static FileOutputStream fos = null;
    public static XSSFWorkbook workbook = null;
    public static XSSFSheet sheet = null;
    public static XSSFRow row = null;
    public static XSSFCell cell = null;
    public static String xlFilePath = System.getProperty("user.dir") + File.separator + "Test.xlsx";
    public static JSONParser parser = new JSONParser();
    public static Object fileObj;
    public static List<String> files;
    public static String paramterJsonFilePath = System.getProperty("user.dir") + File.separator + "Parameter" + File.separator;
    public static String componentJsonFilePath = System.getProperty("user.dir") + File.separator + "Components" + File.separator;
    public static String responseJsonFilePath = System.getProperty("user.dir") + File.separator + "Response" + File.separator;
    public static String featureFilePath = System.getProperty("user.dir") + File.separator + "Features" + File.separator;


    //**********************************Component Generator from swagger file*****************************//

    public static void componentGenerator(String swaggerJsonFilePath) throws IOException, ParseException {

        try {
            cleanDirectory(componentJsonFilePath);
            fileObj = parser.parse(new FileReader(swaggerJsonFilePath));
            JSONObject componentsJSON = new JSONObject(fileObj.toString()).getJSONObject("components").getJSONObject("schemas");
            List<String> componentsList = new ArrayList<>(componentsJSON.keySet());

            for (int i = 0; i < componentsList.size(); i++) {
                JSONObject getComponentJSON = componentsJSON.getJSONObject(componentsList.get(i));
                List<String> componentDataList = new ArrayList<>(getComponentJSON.keySet());

                for (int j = 0; j < componentDataList.size(); j++) {
                    if (componentDataList.get(j).equals("properties")) {
                        JSONObject componentJson = getComponentJSON.getJSONObject("properties");
                        jsonFileGenerator(componentJsonFilePath, componentsList.get(i), componentJson.toString());
                    }
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        resolveComponents(componentJsonFilePath);
    }

    public static void resolveComponents(String componentFilePath) throws IOException, ParseException {
        files = getFiles(componentFilePath);

        for (int i = 0; i < files.size(); i++) {

            String componentFileName = getFileName(componentFilePath, files.get(i));
            fileObj = parser.parse(new FileReader(files.get(i)));
            JSONObject componentJson = new JSONObject(fileObj.toString());

            while (componentJson.toString().contains("#/components")) {
                componentJson = replacekeyInJSONObject(componentJson);
            }
            //System.out.println(componentJson);
            jsonFileGenerator(componentFilePath, componentFileName, componentJson.toString());
        }
    }

    //**********************************Response Generator from swagger file*****************************//

    public static void responseGenerator(String swaggerJsonFilePath) throws Exception {
        try {
            cleanDirectory(responseJsonFilePath);
            fileObj = parser.parse(new FileReader(swaggerJsonFilePath));
            JSONObject pathJson = new JSONObject(fileObj.toString()).getJSONObject("paths");

            //Resources
            List<String> resourceList = new ArrayList<>(pathJson.keySet());

            for (int i = 0; i < resourceList.size(); i++) {
                JSONObject resourceJson = pathJson.getJSONObject(resourceList.get(i));

                //Request Method
                List<String> methodList = new ArrayList<>(resourceJson.keySet());

                for (int j = 0; j < methodList.size(); j++) {
                    JSONObject responseJson = resourceJson.getJSONObject(methodList.get(j)).getJSONObject("responses");

                    resourceName = resourceList.get(i).replaceAll("/", "-");
                    filename = "response" + resourceName + "_" + methodList.get(j).toUpperCase();
                    jsonFileGenerator(responseJsonFilePath, filename, responseJson.toString());
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        resolveResponse(responseJsonFilePath);
    }


    public static void resolveResponse(String responseJsonFilePath) throws IOException, ParseException {
        files = getFiles(responseJsonFilePath);

        for (int i = 0; i < files.size(); i++) {

            String responseFileName = getFileName(responseJsonFilePath, files.get(i));
            //  System.out.println("For " + responseFileName);

            fileObj = parser.parse(new FileReader(files.get(i)));
            JSONObject responseJson = new JSONObject(fileObj.toString());

            while (responseJson.toString().contains("#/components")) {
                responseJson = replacekeyInJSONObject(responseJson);
            }

            //System.out.println(responseJson);
            jsonFileGenerator(responseJsonFilePath, responseFileName, responseJson.toString());
        }
    }

    //**********************************Parameter Generator from swagger file*****************************//

    public static void parameterGenerator(String swaggerJsonFilePath) throws Exception {

        JSONObject paramJson = new JSONObject();

        try {
            cleanDirectory(paramterJsonFilePath);
            fileObj = parser.parse(new FileReader(swaggerJsonFilePath));
            JSONObject pathJson = new JSONObject(fileObj.toString()).getJSONObject("paths");

            //Resources
            List<String> resourceList = new ArrayList<>(pathJson.keySet());

            for (int i = 0; i < resourceList.size(); i++) {
                JSONObject resourceJson = pathJson.getJSONObject(resourceList.get(i));

                //Request Method
                List<String> methodList = new ArrayList<>(resourceJson.keySet());

                for (int j = 0; j < methodList.size(); j++) {

                    if (resourceJson.getJSONObject(methodList.get(j)).keySet().contains("requestBody")) {
                        JSONObject requestBodyJson = resourceJson.getJSONObject(methodList.get(j)).getJSONObject("requestBody");
                        resourceName = resourceList.get(i).replaceAll("/", "-");
                        filename = "requestBody" + resourceName + "_" + methodList.get(j).toUpperCase();
                        jsonFileGenerator(paramterJsonFilePath, filename, requestBodyJson.toString());

                    } else if (resourceJson.getJSONObject(methodList.get(j)).keySet().contains("parameters")) {
                        JSONArray parameterJsonArray = resourceJson.getJSONObject(methodList.get(j)).getJSONArray("parameters");
                        resourceName = resourceList.get(i).replaceAll("/", "-");
                        filename = "param" + resourceName + "_" + methodList.get(j).toUpperCase();
                        paramJson.put("parameters", parameterJsonArray);
                        jsonFileGenerator(paramterJsonFilePath, filename, paramJson.toString());
                    }
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        resolveParameter(paramterJsonFilePath);
    }

    public static void resolveParameter(String parameterJsonFilePath) throws IOException, ParseException {

        files = getFiles(parameterJsonFilePath);

        for (int i = 0; i < files.size(); i++) {
            String paramterFileName = getFileName(parameterJsonFilePath, files.get(i));
            fileObj = parser.parse(new FileReader(files.get(i)));
            JSONObject parameterJson = new JSONObject(fileObj.toString());

            while (parameterJson.toString().contains("#/components")) {
                parameterJson = replacekeyInJSONObject(parameterJson);
            }
            jsonFileGenerator(parameterJsonFilePath, paramterFileName, parameterJson.toString());
        }

    }

    //*********************************Other Reusable Utilities************************************//

    public static JSONObject createJSONObject(String jsonString) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object fileObj = jsonParser.parse(new FileReader(jsonString));
        JSONObject jsonObject = new JSONObject(fileObj.toString());
        return jsonObject;
    }

    public static JSONObject replacekeyInJSONObject(JSONObject jsonObject) throws IOException, ParseException {

        for (Object key : jsonObject.keySet()) {

            String stringJson = jsonObject.get(key.toString()).toString();
            String[] arrayJson = stringJson.split(":");
            String firstElementString = arrayJson[0];

            if (firstElementString.contains("$ref") && key.toString().equals("items")) {
                String[] fileNameArray = jsonObject.get(key.toString()).toString().split("/");
                String definitionFileName = fileNameArray[fileNameArray.length - 1].replaceAll("[-\\[\\]+.^:,}\"]", "");
                JSONObject definitionJSon = createJSONObject(componentJsonFilePath + definitionFileName + ".json");
                JSONArray ja = new JSONArray();
                ja.put(definitionJSon);
                jsonObject.put(key.toString(), ja);
                return jsonObject;

            } else if (firstElementString.contains("$ref") && !key.toString().equals("items")) {
                String[] fileNameArray = jsonObject.get(key.toString()).toString().split("/");
                String definitionFileName = fileNameArray[fileNameArray.length - 1].replaceAll("[-\\[\\]+.^:,}\"]", "");
                JSONObject definitionJSon = createJSONObject(componentJsonFilePath + definitionFileName + ".json");
                jsonObject.put(key.toString(), definitionJSon);
                return jsonObject;

            } else if (key.equals("items") && jsonObject.get(key.toString()).toString().startsWith("[")) {
                JSONArray arrayItemJson = jsonObject.getJSONArray(key.toString());
                List<String> arrayItemList = new ArrayList<>(arrayItemJson.getJSONObject(0).keySet());
                JSONObject arrayJsonObject = (JSONObject) arrayItemJson.get(0);
                for (int l = 0; l < arrayItemList.size(); l++) {
                    if (arrayJsonObject.get(arrayItemList.get(l)).toString().contains("components")) {
                        String[] fileNameArray = arrayJsonObject.get(arrayItemList.get(l)).toString().split("/");

                        int index = fileNameArray[fileNameArray.length - 1].indexOf("\"");
                        String definitionFileName = fileNameArray[fileNameArray.length - 1].substring(0, index);
                        JSONObject definitionJSon = createJSONObject(componentJsonFilePath + definitionFileName + ".json");
                        if (!arrayItemList.get(l).contains("items")) {
                            arrayJsonObject.put(arrayItemList.get(l), definitionJSon);
                            return jsonObject;
                        } else if (arrayItemList.get(l).contains("items")) {
                            JSONArray ja = new JSONArray();
                            ja.put(definitionJSon);
                            arrayJsonObject.put(arrayItemList.get(l), ja);
                            return jsonObject;
                        }

                    }
                }

            } else if (jsonObject.get(key.toString()) instanceof JSONObject) {
                JSONObject modifiedJsonobject = (JSONObject) jsonObject.get(key.toString());

                if (modifiedJsonobject != null) {
                    replacekeyInJSONObject(modifiedJsonobject);
                }
            }
        }
        return jsonObject;
    }

    public static void cleanDirectory(String directoryToClean) {

        String screen_folder = directoryToClean;
        File file = new File(screen_folder);
        try {
            FileUtils.cleanDirectory(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String filePath, String files) {
        String[] fileArray = (filePath + files).split("\\\\");
        int fileArrayLength = fileArray.length - 1;
        String fileName = fileArray[fileArrayLength].replaceAll(".json", "");
        return fileName;
    }

    public static List<String> getFiles(String filePath) {
        try
                (Stream<Path> walk = Files.walk(Paths.get(filePath))) {
            files = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    public static void jsonFileGenerator(String filePath, String fileDefinationName, String data) {
        {
            try {
                //Writing data into JSON file

                FileWriter file = new FileWriter(filePath + fileDefinationName + ".json");
                file.write(data);
                file.close();
            } catch (IOException e) {
                System.out.println("IO Exception: File not created for for defintion " + fileDefinationName);
            } catch (JSONException e) {
                System.out.println("JSON Exception: Parameters are not available for definition " + fileDefinationName);
            } catch (Exception e) {
                System.out.println("UNKNOWN Exception: Unknown Exception for definition" + fileDefinationName + "Below is exception");
                e.printStackTrace();
            }
        }
    }

    //********************************************Excel Write Code******************************************

    public static void ExcelApiTest(String xlFilePath) throws Exception {
        fis = new FileInputStream(xlFilePath);
        workbook = new XSSFWorkbook(fis);
        fis.close();
    }

    public static boolean setCellData(String sheetName, String colName, int rowNum, String value) throws Exception {

        ExcelApiTest(xlFilePath);
        try {
            int col_Num = -1;
            sheet = workbook.getSheet(sheetName);

            row = sheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equals(colName)) {
                    col_Num = i;
                }
            }

            sheet.autoSizeColumn(col_Num);
            row = sheet.getRow(rowNum - 1);
            if (row == null)
                row = sheet.createRow(rowNum - 1);

            cell = row.getCell(col_Num);
            if (cell == null)
                cell = row.createCell(col_Num);

            cell.setCellValue(value);

            fos = new FileOutputStream(xlFilePath);
            workbook.write(fos);
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}





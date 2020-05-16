
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONEdit {

    public static String string = "D:\\workspace\\SwaggerUtil_V3\\Components\\Dataphone.json";

    public static String componentFilePath = "D:\\workspace\\SwaggerUtil_V3\\Components\\";


    public static void main(String[] args) throws IOException, ParseException {

      //  JSONObject json = JSONEdit.createJSONObject(string);

     //   JSONObject y = JSONEdit.replacekeyInJSONObject(json);
    //    System.out.println(y);
    }

    public static JSONObject replacekeyInJSONObject(JSONObject jsonObject) throws IOException, ParseException {

        for (Object key : jsonObject.keySet()) {

            String stringJson = jsonObject.get(key.toString()).toString();
            String[] arrayJson = stringJson.split(":");
            String firstElementString = arrayJson[0];

            if (firstElementString.contains("$ref") && key.toString().equals("items")) {
                String[] fileNameArray = jsonObject.get(key.toString()).toString().split("/");
                String definitionFileName = fileNameArray[fileNameArray.length - 1].replaceAll("[-\\[\\]+.^:,}\"]", "");
                JSONObject definitionJSon = createJSONObject(componentFilePath + definitionFileName + ".json");
                JSONArray ja = new JSONArray();
                ja.put(definitionJSon);
                jsonObject.put(key.toString(), ja);
                return jsonObject;

            } else if (firstElementString.contains("$ref") && !key.toString().equals("items")) {
                String[] fileNameArray = jsonObject.get(key.toString()).toString().split("/");
                String definitionFileName = fileNameArray[fileNameArray.length - 1].replaceAll("[-\\[\\]+.^:,}\"]", "");
                JSONObject definitionJSon = createJSONObject(componentFilePath + definitionFileName + ".json");
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
                        String definitionFileName = fileNameArray[fileNameArray.length - 1].substring(0,index);
                        JSONObject definitionJSon = createJSONObject(componentFilePath + definitionFileName + ".json");
                        if(!arrayItemList.get(l).contains("items")) {
                            arrayJsonObject.put(arrayItemList.get(l), definitionJSon);
                            return jsonObject;
                        }
                        else if(arrayItemList.get(l).contains("items")){
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

    public static JSONObject createJSONObject(String jsonString) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        Object fileObj = jsonParser.parse(new FileReader(jsonString));
        JSONObject jsonObject = new JSONObject(fileObj.toString());
        return jsonObject;
    }
}

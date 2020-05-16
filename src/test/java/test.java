
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test {

    public static List<String> li = new ArrayList<String>();
    public static JSONObject temp = new JSONObject();
    public static String string = "D:\\workspace\\SwaggerUtil_V3\\A\\tes1.json";

    public static String componentFilePath = "D:\\workspace\\SwaggerUtil_V3\\Components\\";


    public static void main(String[] args) throws IOException, ParseException {

        JSONObject json = test.createJSONObject(string);

        test.replacekeyInJSONObject(json);
       // System.out.println(y);
    }

    public static JSONObject replacekeyInJSONObject(JSONObject jsonObject) throws IOException, ParseException {
        outerloop:
        for (Object key : jsonObject.keySet()) {
            System.out.println(key);

            if (key.equals(key.toString()) && jsonObject.get(key.toString()).toString().startsWith("[")) {
                    JSONArray arrayJson = jsonObject.getJSONArray(key.toString());
                    List<String> list2 = new ArrayList<>(arrayJson.getJSONObject(0).keySet());
                    JSONObject arrayJsonObject = (JSONObject) arrayJson.get(0);
                    for (int l = 0; l < list2.size(); l++) {
                        if (arrayJsonObject.get(list2.get(l)).toString().contains("components")){

                        }



                }



            } else if (jsonObject.get(key.toString()) instanceof JSONObject) {
                JSONObject modifiedJsonobject = (JSONObject) jsonObject.get(key.toString());
                temp = jsonObject;
                if (modifiedJsonobject != null) {
                    replacekeyInJSONObject(modifiedJsonobject);
                }
            }
        }
        return jsonObject;
    }

    private static JSONObject createJSONObject(String jsonString) throws IOException, ParseException {
        //  JSONObject  jsonObject=new JSONObject();
        JSONParser jsonParser = new JSONParser();

        Object fileObj = jsonParser.parse(new FileReader(jsonString));
        JSONObject jsonObject = new JSONObject(fileObj.toString());
        return jsonObject;
    }
}

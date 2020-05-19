import java.io.File;

public class SwaggerUtilRunner {

    // Petstore,bestbuy,Uber,GameTvClient,Conduit

    public static String swaggerJsonFilePath = System.getProperty("user.dir")+ File.separator + "Petstore.json";

    public static void main(String[] args) throws Exception {
      // SwaggerUtils.dataGenerator(swaggerJsonFilePath,"/pet","POST");
      SwaggerUtils.dataGenerator(swaggerJsonFilePath);
    }
}

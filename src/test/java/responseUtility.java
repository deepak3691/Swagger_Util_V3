import java.io.File;

public class responseUtility {

  // public static String swaggerJsonFilePath = "D:\\workspace\\SwaggerUtil_V3\\Petstore.json";
  //  public static String swaggerJsonFilePath = "D:\\workspace\\SwaggerUtil_V3\\bestbuy.json";
 //   public static String swaggerJsonFilePath = "D:\\workspace\\SwaggerUtil_V3\\Uber.json";
 //   public static String swaggerJsonFilePath = "D:\\workspace\\SwaggerUtil_V3\\GameTvClient.json";
    public static String swaggerJsonFilePath = "D:\\workspace\\SwaggerUtil_V3\\FBNKAIntegration.json";
//    public static String swaggerJsonFilePath = System.getProperty("user.dir")+ File.separator + "Conduit.json";


    public static void main(String[] args) throws Exception {

        KarateUtils.componentGenerator(swaggerJsonFilePath);
        KarateUtils.responseGenerator(swaggerJsonFilePath);
        KarateUtils.parameterGenerator(swaggerJsonFilePath);
    }
}

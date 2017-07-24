
import java.io.FileReader;
import java.util.*;

public class employee_id {

    public static void main(String[] args) throws Exception{
        Properties props=new Properties();
        props.load(new FileReader("app.properties"));
        System.out.println(props.getProperty("api.ai key"));
    }
}

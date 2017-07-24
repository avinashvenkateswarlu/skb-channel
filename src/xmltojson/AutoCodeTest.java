package xmltojson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;
import javax.tools.ToolProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author root
 */
public class AutoCodeTest {
    
    private static final int PRETTY_PRINT_INDENT_FACTOR = 4;
    static boolean file_changed=true;

    public AutoCodeTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    File f=new File("index.xml");
                    long last_modified=f.lastModified();
                    
                    while(true)
                    {
                        f=new File("index.xml");
                        if(last_modified!=f.lastModified())
                        {
                            System.out.println("If called");
                            last_modified=f.lastModified();
                            file_changed=true;
                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    
    
    public static void main(String args[]) throws Exception
    {
        
        new AutoCodeTest();
        
        while(true)
        {
            try
            {
                Thread.sleep(500);
            }
            catch(Exception ex)
            {
                
            }
                
            if(file_changed)
            {
                
         String XML_TEXT="";
         FileReader fr=new FileReader("index.xml");
         Scanner sc=new Scanner(fr);
         while(sc.hasNext())
         {
          XML_TEXT=XML_TEXT+sc.nextLine();
         }
         //System.out.println(XML_TEXT);
         JSONObject xmlJSONObj = XML.toJSONObject(XML_TEXT);
         
         
         
        String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
        
        JSONArray jr=(JSONArray) xmlJSONObj.getJSONObject("channels").get("channel");
        File f=new File("k.java");
        FileWriter fw=new FileWriter(f);
        fw.write("" +
                jr.getJSONObject(1).getString("imports")+
"public class k {" +
"     static public void main(String args[]){"
                + jr.getJSONObject(1).getString("code")
                + "}" +
                        jr.getJSONObject(1).getString("function")+
"}");
        fw.flush();
        fw.close();
        
        Compiler.enable();
        ToolProvider.getSystemJavaCompiler().run(null, null, null, f.getAbsoluteFile().toString());
        URL u[]=new URL[1];
        u[0]=new URL("file:///private/var/root/Desktop/repo/sample/xmltojson/");
        //URLClassLoader.newInstance(u, ToolProvider.getSystemToolClassLoader().getParent()).loadClass("f").newInstance();
        ClassLoader cl=new URLClassLoader(u);
        
        //System.out.println(u[0]);
        Class c=cl.loadClass("k");
        Object ob=c.newInstance();
        //System.out.println(c.getDeclaredFields().length);
        for (Field field : c.getDeclaredFields()) {
            System.out.println(field.get(ob));
        }
        
         for (Method m : c.getMethods()) {
             if(m.getName().compareTo("main")==0)
             {
                 Object[] arguments = new Object[]{args};
                 m.invoke(null,arguments);
             }
        }
         file_changed=false;
            }
            
        }
    }
    
}

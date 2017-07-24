package xmltojson;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import static java.util.Map.Entry;

public class Xmltojson {
 
    private static final int PRETTY_PRINT_INDENT_FACTOR = 4;
    
    public static void main(String[] args) throws Exception{
        String XML_TEXT="";
         FileReader fr=new FileReader("index.xml");
         Scanner sc=new Scanner(fr);
         while(sc.hasNext())
         {
          XML_TEXT=XML_TEXT+sc.nextLine();
         }
         //System.out.println(XML_TEXT);
         JSONObject xmlJSONObj = XML.toJSONObject(XML_TEXT);
         
         
         
        //String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
        
        //JSONArray jr=(JSONArray) xmlJSONObj.getJSONObject("channels").get("channel");
        
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
         new Memorymonitor();
            }
        });
        
        HashMap<String,Object> channels=parsejson("channels", xmlJSONObj.getJSONObject("channels").getJSONArray("channel"));
        for(Entry<String,Object> ch : channels.entrySet())
        {
            if(ch.getValue() instanceof HashMap)
            {
                HashMap<String,Object> channel=(HashMap<String, Object>) ch.getValue();
                
                if(channel.containsKey("name"))
                {
                    switch(((String) channel.get("name")).toLowerCase().trim())
                    {
                        case "skype for business" :
                        {
                            Channels.SkypeForBusiness.StartChannel ob=new Channels.SkypeForBusiness.StartChannel(channel);
                        }
                                        break;
                        case "web" :
                    {
                            System.out.println("channel web calleds");
                        }
                                        break;
                        default : 
                        {
                            System.out.println("Default called");
                        }
                    }
                }
            }
        }
        
        
        
        /*File f=new File("k.java");
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
        System.out.println(u[0]);
        Class c=cl.loadClass("k");
        Object ob=c.newInstance();
        System.out.println(c.getDeclaredFields().length);
        for (Field field : c.getDeclaredFields()) {
            System.out.println(field.get(ob));
        }
        
         for (Method m : c.getMethods()) {
             if(m.getName().compareTo("main")==0)
             {
                 Object[] arguments = new Object[]{args};
                 m.invoke(null,arguments);
             }
        }*/
        
    }
    
    static public HashMap<String, Object> parsejson(String name,Object ob)
    {
            HashMap<String, Object> result=new HashMap<>();
            
            if(ob instanceof String)
            {
                result.put(name, ob);
                
            }
            else if(ob instanceof JSONObject)
            {
                
                HashMap<String,Object> temp=new HashMap<>();
                for(String key : ((JSONObject) ob).keySet())
                {
                    temp.putAll(parsejson(key, ((JSONObject) ob).get(key)));
                 }
                result.put(name, temp);
            }
            else if (ob instanceof JSONArray)
            {
                
                JSONArray jr=(JSONArray)ob;
                for(int i=0;i<jr.length();i++)
                {
                    result.putAll(parsejson(name+"-"+i, jr.get(i)));
                }
            }
            
            return result;
    }
    
    
    static public Object penetrate(HashMap<String,Object> data,String search,String seperator)
    {
        StringTokenizer st=new StringTokenizer(search, seperator);
        ArrayList<String> sequence=new ArrayList<>();
        while(st.hasMoreTokens())
        {
            sequence.add(st.nextToken().trim());
        }
        
        Object ob=null;
        //System.out.println("\n\n******************************");
        //System.out.println("Actual data:");
        //System.out.println(data);
        //System.out.println("seraching for: "+search);
        for(int i=0;i<sequence.size();i++)
        {
            if(!data.containsKey(sequence.get(i)))
            {
                return "Not found";
            }
            if(data.get(sequence.get(i)) instanceof HashMap)
            {
                data=(HashMap<String, Object>)data.get(sequence.get(i));
                ob=data;
            }
            else
            {
                ob=data.get(sequence.get(i));
            }
        }
        return ob;
    }
    
    static public String trimHtml(String htmltext)
    {
        while(htmltext.contains("<"))
        {
            htmltext=htmltext.replace(htmltext.substring(htmltext.indexOf("<"),htmltext.indexOf(">")+1),"");
        }
        return htmltext;
    }
}


class Memorymonitor extends JFrame
{

    JTextField total,used,free,max;
    
    public Memorymonitor() throws HeadlessException {
        setTitle("Memory Utilization");
        total=new JTextField();
        used=new JTextField();
        free=new JTextField();
        max=new JTextField();
        setLayout(new GridLayout(4, 2));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
                setVisible(false);
                dispose();
            }
        });
        setSize(new Dimension(500, 200));
        setVisible(true);
        add(new JLabel("Used Memory:"));
        add(used);
        add(new JLabel("Free Memory:"));
        add(free);
        add(new JLabel("Total Memory:"));
        add(total);
        add(new JLabel("Max Memory:"));
        add(max);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        getmemory();
    }
    
    void getmemory()
    {
        Runtime rt=Runtime.getRuntime();
        long free_mem=rt.freeMemory();
        long total_mem=rt.totalMemory();
        long max_mem=rt.maxMemory();
        rt.traceInstructions(true);
        rt.traceMethodCalls(true);
        
        used.setText((total_mem-free_mem)+"");
        free.setText(free_mem+"");
        total.setText(total_mem+"");
        max.setText(max_mem+"");
        
        final Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                getmemory();
                t.cancel();
                t.purge();
                System.gc();
            }
        }, 2000);
        
    }
}
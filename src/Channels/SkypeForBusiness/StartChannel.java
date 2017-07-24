/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Channels.SkypeForBusiness;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.json.XML;
import xmltojson.Xmltojson;
import xmltojson.http.RequestResponse;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class StartChannel implements Runnable{

    
    static HashMap<String,Object> config;
    HashMap<String, Object> data;
    HashMap<String, Object> sessions;
    Properties pro;
    StartChannel cur_ob;
    
    public StartChannel(HashMap<String,Object> config) {
        cur_ob=this;
        pro=new Properties();
        try
        {
            pro.load(new FileReader("app.properties"));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        data=new HashMap<String, Object>();
        sessions=new HashMap<>();
        this.config=config;
        new Thread(this).run();
    }

    @Override
    public void run() {
        if(testconnectiontoserver())
        {
            getToken();
            /*if(data.containsKey("access_token"))
            {
                getApplicationUrls();
                if(data.containsKey("resource"))
                {
                    
                    getappid();
                    //getApplicationUrls2();
                    makeMeAvailable();
                    data.put("events-url", "1");
                    System.out.println("Came inside");
                    getEventUrl();
                    System.out.println("Came Outside");
                    acceptMessageInvitations();
                    processIncomingmessages();
                }
            }*/
        }
        else
        {
            System.out.println("Unable to connect to the server");
        }
    }
    
    void acceptMessageInvitations()
    {
        if(this.data.containsKey("invitations"))
        {
            for(Map.Entry<String,HashMap<String,HashMap<String,String>>> record : ((HashMap<String,HashMap<String,HashMap<String,String>>>) this.data.get("invitations")).entrySet())
            {
                //System.out.println("**************\n\ndata forwarded to messaging invitation is \n"+record);
                String url=record.getKey();
                HashMap<String,String> headers=new HashMap<String, String>();
                headers.put("Authorization", this.data.get("access_token").toString());

                String method="POST";

                String parameters="some";

                HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
                for(int i : ob2.keySet())
                {
                    System.out.println(ob2);
                    if(!ob2.containsKey(10000))
                    {
                        HashMap<String,HashMap<String,String>> msg=record.getValue();
                        Set<String> ks=msg.keySet();
                        String ks_ls="";
                        for(String tt : ks)
                        {
                            ks_ls=tt;
                        }
                        
                        
                                                    if(this.data.containsKey("messages"))
                                                    {
                                                        HashMap<String,HashMap<String,String>> msg2=(HashMap<String,HashMap<String,String>>) this.data.get("messages");
                                                        
                                                        if(!msg2.containsKey(ks_ls))
                                                        {
                                                            msg.putAll(msg);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        this.data.put("messages",msg);
                                                    }
                        ((HashMap<String,HashMap<String,HashMap<String,String>>>) this.data.get("invitations")).remove(url);
                        System.gc();
                    }
                    else
                    {
                        getToken();
                        return;
                    }
                }
            }
        }
             Timer t=new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                   acceptMessageInvitations();
                                    t.cancel();
                                    t.purge();
                                    System.gc();
                                }
                            }, 5*1000);
                    
    }
    
    void processIncomingmessages()
    {
        if(this.data.containsKey("messages"))
        {
            try
            {
            for(Map.Entry<String,HashMap<String,String>> record : ((HashMap<String,HashMap<String,String>>) this.data.get("messages")).entrySet())
            {
                try
                {
                System.out.println("********************\n"+record+"*****************************");
                
                HashMap<String,String> params=record.getValue();
                
                
                HashMap<String,String> headers=new HashMap<>();
                String parameters=params.get("message");
                
                if(!sessions.keySet().contains(params.get("endpoint")))
                {
                    JSONObject ob=new JSONObject();
                    ob.put("source", "Skype for business");
                    ob.put("sesid", "unknown");
                    ob.put("message", parameters);
                    
                    parameters="source=Skype for business&sesid=unknown&message="+parameters.replace("\n", "");
                    HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, pro.getProperty("server.url"), "POST", parameters);
                    if(ob2.containsKey(200))
                    {
                        parameters=ob2.get(200).toString();
                        parameters=new String(ob2.get(200).toString().getBytes("UTF-8"));
                    }
                    sessions.put(params.get("endpoint"), new JSONObject(parameters));
                    System.out.println(((JSONObject)sessions.get(params.get("endpoint"))).toString(8));
                    parameters=new JSONObject(parameters).getString("message");
                }
                else
                {
                    JSONObject ob=new JSONObject();
                    ob.put("source", "Skype for business");
                    System.out.println(((JSONObject)sessions.get(params.get("endpoint"))).toString(8));
                    ob.put("sesid", ((JSONObject)sessions.get(params.get("endpoint"))).getString("sesid"));
                    ob.put("message", parameters);
                    
                    parameters="source=Skype for business&sesid="+((JSONObject)sessions.get(params.get("endpoint"))).getString("sesid")+"&message="+parameters.replace("\n", "");
                    headers.put("Cookie","JSESSIONID="+((JSONObject)sessions.get(params.get("endpoint"))).getString("sesid"));
                    HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, pro.getProperty("server.url"), "POST", parameters);
                    if(ob2.containsKey(200))
                    {
                        parameters=ob2.get(200).toString();
                    }
                    sessions.put(params.get("endpoint"), new JSONObject(parameters));
                    System.out.println(((JSONObject)sessions.get(params.get("endpoint"))).toString(8));
                    parameters=new JSONObject(parameters).getString("message");
                }
               
                headers=new HashMap<String, String>();
                if(params.get("message_type").toLowerCase().contains("html"))
                {
                    headers.put("content-type","text/html");
                    
                    //back slash consts
                    parameters=parameters.replace(" ", "&nbsp;");
                    parameters=parameters.replace("\n", "<br/>");
                    parameters=parameters.replace("\t", "<br/>");
                }
                else
                {
                    headers.put("content-type","text/plain");
                    
                    //back slash consts
                    parameters=parameters.replace("<br/>", "\n");
                    parameters=parameters.replace("<hr/>", "\n");
                    parameters=parameters.replace("&nbsp;", " ");
                }
                headers.put("Authorization", cur_ob.data.get("access_token").toString());
                
                
                String method="POST";
                
                System.out.println( params.get("endpoint"));
                HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, params.get("endpoint").toString() +"/messages", method, parameters);
                for(int i : ob2.keySet())
                {
                    System.out.println("From sending message: " + ob2);
                    if(i!=10000)
                    {
                        ((HashMap<String,HashMap<String,String>>) cur_ob.data.get("messages")).remove(record.getKey());
                    }
                }
                
                }
                catch(ConcurrentModificationException ex)
                {

                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                
            }
            }
            catch(ConcurrentModificationException ex)
            {
                
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
             Timer t=new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                   processIncomingmessages();
                                    t.cancel();
                                    t.purge();
                                    System.gc();
                                }
                            }, 1000);
                    
    }
    
    void getappid()
    {
        String temp=((HashMap<String,Object>) data.get("resource")).toString();
        int index=temp.indexOf("/applications/")+"/applications/".length();
        data.put("appid",temp.substring(index,temp.indexOf("/", index+1)));
    }
    
    void getApplicationUrls2()
    {
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/xml");
        headers.put("Authorization", this.data.get("access_token").toString());
        String url=ob.get("application").toString()+"/"+data.get("appid");
        String method="GET";
        
        
        String parameters=null;
        
        HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
        for(int i : ob2.keySet())
        {
            if(i==10000)
            {
                makeMeAvailable();
            }
            
            if(i==200 || i==201)
            {
                
                this.data.put("resource",XML.toJSONObject(ob2.get(200).toString()));
                System.out.println(((JSONObject)this.data.get("resource")).toString(5));
                //System.out.println(((JSONObject)this.data.get("resource")).toString(4));
            }
            else
            {
                System.out.println(ob2);
            }
        }
    }
    
    void getEventUrl()
    {
        System.out.println("Get Event Url");
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Authorization", this.data.get("access_token").toString());
        //headers.put("accept", "application/xml");
        
        String url=ob.get("application").toString()+"/"+data.get("appid")+"/events?ack="+data.get("events-url");
        System.out.println(url);
        String method="GET";
        
        
        String parameters=null;
        
        HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
        for(int i : ob2.keySet())
        {
            if(ob.containsKey(10000))
            {
                getToken();
                return;
            }
            
            if(i==401)
            {
                getToken();
                return;
            }
            //System.out.println(ob2);
            if(ob2.containsKey(200))
            {
                
                String seperator="\n";
                String search=null;
                if(ob2.get(200).toString().toLowerCase().contains("next"))
                {
                    search="_links"+seperator
                        + "_links"+seperator
                        + "next"+seperator
                        + "href";
                }
                if(ob2.get(200).toString().toLowerCase().contains("resync"))
                {
                    search="_links"+seperator
                        + "_links"+seperator
                        + "resync"+seperator
                        + "href";
                }

                if(search!=null)
                {
                    HashMap<String,Object> data=xmltojson.Xmltojson.parsejson("_links", new JSONObject(ob2.get(200).toString()));
                    search=(xmltojson.Xmltojson.penetrate(data, search, seperator)).toString();
                    String temp="applications/"+this.data.get("appid")+"/events?ack=";
                    search=search.substring(search.lastIndexOf(temp)+temp.length());
                    this.data.put("events-url", search);
                    if(data!=null)
                    {
                        getactiveinvitations(data);
                    }
                }
            }
            
            
            System.out.println("Timer is setting");
            Timer t=new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getEventUrl();
                            t.cancel();
                            t.purge();
                            System.gc();
                        }
                    }, 3000);
        }
    }
    
    void getactiveinvitations(HashMap<String,Object> data)
    {
        try
        {
            if(!data.containsKey("_links"))
            {
                return;
            }

            data=(HashMap < String, Object >) data.get("_links");
            HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");

            for(String keys: data.keySet())
            {
                if(keys.toLowerCase().contains("sender-"))
                {
                    HashMap<String,Object> temp=(HashMap<String,Object>) data.get(keys);

                    String seprator="\n";
                    String search="";
                    //System.out.println(temp);
                    if(temp.get("rel").toString().toLowerCase().contains("communication"))
                    {
                        for(String event : temp.keySet())
                        {
                            if(event.toLowerCase().startsWith("events-"))
                            {
                                search=event+seprator+"link"+seprator+"rel";
                                if(xmltojson.Xmltojson.penetrate(temp, search, seprator).toString().equalsIgnoreCase("messaginginvitation"))
                                {
                                    search=event+seprator+"_embedded"+seprator+"messagingInvitation";
                                    HashMap<String, Object> msg_info=(HashMap<String, Object>)Xmltojson.penetrate(temp, search, seprator);
                                    if(msg_info.get("state").toString().toLowerCase().contains("connecting"))
                                    {
                                        System.out.println("msg: info: "+msg_info+"\n**************\n");
                                        search="_links"+seprator+"accept"+seprator+"href";
                                        String accept_url=Xmltojson.penetrate(msg_info, search, seprator).toString();
                                        
                                        if(accept_url.equalsIgnoreCase("not found"))
                                        {
                                            search="_links"+seprator+"self"+seprator+"href";
                                            accept_url=Xmltojson.penetrate(msg_info, search, seprator).toString()+"/accept";
                                        }
                                        
                                        search="applications/"+this.data.get("appid")+"/";
                                        String key=ob.get("application").toString()+"/"+this.data.get("appid")+"/"+accept_url.substring(accept_url.lastIndexOf(search)+search.length());
                                        
                                        search="_links"+seprator+"message"+seprator+"href";
                                        String message_text=Xmltojson.penetrate(msg_info, search, seprator).toString();
                                        message_text=URLDecoder.decode(new String(message_text.substring(message_text.indexOf(",")+1).getBytes(),"UTF-8"));
                                        String msg_type="plain";
                                        
                                        search="_links"+seprator+"messaging"+seprator+"href";
                                        accept_url=Xmltojson.penetrate(msg_info, search, seprator).toString();
                                        search="applications/"+this.data.get("appid")+"/";
                                        String messaging_url=ob.get("application").toString()+"/"+this.data.get("appid")+"/"+accept_url.substring(accept_url.lastIndexOf(search)+search.length());
                                        
                                        search="_embedded"+seprator+"from"+seprator+"_links"+seprator+"contact"+seprator+"href";
                                        String sender=Xmltojson.penetrate(msg_info, search, seprator).toString();
                                        
                                                        HashMap<String,HashMap<String,String>> message=new HashMap<>();
                                                        
                                                        HashMap<String,String> msg_details=new HashMap<>();
                                                        msg_details.put("sender", sender);
                                                        msg_details.put("message", message_text);
                                                        msg_details.put("endpoint", messaging_url);
                                                        msg_details.put("message_type", msg_type);
                                                        message.put(messaging_url+"/messages/1", msg_details);
                                                        
                                                        
                                        HashMap<String,HashMap<String,HashMap<String,String>>> msg=new HashMap<>();
                                        msg.put(key, message);
                                        
                                        if(this.data.containsKey("invitations"))
                                        {
                                            HashMap<String,HashMap<String,HashMap<String,String>>> dp=(HashMap<String,HashMap<String,HashMap<String,String>>>) this.data.get("invitations");
                                            dp.putAll(msg);
                                        }
                                        else
                                        {
                                            this.data.put("invitations", msg);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(temp.get("rel").toString().toLowerCase().contains("conversation"))
                    {
                        for(String event : temp.keySet())
                        {
                            if(event.toLowerCase().startsWith("events-"))
                            {
                                search=event+seprator+"link"+seprator+"rel";
                                if(xmltojson.Xmltojson.penetrate(temp, search, seprator).toString().equalsIgnoreCase("message"))
                                {
                                    search=event+seprator+"_embedded"+seprator+"message"+seprator+"rel";
                                    Object dummy=xmltojson.Xmltojson.penetrate(temp, search, seprator);
                                    if(dummy instanceof String)
                                    {
                                        if(dummy.toString().equalsIgnoreCase("message"))
                                        {
                                            search=event+seprator+"_embedded"+seprator+"message"+seprator+"direction";
                                            dummy=xmltojson.Xmltojson.penetrate(temp, search, seprator);
                                            if(dummy instanceof String)
                                            {
                                                if(dummy.toString().equalsIgnoreCase("Incoming"))
                                                {
                                                    search=event+seprator+"_embedded"+seprator+"message";
                                                    HashMap<String, Object> msg_info=(HashMap<String, Object>)Xmltojson.penetrate(temp, search, seprator);
                                                    
                                                    search="_links"+seprator+"plainMessage"+seprator+"href";
                                                    Object tp=Xmltojson.penetrate(msg_info, search, seprator);
                                                    String message_text="";
                                                    String msg_type="plain";

                                                    if(tp instanceof String && (!tp.toString().equalsIgnoreCase("Not found")))
                                                    {
                                                        message_text=tp.toString();
                                                        message_text=URLDecoder.decode(new String(message_text.substring(message_text.indexOf(",")+1).getBytes(),"UTF-8"));
                                                    }
                                                    else
                                                    {
                                                        msg_type="html";
                                                        search="_links"+seprator+"htmlMessage"+seprator+"href";
                                                        tp=Xmltojson.penetrate(msg_info, search, seprator);
                                                        message_text=tp.toString();
                                                        message_text=URLDecoder.decode(new String(message_text.substring(message_text.indexOf(",")+1).getBytes(),"UTF-8"));
                                                        message_text=Xmltojson.trimHtml(message_text);
                                                    }

                                                    System.out.println("Message:"+message_text);
                                                    search="_links"+seprator+"self"+seprator+"href";
                                                    String key=Xmltojson.penetrate(msg_info, search, seprator).toString();

                                                    search="_links"+seprator+"contact"+seprator+"href";
                                                    String sender=Xmltojson.penetrate(msg_info, search, seprator).toString();

                                                    search="_links"+seprator+"messaging"+seprator+"href";
                                                    String accept_url=Xmltojson.penetrate(msg_info, search, seprator).toString();
                                                    search="applications/"+this.data.get("appid")+"/";
                                                    accept_url=ob.get("application").toString()+"/"+this.data.get("appid")+"/"+accept_url.substring(accept_url.lastIndexOf(search)+search.length());

                                                        HashMap<String,HashMap<String,String>> message=new HashMap<>();
                                                        HashMap<String,String> msg_details=new HashMap<>();
                                                        msg_details.put("sender", sender);
                                                        msg_details.put("message", message_text);
                                                        msg_details.put("endpoint", accept_url);
                                                        msg_details.put("message_type", msg_type);
                                                        message.put(key, msg_details);

                                                    if(this.data.containsKey("messages"))
                                                    {
                                                        HashMap<String,HashMap<String,String>> msg=(HashMap<String,HashMap<String,String>>) this.data.get("messages");
                                                        if(!msg.containsKey(key))
                                                        {
                                                            msg.putAll(message);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        this.data.put("messages",message);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        System.out.println("********\nelse called**********\n");
                        System.out.println(temp+"\n**********");
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
               
    }
    void makeMeAvailable()
    {
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/xml");
        headers.put("Authorization", this.data.get("access_token").toString());
        String url=ob.get("application").toString()+"/"+data.get("appid")+"/me/makeMeAvailable";
        String method="POST";
        
        
        String parameters="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
"<input xmlns=\"http://schemas.microsoft.com/rtc/2012/03/ucwa\">" +
"  <property name=\"audioPreference\">PhoneAudio</property>" +
"  <property name=\"phoneNumber\">1234567890</property>" +
"  <property name=\"signInAs\">Online</property>" +
"  <propertyList name=\"supportedMessageFormats\">" +
"    <item>Plain</item>" +
"    <item>Html</item>" +
"  </propertyList>" +
"  <propertyList name=\"supportedModalities\">" +
"    <item>Messaging</item>" +
"  </propertyList>" +
"</input>";
        
        HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
        for(int i : ob2.keySet())
        {
            System.out.println(ob2);
            if(i==10000)
            {
                makeMeAvailable();
            }
            
            if(i==401)
            {
                getToken();
                makeMeAvailable();
            }
            if(i==204 || i==409) //This is for success
            {
                
                reportMyActivity();
            }
        }
    }
    
    void reportMyActivity()
    {
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Authorization", this.data.get("access_token").toString());
        
        
        String url=ob.get("application").toString()+"/"+data.get("appid")+"/me/reportMyActivity";
        String method="POST";
        
        
        String parameters="something";
        
        HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
        for(int i : ob2.keySet())
        {
            if(i!=204)
            {
                makeMeAvailable();
            }
            else if(i==401)
            {
                getToken();
                makeMeAvailable();
            }
            else if(i==10000)
            {
                getToken();
                return;
            }
            else
            {
                 Timer t=new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            reportMyActivity();
                            t.cancel();
                            t.purge();
                            System.gc();
                        }
                    }, TimeUnit.MINUTES.toSeconds(2)*1000);
            }
        }
    }
    
    boolean testconnectiontoserver()
    {
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        //headers.put("Authorization", "Bearer cwt=AAEBHAEFAAAAAAAFFQAAABUk4xMYEWGJnC0K52Q5DACBEFQFBgF3q6ZQtVuiM4z_zbKCAjKGgyCi2x2bWhxF8ODvQJ_VpLne0Gzfa8ESsqRsDxgAIuKjQYYIS6ppUOmb1AgNELl2pwzmXnBZrfEKUmfL5-I");
        String url=ob.get("application").toString();
        String method="GET";
        String parameters=null;
        for(int i : new RequestResponse().requestHandler(headers, url, method, parameters).keySet())
        {
            if(i!=10000)
            {
                return true;
            }
        }
        return false;
    }
    
    void getApplicationUrls()
    {
        Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                getApplicationUrls();
                t.cancel();
                t.purge();
                System.gc();
            }
        }, 60000);
        
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/xml");
        headers.put("Authorization", this.data.get("access_token").toString());
        String url=ob.get("application").toString();
        String method="POST";
        
        
        String parameters="{" +
"  userAgent : \"Desktop\"," +
"  endpointId : \"9622886600\"," +
"  culture : \"en-us\"" +
"}";
        HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
        for(int i : ob2.keySet())
        {
            if(i==10000)
            {
                getToken();
                return;
            }
            
            if(i==200)
            {
                this.data.putAll(xmltojson.Xmltojson.parsejson("resource",XML.toJSONObject(ob2.get(200).toString())));
                getappid();
                this.data.put("events-url", 1+"");
            }
            else if(i==201)
            {
                this.data.putAll(xmltojson.Xmltojson.parsejson("resource",XML.toJSONObject(ob2.get(201).toString())));
                getappid();
                this.data.put("events-url", 1+"");
            }
            else if(i==401)
            {
                getToken();
                return;
            }
            else
            {
                System.out.println(ob2);
            }
        }
    }
    
    void getToken()
    {
        HashMap<String,Object> ob=(HashMap<String,Object>) config.get("urls");
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        //headers.put("Authorization", "Bearer cwt=AAEBHAEFAAAAAAAFFQAAABUk4xMYEWGJnC0K52Q5DACBEFQFBgF3q6ZQtVuiM4z_zbKCAjKGgyCi2x2bWhxF8ODvQJ_VpLne0Gzfa8ESsqRsDxgAIuKjQYYIS6ppUOmb1AgNELl2pwzmXnBZrfEKUmfL5-I");
        String url=ob.get("authentication").toString();
        String method="GET";
        
        ob=(HashMap<String,Object>) config.get("credentials");
        String parameters="grant_type=password&username="+ob.get("username")+"&password="+ob.get("password");
        HashMap<Integer,Object> ob2=new RequestResponse().requestHandler(headers, url, method, parameters);
        for(int i : ob2.keySet())
        {
            if(i==10000)
            {
                //getToken();
            }
            
            if(i==200)
            {
                
                Type type=new TypeToken<LinkedTreeMap<String,String>>(){}.getType();
                LinkedTreeMap<String,String> map =new Gson().fromJson(ob2.get(200).toString(),type);
                this.data.put("access_token", "Bearer "+map.get("access_token"));
                System.out.println("Bearer "+map.get("access_token"));
                if(data.containsKey("access_token"))
                {
                    getApplicationUrls();
                    if(data.containsKey("resource"))
                    {

                        getappid();
                        //getApplicationUrls2();
                        makeMeAvailable();
                        data.put("events-url", "1");
                        System.out.println("Came inside");
                        getEventUrl();
                        System.out.println("Came Outside");
                        acceptMessageInvitations();
                        processIncomingmessages();
                    }
                }
                return;
            }
            else
            {
                System.out.println(ob2);
            }
        }
        
//        if(this.data.containsKey("access_token"))
//        {
//            Timer t=new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    getToken();
//                    t.cancel();
//                    t.purge();
//                    System.gc();
//                }
//            }, TimeUnit.HOURS.toSeconds(7)*1000);
//        }
//        else
        {
            Timer t=new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    getToken();
                    t.cancel();
                    t.purge();
                    System.gc();
                }
            },1000);
        }
    }
}
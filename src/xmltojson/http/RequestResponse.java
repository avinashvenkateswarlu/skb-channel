/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xmltojson.http;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class RequestResponse {
 
    public static void main(String args[])
    {
        HashMap<String,String> headers=new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Authorization", "Bearer cwt=AAEBHAEFAAAAAAAFFQAAABUk4xMYEWGJnC0K52Q5DACBEFQFBgF3q6ZQtVuiM4z_zbKCAjKGgyCi2x2bWhxF8ODvQJ_VpLne0Gzfa8ESsqRsDxgAIuKjQYYIS6ppUOmb1AgNELl2pwzmXnBZrfEKUmfL5-I");
        String url="https://ndalyncweb.hcl.com/Autodiscover/AutodiscoverService.svc/root/oauth/user?originalDomain=hcl.com";
        String method="GET";
        String parameters=null;
        //parameters=URLEncoder.encode(parameters);
        System.out.println(new RequestResponse().requestHandler(headers, url, method, parameters));
    }

    public HashMap<Integer,Object> requestHandler(HashMap<String,String> headers,String url,String method,String parameters) {
        //System.out.println("Url: " + url + "\nparameters: "+parameters+"\nMethod: "+method+"\nHeaders: "+headers);
        try
        {
            HttpURLConnection con=(HttpURLConnection) new URL(url).openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod(method);
            
            for(Map.Entry<String,String> header : headers.entrySet())
            {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
            
            if(parameters!=null)
            {
                con.getOutputStream().write(parameters.getBytes());
            }
            
            for(Map.Entry<String,List<String>> entry : con.getHeaderFields().entrySet())
            {
                //System.out.println(entry.getKey()+"\t:\t"+entry.getValue());
            }
            
            
            Scanner sc=null;
            if(con.getErrorStream()!=null)
            {
                String temp="";
                sc=new Scanner(con.getErrorStream());
                while(sc.hasNext())
                {
                    temp=temp+sc.nextLine();
                }
                HashMap<Integer,Object> res=new HashMap<Integer,Object>();
                res.put(con.getResponseCode(),temp);
                return res;
            }
            
            
            if(sc==null)
            {
                String response="";
                sc=new Scanner(con.getInputStream());
                while(sc.hasNext())
                {
                    response=response+sc.nextLine();
                }
                HashMap<Integer,Object> res=new HashMap<Integer,Object>();
                res.put(con.getResponseCode(),response);
                return res;
            }
            
            //This lines will never be called
            HashMap<Integer,Object> res=new HashMap<Integer,Object>();
            res.put(con.getResponseCode(),"");
            return res;
            
        }
        catch(Exception ex)
        {
            //ex.printStackTrace();
            HashMap<Integer,Object> res=new HashMap<Integer,Object>();
            res.put(10000,ex.toString());
            return res;
        }
    }
}

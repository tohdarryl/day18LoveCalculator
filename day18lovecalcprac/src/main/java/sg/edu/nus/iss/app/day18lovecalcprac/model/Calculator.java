package sg.edu.nus.iss.app.day18lovecalcprac.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Random;

import org.springframework.http.HttpStatusCode;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Calculator implements Serializable{
    private String fname;
    private String sname;
    private String percentage;
    private String result;
    private String Id;

    public Calculator(){
        this.Id = generateID(8);
    }

    public String getFname() {
        return fname;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }
    public String getSname() {
        return sname;
    }
    public void setSname(String sname) {
        this.sname = sname;
    }
    public String getPercentage() {
        return percentage;
    }
    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    
    public static Calculator create(String json) throws IOException{
        //Instantiate an object from this Calc class
        Calculator c = new Calculator();
        //Put inputStream inside try()
        try( InputStream is = new ByteArrayInputStream(json.getBytes())){
            //Using JsonReader to read input as an Object
            JsonReader r = Json.createReader(is);
            JsonObject o = r.readObject();
            // remove endoing chars from API
            // String person1Name = URLDecoder.decode(o.getString(fname, UTF-8))
            String person1Name = URLDecoder.decode(o.getString("fname"), "UTF-8");
            String person2Name = URLDecoder.decode(o.getString("sname"), "UTF-8");
            
            c.setSname(o.getString(person2Name));
            c.setFname(o.getString(person1Name));
            c.setPercentage(o.getString("percentage"));
            c.setResult(o.getString("result"));
            if(Integer.parseInt(o.getString("percentage")) >= 75){
                    c.setResult("Compatible");
            }else if(Integer.parseInt(o.getString("percentage")) < 75 && Integer.parseInt(o.getString("percentage")) > 1){
                    c.setResult("Non-Compatible");
            }else if(Integer.parseInt(o.getString("percentage")) == 0){
                    c.setResult(HttpStatusCode.valueOf(418).toString());
            }
            

        }
        return c;
    }

    private synchronized String generateID(int numOfChar){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while(sb.length() < numOfChar){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0,numOfChar);
    }
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }



}

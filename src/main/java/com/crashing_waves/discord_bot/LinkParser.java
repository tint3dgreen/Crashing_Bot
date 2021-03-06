package com.crashing_waves.discord_bot;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;


public class LinkParser {

    //Takes in a .json file and returns a usable JSONObject
    private JSONObject fileToJSON(File file){
        try {
            InputStream is = new FileInputStream(file);
            String jsonTxt = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(jsonTxt);
            return json;
        }catch (IOException | JSONException e){
            e.printStackTrace();
            return  new JSONObject();
        }
    }

    //Takes a valid link to a tik tok video and returns a link to its location in local storage
    private File tikTokParse(String url){
        //run the python command to get the url
        String command = "python ./src/main/python/TTAPI.py";
        try{
            Process p = Runtime.getRuntime().exec(command +" \"" +url + "\"");
            p.waitFor();
            return new File("Media/video.mp4");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new File("src/main/java/rescources/error.png");
        }
    }

    //This method's job is to decide which URL parser will recieve and parse the URL
    public MessageBuilder handleLink(String url){
         String Tik_tok = ("https://[a-z]*.tiktok.com[^\s]*");

         if (Pattern.matches(Tik_tok,url)){
             MessageBuilder message = new MessageBuilder().addAttachment(tikTokParse(url));
             try{
                 //I may want to move these to a separate method later
                 File file = new File("./Media/Attributes.json");
                 JSONObject json = fileToJSON(file);
                 String Title = json.getString("Title");
                 String Author = json.getString("Author");
                 String Pfp = json.getString("pfp");
                 EmbedBuilder embed = new EmbedBuilder()
                         .setAuthor(Author,url,Pfp)
                         .setDescription(Title)
                         .setFooter(json.getString("soundTitle"),json.getString("soundThumb"));
                 return message.addEmbed(embed);
             } catch(JSONException e ){
                 e.printStackTrace();
             }
             return message;
         }
         return new MessageBuilder().setContent("Oops, Im a bad bot, I can't Use that link yet");

    }

    //Checks if the URL is from Tik Tok returns Boolean
    public boolean isTikTokURL(String url){
        return Pattern.matches("https://[a-z]*.tiktok.com[^\s]*",url);
    }
}

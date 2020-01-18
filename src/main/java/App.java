//package com.codetriage.scraper;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class App {

    private static String[] videos = {"https://www.youtube.com/user/TrU3Ta1ent/videos",
            "https://www.youtube.com/channel/UCboMX_UNgaPBsUOIgasn3-Q/videos",
            "https://www.youtube.com/user/AchievementHunter/videos",
            "https://www.youtube.com/user/LetsPlay/videos", "https://www.youtube.com/user/quill18/videos",
            "https://www.youtube.com/user/B1GnBr0wN/videos"};



    public static void main(String[] args) {
        try {
            String[] videos = getYoutubeArray();

            int i = 0;
            String yesterday = getYesterday();
            for(String video: videos){
                List<String> videosList = getYoutubeVideos(video, yesterday);
                printVideos(videosList);
            }
            System.out.println("_________________");

            // With the document fetched, we use JSoup's title() method to fetch the title

            // In case of any IO errors, we want the messages written to the console
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getYoutubeVideos(String url, String yesterday) throws IOException{
        Document doc = Jsoup.connect(url).get();
        System.out.printf("Title: %s\n", doc.title());

        Elements repositories = doc.select("h3.yt-lockup-title a");
        String dateString = "";
        List<String> videos = new ArrayList<>();
        for(Element repository : repositories ){
            String hyperLink = "https://www.youtube.com/" + repository.attr("href");
//            String hyperLink = "https://www.youtube.com/watch?v=pYTjCwFDtAo&t";

            String urlLink = repository.text();
            Document videoDoc = Jsoup.connect(hyperLink).get();
            Elements date = videoDoc.select("script");
            for(Element element : date ) {
                if (element.data().contains("dateText")) {
                    dateString = getVideoDate(element);
                }
            }
            if(dateString.equals(yesterday)){
                String link = "=HYPERLINK(\"" + hyperLink + "\";\"" +  urlLink  + "\")" + "\n";
                videos.add(link);
            }else if (!dateString.equals("")){
                String videoDateString = (dateString.split(" ")[1]);
                int videoDate =  Integer.parseInt(videoDateString.substring(0, videoDateString.length() - 1));
                String yesterDateString = (yesterday.split(" ")[1]);
                int yesterDate =  Integer.parseInt(yesterDateString.substring(0, yesterDateString.length() - 1));
                if(videoDate<yesterDate){
                    return videos;
                }
            }
        }
        return videos;
    }

    private static String getVideoDate(Element element) {
        Pattern pattern = Pattern.compile("dateText[^}]*");
        Matcher matcher = pattern.matcher(element.data());
        String stream = "Streamed";
        if (matcher.find()) {
            try{
                if ( matcher.group().toLowerCase().contains(stream.toLowerCase()) ) {
                    return matcher.group().split("\"")[4].substring(17, 28);
                }else{
                    return matcher.group().split("\"")[4].substring(13, 25);
                }
            }
            catch(StringIndexOutOfBoundsException e){
                return matcher.group().split("\"")[4];
            }
        }
        return "";
    }

    private static void printVideos(List<String> videos){
        for(int j=videos.size() - 1; j>=0; j--){
            System.out.print(videos.get(j));
        }
    }

    private static String[] getYoutubeArray(){
        return videos;
    }

    private static String getYesterday(){
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        Date yesterday = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(yesterday);
        cal.add(Calendar.DATE, -1); //minus number would decrement the days
        return formatter.format(cal.getTime());
    }


}
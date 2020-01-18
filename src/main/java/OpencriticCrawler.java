import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpencriticCrawler {
    static String[] url = {"https://opencritic.com/browse/ps4/last90", "https://opencritic.com/browse/pc/last90"};
//    static String[] url = {"https://opencritic.com/browse/ps4", "https://opencritic.com/browse/pc"};
    static String month = "Dec";


    private static String[] getUrl() {
        return url;
    }

    private static String getMonth() {
        return month;
    }


    public static void main(String[] args) {
        try {
            String[] urls = getUrl();
            int index = 0;
            for(String url : urls){
                Document doc = Jsoup.connect(url).get();
                System.out.printf("Title: %s\n", doc.title());
                getVideoGames(url, 1, index);
                System.out.println("_________________");
                index += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getVideoGames(String url, int page, int index) throws IOException{
        try{
            Document doc = Jsoup.connect(url).get();
            Elements repositories = doc.select("div.game-row");
            String month = getMonth();
            for(Element repository : repositories ){
                Elements date = repository.select("div.first-release-date");
                Elements score = repository.select("div.score");
                Elements name = repository.select("div.game-name");
                if(Integer.parseInt(score.get(0).text()) < 75){
                    return;
                }
                if(month.equals("")){
                    System.out.print(name.get(0).text() + ": ");
                    System.out.println(date.get(0).text() + " - " + score.get(0).text() );
                }else if (date.get(0).text().contains(month)){
                    System.out.print(name.get(0).text() + ": ");
                    System.out.println(date.get(0).text() + " - " + score.get(0).text() );
                }
            }
        }catch (IndexOutOfBoundsException e){
            page += 1;
            String next_page = getUrl()[index];
            next_page = next_page +"?page=" + page;
            System.out.println(next_page);
            getVideoGames(next_page, page, index);
        }
    }
}

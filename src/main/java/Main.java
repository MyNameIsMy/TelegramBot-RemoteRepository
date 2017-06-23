import com.google.gson.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.send.SendVideo;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

class MyBot extends TelegramLongPollingBot {

    private OkHttpClient client;

    MyBot(){
        client = new OkHttpClient();
    }

    public String getBotUsername() {
        return "DyadyaPyosBot";
    }

    public String getBotToken() {
        return "353067862:AAEKgES_PsnQSrd9zRHxKFeKsjz4FaeuYF8";
    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            try {
                answerMaker(update, update.getMessage().getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ////Вспомогательный метод отправляющий просто строку.
    void sendTextAnswer(Update update, String answer){
        SendMessage sendMessage = new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText(answer);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Вспомогательный метод отправляющий изображение из интернета с помощью ПРЯМОЙ ссылки на его.
    void sendImageAnswer(Update update, String url) {
        SendPhoto sendPhotoRequest = new SendPhoto()
                .setChatId(update.getMessage().getChatId())
                .setPhoto(url);
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Вспомогательный метод отправляющий видео из интернета с помощью ПРЯМОЙ ссылки на его.
    void sendVideoAnswer(Update update, String url){
        SendVideo sendVideo = new SendVideo()
                .setChatId(update.getMessage().getChatId())
                .setVideo(url);
        try {
            sendVideo(sendVideo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //Метод изпользует вспомогательные методы отправки разных видов ответа,
    private void answerMaker(Update update, String message) throws IOException {
        if (notificationOfAMention(message)){
            String searchAPIKey = "AIzaSyD4nKNmqlgMRjiLdmXskRvtJWfga37X-zw";
            String cx = "007023760257076395866:-048rw3b34m";
            String input = run("https://www.googleapis.com/customsearch/v1?searchType=image&imgSize=xlarge&alt=json&num=10&start=1" + "&key=" + searchAPIKey + "&q=мопс+дядя+пес" + "&cx=" + cx);
            JsonParser parser = new JsonParser();
            JsonObject mainObject = parser.parse(input).getAsJsonObject();
            JsonArray pItem = mainObject.getAsJsonArray("items");
            ArrayList<String> arrayList = new ArrayList<String>();
            Random random = new Random();
            for (JsonElement user : pItem) {
                JsonObject userObject = user.getAsJsonObject();
                arrayList.add(userObject.get("link").getAsString());
            }
            sendImageAnswer(update, arrayList.get(random.nextInt(arrayList.size())));
        }
    }

    //Метод проверяет на наличие слова "мопс" в сообщении
    boolean notificationOfAMention(String str){
        char[] charArray = str.toLowerCase().toCharArray();
        for (int i = 0; i < charArray.length; i++){
            if (charArray[i] == 'м'){
                int i1 = i + 1;
                if (charArray[i1] == 'о'){
                    i1 += 1;
                    if (charArray[i1] == 'п'){
                        i1 += 1;
                        if (charArray[i1] == 'с'){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Bot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
//        System.out.println(update.getMessage().getText());// TODO
//        System.out.println(update.getMessage().getFrom().getFirstName());// TODO

        Random rand = new Random();
        SendMessage response = new SendMessage();
        String command = update.getMessage().getText();
        if (command.equals("/wisdom")) {
            String message = messages.get(rand.nextInt(messages.size()));

            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);
            try {
                execute(response);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (command.equals("/cat")) {
            String imageUrl = "https://cataas.com/cat";
            InputFile inputFile = null;
            try {
                URL url = new URL(imageUrl);
                BufferedImage image = ImageIO.read(url);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                inputFile = new InputFile(inputStream, "cat.jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Create a new SendPhoto object with the cat picture
            SendPhoto photo = new SendPhoto();
            photo.setChatId(update.getMessage().getChatId().toString());
            photo.setPhoto(inputFile);

            try {
                execute(photo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else if(command.equalsIgnoreCase("/cursed"))
        {
          response.setText("please input your text (it will take a few seconds to process it)");
          response.setChatId(update.getMessage().getChatId().toString());

            try {
                execute(response);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            Thread inputThread = new Thread(() -> {
                long startTime = System.currentTimeMillis();
                long timeLimit = 5000; // Maximum time to wait for user input (in milliseconds)

                while (System.currentTimeMillis() - startTime < timeLimit) {
                    try {
                        Thread.sleep(1000); // Wait for 1 second before checking for new updates again
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    GetUpdates updatesRequest = new GetUpdates();
                    updatesRequest.setOffset(update.getUpdateId() + 1);

                    List<Update> updates;
                    try {
                        updates = execute(updatesRequest);
                    } catch (TelegramApiException e) {
                        response.setText("an unknown error occured, try again");
                        response.setChatId(update.getMessage().getChatId().toString());
                        try {
                            execute(response);
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(ex);
                        }
                        throw new RuntimeException(e);
                    }

                    if (!updates.isEmpty()) {
                        Update latestUpdate = updates.get(0);
                        Message updatedMessage = latestUpdate.getMessage();

                        if (updatedMessage != null && !updatedMessage.getText().isEmpty() && !updatedMessage.getText().equals("/cursed")) {
                            String resp = updatedMessage.getText();

                            // Process the user's input (e.g., apply curseText() method)
                            String processedText = curseText(resp);

                            // Create a new response message with the processed text
                            SendMessage processedResponse = new SendMessage();
                            processedResponse.setText(processedText);
                            processedResponse.setChatId(update.getMessage().getChatId().toString());

                            try {
                                execute(processedResponse);
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }

                            // Exit the loop if a valid update is processed
                            break;
                        }
                    }
                }
            });

            inputThread.start();

        }


    }

    private static String curseText(String text) {
        StringBuilder cursedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            String combiningCharacter = getCombiningCharacter();
            cursedText.append(c).append(combiningCharacter);
        }

        return cursedText.toString();
    }

    private static String getCombiningCharacter() {
        // Define an array of combining characters
        String[] combiningCharacters = {
                "\u0300", "\u0301", "\u0302", "\u0303", "\u0304",
                "\u0305", "\u0306", "\u0307", "\u0308", "\u0309",
                "\u030A", "\u030B", "\u030C", "\u030D", "\u030E",
                "\u0328", "\u0329", "\u032A", "\u032B", "\u032C",
                "\u032D", "\u032E", "\u032F", "\u0330", "\u0331",
                "\u0332", "\u0333", "\u0334", "\u0335", "\u0336",
                "\u0337", "\u0338", "\u0339", "\u033A", "\u033B",
                "\u033C", "\u033D", "\u033E", "\u033F", "\u0340",
                "\u0341", "\u0342", "\u0343", "\u0344", "\u0345",
                // Add more combining characters as desired
        };

        // Generate a random index to select a combining character from the array
        int randomIndex = (int) (Math.random() * combiningCharacters.length);

        return combiningCharacters[randomIndex];
    }




    @Override
    public String getBotUsername() {

        return "shibboleth_bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "6064189021:AAHXHN5w5XQXXlaohAuUeHFpPhwDujDeeRw";
    }


    static String filePath = "C:\\ACS_JavaProgramming1\\web\\Duce\\src\\main\\resources\\messages.html";
    static ArrayList<String> messages = new ArrayList<>();


    static File input = new File(filePath);
    static Document doc = null;

    static {
        try {
            doc = Jsoup.parse(input, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    static Elements messageElements = doc.select("div.text");

    static {
        for (Element element : messageElements) {
            String message = element.text();
            messages.add(message);

        }
    }
    static{
        for(int i = 0; i<messages.size(); i++)
        {
            System.out.println(messages.get(i));
            System.out.println();
        }
    }



}









package spec.mavenproject2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import spec.Currency;
import spec.CurrencyConversionService;
import spec.CurrencyModeService;
import spec.NbrbCurrencyConversionService;

public class Mavenproject2 extends TelegramLongPollingBot{
    
    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();
    
     @Override
    public String getBotToken() {
        return "5481987693:AAFD1ctjiTXv3xop0zRz7ja9EIuVl2zKeQo";
    }
    @Override
    public String getBotUsername() {
        return "@KorkiTest_bot";    } 
    
    public static void main(String[] args) throws TelegramApiException{
        Mavenproject2 bot= new Mavenproject2();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
        
        
    }

    @Override
    
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()){
            try {
                handleCallback(update.getCallbackQuery());
            } catch (TelegramApiException ex) {
                Logger.getLogger(Mavenproject2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (update.hasMessage()){
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException ex) {
                Logger.getLogger(Mavenproject2.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }
   
    private String getCurButton(Currency saved, Currency cur){
     return saved == cur ? cur + "✔️️" : cur.name();
    }
    
    private void handleMessage(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities() ){
            Optional<MessageEntity> commandEntity =  message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()){
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command){
                    case "/changer" -> { 
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency =  currencyModeService.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
                        for (Currency currency : Currency.values()) {
                            buttons.add(
                                    Arrays.asList(
                                            InlineKeyboardButton.builder()
                                                    .text(getCurButton(originalCurrency, currency))
                                                    .callbackData("ORIGINAL:"+ currency)
                                                    .build(),
                                            InlineKeyboardButton.builder()
                                                    .text(getCurButton(targetCurrency, currency))
                                                    .callbackData("TARGET:"+ currency)
                                                    .build()));
                        }
                        
                        
                        execute(
                                SendMessage.builder()
                                        .text("Выберите валюту из которой конвертировать в какую. И отправьте сумму для конвертации:")
                                        .chatId(message.getChatId().toString())
                                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                        .build());
                    }
                }
                return;
            }
        }
        if (message.hasText()){
            String messageText = message.getText();
            Optional<Double> value = parseDouble(messageText);
            Currency orig = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency target = currencyModeService.getTargetCurrency(message.getChatId());
            double ratio = currencyConversionService.getConversionRatio(orig, target);
            if (value.isPresent()){
                execute(
                        SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text(String.format("%4.2f %s is %4.2f %s", value.get(), orig, (value.get()*ratio), target))
                                .build());
                return;
            }
        
             
        }
        
        
    
    }
    private Optional<Double> parseDouble(String messageText){
        
            return Optional.of(Double.parseDouble(messageText));
               
        
    }
    
    private void handleCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCur = Currency.valueOf(param[1]);
        switch (action){
            case "ORIGINAL" -> currencyModeService.setOriginalCurrency(message.getChatId(), newCur); 
            case "TARGET" -> currencyModeService.setTargetCurrency(message.getChatId(), newCur);
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency =  currencyModeService.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
                        for (Currency currency : Currency.values()) {
                            buttons.add(
                                Arrays.asList(
                                    InlineKeyboardButton.builder()
                                            .text(getCurButton(originalCurrency, currency))
                                            .callbackData("ORIGINAL:"+ currency)
                                            .build(),
                                    InlineKeyboardButton.builder()
                                            .text(getCurButton(targetCurrency, currency))
                                            .callbackData("TARGET:"+ currency)
                                            .build()));
                        }
        try {
            execute(EditMessageReplyMarkup.builder()
                    .chatId(message.getChatId().toString())
                    .messageId(message.getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                    .build());
        } catch (TelegramApiException telegramApiException) {
        }
    }

   

   
}

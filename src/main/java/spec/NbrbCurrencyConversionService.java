/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class NbrbCurrencyConversionService implements CurrencyConversionService {
    static JSONObject json;

    public static JSONObject getJson() {
        return json;
    }
    

  @Override
  public double getConversionRatio(Currency original, Currency target) {
    double originalRate = getRate(original);
    double targetRate = getRate(target);
    return originalRate / targetRate;
  }

  
  private double getRate(Currency currency) {
        try {
            if (currency.getName().equals("RUB")){
                return 1;
            }
            URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            StringBuilder response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            JSONObject obj = new JSONObject(response.toString());
            JSONObject valute = obj.getJSONObject("Valute");
            JSONObject val = valute.getJSONObject(currency.getName());
            
            double scale = val.getDouble("Nominal");
            double rate = val.getDouble("Value");
            
            return rate / scale;
        } catch (MalformedURLException ex) {
            Logger.getLogger(NbrbCurrencyConversionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(NbrbCurrencyConversionService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NbrbCurrencyConversionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
  }


}

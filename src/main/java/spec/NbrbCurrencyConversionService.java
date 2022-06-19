/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spec;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.SneakyThrows;
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

  @SneakyThrows
  private double getRate(Currency currency) {
    if (currency.getName().equals("RUB")){
        return 1;
    }
    URL url = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuilder response = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
             response.append(inputLine);
    }
    in.close();
    JSONObject obj = new JSONObject(response.toString());
        JSONObject valute = obj.getJSONObject("Valute");
        JSONObject val = valute.getJSONObject(currency.getName());
        
        double scale = val.getDouble("Nominal");
        double rate = val.getDouble("Value");
    
    return rate / scale;
  }


}

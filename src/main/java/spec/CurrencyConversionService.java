/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spec;

public interface CurrencyConversionService {

  static CurrencyConversionService getInstance() {
    return new NbrbCurrencyConversionService();
  }

  double getConversionRatio(Currency original, Currency target);
}
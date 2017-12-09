package dz.ava.resources;

import dz.ava.bean.CurrencyConversionBean;
import dz.ava.feign.CurrencyExchangeServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionResource {


    @Autowired
    private CurrencyExchangeServiceProxy currencyExchangeServiceProxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable String from,
                                                  @PathVariable String to,
                                                  @PathVariable BigDecimal quantity) {

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<CurrencyConversionBean> responseEntity = restTemplate.getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversionBean.class,
                uriVariables);

        CurrencyConversionBean currencyConversionBean = responseEntity.getBody();

        return new CurrencyConversionBean(currencyConversionBean.getId(),
                from, to,
                currencyConversionBean.getConversionMultiple(), quantity,
                quantity.multiply(currencyConversionBean.getConversionMultiple()),
                currencyConversionBean.getPort());
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from,
                                                  @PathVariable String to,
                                                  @PathVariable BigDecimal quantity) {

        CurrencyConversionBean currencyConversionBean = currencyExchangeServiceProxy.retrieveExchangeValue(from, to);

        return new CurrencyConversionBean(currencyConversionBean.getId(),
                from, to,
                currencyConversionBean.getConversionMultiple(), quantity,
                quantity.multiply(currencyConversionBean.getConversionMultiple()),
                currencyConversionBean.getPort());
    }
}

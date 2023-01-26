package sg.edu.nus.iss.app.day18lovecalc.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.edu.nus.iss.app.day18lovecalc.model.Calculator;

@Qualifier("LoveCalcService")
@Service
public class LoveCalcService {
    // business logic that communicates with external API
    private static final String OPEN_LOVECALC_URL = "https://love-calculator.p.rapidapi.com/getPercentage";

    //redis
    private static final String CALC_ENTITY = "calculatorlist";
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    //To add save method after getting response body back
    public void save(final Calculator calc){
        redisTemplate.opsForList()
            .leftPush(CALC_ENTITY, calc.getId());
        redisTemplate.opsForHash()
            .put( CALC_ENTITY+ "_Map", calc.getId(), calc);
    }
    
    public List<Calculator> findAll(){
        List<Object> fromCalculatorList = redisTemplate.opsForList()
            .range(CALC_ENTITY, 0, -1);
        List<Calculator> calcs = redisTemplate.opsForHash()
            .multiGet(CALC_ENTITY+ "_Map", fromCalculatorList)
            .stream()
            .filter(Calculator.class::isInstance)
            .map(Calculator.class::cast)
            .toList();
        
        return calcs;
    }

    //Url builder for Request and Response
    public Optional<Calculator> getResult(String sname, String fname) throws IOException, InterruptedException {
        // export OPEN_LOVECALC_URL_API_KEY=""
        // String apiKey = System.getenv("OPEN_LOVECALC_URL_API_KEY");
        // finalise URL: OPEN_LOVECALC_URL?fname=John&sname=Kelly&appId=""; to get
        // response
        String loveCalcUrl = UriComponentsBuilder
                .fromUriString(OPEN_LOVECALC_URL)
                .queryParam("sname", sname)
                .queryParam("fname", fname)
                .toUriString();

        System.out.println(loveCalcUrl);

        // HttpRequest request = HttpRequest.newBuilder()
        //         .uri(URI.create(
        //                 loveCalcUrl))
        //         .header("X-RapidAPI-Key", apiKey)
        //         .header("X-RapidAPI-Host", "love-calculator.p.rapidapi.com")
        //         .method("GET", HttpRequest.BodyPublishers.noBody())
        //         .build();

        // HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        // System.out.println(response.body());

        // .getForEntity: retrieves resources from the given URI/URL templates or Fetch data on the basis of key properties (sent as path variables)

        // resp = template.getForEntity(loveCalcUrl, String.class);
        
        // Calculator c = Calculator.create(response.body());
        

        // RestTemplate is a synchronous client to perform HTTP requests
        
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = null;
        HttpHeaders headers = new HttpHeaders();
        String loverApiKey = System.getenv("LOVER_API_KEY");
        String loverApiHost = System.getenv("LOVER_API_HOST");

        // Headers are for validation

        headers.set("X-RapidAPI-Key", loverApiKey);
        headers.set("X-RapidAPI-Host", loverApiHost);
        //Represents a HTTP response or request entity, consisting of headers and body
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        //template.exchange is to submit a request to get a Response
        resp = template.exchange(loveCalcUrl, HttpMethod.GET,
                requestEntity, String.class);
        System.out.println(resp);
       
        Calculator c = Calculator.create(resp.getBody());
       
        if (c != null){
            save(c);
            return Optional.of(c);
        }

        return Optional.empty();

    }
}

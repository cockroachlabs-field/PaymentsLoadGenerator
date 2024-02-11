package com.cockroachlabs.field.paymentsdemo.PaymentsLoadGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.Year;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoadGenerationService {

    private final int maxRequests;
    private final int numberOfThreads;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom rnd = new SecureRandom();
    private final ObjectMapper objectMapper;

    private AtomicInteger counterApproved = new AtomicInteger(0);
    private AtomicInteger counterDeclined = new AtomicInteger(0);
    private AtomicInteger counterError = new AtomicInteger(0);


    private URI paymentsGatewayUri;

    public LoadGenerationService(@Value("${payment_demo.load_gen.number_of_threads}") int numberOfThreads,
                                 @Value("${payment_demo.load_gen.number_of_transactions}") int maxRequests,
                                 @Value("${payment_demo.gateway_url}") String paymentsGatewayUriString) {
        this.numberOfThreads = numberOfThreads;
        this.maxRequests = maxRequests;

        if (paymentsGatewayUriString == null || paymentsGatewayUriString.length() == 0) {
            throw new IllegalArgumentException("The payments gateway URI is a required setting.");
        }

        try {
            paymentsGatewayUri = URI.create(paymentsGatewayUriString);
        } catch (Exception ex) {
            throw new IllegalArgumentException("The payments gateway URI must be a valid URI value.");
        }

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    public void Run() {

        System.out.println("Starting with parameters: threads=" + this.numberOfThreads + "; totalRequests=" + this.maxRequests + "; gatewayUri=" + this.paymentsGatewayUri.toString());

        AtomicInteger counter = new AtomicInteger(0);

        ExecutorService executorService = null;
        try {
            executorService = Executors.newFixedThreadPool(numberOfThreads);
            while(counter.get() < maxRequests) {
                Future handler = executorService.submit(() -> {
                    try {
                        HttpRequest postRequest = HttpRequest.newBuilder()
                                .uri(paymentsGatewayUri)
                                .timeout(Duration.ofSeconds(5))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(getRandomTransactionInput()))
                                .build();
                        HttpResponse<String> httpResponse;
                        try {
                            httpResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        String statusCode = String.valueOf(httpResponse.statusCode());

                        int currentCount = counter.incrementAndGet();
                        if (statusCode.equals("200")) {
                            System.out.println("Successful response from Payments Gateway (" + currentCount + ") -- HTTP " + statusCode + " at " + Instant.now().toString());
                        } else {
                            System.out.println("Error response from Payments Gateway (" + currentCount + ") -- HTTP " + statusCode + " at " + Instant.now().toString());
                        }

                        CardTransaction returned = null;
                        try {
                            returned = objectMapper.readValue(httpResponse.body(), CardTransaction.class);
                        } catch (JsonProcessingException e) {
                            System.out.println("Error parsing response into a CardTransaction instance");
                            e.printStackTrace();
                            //continue to process
                        }
                        if (returned != null) {
                            if (returned.getStatus().equals("APP")) {
                                counterApproved.incrementAndGet();
                            } else if (returned.getStatus().equals("DEC")) {
                                counterDeclined.incrementAndGet();
                            } else if (returned.getStatus().equals("ERR")) {
                                counterError.incrementAndGet();
                            }
                        }


                    } catch (Exception e) {
                        // Handle exceptions here
                        System.out.println("Error response from Payments Gateway");
                        e.printStackTrace();
                    }
                });
                //by calling get() here, we're losing the benefits of being multi-threaded, but it makes the demo
                //  more predictable in terms of how many requests go through
                //  and it keeps the memory usage constant
                try {
                    handler.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }
        } finally {
            if (executorService != null) {
                // Shutdown the thread pool (but let all threads finish gracefully)
                executorService.shutdown();
                try {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println("Sent " + String.valueOf(counter.get()) + " requests");
        System.out.println(" Approved:  " + String.valueOf(counterApproved.get()) + " requests");
        System.out.println(" Declined:  " + String.valueOf(counterDeclined.get()) + " requests");
        System.out.println(" Error " + String.valueOf(counterError.get()) + " requests");
    }

    private String getRandomTransactionInput() {
        /* example of what we want
        {
          "amount": 100,
          "currency": "USD",
          "cardNumber": "4111111111111111",
          "cardExpirationMonth": "12",
          "cardExpirationYear": "2029",
          "cardHolderName": "JOHN Q PUBLIC",
          "merchantCode": "WELSHGOODS",
          "merchantReferenceCode": "240204-0001"
        }
        */

        String amount = String.valueOf(getRandomInteger(2, 1000));
        String currency = "USD";
        //TODO: VISA cards start with 4 and have 16 digits
        //      - come back and make this randomly do MC, AMEX, and Discover
        //generate 6-digit BIN (Bank Identification Number)
        String cardBin = getRandomBinVisa();
        String cardNumber = getCardNumber(cardBin, 16);
        String cardMonth = String.valueOf(getRandomInteger(1, 12));
        int nextYear = Year.now().getValue() + 1;
        String cardYear = String.valueOf(getRandomInteger(nextYear, nextYear + 8));
        String cardHolderName = getRandomLetters(5) + " " + getRandomLetters(10);
        String merchantCode = "WELSHGOODS";
        String merchantReferenceCode = String.valueOf(getRandomInteger(10000, 99999)) + "-" + getRandomLetters(5);

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"amount\": \"");
        sb.append(amount);
        sb.append("\", \"currency\": \"");
        sb.append(currency);
        sb.append("\", \"cardNumber\": \"");
        sb.append(cardNumber);
        sb.append("\", \"cardExpirationMonth\": \"");
        sb.append(cardMonth);
        sb.append("\", \"cardExpirationYear\": \"");
        sb.append(cardYear);
        sb.append("\", \"cardHolderName\": \"");
        sb.append(cardHolderName);
        sb.append("\", \"merchantCode\": \"");
        sb.append(merchantCode);
        sb.append("\", \"merchantReferenceCode\": \"");
        sb.append(merchantReferenceCode);
        sb.append("\"");
        sb.append("}");

        String jsonString = sb.toString();

        //System.out.println("Input generated: " + jsonString);

        return jsonString;
    }

    private String getRandomBinVisa() {
        return "4" + //leading 4 means VISA
                String.valueOf(getRandomInteger(0, 9)) +
                String.valueOf(getRandomInteger(0, 9)) +
                String.valueOf(getRandomInteger(0, 9)) +
                String.valueOf(getRandomInteger(0, 9)) +
                String.valueOf(getRandomInteger(0, 9));
    }

    private Integer getRandomInteger(int low, int high) {
        int result = rnd.nextInt(high - low) + low;
        return result;
    }

    //got this code from: https://gist.github.com/halienm/b929d2bc62eb69a1726a0c76a3dbbd57
    private String getCardNumber(String bin, int length) {

        // The number of random digits that we need to generate is equal to the
        // total length of the card number minus the start digits given by the
        // user, minus the check digit at the end.
        int randomNumberLength = length - (bin.length() + 1);

        StringBuilder builder = new StringBuilder(bin);
        for (int i = 0; i < randomNumberLength; i++) {
            int digit = rnd.nextInt(10);
            builder.append(digit);
        }

        // Do the Luhn algorithm to generate the check digit.
        int checkDigit = this.getCheckDigit(builder.toString());
        builder.append(checkDigit);

        return builder.toString();
    }

    /**
     * Generates the check digit required to make the given credit card number
     * valid (i.e. pass the Luhn check)
     *
     * @param number
     *            The credit card number for which to generate the check digit.
     * @return The check digit required to make the given credit card number
     *         valid.
     */
    private int getCheckDigit(String number) {

        // Get the sum of all the digits, however we need to replace the value
        // of the first digit, and every other digit, with the same digit
        // multiplied by 2. If this multiplication yields a number greater
        // than 9, then add the two digits together to get a single digit
        // number.
        //
        // The digits we need to replace will be those in an even position for
        // card numbers whose length is an even number, or those is an odd
        // position for card numbers whose length is an odd number. This is
        // because the Luhn algorithm reverses the card number, and doubles
        // every other number starting from the second number from the last
        // position.
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }

        // The check digit is the number required to make the sum a multiple of
        // 10.
        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
    }

    private String getRandomLetters(int size) {
        StringBuilder sb = new StringBuilder(size);
        for(int i = 0; i < size; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}

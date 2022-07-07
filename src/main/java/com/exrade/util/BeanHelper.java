package com.exrade.util;

import com.exrade.models.Permission;
import com.exrade.models.Role;
import com.exrade.models.userprofile.security.ExPermission;
import com.exrade.models.userprofile.security.ExRole;
import com.exrade.platform.exception.ExException;
import com.exrade.platform.exception.ExNotFoundException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author Rhidoy
 * @created 20/09/2021
 */
@Component
public class BeanHelper {

    public static final String API_HOST = "http://localhost:8080/";
    public static final String API_URL = API_HOST;
    public static final int CONNECTION_TIME_OUT = 1000 * 30; //millis*second

    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .clientConnector(getConnector())
//                .baseUrl(API_URL)
                .filter(logeRequest())
                .filter(errorHandle())
                .build();

    }

    private ClientHttpConnector getConnector() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
        return new ReactorClientHttpConnector(httpClient);
    }

    private ExchangeFilterFunction logeRequest() {
        return (clientRequest, next) -> {
            System.out.println(String.format("Request {%s} {%s}", clientRequest.method(), clientRequest.url()));
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> System.out.println(String.format("Request {%s} {%s}", name, value))));
            return next.exchange(clientRequest);
        };
    }

    private ExchangeFilterFunction errorHandle() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError()) {
                System.out.println("\nException found with code : " + clientResponse.statusCode() + "\n");
                System.out.println(Mono.just(clientResponse).toString());
                if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new ExNotFoundException(errorBody)));
                } else return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)));
            } else if (clientResponse.statusCode().is5xxServerError()) {
                if (clientResponse.statusCode() == HttpStatus.NOT_IMPLEMENTED) {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new ExException(errorBody)));
                } else
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)));
            } else return Mono.just(clientResponse);
        });
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        final SimpleModule roleModule = new SimpleModule()
                .addAbstractTypeMapping(Role.class, ExRole.class);
        final SimpleModule permissionModule = new SimpleModule()
                .addAbstractTypeMapping(Permission.class, ExPermission.class);

        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(roleModule)
                .registerModule(permissionModule)
                //will not throw exception for unknown property
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                //disabled all setter/getter methods
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                //allow only by field
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }
}

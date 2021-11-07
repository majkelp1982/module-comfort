package pl.smarthouse.module.comfort.service;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.smarthouse.loghandler.model.ErrorDto;
import pl.smarthouse.loghandler.model.InfoDto;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class LogService {
  WebClient logWebClient;

  public Mono<String> error(final ErrorDto errorDto) {
    return logWebClient
        .post()
        .uri("/error")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(errorDto)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> info(final InfoDto infoDto) {
    return logWebClient
        .post()
        .uri("/info")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(infoDto)
        .retrieve()
        .bodyToMono(String.class);
  }
}

package pl.smarthouse.module.comfort.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pl.smarthouse.loghandler.configuration.LogConfiguration;
import pl.smarthouse.loghandler.model.ErrorDto;
import pl.smarthouse.loghandler.model.InfoDto;
import reactor.core.publisher.Mono;

@Service
public class LogService {
  LogConfiguration logConfiguration = new LogConfiguration();

  public Mono<String> error(final ErrorDto errorDto) {
    return logConfiguration
        .webClient()
        .post()
        .uri("/error")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(errorDto)
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> info(final InfoDto infoDto) {
    return logConfiguration
        .webClient()
        .post()
        .uri("/info")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(infoDto)
        .retrieve()
        .bodyToMono(String.class);
  }
}

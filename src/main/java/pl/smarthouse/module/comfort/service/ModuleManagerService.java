package pl.smarthouse.module.comfort.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class ModuleManagerService {
  WebClient moduleManagerWebClient;

  public Mono<String> getDBModuleIpAddress(final String macAddress) {
    return moduleManagerWebClient
        .get()
        .uri("/ip?macAddress=" + macAddress)
        .retrieve()
        .bodyToMono(String.class);
  }
}

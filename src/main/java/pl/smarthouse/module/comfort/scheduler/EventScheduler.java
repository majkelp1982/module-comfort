package pl.smarthouse.module.comfort.scheduler;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.comfort.service.ActionService;
import pl.smarthouse.module.comfort.service.LogService;
import pl.smarthouse.module.comfort.service.ModuleManagerService;
import pl.smarthouse.module.comfort.service.ModuleService;
import pl.smarthouse.module.comfort.utils.LogUtils;
import pl.smarthouse.module.utils.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;

import static pl.smarthouse.module.comfort.configuration.ModuleConfiguration.MAC_ADDRESS;

@EnableScheduling
@AllArgsConstructor
@Service
public class EventScheduler {

  private static final String NO_IP_FOUND = "No IP found for mac address %s";
  private static final int MAX_RETRY_MS = 10 * 60 * 1000;
  private static int retryMs = 5000;
  ModuleConfiguration moduleConfig;
  ModuleService moduleService;
  ActionService actionService;
  LogService logService;
  ModuleManagerService moduleManagerService;

  @Scheduled(fixedDelay = 10000)
  public void eventScheduler() {
    Mono.justOrEmpty(moduleConfig.getBaseIPAddress())
        .switchIfEmpty(moduleManagerService.getDBModuleIpAddress(MAC_ADDRESS))
        .switchIfEmpty(Mono.error(new ConnectException(String.format(NO_IP_FOUND, MAC_ADDRESS))))
        .doOnSuccess((ip) -> moduleConfig.setBaseIPAddress(ip))
        .doOnError(
            throwable -> {
              logService.error(LogUtils.error(throwable.getMessage())).subscribe();
            })
        .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(retryMs)))
        .doOnSuccess(ignore -> retryMs = 1000)
        .doOnError(
            throwable -> {
              retryMs = retryMs * 2;
              if (retryMs > MAX_RETRY_MS) {
                retryMs = MAX_RETRY_MS;
              }
              logService.error(LogUtils.error(throwable.getMessage())).subscribe();
            })
        .block();

    moduleService
        .sendCommandToModule()
        .doOnError(
            WebClientResponseException.class,
            ex -> {
              // When no configuration
              if (ex.getStatusCode() == HttpStatus.NOT_IMPLEMENTED) {
                moduleService.sendConfigurationToModule().subscribe();
                logService.info(LogUtils.info("Send configuration"));
              }
            })
        .doOnError(
            WebClientRequestException.class,
            ex -> {
              logService.error(LogUtils.error(ex.getMessage())).subscribe();
            })
        .doOnError(
            throwable -> {
              logService.error(LogUtils.error(throwable.getMessage())).subscribe();
              moduleConfig.setBaseIPAddress(null);
            })
        .doOnSuccess(
            moduleResponse -> {
              ModelMapper.copyResponseData(moduleConfig.getModuleConfig(), moduleResponse);
              actionService.actionService();
            })
        .subscribe();
  }
}

package pl.smarthouse.module.comfort.scheduler;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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

import java.time.Duration;

import static pl.smarthouse.module.comfort.configuration.ModuleConfiguration.MAC_ADDRESS;

@EnableScheduling
@AllArgsConstructor
@Service
public class EventScheduler {
  ModuleConfiguration moduleConfig;
  ModuleService moduleService;
  ActionService actionService;
  LogService logService;
  ModuleManagerService moduleManagerService;

  @Scheduled(fixedDelay = 10000)
  public void eventScheduler() {
    Mono.justOrEmpty(moduleConfig.getBaseIPAddress())
        .switchIfEmpty(moduleManagerService.getDBModuleIpAddress(MAC_ADDRESS))
        .doOnSuccess(ip -> moduleConfig.setBaseIPAddress(ip))
        .doOnError(
            throwable -> {
              System.out.println(throwable.getCause().getMessage());
            })
        .retryWhen(Retry.fixedDelay(10, Duration.ofMillis(1000)))
        .then(moduleService.sendCommandToModule())
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
            throwable -> {
              logService.error(LogUtils.error(throwable.getMessage())).subscribe();
              System.out.println(throwable.getCause().getMessage());
            })
        .doOnSuccess(
            moduleResponse -> {
              ModelMapper.copyResponseData(moduleConfig.getModuleConfig(), moduleResponse);
              actionService.actionService();
            })
        .subscribe();
  }
}

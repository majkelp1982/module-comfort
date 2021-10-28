package pl.smarthouse.module.comfort.scheduler;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.comfort.service.ActionService;
import pl.smarthouse.module.comfort.service.ModuleService;
import pl.smarthouse.module.utils.ModelMapper;

@EnableScheduling
@AllArgsConstructor
@Service
public class EventScheduler {
  ModuleConfiguration moduleConfig;
  ModuleService moduleService;
  ActionService actionService;

  @Scheduled(fixedDelay = 10000)
  public void eventScheduler() throws Exception {
    moduleService
        .sendCommandToModule()
        .doOnError(
            WebClientResponseException.class,
            ex -> {
              // When no configuration
              if (ex.getStatusCode() == HttpStatus.NOT_IMPLEMENTED) {
                moduleService.sendConfigurationToModule().subscribe();
              }
            })
        .doOnError(
            throwable -> {
              // TODO timeout etc.
              System.out.println(throwable.getMessage());
            })
        .doOnSuccess(
            moduleResponse -> {
              ModelMapper.copyResponseData(moduleConfig.getModuleConfig(), moduleResponse);
              actionService.actionService();
            })
        .subscribe();
  }
}

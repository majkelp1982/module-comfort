package pl.smarthouse.module.comfort.service;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.smarthouse.module.GPO.enums.PinAction;
import pl.smarthouse.module.GPO.model.PinCommand;
import pl.smarthouse.module.GPO.utils.PinModelMapper;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.command.ModuleCommand;
import pl.smarthouse.module.response.ModuleResponse;
import pl.smarthouse.module.sensors.model.SensorCommand;
import pl.smarthouse.module.sensors.model.enums.SensorAction;
import pl.smarthouse.module.sensors.utils.SensorModelMapper;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ModuleService {
  private final WebClient webClient;
  ModuleConfiguration moduleConfig;

  public ModuleCommand getCommandBody() {
    final Set<PinCommand> pinActionSet =
        moduleConfig.getModuleConfig().getPinDaoSet().stream()
            .filter(pinDao -> !PinAction.NO_ACTION.equals(pinDao.getAction()))
            .map(pinDao -> PinModelMapper.toPinCommand(pinDao))
            .collect(Collectors.toSet());

    final Set<SensorCommand> sensorActionSet =
        moduleConfig.getModuleConfig().getSensorDaoSet().stream()
            .filter(sensorDao -> !SensorAction.NO_ACTION.equals(sensorDao.getAction()))
            .map(sensorDao -> SensorModelMapper.toSensorCommand(sensorDao))
            .collect(Collectors.toSet());

    return ModuleCommand.builder()
        .type(moduleConfig.getModuleConfig().getType())
        .version(moduleConfig.getModuleConfig().getVersion())
        .pinCommandSet(pinActionSet)
        .sensorCommandSet(sensorActionSet)
        .build();
  }

  public Mono<ModuleResponse> sendCommandToModule() {
    return webClient
        .post()
        .uri("/action")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(getCommandBody())
        .retrieve()
        .bodyToMono(ModuleResponse.class);
  }

  public Mono<String> sendConfigurationToModule() {
    return webClient
        .post()
        .uri("/configuration")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(moduleConfig.getModuleConfig())
        .retrieve()
        .bodyToMono(String.class);
  }
}

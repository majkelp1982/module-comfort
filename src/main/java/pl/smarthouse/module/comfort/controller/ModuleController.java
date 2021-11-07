package pl.smarthouse.module.comfort.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.comfort.service.ModuleService;
import pl.smarthouse.module.command.ModuleCommand;
import pl.smarthouse.module.config.model.ModuleConfigDto;

@AllArgsConstructor
@RestController
public class ModuleController {

  ModuleConfiguration moduleConfiguration;
  ModuleService moduleService;

  @GetMapping(value = "/config", produces = "application/json")
  public ModuleConfigDto getConfiguration() {
    return moduleConfiguration.getModuleConfigDto();
  }

  @GetMapping(value = "/command", produces = "application/json")
  public ModuleCommand getCommand() {
    return moduleService.getCommandBody();
  }

  // fixme only temorary to check communication with ESP32
  @GetMapping(value = "/sendconfig", produces = "plain/text")
  public String sendConfig() {
    moduleService.sendConfigurationToModule();
    return "OK";
  }

  // fixme only temorary to check communication with ESP32
  @GetMapping(value = "/sendaction", produces = "plain/text")
  public String sendAction() throws Exception {
    moduleService.sendCommandToModule();
    return "OK";
  }
}

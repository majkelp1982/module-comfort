package pl.smarthouse.module.comfort.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.comfort.service.ModuleService;
import pl.smarthouse.module.command.ModuleCommand;
import pl.smarthouse.module.config.ModuleConfig;

@AllArgsConstructor
@RestController
public class ModuleController {

  ModuleConfiguration moduleConfiguration;
  ModuleService moduleService;

  @GetMapping(value = "/config", produces = "application/json")
  public ModuleConfig getConfiguration() {
    return moduleConfiguration.getModuleConfig();
  }

  @GetMapping(value = "/command", produces = "application/json")
  public ModuleCommand getCommand() {
    return moduleService.getCommandBody();
  }
}

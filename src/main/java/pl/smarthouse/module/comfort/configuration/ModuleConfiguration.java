package pl.smarthouse.module.comfort.configuration;

import org.springframework.context.annotation.Configuration;
import pl.smarthouse.module.GPO.enums.PinAction;
import pl.smarthouse.module.GPO.enums.PinModes;
import pl.smarthouse.module.GPO.model.PinDao;
import pl.smarthouse.module.config.ModuleConfig;
import pl.smarthouse.module.config.model.ModuleConfigDto;
import pl.smarthouse.module.sensors.model.SensorDao;
import pl.smarthouse.module.sensors.model.enums.SensorAction;
import pl.smarthouse.module.sensors.model.sensorBME280SPI.SensorBME280SPIDao;

import java.util.HashSet;
import java.util.Set;

import static pl.smarthouse.module.comfort.constants.Module.MODULE_NAME;
import static pl.smarthouse.module.comfort.constants.Module.VERSION;
import static pl.smarthouse.module.comfort.constants.Pins.PIN_LIGHT_INTENSITY;
import static pl.smarthouse.module.comfort.constants.Sensors.BME_ZEW;

@Configuration
public class ModuleConfiguration {
  private final ModuleConfig moduleConfig;

  public ModuleConfiguration() {
    moduleConfig =
        ModuleConfig.builder()
            .type(MODULE_NAME)
            .version(VERSION)
            .pinDaoSet(getPinsDao())
            .sensorDaoSet(getSensorsDao())
            .build();
  }

  public ModuleConfigDto getModuleConfigDto() {
    return moduleConfig.getConfig();
  }

  public ModuleConfig getModuleConfig() {
    return moduleConfig;
  }

  private Set<PinDao> getPinsDao() {
    final Set<PinDao> pinDaos = new HashSet<>();
    pinDaos.add(
        PinDao.builder()
            .pinNumber(PIN_LIGHT_INTENSITY)
            .mode(PinModes.ANALOG)
            .action(PinAction.READ_ANALOG)
            .build());

    pinDaos.add(
        PinDao.builder()
            .pinNumber(1)
            .mode(PinModes.OUTPUT)
            .action(PinAction.READ)
            .defaultLatchTime(1000)
            .build());
    pinDaos.add(
        PinDao.builder()
            .pinNumber(2)
            .mode(PinModes.OUTPUT_OPEN_DRAIN)
            .defaultLatchTime(1000)
            .action(PinAction.READ)
            .build());
    pinDaos.add(PinDao.builder().pinNumber(3).mode(PinModes.INPUT).action(PinAction.READ).build());
    pinDaos.add(
        PinDao.builder().pinNumber(4).mode(PinModes.INPUT_PULLDOWN).action(PinAction.READ).build());
    pinDaos.add(
        PinDao.builder().pinNumber(6).mode(PinModes.INPUT_PULLUP).action(PinAction.READ).build());
    return pinDaos;
  }

  private Set<SensorDao> getSensorsDao() {
    final Set<SensorDao> sensorDaoSet = new HashSet<>();
    sensorDaoSet.add(
        SensorBME280SPIDao.builder().name(BME_ZEW).csPin(13).action(SensorAction.READ).build());
    return sensorDaoSet;
  }
}

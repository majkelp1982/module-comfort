package pl.smarthouse.module.comfort.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.sensors.model.sensorBME280SPI.SensorBME280SPIDao;
import pl.smarthouse.module.sensors.model.sensorBME280SPI.SensorBME280SPIResponse;
import pl.smarthouse.module.utils.ModelMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pl.smarthouse.module.comfort.constants.Sensors.BME280;

@Service
@AllArgsConstructor
public class ActionService {

  ModuleConfiguration moduleConfig;
  ExternalModuleService externalModuleService;

  public void actionService() {
    final SensorBME280SPIDao bme280 =
        (SensorBME280SPIDao) ModelMapper.findSensor(moduleConfig.getModuleConfig(), BME280);
    SensorBME280SPIResponse sensorBME280Resp = null;
    try {
      sensorBME280Resp = SensorBME280SPIResponse.map(bme280.getResponse());
    } catch (final JsonProcessingException ex) {
      ex.printStackTrace();
    }
    System.out.print(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")));
    System.out.println(" " + sensorBME280Resp.toString());
    externalModuleService
        .sendBME280DataToExternalModule(sensorBME280Resp)
        .doOnError(error -> System.out.println(error.getMessage()))
        .doOnSuccess(
            s -> {
              System.out.println(s);
            })
        .subscribe();
  }
}

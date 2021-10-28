package pl.smarthouse.module.comfort.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.smarthouse.module.comfort.configuration.ModuleConfiguration;
import pl.smarthouse.module.sensors.model.sensorBME280SPI.SensorBME280SPIResponse;
import pl.smarthouse.module.utils.ModelMapper;

import static pl.smarthouse.module.comfort.constants.Pins.PIN_LIGHT_INTENSITY;
import static pl.smarthouse.module.comfort.constants.Sensors.BME_ZEW;

@Service
@AllArgsConstructor
public class ActionService {

  ModuleConfiguration moduleConfig;

  public void actionService() {

    final int lightIntensity =
        ModelMapper.findPin(moduleConfig.getModuleConfig(), PIN_LIGHT_INTENSITY).getAnalogState();
    final String sensorResp =
        ModelMapper.findSensor(moduleConfig.getModuleConfig(), BME_ZEW).getResponse();
    SensorBME280SPIResponse sensorBME280SPIResponse = null;
    try {
      sensorBME280SPIResponse = SensorBME280SPIResponse.map(sensorResp);
    } catch (final JsonProcessingException ex) {
      ex.printStackTrace();
    }

    System.out.println(sensorBME280SPIResponse.toString());
  }
}

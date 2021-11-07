package pl.smarthouse.module.comfort.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import pl.smarthouse.module.GPO.model.PinDao;
import pl.smarthouse.module.comfort.service.ModuleManagerService;
import pl.smarthouse.module.config.ModuleConfig;
import pl.smarthouse.module.config.model.ModuleConfigDto;
import pl.smarthouse.module.sensors.model.SensorDao;
import pl.smarthouse.module.sensors.model.enums.SensorAction;
import pl.smarthouse.module.sensors.model.sensorBME280SPI.SensorBME280SPIDao;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static pl.smarthouse.module.comfort.constants.Module.MODULE_NAME;
import static pl.smarthouse.module.comfort.constants.Module.VERSION;
import static pl.smarthouse.module.comfort.constants.Sensors.BME280;

@Configuration
@Getter
@Setter
public class ModuleConfiguration {
  // module mac address
  public static final String MAC_ADDRESS = "3C:71:BF:4D:77:C8";
  private final ModuleConfig moduleConfig;
  private String baseIPAddress;

  public ModuleConfiguration(final ModuleManagerService moduleManagerService) {
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
    // no pins needed
    final Set<PinDao> pinDaos = new HashSet<>();
    return pinDaos;
  }

  private Set<SensorDao> getSensorsDao() {
    final Set<SensorDao> sensorDaoSet = new HashSet<>();
    sensorDaoSet.add(
        SensorBME280SPIDao.builder().name(BME280).csPin(4).action(SensorAction.READ).build());
    return sensorDaoSet;
  }

  @Bean
  WebClient webClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient()))
        .build();
  }

  private HttpClient httpClient() {
    return HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
        .responseTimeout(Duration.ofMillis(5000))
        .doOnConnected(
            conn ->
                conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));
  }
}

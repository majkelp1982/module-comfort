package pl.smarthouse.module.comfort.utils;

import pl.smarthouse.loghandler.model.ErrorDto;
import pl.smarthouse.loghandler.model.InfoDto;

public class LogUtils {
  private static final String MODULE_NAME = "module-comfort";

  public static ErrorDto error(final String message) {
    return ErrorDto.builder().moduleName(MODULE_NAME).message(message).build();
  }

  public static InfoDto info(final String message) {
    return InfoDto.builder().moduleName("module-comfort").message(message).build();
  }
}

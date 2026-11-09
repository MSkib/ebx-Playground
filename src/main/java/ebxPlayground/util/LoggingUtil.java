package ebxPlayground.util;

import com.orchestranetworks.service.LoggingCategory;

public class LoggingUtil {
  private LoggingUtil() {}

  public static LoggingCategory getLogger() {
    return LoggingCategory.getKernel();
  }
}

package ebxPlayground.util;

import static ebxPlayground.util.TableType.*;

import com.orchestranetworks.schema.Path;
import ebxPlayground.binding.TestDataModelPaths;
import java.util.Map;

public class TableUtil {
  private TableUtil() {}

  public static final Map<TableType, Path> TABLE_TO_ROOT =
      Map.ofEntries(
          Map.entry(BRANCH_SITE, TestDataModelPaths._Root_TestTable.getPathInSchema()),
          Map.entry(OPENING_HOUR, TestDataModelPaths._Root_Address.getPathInSchema()),
          Map.entry(CLOSING_DAY, TestDataModelPaths._Root_Contact.getPathInSchema()));
}

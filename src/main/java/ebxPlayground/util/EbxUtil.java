package ebxPlayground.util;

import static ebxPlayground.util.LoggingUtil.getLogger;
import static ebxPlayground.util.TableUtil.TABLE_TO_ROOT;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.query.QueryResult;
import com.orchestranetworks.query.Tuple;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.ValueContextForUpdate;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// Custom class for general ebx operation

public class EbxUtil {
  private static final String EBX_DATE_TIME_FORMAT_STRING = "EEE MMM dd HH:mm:ss zzz yyyy";
  public static final DateTimeFormatter EBX_DATE_TIME_FORMAT =
      DateTimeFormatter.ofPattern(EBX_DATE_TIME_FORMAT_STRING);
  private static final Set<String> TECHNICAL_FIELD_INDICATOR =
      Set.of(
          "DQScore",
          "Matching",
          "AS_",
          "Counter",
          "Deleted",
          "Association_Reverse",
          "HouseNumberOverallNumbers",
          "Linked_Field",
          "IsAnonymized,");
  public static final String ADAPTATION = "adaptation";

  private EbxUtil() {}

  public static AdaptationHome getDataspace(String dataSpaceName) {
    return Repository.getDefault().lookupHome(HomeKey.forBranchName(dataSpaceName));
  }

  public static Adaptation getDatasetAdaptation(AdaptationHome dataSpace, String dataSetName) {

    if (dataSpace == null || dataSetName == null) {
      return null;
    }

    try {
      return dataSpace.findAdaptationOrNull(AdaptationName.forName(dataSetName));
    } catch (Exception e) {
      getLogger().error("Error fetching dataset adaptation: " + e.getMessage());
      return null;
    }
  }

  public static String getFieldValueAsString(Adaptation rec, Path fieldPath) {
    Object recordValue = rec.getValueWithoutResolution(fieldPath);
    return recordValue != null ? recordValue.toString() : "";
  }

  public static String getFieldValueAsString(ValueContext valueContext, Path fieldPath) {
    Object recordValue = valueContext.getValue(fieldPath);
    return recordValue != null ? recordValue.toString() : "";
  }

  public static AdaptationTable getAdaptationTable(Adaptation dataset, Path tablePath) {
    if (dataset == null) {
      return null; // Handle case where dataset is not found
    }

    return dataset.getTable(tablePath);
  }

  public static Map<Path, Object> recordToMap(Adaptation rec, Class<?> clazz) {
    Map<Path, Object> result = new HashMap<>();
    getPathsFromBinding(clazz).stream()
        .filter(p -> TECHNICAL_FIELD_INDICATOR.stream().noneMatch(s -> p.format().contains(s)))
        .forEach(p -> result.put(p, rec.get(p)));
    return result;
  }

  public static Set<Path> getNontechnicalFields(Class<?> clazz) {
    return getPathsFromBinding(clazz).stream()
        .filter(p -> TECHNICAL_FIELD_INDICATOR.stream().noneMatch(s -> p.format().contains(s)))
        .filter(p -> !Path.parse("./ID").equals(p))
        .filter(p -> !p.format().contains("FK_"))
        .collect(Collectors.toSet());
  }

  static List<Path> getPathsFromBinding(Class<?> clazz) {
    List<Path> paths = new ArrayList<>();

    for (Field field : clazz.getDeclaredFields()) {
      int mods = field.getModifiers();
      if (Modifier.isPublic(mods)
          && Modifier.isStatic(mods)
          && Modifier.isFinal(mods)
          && field.getType().equals(Path.class)) {
        try {
          Path path = (Path) field.get(null);
          paths.add(path);
        } catch (java.lang.IllegalAccessException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }
    return paths;
  }

  public static void removeTechnicalFields(Map<Path, ?> map) {
    map.keySet()
        .removeIf(p -> TECHNICAL_FIELD_INDICATOR.stream().anyMatch(s -> p.format().contains(s)));
  }

  public static boolean isTableType(ValueContext rec, TableType tableType) {
    return Objects.equals(TABLE_TO_ROOT.get(tableType), rec.getNode().getPathInSchema());
  }

  public static Set<Adaptation> getNonNullAdaptationsByQuery(String query, Adaptation dataset) {
    try (QueryResult<Tuple> result = dataset.createQueryBuilder().build(query).getResult()) {
      return StreamSupport.stream(result.spliterator(), false)
          .map(t -> t.get(ADAPTATION, Adaptation.class))
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
    }
  }

  public static Integer getId(Adaptation rec, Path path) {
    if (rec == null || rec.get(path) == null || "".equals(rec.get(path))) {
      return null;
    }
    return switch (rec.get(path)) {
      case Integer i -> i;
      case String s -> Integer.valueOf(s);
      default ->
          throw new IllegalArgumentException(
              "%s of %s is neither String nor Integer".formatted(path, rec.getAdaptationName()));
    };
  }

  public static Integer getId(ValueContext vc, Path path) {
    if (vc == null || vc.getValue(path) == null || "".equals(vc.getValue(path))) {
      return null;
    }
    return switch (vc.getValue(path)) {
      case Integer i -> i;
      case String s -> Integer.valueOf(s);
      default ->
          throw new IllegalArgumentException(
              "%s of %s is neither String nor Integer".formatted(path, vc.getAdaptationTable()));
    };
  }

  public static void setValueWithMaxLength(
      ValueContextForUpdate vcfu, String value, Path path, int length) {
    if (value != null && value.length() > length) {
      value = value.substring(0, length);
    }
    vcfu.setValue(value, path);
  }

  public static boolean hasNOrMoreRepeatingChars(String string, int amount) {
    String regex = "(.)\\1{%s,}".formatted(amount - 1);
    Matcher matcher = Pattern.compile(regex).matcher(string.toLowerCase());
    return matcher.find();
  }


  public static Set<Adaptation> getNonNullAdaptationsByQuery(Adaptation dataset, String query) {
    try (QueryResult<Tuple> result = dataset.createQueryBuilder().build(query).getResult()) {
      return StreamSupport.stream(result.spliterator(), false)
          .map(t -> t.get("adaptation", Adaptation.class))
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
    }
  }
}

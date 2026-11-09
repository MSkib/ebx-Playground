package ebxPlayground.util;

import com.orchestranetworks.schema.Path;

import ebxPlayground.binding.TestDataModelPaths;

public enum TableType {
  BRANCH_SITE("BranchSite"),
  OPENING_HOUR("OpeningHour"),
  CLOSING_DAY("ClosingDay"),
  SERVICE_REGION("ServiceRegion"),
  DELIVERY_BASE("DeliveryBase"),
  BRANCH_SITE_PERSON("BranchSitePerson"),
  MUNICIPALITY("Municipality"),
  POST_PARTNER_COMPANY("PostPartnerCompany");

  public final String value;

  TableType(String value) {
    this.value = value;
  }

  public static TableType fromValue(String value) {
    for (TableType type : values()) {
      if (type.value.equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException(value + " is not a valid table type.");
  }

  public static Path getTablePath(TableType type) {
    return switch (type) {
      case BRANCH_SITE -> TestDataModelPaths._Root_TestTable.getPathInSchema();
      case OPENING_HOUR -> TestDataModelPaths._Root_Address.getPathInSchema();
      case CLOSING_DAY -> TestDataModelPaths._Root_Contact.getPathInSchema();
      default -> throw new IllegalArgumentException("Unexpected value: " + type);
    };
  }
}

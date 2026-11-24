package ebxPlayground.util;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;

import static ebxPlayground.util.EbxUtil.getDatasetAdaptation;
import static ebxPlayground.util.EbxUtil.getDataspace;

public class DataSpacesSets {
  public static final String TEST_DATASPACE_NAME = "TESTDataspace";

  public static final AdaptationHome TEST_DATASPACE = getDataspace(TEST_DATASPACE_NAME);

  public static final String TEST_DATASET_NAME = "TESTSite";

  public static final Adaptation TEST_DATASET =
      getDatasetAdaptation(TEST_DATASPACE, TEST_DATASET_NAME);

  private DataSpacesSets() {}
}

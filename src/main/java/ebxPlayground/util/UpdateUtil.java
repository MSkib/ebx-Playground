package ebxPlayground.util;

import com.onwbp.adaptation.*;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

/**
 * Can be used for inserts/upserts/updates. Can be used with existing ProcedureContext or without.
 * Without an existing ProcedureContext, the ProgrammaticService is used.
 */
public class UpdateUtil {

  private final Session session;

  public UpdateUtil(Session session) {
    this.session = session;
  }

  public static void update(
      ProcedureContext procedureContext,
      Adaptation rec,
      BiConsumer<Adaptation, ValueContextForUpdate> updater) {
    update(procedureContext, Collections.singletonList(rec), updater);
  }

  public static void update(
      ProcedureContext procedureContext,
      List<Adaptation> records,
      BiConsumer<Adaptation, ValueContextForUpdate> updater) {
    runWrapped(procedureContext, proc -> updateUnwrapped(proc, records, updater));
  }

  public static void updateWithValueContext(
      ProcedureContext procedureContext,
      List<Adaptation> records,
      BiConsumer<ValueContext, ValueContextForUpdate> updater) {
    runWrapped(procedureContext, proc -> updateUnwrappedWithValueContext(proc, records, updater));
  }

  public static void delete(ProcedureContext procedureContext, Adaptation rec) {
    delete(procedureContext, Collections.singletonList(rec));
  }

  public static void delete(ProcedureContext procedureContext, List<Adaptation> records) {
    runWrapped(procedureContext, proc -> deleteUnwrapped(proc, records));
  }

  public static void updateUnwrapped(
      ProcedureContext procedureContext,
      List<Adaptation> records,
      BiConsumer<Adaptation, ValueContextForUpdate> updater) {
    try {
      for (Adaptation rec : records) {
        ValueContextForUpdate valueContext = procedureContext.getContext(rec.getAdaptationName());
        updater.accept(rec, valueContext);
        procedureContext.doModifyContent(rec, valueContext);
      }
    } catch (OperationException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void updateUnwrappedWithValueContext(
      ProcedureContext procedureContext,
      List<Adaptation> records,
      BiConsumer<ValueContext, ValueContextForUpdate> updater) {
    try {
      for (Adaptation rec : records) {
        ValueContextForUpdate valueContext = procedureContext.getContext(rec.getAdaptationName());
        updater.accept(rec.createValueContext(), valueContext);
        procedureContext.doModifyContent(rec, valueContext);
      }
    } catch (OperationException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void deleteUnwrapped(ProcedureContext procedureContext, List<Adaptation> records) {
    try {
      for (Adaptation rec : records) {
        procedureContext.doDelete(rec.getAdaptationName(), false);
      }
    } catch (OperationException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static void insert(
      ProcedureContext procedureContext, Map<Path, Object> rec, AdaptationTable table) {
    upsert(procedureContext, rec, table, null, null);
  }

  public static void insert(
      ProcedureContext procedureContext, List<Map<Path, Object>> records, AdaptationTable table) {
    upsert(procedureContext, records, table, null, null);
  }

  public static void upsert(
      ProcedureContext procedureContext,
      Map<Path, Object> rec,
      AdaptationTable table,
      Path upsertKey,
      String additionalFilter) {
    upsert(procedureContext, Collections.singletonList(rec), table, upsertKey, additionalFilter);
  }

  public static void upsert(
      ProcedureContext procedureContext,
      List<Map<Path, Object>> records,
      AdaptationTable table,
      Path upsertKey,
      String additionalFilter) {
    runWrapped(
        procedureContext,
        proc -> upsertUnwrapped(procedureContext, records, table, upsertKey, additionalFilter));
  }

  public static void upsertUnwrapped(
      ProcedureContext procedureContext,
      List<Map<Path, Object>> records,
      AdaptationTable table,
      Path upsertKey,
      String additionalFilter) {
    try {
      for (Map<Path, Object> rec : records) {
        Adaptation row = null;
        if (upsertKey != null) {
          Request request = table.createRequest();
          request.setXPathFilter(
              upsertKey.format()
                  + "='"
                  + rec.get(upsertKey)
                  + "'"
                  + (additionalFilter == null ? "" : additionalFilter));
          try (RequestResult result = request.execute()) {
            row = result.nextAdaptation();
          }
        }
        if (row != null) {
          ValueContextForUpdate valueContext = procedureContext.getContext(row.getAdaptationName());
          rec.forEach((p, o) -> setValueWithXsFallback(valueContext, o, p));
          procedureContext.doModifyContent(row, valueContext);
        } else { // not found or insert semantic (upsertKey = null)
          ValueContextForUpdate valueContext = procedureContext.getContextForNewOccurrence(table);
          rec.forEach((p, o) -> setValueWithXsFallback(valueContext, o, p));
          procedureContext.doCreateOccurrence(valueContext, table);
        }
      }
    } catch (OperationException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static void setValueWithXsFallback(
      ValueContextForUpdate valueContext, Object value, Path path) {
    try {
      valueContext.setValue(value, path);
    } catch (IllegalArgumentException e) {
      if (value == null) {
        throw e;
      }
      try {
        valueContext.setValueFromXsString(value.toString(), path);
      } catch (Exception fallbackException) {
        throw e;
      }
    }
  }

  private static void runWrapped(ProcedureContext procedureContext, Procedure operation) {
    boolean triggerActivation = procedureContext.isTriggerActivation();
    boolean allPrivileges = procedureContext.isAllPrivileges();
    try {
      procedureContext.setTriggerActivation(false);
      procedureContext.setAllPrivileges(true);
      operation.execute(procedureContext);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    } finally {
      procedureContext.setTriggerActivation(triggerActivation);
      procedureContext.setAllPrivileges(allPrivileges);
    }
  }

  /**
   * Updates possibly async with the Programmatic Service
   *
   * <p><b>Note:</b> Async, so the update is not guaranteed to have been performed after the method
   * returns. The update might also fail due to several reasons.
   *
   * @param rec The record, which will be updated
   * @param updater A function that updates the record, by setting values in the
   *     ValueContextForUpdate, while having access to the original Adaptation
   */
  public CompletableFuture<Void> update(
      Adaptation rec, BiConsumer<Adaptation, ValueContextForUpdate> updater) {
    return update(Collections.singletonList(rec), updater);
  }

  public CompletableFuture<Void> update(
      List<Adaptation> records, BiConsumer<Adaptation, ValueContextForUpdate> updater) {

    AdaptationHome dataspace = records.getFirst().getHome();
    Procedure procedure = procedureContext -> updateUnwrapped(procedureContext, records, updater);
    return updateWithPsWithDefaultWrapper(dataspace, procedure);
  }

  public CompletableFuture<Void> updateWithValueContext(
      List<Adaptation> records, BiConsumer<ValueContext, ValueContextForUpdate> updater) {

    AdaptationHome dataspace = records.getFirst().getHome();
    Procedure procedure =
        procedureContext -> updateUnwrappedWithValueContext(procedureContext, records, updater);
    return updateWithPsWithDefaultWrapper(dataspace, procedure);
  }

  public CompletableFuture<Void> delete(Adaptation rec) {
    return delete(Collections.singletonList(rec));
  }

  public CompletableFuture<Void> delete(List<Adaptation> records) {

    AdaptationHome dataspace = records.getFirst().getHome();
    Procedure procedure = procedureContext -> deleteUnwrapped(procedureContext, records);
    return updateWithPsWithDefaultWrapper(dataspace, procedure);
  }

  public CompletableFuture<Void> insert(
      Map<Path, Object> rec, AdaptationTable table, AdaptationHome dataspace) {
    return upsert(rec, table, dataspace, null, null);
  }

  public CompletableFuture<Void> insert(
      List<Map<Path, Object>> records, AdaptationTable table, AdaptationHome dataspace) {
    return upsert(records, table, dataspace, null, null);
  }

  public CompletableFuture<Void> upsert(
      Map<Path, Object> rec,
      AdaptationTable table,
      AdaptationHome dataspace,
      Path upsertKey,
      String additionalFilter) {
    return upsert(Collections.singletonList(rec), table, dataspace, upsertKey, additionalFilter);
  }

  public CompletableFuture<Void> upsert(
      List<Map<Path, Object>> records,
      AdaptationTable table,
      AdaptationHome dataspace,
      Path upsertKey,
      String additionalFilter) {
    Procedure upsert =
        procedureContext ->
            upsertUnwrapped(procedureContext, records, table, upsertKey, additionalFilter);
    return updateWithPsWithDefaultWrapper(dataspace, upsert);
  }

  public CompletableFuture<Void> updateWithPsWithDefaultWrapper(
      AdaptationHome dataspace, Procedure operation) {
    return updateWithPsWithWrapper(
        dataspace, operation, op -> procedureContext -> runWrapped(procedureContext, op));
  }

  public CompletableFuture<Void> updateWithPsWithoutWrapper(
      AdaptationHome dataspace, Procedure operation) {
    return updateWithPsWithWrapper(dataspace, operation, op -> op);
  }

  private CompletableFuture<Void> updateWithPsWithWrapper(
      AdaptationHome dataspace, Procedure operation, UnaryOperator<Procedure> wrapper) {

    ProgrammaticService ps = ProgrammaticService.createForSession(session, dataspace);

    Procedure operationWrapped = wrapper.apply(operation);
    // Execute the procedure to create the audit entry, will be retried
    LoggingCategory.getKernel().info("UpdateUtil::updateWithPsWithWrapper: starting procedure");
    return RetryUtil.retryWithExponentialDelay(
        () -> ps.execute(operationWrapped), 5, 1, TimeUnit.SECONDS, e -> {});
  }
}

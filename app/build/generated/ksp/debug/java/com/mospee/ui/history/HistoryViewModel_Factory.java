package com.mospee.ui.history;

import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.domain.usecase.DeleteTripUseCase;
import com.mospee.domain.usecase.GetAllTripsUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<GetAllTripsUseCase> getAllTripsUseCaseProvider;

  private final Provider<DeleteTripUseCase> deleteTripUseCaseProvider;

  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  public HistoryViewModel_Factory(Provider<GetAllTripsUseCase> getAllTripsUseCaseProvider,
      Provider<DeleteTripUseCase> deleteTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider) {
    this.getAllTripsUseCaseProvider = getAllTripsUseCaseProvider;
    this.deleteTripUseCaseProvider = deleteTripUseCaseProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(getAllTripsUseCaseProvider.get(), deleteTripUseCaseProvider.get(), prefsRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(
      Provider<GetAllTripsUseCase> getAllTripsUseCaseProvider,
      Provider<DeleteTripUseCase> deleteTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider) {
    return new HistoryViewModel_Factory(getAllTripsUseCaseProvider, deleteTripUseCaseProvider, prefsRepositoryProvider);
  }

  public static HistoryViewModel newInstance(GetAllTripsUseCase getAllTripsUseCase,
      DeleteTripUseCase deleteTripUseCase, UserPreferencesRepository prefsRepository) {
    return new HistoryViewModel(getAllTripsUseCase, deleteTripUseCase, prefsRepository);
  }
}

package com.mospee.ui.summary;

import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.data.repository.WeatherRepository;
import com.mospee.domain.usecase.DeleteTripUseCase;
import com.mospee.domain.usecase.GetTripDetailsUseCase;
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
public final class TripSummaryViewModel_Factory implements Factory<TripSummaryViewModel> {
  private final Provider<GetTripDetailsUseCase> getTripDetailsUseCaseProvider;

  private final Provider<DeleteTripUseCase> deleteTripUseCaseProvider;

  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  private final Provider<WeatherRepository> weatherRepositoryProvider;

  public TripSummaryViewModel_Factory(Provider<GetTripDetailsUseCase> getTripDetailsUseCaseProvider,
      Provider<DeleteTripUseCase> deleteTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<WeatherRepository> weatherRepositoryProvider) {
    this.getTripDetailsUseCaseProvider = getTripDetailsUseCaseProvider;
    this.deleteTripUseCaseProvider = deleteTripUseCaseProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
    this.weatherRepositoryProvider = weatherRepositoryProvider;
  }

  @Override
  public TripSummaryViewModel get() {
    return newInstance(getTripDetailsUseCaseProvider.get(), deleteTripUseCaseProvider.get(), prefsRepositoryProvider.get(), weatherRepositoryProvider.get());
  }

  public static TripSummaryViewModel_Factory create(
      Provider<GetTripDetailsUseCase> getTripDetailsUseCaseProvider,
      Provider<DeleteTripUseCase> deleteTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<WeatherRepository> weatherRepositoryProvider) {
    return new TripSummaryViewModel_Factory(getTripDetailsUseCaseProvider, deleteTripUseCaseProvider, prefsRepositoryProvider, weatherRepositoryProvider);
  }

  public static TripSummaryViewModel newInstance(GetTripDetailsUseCase getTripDetailsUseCase,
      DeleteTripUseCase deleteTripUseCase, UserPreferencesRepository prefsRepository,
      WeatherRepository weatherRepository) {
    return new TripSummaryViewModel(getTripDetailsUseCase, deleteTripUseCase, prefsRepository, weatherRepository);
  }
}

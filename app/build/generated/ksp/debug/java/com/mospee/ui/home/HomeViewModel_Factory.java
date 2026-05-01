package com.mospee.ui.home;

import android.location.LocationManager;
import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.data.repository.WeatherRepository;
import com.mospee.domain.usecase.GetAllTripsUseCase;
import com.mospee.domain.usecase.GetLastTripUseCase;
import com.mospee.location.LocationClient;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetLastTripUseCase> getLastTripUseCaseProvider;

  private final Provider<GetAllTripsUseCase> getAllTripsUseCaseProvider;

  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  private final Provider<LocationManager> locationManagerProvider;

  private final Provider<LocationClient> locationClientProvider;

  private final Provider<WeatherRepository> weatherRepositoryProvider;

  public HomeViewModel_Factory(Provider<GetLastTripUseCase> getLastTripUseCaseProvider,
      Provider<GetAllTripsUseCase> getAllTripsUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<LocationManager> locationManagerProvider,
      Provider<LocationClient> locationClientProvider,
      Provider<WeatherRepository> weatherRepositoryProvider) {
    this.getLastTripUseCaseProvider = getLastTripUseCaseProvider;
    this.getAllTripsUseCaseProvider = getAllTripsUseCaseProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
    this.locationManagerProvider = locationManagerProvider;
    this.locationClientProvider = locationClientProvider;
    this.weatherRepositoryProvider = weatherRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getLastTripUseCaseProvider.get(), getAllTripsUseCaseProvider.get(), prefsRepositoryProvider.get(), locationManagerProvider.get(), locationClientProvider.get(), weatherRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetLastTripUseCase> getLastTripUseCaseProvider,
      Provider<GetAllTripsUseCase> getAllTripsUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<LocationManager> locationManagerProvider,
      Provider<LocationClient> locationClientProvider,
      Provider<WeatherRepository> weatherRepositoryProvider) {
    return new HomeViewModel_Factory(getLastTripUseCaseProvider, getAllTripsUseCaseProvider, prefsRepositoryProvider, locationManagerProvider, locationClientProvider, weatherRepositoryProvider);
  }

  public static HomeViewModel newInstance(GetLastTripUseCase getLastTripUseCase,
      GetAllTripsUseCase getAllTripsUseCase, UserPreferencesRepository prefsRepository,
      LocationManager locationManager, LocationClient locationClient,
      WeatherRepository weatherRepository) {
    return new HomeViewModel(getLastTripUseCase, getAllTripsUseCase, prefsRepository, locationManager, locationClient, weatherRepository);
  }
}

package com.mospee.ui.home;

import android.location.LocationManager;
import com.mospee.data.repository.UserPreferencesRepository;
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

  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  private final Provider<LocationManager> locationManagerProvider;

  private final Provider<LocationClient> locationClientProvider;

  public HomeViewModel_Factory(Provider<GetLastTripUseCase> getLastTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<LocationManager> locationManagerProvider,
      Provider<LocationClient> locationClientProvider) {
    this.getLastTripUseCaseProvider = getLastTripUseCaseProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
    this.locationManagerProvider = locationManagerProvider;
    this.locationClientProvider = locationClientProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getLastTripUseCaseProvider.get(), prefsRepositoryProvider.get(), locationManagerProvider.get(), locationClientProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetLastTripUseCase> getLastTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<LocationManager> locationManagerProvider,
      Provider<LocationClient> locationClientProvider) {
    return new HomeViewModel_Factory(getLastTripUseCaseProvider, prefsRepositoryProvider, locationManagerProvider, locationClientProvider);
  }

  public static HomeViewModel newInstance(GetLastTripUseCase getLastTripUseCase,
      UserPreferencesRepository prefsRepository, LocationManager locationManager,
      LocationClient locationClient) {
    return new HomeViewModel(getLastTripUseCase, prefsRepository, locationManager, locationClient);
  }
}

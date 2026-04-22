package com.mospee.ui.home;

import android.location.LocationManager;
import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.domain.usecase.GetLastTripUseCase;
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

  public HomeViewModel_Factory(Provider<GetLastTripUseCase> getLastTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<LocationManager> locationManagerProvider) {
    this.getLastTripUseCaseProvider = getLastTripUseCaseProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
    this.locationManagerProvider = locationManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getLastTripUseCaseProvider.get(), prefsRepositoryProvider.get(), locationManagerProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetLastTripUseCase> getLastTripUseCaseProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<LocationManager> locationManagerProvider) {
    return new HomeViewModel_Factory(getLastTripUseCaseProvider, prefsRepositoryProvider, locationManagerProvider);
  }

  public static HomeViewModel newInstance(GetLastTripUseCase getLastTripUseCase,
      UserPreferencesRepository prefsRepository, LocationManager locationManager) {
    return new HomeViewModel(getLastTripUseCase, prefsRepository, locationManager);
  }
}

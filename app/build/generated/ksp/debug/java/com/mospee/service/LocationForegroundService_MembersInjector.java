package com.mospee.service;

import android.location.LocationManager;
import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.domain.repository.TripRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class LocationForegroundService_MembersInjector implements MembersInjector<LocationForegroundService> {
  private final Provider<TripRepository> repositoryProvider;

  private final Provider<LocationManager> locationManagerProvider;

  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  public LocationForegroundService_MembersInjector(Provider<TripRepository> repositoryProvider,
      Provider<LocationManager> locationManagerProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.locationManagerProvider = locationManagerProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
  }

  public static MembersInjector<LocationForegroundService> create(
      Provider<TripRepository> repositoryProvider,
      Provider<LocationManager> locationManagerProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider) {
    return new LocationForegroundService_MembersInjector(repositoryProvider, locationManagerProvider, prefsRepositoryProvider);
  }

  @Override
  public void injectMembers(LocationForegroundService instance) {
    injectRepository(instance, repositoryProvider.get());
    injectLocationManager(instance, locationManagerProvider.get());
    injectPrefsRepository(instance, prefsRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.mospee.service.LocationForegroundService.repository")
  public static void injectRepository(LocationForegroundService instance,
      TripRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.mospee.service.LocationForegroundService.locationManager")
  public static void injectLocationManager(LocationForegroundService instance,
      LocationManager locationManager) {
    instance.locationManager = locationManager;
  }

  @InjectedFieldSignature("com.mospee.service.LocationForegroundService.prefsRepository")
  public static void injectPrefsRepository(LocationForegroundService instance,
      UserPreferencesRepository prefsRepository) {
    instance.prefsRepository = prefsRepository;
  }
}

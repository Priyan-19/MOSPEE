package com.mospee;

import com.mospee.data.remote.FirebaseManager;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  private final Provider<FirebaseManager> firebaseManagerProvider;

  private final Provider<TripRepository> tripRepositoryProvider;

  public MainActivity_MembersInjector(Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<FirebaseManager> firebaseManagerProvider,
      Provider<TripRepository> tripRepositoryProvider) {
    this.prefsRepositoryProvider = prefsRepositoryProvider;
    this.firebaseManagerProvider = firebaseManagerProvider;
    this.tripRepositoryProvider = tripRepositoryProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<UserPreferencesRepository> prefsRepositoryProvider,
      Provider<FirebaseManager> firebaseManagerProvider,
      Provider<TripRepository> tripRepositoryProvider) {
    return new MainActivity_MembersInjector(prefsRepositoryProvider, firebaseManagerProvider, tripRepositoryProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPrefsRepository(instance, prefsRepositoryProvider.get());
    injectFirebaseManager(instance, firebaseManagerProvider.get());
    injectTripRepository(instance, tripRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.mospee.MainActivity.prefsRepository")
  public static void injectPrefsRepository(MainActivity instance,
      UserPreferencesRepository prefsRepository) {
    instance.prefsRepository = prefsRepository;
  }

  @InjectedFieldSignature("com.mospee.MainActivity.firebaseManager")
  public static void injectFirebaseManager(MainActivity instance, FirebaseManager firebaseManager) {
    instance.firebaseManager = firebaseManager;
  }

  @InjectedFieldSignature("com.mospee.MainActivity.tripRepository")
  public static void injectTripRepository(MainActivity instance, TripRepository tripRepository) {
    instance.tripRepository = tripRepository;
  }
}

package com.mospee.data.repository;

import com.mospee.data.local.dao.LocationPointDao;
import com.mospee.data.local.dao.TripDao;
import com.mospee.data.remote.FirebaseManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TripRepositoryImpl_Factory implements Factory<TripRepositoryImpl> {
  private final Provider<TripDao> tripDaoProvider;

  private final Provider<LocationPointDao> locationPointDaoProvider;

  private final Provider<FirebaseManager> firebaseManagerProvider;

  public TripRepositoryImpl_Factory(Provider<TripDao> tripDaoProvider,
      Provider<LocationPointDao> locationPointDaoProvider,
      Provider<FirebaseManager> firebaseManagerProvider) {
    this.tripDaoProvider = tripDaoProvider;
    this.locationPointDaoProvider = locationPointDaoProvider;
    this.firebaseManagerProvider = firebaseManagerProvider;
  }

  @Override
  public TripRepositoryImpl get() {
    return newInstance(tripDaoProvider.get(), locationPointDaoProvider.get(), firebaseManagerProvider.get());
  }

  public static TripRepositoryImpl_Factory create(Provider<TripDao> tripDaoProvider,
      Provider<LocationPointDao> locationPointDaoProvider,
      Provider<FirebaseManager> firebaseManagerProvider) {
    return new TripRepositoryImpl_Factory(tripDaoProvider, locationPointDaoProvider, firebaseManagerProvider);
  }

  public static TripRepositoryImpl newInstance(TripDao tripDao, LocationPointDao locationPointDao,
      FirebaseManager firebaseManager) {
    return new TripRepositoryImpl(tripDao, locationPointDao, firebaseManager);
  }
}

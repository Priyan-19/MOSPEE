package com.mospee.di;

import com.mospee.data.repository.TripRepositoryImpl;
import com.mospee.domain.repository.TripRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideTripRepositoryFactory implements Factory<TripRepository> {
  private final Provider<TripRepositoryImpl> implProvider;

  public AppModule_ProvideTripRepositoryFactory(Provider<TripRepositoryImpl> implProvider) {
    this.implProvider = implProvider;
  }

  @Override
  public TripRepository get() {
    return provideTripRepository(implProvider.get());
  }

  public static AppModule_ProvideTripRepositoryFactory create(
      Provider<TripRepositoryImpl> implProvider) {
    return new AppModule_ProvideTripRepositoryFactory(implProvider);
  }

  public static TripRepository provideTripRepository(TripRepositoryImpl impl) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTripRepository(impl));
  }
}

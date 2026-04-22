package com.mospee.domain.usecase;

import com.mospee.domain.repository.TripRepository;
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
public final class StartTripUseCase_Factory implements Factory<StartTripUseCase> {
  private final Provider<TripRepository> repositoryProvider;

  public StartTripUseCase_Factory(Provider<TripRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public StartTripUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static StartTripUseCase_Factory create(Provider<TripRepository> repositoryProvider) {
    return new StartTripUseCase_Factory(repositoryProvider);
  }

  public static StartTripUseCase newInstance(TripRepository repository) {
    return new StartTripUseCase(repository);
  }
}

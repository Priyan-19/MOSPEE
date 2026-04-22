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
public final class GetLastTripUseCase_Factory implements Factory<GetLastTripUseCase> {
  private final Provider<TripRepository> repositoryProvider;

  public GetLastTripUseCase_Factory(Provider<TripRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetLastTripUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetLastTripUseCase_Factory create(Provider<TripRepository> repositoryProvider) {
    return new GetLastTripUseCase_Factory(repositoryProvider);
  }

  public static GetLastTripUseCase newInstance(TripRepository repository) {
    return new GetLastTripUseCase(repository);
  }
}

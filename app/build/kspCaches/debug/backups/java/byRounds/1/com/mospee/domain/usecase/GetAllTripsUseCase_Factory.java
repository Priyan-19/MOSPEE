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
public final class GetAllTripsUseCase_Factory implements Factory<GetAllTripsUseCase> {
  private final Provider<TripRepository> repositoryProvider;

  public GetAllTripsUseCase_Factory(Provider<TripRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetAllTripsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetAllTripsUseCase_Factory create(Provider<TripRepository> repositoryProvider) {
    return new GetAllTripsUseCase_Factory(repositoryProvider);
  }

  public static GetAllTripsUseCase newInstance(TripRepository repository) {
    return new GetAllTripsUseCase(repository);
  }
}

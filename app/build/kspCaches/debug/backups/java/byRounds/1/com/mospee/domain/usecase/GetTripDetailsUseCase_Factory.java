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
public final class GetTripDetailsUseCase_Factory implements Factory<GetTripDetailsUseCase> {
  private final Provider<TripRepository> repositoryProvider;

  public GetTripDetailsUseCase_Factory(Provider<TripRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetTripDetailsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetTripDetailsUseCase_Factory create(Provider<TripRepository> repositoryProvider) {
    return new GetTripDetailsUseCase_Factory(repositoryProvider);
  }

  public static GetTripDetailsUseCase newInstance(TripRepository repository) {
    return new GetTripDetailsUseCase(repository);
  }
}

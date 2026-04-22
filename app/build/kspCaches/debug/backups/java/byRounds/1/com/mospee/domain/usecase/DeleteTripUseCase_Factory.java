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
public final class DeleteTripUseCase_Factory implements Factory<DeleteTripUseCase> {
  private final Provider<TripRepository> repositoryProvider;

  public DeleteTripUseCase_Factory(Provider<TripRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DeleteTripUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static DeleteTripUseCase_Factory create(Provider<TripRepository> repositoryProvider) {
    return new DeleteTripUseCase_Factory(repositoryProvider);
  }

  public static DeleteTripUseCase newInstance(TripRepository repository) {
    return new DeleteTripUseCase(repository);
  }
}

package com.mospee.ui.trip;

import android.content.Context;
import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.domain.repository.TripRepository;
import com.mospee.domain.usecase.StartTripUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LiveTripViewModel_Factory implements Factory<LiveTripViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<StartTripUseCase> startTripUseCaseProvider;

  private final Provider<TripRepository> tripRepositoryProvider;

  private final Provider<UserPreferencesRepository> prefsRepositoryProvider;

  public LiveTripViewModel_Factory(Provider<Context> contextProvider,
      Provider<StartTripUseCase> startTripUseCaseProvider,
      Provider<TripRepository> tripRepositoryProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.startTripUseCaseProvider = startTripUseCaseProvider;
    this.tripRepositoryProvider = tripRepositoryProvider;
    this.prefsRepositoryProvider = prefsRepositoryProvider;
  }

  @Override
  public LiveTripViewModel get() {
    return newInstance(contextProvider.get(), startTripUseCaseProvider.get(), tripRepositoryProvider.get(), prefsRepositoryProvider.get());
  }

  public static LiveTripViewModel_Factory create(Provider<Context> contextProvider,
      Provider<StartTripUseCase> startTripUseCaseProvider,
      Provider<TripRepository> tripRepositoryProvider,
      Provider<UserPreferencesRepository> prefsRepositoryProvider) {
    return new LiveTripViewModel_Factory(contextProvider, startTripUseCaseProvider, tripRepositoryProvider, prefsRepositoryProvider);
  }

  public static LiveTripViewModel newInstance(Context context, StartTripUseCase startTripUseCase,
      TripRepository tripRepository, UserPreferencesRepository prefsRepository) {
    return new LiveTripViewModel(context, startTripUseCase, tripRepository, prefsRepository);
  }
}

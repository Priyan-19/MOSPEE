package com.mospee;

import android.app.Activity;
import android.app.Service;
import android.location.LocationManager;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.mospee.data.local.AppDatabase;
import com.mospee.data.local.dao.LocationPointDao;
import com.mospee.data.local.dao.TripDao;
import com.mospee.data.remote.FirebaseManager;
import com.mospee.data.repository.TripRepositoryImpl;
import com.mospee.data.repository.UserPreferencesRepository;
import com.mospee.di.AppModule_ProvideDatabaseFactory;
import com.mospee.di.AppModule_ProvideFusedLocationProviderClientFactory;
import com.mospee.di.AppModule_ProvideLocationClientFactory;
import com.mospee.di.AppModule_ProvideLocationManagerFactory;
import com.mospee.di.AppModule_ProvideLocationPointDaoFactory;
import com.mospee.di.AppModule_ProvideTripDaoFactory;
import com.mospee.di.AppModule_ProvideTripRepositoryFactory;
import com.mospee.domain.repository.TripRepository;
import com.mospee.domain.usecase.DeleteTripUseCase;
import com.mospee.domain.usecase.GetAllTripsUseCase;
import com.mospee.domain.usecase.GetLastTripUseCase;
import com.mospee.domain.usecase.GetTripDetailsUseCase;
import com.mospee.domain.usecase.StartTripUseCase;
import com.mospee.location.LocationClient;
import com.mospee.service.LocationForegroundService;
import com.mospee.service.LocationForegroundService_MembersInjector;
import com.mospee.ui.history.HistoryViewModel;
import com.mospee.ui.history.HistoryViewModel_HiltModules;
import com.mospee.ui.home.HomeViewModel;
import com.mospee.ui.home.HomeViewModel_HiltModules;
import com.mospee.ui.settings.SettingsViewModel;
import com.mospee.ui.settings.SettingsViewModel_HiltModules;
import com.mospee.ui.summary.TripSummaryViewModel;
import com.mospee.ui.summary.TripSummaryViewModel_HiltModules;
import com.mospee.ui.trip.LiveTripViewModel;
import com.mospee.ui.trip.LiveTripViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerMospeeApp_HiltComponents_SingletonC {
  private DaggerMospeeApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MospeeApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MospeeApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MospeeApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MospeeApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MospeeApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MospeeApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MospeeApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MospeeApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MospeeApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MospeeApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MospeeApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MospeeApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MospeeApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>of(LazyClassKeyProvider.com_mospee_ui_history_HistoryViewModel, HistoryViewModel_HiltModules.KeyModule.provide(), LazyClassKeyProvider.com_mospee_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide(), LazyClassKeyProvider.com_mospee_ui_trip_LiveTripViewModel, LiveTripViewModel_HiltModules.KeyModule.provide(), LazyClassKeyProvider.com_mospee_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide(), LazyClassKeyProvider.com_mospee_ui_summary_TripSummaryViewModel, TripSummaryViewModel_HiltModules.KeyModule.provide()));
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPrefsRepository(instance, singletonCImpl.userPreferencesRepositoryProvider.get());
      MainActivity_MembersInjector.injectFirebaseManager(instance, singletonCImpl.firebaseManagerProvider.get());
      MainActivity_MembersInjector.injectTripRepository(instance, singletonCImpl.provideTripRepositoryProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_mospee_ui_settings_SettingsViewModel = "com.mospee.ui.settings.SettingsViewModel";

      static String com_mospee_ui_summary_TripSummaryViewModel = "com.mospee.ui.summary.TripSummaryViewModel";

      static String com_mospee_ui_home_HomeViewModel = "com.mospee.ui.home.HomeViewModel";

      static String com_mospee_ui_history_HistoryViewModel = "com.mospee.ui.history.HistoryViewModel";

      static String com_mospee_ui_trip_LiveTripViewModel = "com.mospee.ui.trip.LiveTripViewModel";

      @KeepFieldType
      SettingsViewModel com_mospee_ui_settings_SettingsViewModel2;

      @KeepFieldType
      TripSummaryViewModel com_mospee_ui_summary_TripSummaryViewModel2;

      @KeepFieldType
      HomeViewModel com_mospee_ui_home_HomeViewModel2;

      @KeepFieldType
      HistoryViewModel com_mospee_ui_history_HistoryViewModel2;

      @KeepFieldType
      LiveTripViewModel com_mospee_ui_trip_LiveTripViewModel2;
    }
  }

  private static final class ViewModelCImpl extends MospeeApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<HistoryViewModel> historyViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LiveTripViewModel> liveTripViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<TripSummaryViewModel> tripSummaryViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetAllTripsUseCase getAllTripsUseCase() {
      return new GetAllTripsUseCase(singletonCImpl.provideTripRepositoryProvider.get());
    }

    private DeleteTripUseCase deleteTripUseCase() {
      return new DeleteTripUseCase(singletonCImpl.provideTripRepositoryProvider.get());
    }

    private GetLastTripUseCase getLastTripUseCase() {
      return new GetLastTripUseCase(singletonCImpl.provideTripRepositoryProvider.get());
    }

    private StartTripUseCase startTripUseCase() {
      return new StartTripUseCase(singletonCImpl.provideTripRepositoryProvider.get());
    }

    private GetTripDetailsUseCase getTripDetailsUseCase() {
      return new GetTripDetailsUseCase(singletonCImpl.provideTripRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.historyViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.liveTripViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.tripSummaryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>of(LazyClassKeyProvider.com_mospee_ui_history_HistoryViewModel, ((Provider) historyViewModelProvider), LazyClassKeyProvider.com_mospee_ui_home_HomeViewModel, ((Provider) homeViewModelProvider), LazyClassKeyProvider.com_mospee_ui_trip_LiveTripViewModel, ((Provider) liveTripViewModelProvider), LazyClassKeyProvider.com_mospee_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider), LazyClassKeyProvider.com_mospee_ui_summary_TripSummaryViewModel, ((Provider) tripSummaryViewModelProvider)));
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_mospee_ui_settings_SettingsViewModel = "com.mospee.ui.settings.SettingsViewModel";

      static String com_mospee_ui_summary_TripSummaryViewModel = "com.mospee.ui.summary.TripSummaryViewModel";

      static String com_mospee_ui_history_HistoryViewModel = "com.mospee.ui.history.HistoryViewModel";

      static String com_mospee_ui_trip_LiveTripViewModel = "com.mospee.ui.trip.LiveTripViewModel";

      static String com_mospee_ui_home_HomeViewModel = "com.mospee.ui.home.HomeViewModel";

      @KeepFieldType
      SettingsViewModel com_mospee_ui_settings_SettingsViewModel2;

      @KeepFieldType
      TripSummaryViewModel com_mospee_ui_summary_TripSummaryViewModel2;

      @KeepFieldType
      HistoryViewModel com_mospee_ui_history_HistoryViewModel2;

      @KeepFieldType
      LiveTripViewModel com_mospee_ui_trip_LiveTripViewModel2;

      @KeepFieldType
      HomeViewModel com_mospee_ui_home_HomeViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.mospee.ui.history.HistoryViewModel 
          return (T) new HistoryViewModel(viewModelCImpl.getAllTripsUseCase(), viewModelCImpl.deleteTripUseCase(), singletonCImpl.userPreferencesRepositoryProvider.get());

          case 1: // com.mospee.ui.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.getLastTripUseCase(), singletonCImpl.userPreferencesRepositoryProvider.get(), singletonCImpl.provideLocationManagerProvider.get(), singletonCImpl.provideLocationClientProvider.get());

          case 2: // com.mospee.ui.trip.LiveTripViewModel 
          return (T) new LiveTripViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), viewModelCImpl.startTripUseCase(), singletonCImpl.provideTripRepositoryProvider.get(), singletonCImpl.userPreferencesRepositoryProvider.get(), singletonCImpl.provideLocationManagerProvider.get(), singletonCImpl.provideLocationClientProvider.get());

          case 3: // com.mospee.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.userPreferencesRepositoryProvider.get());

          case 4: // com.mospee.ui.summary.TripSummaryViewModel 
          return (T) new TripSummaryViewModel(viewModelCImpl.getTripDetailsUseCase(), viewModelCImpl.deleteTripUseCase(), singletonCImpl.userPreferencesRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MospeeApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MospeeApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectLocationForegroundService(
        LocationForegroundService locationForegroundService) {
      injectLocationForegroundService2(locationForegroundService);
    }

    @CanIgnoreReturnValue
    private LocationForegroundService injectLocationForegroundService2(
        LocationForegroundService instance) {
      LocationForegroundService_MembersInjector.injectRepository(instance, singletonCImpl.provideTripRepositoryProvider.get());
      LocationForegroundService_MembersInjector.injectLocationManager(instance, singletonCImpl.provideLocationManagerProvider.get());
      LocationForegroundService_MembersInjector.injectPrefsRepository(instance, singletonCImpl.userPreferencesRepositoryProvider.get());
      LocationForegroundService_MembersInjector.injectLocationClient(instance, singletonCImpl.provideLocationClientProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends MospeeApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

    private Provider<FirebaseManager> firebaseManagerProvider;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<TripRepositoryImpl> tripRepositoryImplProvider;

    private Provider<TripRepository> provideTripRepositoryProvider;

    private Provider<LocationManager> provideLocationManagerProvider;

    private Provider<FusedLocationProviderClient> provideFusedLocationProviderClientProvider;

    private Provider<LocationClient> provideLocationClientProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private TripDao tripDao() {
      return AppModule_ProvideTripDaoFactory.provideTripDao(provideDatabaseProvider.get());
    }

    private LocationPointDao locationPointDao() {
      return AppModule_ProvideLocationPointDaoFactory.provideLocationPointDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.userPreferencesRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesRepository>(singletonCImpl, 0));
      this.firebaseManagerProvider = DoubleCheck.provider(new SwitchingProvider<FirebaseManager>(singletonCImpl, 1));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 4));
      this.tripRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<TripRepositoryImpl>(singletonCImpl, 3));
      this.provideTripRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TripRepository>(singletonCImpl, 2));
      this.provideLocationManagerProvider = DoubleCheck.provider(new SwitchingProvider<LocationManager>(singletonCImpl, 5));
      this.provideFusedLocationProviderClientProvider = DoubleCheck.provider(new SwitchingProvider<FusedLocationProviderClient>(singletonCImpl, 7));
      this.provideLocationClientProvider = DoubleCheck.provider(new SwitchingProvider<LocationClient>(singletonCImpl, 6));
    }

    @Override
    public void injectMospeeApp(MospeeApp mospeeApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.mospee.data.repository.UserPreferencesRepository 
          return (T) new UserPreferencesRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.mospee.data.remote.FirebaseManager 
          return (T) new FirebaseManager();

          case 2: // com.mospee.domain.repository.TripRepository 
          return (T) AppModule_ProvideTripRepositoryFactory.provideTripRepository(singletonCImpl.tripRepositoryImplProvider.get());

          case 3: // com.mospee.data.repository.TripRepositoryImpl 
          return (T) new TripRepositoryImpl(singletonCImpl.tripDao(), singletonCImpl.locationPointDao(), singletonCImpl.firebaseManagerProvider.get());

          case 4: // com.mospee.data.local.AppDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // android.location.LocationManager 
          return (T) AppModule_ProvideLocationManagerFactory.provideLocationManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.mospee.location.LocationClient 
          return (T) AppModule_ProvideLocationClientFactory.provideLocationClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideFusedLocationProviderClientProvider.get());

          case 7: // com.google.android.gms.location.FusedLocationProviderClient 
          return (T) AppModule_ProvideFusedLocationProviderClientFactory.provideFusedLocationProviderClient(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}

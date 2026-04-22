package com.mospee.di;

import android.content.Context;
import android.location.LocationManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideLocationManagerFactory implements Factory<LocationManager> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideLocationManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LocationManager get() {
    return provideLocationManager(contextProvider.get());
  }

  public static AppModule_ProvideLocationManagerFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideLocationManagerFactory(contextProvider);
  }

  public static LocationManager provideLocationManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLocationManager(context));
  }
}

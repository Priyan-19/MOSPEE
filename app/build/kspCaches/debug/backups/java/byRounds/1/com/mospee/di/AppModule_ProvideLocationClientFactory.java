package com.mospee.di;

import android.content.Context;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.mospee.location.LocationClient;
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
public final class AppModule_ProvideLocationClientFactory implements Factory<LocationClient> {
  private final Provider<Context> contextProvider;

  private final Provider<FusedLocationProviderClient> clientProvider;

  public AppModule_ProvideLocationClientFactory(Provider<Context> contextProvider,
      Provider<FusedLocationProviderClient> clientProvider) {
    this.contextProvider = contextProvider;
    this.clientProvider = clientProvider;
  }

  @Override
  public LocationClient get() {
    return provideLocationClient(contextProvider.get(), clientProvider.get());
  }

  public static AppModule_ProvideLocationClientFactory create(Provider<Context> contextProvider,
      Provider<FusedLocationProviderClient> clientProvider) {
    return new AppModule_ProvideLocationClientFactory(contextProvider, clientProvider);
  }

  public static LocationClient provideLocationClient(Context context,
      FusedLocationProviderClient client) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLocationClient(context, client));
  }
}

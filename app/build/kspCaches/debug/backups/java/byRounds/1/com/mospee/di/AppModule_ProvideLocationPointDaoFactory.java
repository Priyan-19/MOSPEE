package com.mospee.di;

import com.mospee.data.local.AppDatabase;
import com.mospee.data.local.dao.LocationPointDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideLocationPointDaoFactory implements Factory<LocationPointDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_ProvideLocationPointDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public LocationPointDao get() {
    return provideLocationPointDao(dbProvider.get());
  }

  public static AppModule_ProvideLocationPointDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_ProvideLocationPointDaoFactory(dbProvider);
  }

  public static LocationPointDao provideLocationPointDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLocationPointDao(db));
  }
}

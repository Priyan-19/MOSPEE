package com.mospee.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FirebaseManager_Factory implements Factory<FirebaseManager> {
  @Override
  public FirebaseManager get() {
    return newInstance();
  }

  public static FirebaseManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseManager newInstance() {
    return new FirebaseManager();
  }

  private static final class InstanceHolder {
    private static final FirebaseManager_Factory INSTANCE = new FirebaseManager_Factory();
  }
}

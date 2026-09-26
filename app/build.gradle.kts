plugins {
    id("com.android.application")
}

android {
    namespace = "vn.edu.vhu.ltdd.a2stopwatch"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        applicationId = "vn.edu.vhu.ltdd.a2stopwatch"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

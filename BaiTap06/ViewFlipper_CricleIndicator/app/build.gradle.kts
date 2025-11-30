plugins {
    alias(libs.plugins.android.application)
}

// flatDir repo moved to settings.gradle.kts because dependencyResolutionManagement forbids project repos

android {
    namespace = "vn.hcmute.viewflipper_cricleindicator"
    compileSdk = 36

    defaultConfig {
        applicationId = "vn.hcmute.viewflipper_cricleindicator"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.viewpager2)
    implementation(libs.glide)
    annotationProcessor(libs.glideCompiler)
    implementation(libs.circleindicator)
    implementation(libs.retrofit)
    implementation(libs.retrofitConverterGson)

    // JitPack dependency for autoimageslider is commented out so Gradle won't attempt to fetch it
    // (the project contains a local compatibility shim under com.smarteist.autoimageslider)
    // implementation("com.github.smarteist:autoimageslider:1.4.0")

    // If you prefer to use the real library and can't fetch it from JitPack,
    // download the AAR and place it into app/libs/ and uncomment the following:
    // implementation(name = "autoimageslider-1.4.0", ext = "aar")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
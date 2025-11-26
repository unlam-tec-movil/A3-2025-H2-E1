import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.daggerHiltAndroid)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.compose.compiler)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
        apiVersion.set(KotlinVersion.KOTLIN_2_1)
    }
}

android {
    namespace = "ar.edu.unlam.mobile.scaffolding"
    compileSdk = 36

    defaultConfig {
        applicationId = "ar.edu.unlam.mobile.scaffolding"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        // API keys
        val p = Properties()
        val localProps = File(rootProject.rootDir, "local.properties")
        if (localProps.exists()) p.load(FileInputStream(localProps))
        buildConfigField("String", "API_KEY", "\"${p.getProperty("API_KEY", "")}\"")
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("boolean", "AUTO_LOGIN", "false")
            buildConfigField("String", "DEV_TOKEN", "\"dev_token_123\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("boolean", "AUTO_LOGIN", "false")
            buildConfigField("String", "DEV_TOKEN", "\"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        disable += "PermissionLaunchedDuringComposition"
    }
}

dependencies {

    // BASE

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icon)

    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation.layout)

    // Servicios de Google
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Coil
    implementation(libs.coil)

    // OSMDroid
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Permisos en Compose
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    // Preferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // RETROFIT

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // OnBoarding Pager
    implementation(libs.foundation.pager)

    // HILT

    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Testing para Hilt
    androidTestImplementation(libs.google.dagger.hilt.android.testing)
    testImplementation(libs.google.dagger.hilt.android.testing)

    // TESTS UNITARIOS

    testImplementation(libs.junit)
    testImplementation(libs.org.mockito.android)
    debugImplementation(libs.org.mockito.kotlin)
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    // ANDROID TEST (Instrumented + Compose UI Tests)

    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Versiones correctas obligatorias
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    // Compose UI Testing
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Solo en debug (manifest + tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)

    // Framework Android Test Helpers
    debugImplementation(libs.androidx.test.core)
    testImplementation(kotlin("test"))
}

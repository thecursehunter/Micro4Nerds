plugins {
    alias(libs.plugins.android.application)
    id ("com.google.gms.google-services") // Kích hoạt plugin
}

android {
    namespace = "ueh.edu.vn.md.micro4nerds"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ueh.edu.vn.md.micro4nerds"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import Firebase BoM (Chỉ cần chỉnh version ở dòng này)
    implementation (platform("com.google.firebase:firebase-bom:34.6.0"))

    // Các thư viện con (KHÔNG cần ghi số version nữa -> Tự động đồng bộ)
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-storage") // Để lưu ảnh upload từ Admin
    implementation ("com.google.android.gms:play-services-auth:21.0.0") // Cho Google Sign-In
    implementation ("de.hdodenhof:circleimageview:3.1.0")
}
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Kích hoạt plugin
}

android {
    namespace = "ueh.edu.vn.md.micro4nerds"
    compileSdk = 34

    defaultConfig {
        applicationId = "ueh.edu.vn.md.micro4nerds"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
    implementation("androidx.core:core-splashscreen:1.0.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import Firebase BoM (Chỉ cần chỉnh version ở dòng này)
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    // Các thư viện con (KHÔNG cần ghi số version nữa -> Tự động đồng bộ)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage") // Để lưu ảnh upload từ Admin
    implementation("com.google.android.gms:play-services-auth:21.0.0") // Cho Google Sign-In
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Thư viện Glide, thư viện load ảnh mạnh nhất hiện nay cho Android
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Thư viện Gson
    implementation("com.google.code.gson:gson:2.10.1")
}

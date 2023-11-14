# SpeechSuper Android Kotlin Demo

This demo provides an example of how to integrate the SpeechSuper Android Kotlin SDK into your Android app for pronunciation assessment. Follow these steps to get started:

## Step 1: Configure Your Keys
1. Open the file `app/src/main/java/com/example/demo_for_android_kotlin/MainActivity.kt`.
2. Insert your `appKey` and `secretKey` into the following lines:

```kotlin
   private val appKey = "Insert your appKey here"
   private val secretKey = "Insert your secretKey here"
```


## Step 2: Customize Your Inputs

1. Open the file `app/src/main/java/com/example/demo_for_android_kotlin/TestActivity.kt`.
2. Modify the input parameters according to your needs in the following code block:
    ```kotlin
      val requestObj = JSONObject()
      requestObj.put("coreType", coreType)
      requestObj.put("refText", refText)

       SkegnManager.getInstance(this).startSkegn(requestObj, object: SkegnManager.CallbackResult{
            override fun run(response: String){
                setResult(response)
           }
       });
    ```

## Step 3: Run the Application
1. Run the application on your device or emulator.
2. Click on the item on the screen to navigate to the evaluation screen.

## Step 4: Start and Stop Evaluation
1. On the evaluation screen, click the "START EVAL" button to begin recording and evaluation.
2. Click the "STOP EVAL" button to stop recording and await the results.

## Additional Tips:

### 1. `build.gradle` Configuration
Ensure that your `build.gradle` file includes NDK and sourceSets configurations as follows:
```groovy
android {
    ...
    defaultConfig {
        ...

        ndk {
            abiFilters "armeabi", "armeabi-v7a", "arm64-v8a", "x86"
        }
    }
    ...
    sourceSets {
        main {
            jniLibs.srcDirs = ['libs']
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs')
   ...
}
```
    
### 2. `proguard-rules.pro` Configuration
    
```
...
-keep class com.speechsuper.** { *; }
-keepclasseswithmembernames class * {
    native <methods>;
}
```


# Permission Manager
This is a quick library to handle runtime permissions inline using RXJava2 for V1.x.x and RxJava3 for V2. `Supports > 14`

## Download from repository
This library can be downloaded from repository by adding the following into Android studio gradle file.
```
allProjects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
dependencies {
  compile 'com.github.ksughosh:PermissionsManager:2.0'
}
```

## How to use it
Using this library is simple. Initialize the `PermissionManager` in the `Application` or `Activity`

```kotlin
open class BaseActivity : AppCompatActivity() {
    protected lateinit var permissionManager: PermissionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionManager = PermissionManager.Builder()
                .setActivity(this)    // or use .setApplication(application) in AppClass for DI
                .setEnablesStore(true)
                .build()                   
    }
    
    private fun examplePermissionRequest() {
        permissionManager.requestPermission(
            Manifest.permission.CAMERA, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe (
                {
                  when (it) {
                      is Granted -> Log.d(TAG, "permission granted ${it.permissions}")
                      is Denied -> Log.d(TAG, "permission denied ${it.permissions}")
                      is DeniedForever -> Log.d(TAG, "permission denied forever ${it.permissions}")
                      is Mixed -> Log.d(TAG, "permission Mixed ${it.permissions} " +
                              "with responses ${it.responses.map { it::class.java.simpleName }}")
                  }
                  grantedPermissionCheck()
                }, { Log.e(TAG, "Error", it) }
        )
    }
}
```
To request all permissions on manifest just use `requestManifestPermission` API in the `PermissionManager`. To configure your own set of permissions you can:

```kotlin
  fun askPermission(){
    permissionManager.requestManifestPermission()
        .subscribe(
          {
            when (it) {
                is Granted -> Log.d(TAG, "permission granted ${it.permissions}")
                is Denied -> Log.d(TAG, "permission denied ${it.permissions}")
                is DeniedForever -> Log.d(TAG, "permission denied forever ${it.permissions}")
                is Mixed -> Log.d(TAG, "permission Mixed ${it.permissions} " +
                        "with responses ${it.responses.map { it::class.java.simpleName }}")
            }
          }, { Log.e(TAG, "Error", it) }
        )
  }
```

__Version 2.0__ Addition was checking permission that will provide an `Observable<PermissionData>`. This can be called like:

```kotlin
  fun checkPermission() {
        permissionManager.checkPermission(
                *permissionManager.allManifestPermissions
        ).subscribe { permissionData ->
            Log.d(TAG, "permission checked ${permissionData.permission}, " +
                    "granted:${permissionData.isGranted}, " +
                    "isInStore: ${permissionData.isInStoreValue?.type}"
            )
        }
  }

```
### Change Log V2.0

- New Kotlin version port
- RxJava 3 dependency inclusion
- New contract functionality
- Updated packages
- Clear separation of classes and responsibilities
- Store functionality to save permission result for later comparison
- New API functions
- Version updates

# License
The MIT License (MIT)

Copyright © SUGHOSH KRISHNA KUMAR

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

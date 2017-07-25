# Permission Manager
This is a quick library to handle runtime permissions inline using RXJava2.

## Download from repository
This library can be downloaded from repository by adding the following into Android studio gradle file.
```
allProjects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
dependencies {
  compile "com.github.ksughosh:PermissionsManager:${latest_version}"
}
```

## How to use it
Using this library is simple. You have to extend `PermissionActivity` or `PermissionFragment` that implements the interface `PermissionView`. If you have your own inheritance for Activities or Fragments, just implement the `PermissionView` interface and provide the callbacks to methods respectively. Creative approaches would be with Dagger2 dependency injection and streamlined inheritance. Otherwise, the simple approach is:

```kotlin
class ExampleActivity : PermissionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = PermissionManager(this)
        manager.cameraPermission.subscribe { permission ->
            Log.d("PERMISSION", "Granted: " + permission.isGranted 
                    + "\nis permission asked: " + permission.hasAskedPermission
                    + "\nnever ask permission: " + permission.neverAskPermission)
        }
    }
}
```
To ask custom permissions, use the `requestPermission(permission)` method in the `PermissionManager`. To configure your own set of permissions you can:

```kotlin
  fun askPermission(){
    val permissions = Permission()
    permissions.permissionArray = 
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, 
                Manifest.permission.BODY_SENSORS, 
                Manifest.permission.CAMERA)
                
        permissionManager.requestPermission(permissions).subscribe { perm ->
            Log.d("PERMISSION", "Granted: "+ perm.isGranted 
                    + "\nasked: " + perm.hasAskedPermission
                    + "\nnever: " + perm.neverAskPermission)
        }
  }
```

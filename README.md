:warning: Be care - not stable
#Install
1. clone repository
2. import `opencv310` java sdk module
3. add module dependency to your app
4. copy required cpu architecture to `/app/src/main/jniLibs`
5. config `javah` and `ndk-build.cmd`

If you have problem with :app:compileDebugNdk

Specify path:
`{YourApp} / app / build.gradle`

Place it inside the `defaultConfig` section.

```
defaultConfig {
    ....
    sourceSets.main {
        jniLibs.srcDir 'src/main/libs'
        jni.srcDirs = [] //disable automatic ndk-build call
    }
```

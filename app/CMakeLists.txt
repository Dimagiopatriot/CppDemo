# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

set(pathToProject /home/d_smishny/AndroidStudioProjects/CppDemo)
set(pathToOpenCv /home/d_smishny/CipherMe_Android_App/OpenCV-android-sdk)

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#Two sets suggested by Bruno Alexandre Krinski 20160825
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#Addition suggested by Bruno Alexandre Krinski 20160825
include_directories(${pathToOpenCv}/sdk/native/jni/include)

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

#Addition suggested by Bruno Alexandre Krinski 20160825
add_library( lib_opencv SHARED IMPORTED )
#Addition suggested by Bruno Alexandre Krinski 20160825
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathToProject}/app/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

# target_link_libraries( # Specifies the target library.
#                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
#                       ${log-lib} )

target_link_libraries( native-lib $\{log-lib} lib_opencv)
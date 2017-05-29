# Battyboost for Android

Prototyping repo for internal use.

## Getting started

Install the latest stable release of [Android Studio](https://developer.android.com/studio/index.html). Android Studio's
installation procedure is a bit quirky as it won't import a project if it can't find the necessary Android dependencies.
At the same time, in order to install any dependencies, you need to have a project open. The best way to solve this is
to create a new throw-away project.

From that open project, download the necessary dependencies via Tools > Android > SDK Manager. Here select and install

- the latest SDK Platform. Show package details and include a Google API Atom 64 bit system image
- the Android Emulator, and Intel x86 Emulator Accelerator (HAXM)
- the Google Repository that is part of the Support Repository

Next, manually complete the HAXM accelerator installation. Check the SDK Manager HAXM documentation for where to find
the downloaded installer binary. The emulator is next to useless without the accelerator.

Create a new virtual device via Tools > Android > AVD Manager. You should now be able to test-run the project that you
created on the virtual device, via Run > Run 'app'. If successful, close and delete that project.

You can now use Android Studio's start dialog for importing the battyboost-android project from GitHub.

## Developing

The battyboost-android project consists of several Phone & Tablet modules (those starting with `app-*`) and one Android
Library module (`core`). I use the
[Data Binding Library](https://developer.android.com/topic/libraries/data-binding/index.html) for connecting layouts
with code. All write access goes through `BattyboostClient` in the `core` module.

## License

Copyright (C) 2017 Matthias Schmitt. All rights reserved.

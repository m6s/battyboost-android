# battyboost for Android

Prototyping repo for internal use.

## Getting started

See [AndroidStudio](Documentation/AndroidStudio.md) for instructions on installing the Android SDK and development
environment.

## Developing

The battyboost-android project consists of several Phone & Tablet modules (those starting with `app-*`) and one Android
Library module (`core`). I use the
[Data Binding Library](https://developer.android.com/topic/libraries/data-binding/index.html) for connecting layouts
with code. All write access goes through `BattyboostClient` in the `core` module.

## License

Copyright (C) 2017 Matthias Schmitt. All rights reserved.

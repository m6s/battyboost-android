# Getting started with Android Studio

Install the latest stable release of [Android Studio](https://developer.android.com/studio/index.html). Android Studio's
installation procedure is a bit quirky as it won't import a project if it can't find the necessary Android dependencies.
At the same time, in order to install any dependencies, you need to have a project open. The best way to solve this is
to create a new throw-away project.

From that open project, download the necessary dependencies via Tools > Android > SDK Manager.

On the *SDK Platforms* tab select and install the latest SDK Platform. Check the *Show Package Details* check box in
the bottom right corner. Include a Google Play Intel x86 Atom system image.

On the *SDK Tools* tab select and install the the Android Emulator, and Intel x86 Emulator Accelerator (HAXM). Leave the
other default selection in place. Check the *Show Package Details* check box in the bottom right corner. Make sure the
Google Repository, which is part of the Support Repository, is selected.

Accept your selection to start the download.

After the download is complete, create a new virtual device via Tools > Android > AVD Manager. Choose Nexus 4 as
device, but it doesn't really matter. You should be able to test-run the project that you created on the virtual device,
via Run > Run 'app'. If successful, close and delete that project.

You can now use Android Studio's start dialog for importing the battyboost-android project from version control. Choose
GitHub, not Git, and leave the default host in place when entering your GitHub account data. There will be a second
dialog that asks you for the repository url that you want to check out.

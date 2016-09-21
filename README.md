Floating Windows for Android
----------------------------

### About

Floating Windows for Android, provides an API to display multiple
floating windows within (or without) an activity.  
 It includes all the required methods like `show(), dismiss(), lock()…`
among others in order to have full management of the floating windows
lifecycle.

### Screenshots

![](https://github.com/diegoRodriguezAguila/FloatingWindows/blob/master/screenshots/screenshot-1.png?raw=true =360x576)
![](https://github.com/diegoRodriguezAguila/FloatingWindows/blob/master/screenshots/screenshot-2.png?raw=true =360x576)

### Setup

To setup de library just add the dependency to your module-level gradle:

`compile 'com.diroag.floatingwindows:floatingwindows:0.1.1'`

### Usage

In order to learn how to use the library you can check the [sample
app](https://github.com/diegoRodriguezAguila/FloatingWindows/tree/master/sample).  

The first step is to inherit from `FloatingWindowView`. It is an
abstract class which requires you to implement the method:
`View onCreateView(LayoutInflater inflater)`.
 The classes you extend from it will be your floating window views, you
can create as many as you need. Check it on the sample:
[SampleView](https://github.com/diegoRodriguezAguila/FloatingWindows/blob/master/sample/src/main/java/com/diroag/floatingwindows/sample/floating/SampleView.java)

Once we got our floating window views, next step is to get an instance
of the `FloatingWindowController` class, which controlls all sort of
window operations.
 To do it you could call it on your activity `onCreate` as following:
`FloatingWindowController fwc = FloatingWindowController.create(this);`  
 With the controller class now you are able to show your floating
windows via the methods: `show(…)` or `showAtLocation(…)`. For a
complete example look at the sample's
[MainActivity](https://github.com/diegoRodriguezAguila/FloatingWindows/blob/master/sample/src/main/java/com/diroag/floatingwindows/sample/MainActivity.java)

There are methods that you can call from the controller class or your
floating window views like: `dismiss(), lock(), unlock()`, but the show
methods can only be called from the controller.

### License

Copyright 2015 Diego Rodriguez

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

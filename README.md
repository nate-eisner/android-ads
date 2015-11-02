# AndroidAds (Beta)
A simple Android library that allows an ad slideshow to be added into any view in your app. A request is made to your server that hosts a XML file that lists urls for the location of ads to be displayed in your app. The images are then cached on disk for a simple slideshow to run through each image.

# Start Here

[![Release](https://img.shields.io/github/release/nateisner/android-ads.svg?label=JitPack)](https://jitpack.io/#nateisner/android-ads/)

You must first add JitPack.io to the repositories list in your app's build.gradle file:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

Then, add this to the list of dependencies:

```gradle
dependencies {
    compile 'com.github.nateisner:androidads:0.1.0'
}

```
## Usage

Add a `ViewGroup` to your layout that will act as the root for the images. This can be a `RelativeLayout`, `LinearLayout`, `CoordinatorLayout` ...etc. The sizes of this view will be the size of the ads shown.

Example:


```
<RelativeLayout
        android:id="@+id/adContainer"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_gravity="bottom" />
```
    
        
Then, you can build your AdProvider. Using supported features.


`HeaderFields` object is used to add fields to any HTTP request made by your `AdProvider` Example:  

```
HeaderFields keyheaders = new HeaderFields();
keyheaders.add("FieldName","value");
keyheaders.add("FieldName","valueofanother");
``` 

The header can now be used when building your `AdProvider` like so:

```
new AdProvider.Builder(context)
    .imageServer(serverStringURL) 
    .parseTag("imagefilename") 
    .attachTo(rootView) 
    .imageTime(5000) //in ms
    .useAPIKey(keyserver, keyheaders, keyHeaderName)
    .start();
```


Other features include:

* Additional HeaderFields can be added for use when making the XML request.

    `.requestHeader(HeaderField moreheaders)`

* A Placeholder for when images are loading or cannot be found. 

    `.placeholder(Drawable)`
    

* The disk cache size can be increased or decreased. The default is set to 6 images if the xml has more than the amount set, it will attempt to load from server once it reaches the max on disk.

    `.cacheSize(int size)`
    
* Offline mode will enable your app to use what ever images it has cached on disk if there is no connection with the server. (This feature is not fully implemented yet)

    `.offlineUse(true)`

## Developed By

* Nate Eisner - nate@eisner.io

## License

```
Copyright 2015 Nathan Eisner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

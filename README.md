# AndroidAds (Beta)
A simple Android library that allows an ad slideshow to be added into any view in your app. This allows you to host and control your own ads. Your server sends out a XML response and the `AdProvider` parses that XML, looking for urls located in the set `.parseTag`.  

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
    compile 'com.github.nateisner:android-ads:0.1.1'
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
    
        
Then, you can build your `AdProvider`. Using supported features.

```
new AdProvider.Builder(context)
    .imageServer(serverStringURL) 
    .parseTag("imagefilename") 
    .attachTo(rootView) 
    .imageTime(5000) //in ms
    .useAPIKey(keyserver, keyheaders, keyHeaderName)
    .start();
```


Features include:

* Additional HeaderFields can be added for use when making the XML request.
    
    `HeaderFields` object is used to add fields to any HTTP request made by your `AdProvider` Example:  

    ```
    HeaderFields moreheaders = new HeaderFields();
    moreheaders.add("X-FieldName1","value");
    moreheaders.add("X-FieldName2","valueofanother");
    ``` 

    `.requestHeader(HeaderField moreheaders)`

* A Placeholder for when images are loading or cannot be found. 

    `.placeholder(Drawable)`
    

* The disk cache size can be increased or decreased. The default is set to 6 images if the xml has more than the amount set, it will attempt to load from server once it reaches the max on disk.

    `.cacheSize(int size)`
    
* Offline mode will enable your app to use what ever images it has cached on disk if there is no connection with the server. The images that will be shown are the last images that were cached and hopefully not cleared by the system or user. (This feature is not fully implemented yet)

    `.offlineUse(true)`
    
## How It Works

#### Your Ad Server

* You must have a backend server that hosts all of your images and generates a list of the images in a XML ~~or JSON~~ response 
* The response is structured something like this:

    ```
    <?xml version="1.0" encoding="utf-8"?>
    <xml>
      <albumid>11</albumid>
      <albumtitle>Title</albumtitle>
      <albumdescription>This is an album full of ...</albumdescription>
      <images>
        <image>
          <imageid>26</imageid>
          <imagedescription>Desc of image</imagedescription>
          <imagetitle>Title</imagetitle>
          <imagefilename>http://yourserver.com/561c3c48293d09.81340072/561c3daca3c06.png</imagefilename>
        </image>
        <image>
          <imageid>27</imageid>
          <imagedescription>Desc of image</imagedescription>
          <imagetitle>Title</imagetitle>
          <imagefilename>http://yourserver.com/561c3c48293d09.81340072/561c3dc5c1413.png</imagefilename>
        </image>
      </images>
    </xml>
    ```
    The internal parser parses through the XML for a `.parseTag` that is set when building your `AdProvider`. In this example you would set `.parseTag` to "imagefilename".

* API Key - Sometimes you may want to limit your server's access by using an API key in your HTTP request header. The `AdProvider` can be built with `.useAPIKey()`. Such as:
    
    ```
    //HeaderFields object that maybe needed to get the response
    HeaderFields keyheaders = new HeaderFields();
    keyheaders.add("X-FIELD-NAME", "value");
    
    //String of the url that will repond with the key
    String keyserver = "http://www.apikeyserver.com";
    
    //String name of the HeaderField the key is used in the XML request.
    String keyHeaderField = "X-API-KEY";
    
    .useAPIKey(keyserver, keyheaders, keyHeaderField);
    ```
    The server should respond with `<?xml version="1.0" encoding="utf-8"?>
                                     <xml>
                                       <item>keyvaluehere</item>
                                     </xml>` and that key value will be used for the rest of the session.
                                     
#### Your App

* The main purpose of this library is to simplify a way to insert ads into your app. By using `.attachTo` to insert ads in a `ViewGroup`. This will be a set `View` that you can either programmatically implement or add into your layout's XML. In the example, I have attached the ads in a `RelativeLayout` and set the parameters to specify location and size. The images will crop and fit inside of that view.
* The `AdProvider` has a `stop()` and `restart()` methods that can be used in your activity's `onStop()` or `onResume()` if you don't want the AdProvider to continue to (attempt) run in the background.
* The file cache holds images until cleared by the system or the user. (or overwritten by new instance of this library) This will allow displaying ads with no network connectivity and saving battery by bulk loading. The file cache can be limited if space is an issue but that will mean if `AdProvider` runs into an un-cached image in its list, it will attempt to load that image from the network and delete one from the cache to hold true to the cache limit.
* ~~Image limit can be set for how many images you would like to grab from the list of images given by the server~~ Not yet...

## Future

* Features in the works
    * Total image limit - limit amount grabbed from the server's list response.
    * JSON support
    * Performance enhancements
    * Different animation types for image slideshow
    * ???

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

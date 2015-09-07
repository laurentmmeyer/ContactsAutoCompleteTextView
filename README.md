[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ContactsAutoCompleteTextView-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1826)

## ContactAutoCompleteTextView ##

ContactAutoCompleteTextView is a view which allows the user to search across all his contacts to get email or phone number.

The thread to get the data is asynchronous, which allow the view to load very fast (without this, you can need 1 to 2 seconds for 1500 contacts).

[Javadoc] (http://lolobosse.github.io/ContactsAutoCompleteTextView/)

### Demo ###
![Alt text](http://img15.hostingpics.net/pics/884217ezgifcomgifmaker.gif)

[Play store demo available](https://play.google.com/store/apps/details?id=dev.laurentmeyer.contactautocompleteview)
### Roadmap ###

1. Test the stability of the lib and improve the lifecycles of it (cache data)
2. Add the ability to have postal addresses
3. Add the photo from all contact providers (not only from the local storage --> we want to have as many photos as in the native contact app).
4. Test on other resolutions: XHDPI, XXHDPI, XXXHDPI (Please open issue, even if it works, to allow me to update the readme...)
5. Write/Improve wiki
6. Test the possibility of changing the adapter.
7. Make a maven version.
8. Write a demo app.

### How does it work? ###

__Lib available for Gradle:__

```compile 'com.github.lolobosse.contactsautocompletetextview:library:0.1.2'```

__And for Maven:__

```xml
<dependency>
  <groupId>com.github.lolobosse.contactsautocompletetextview</groupId>
  <artifactId>library</artifactId>
  <version>0.1.2</version>
  <type>aar</type>
</dependency>
```


Create a subfolder in your project folder, navigate to it with terminal and then type:

```bash
git clone https://github.com/lolobosse/ContactsAutoCompleteTextView.git
```
Import it in Android studio as a module.
(or make it directly from VCS Github menu of Android Studio).

Then create a view, for example like this:

```xml
<com.meyerlaurent.cactv.AutoCompleteContactTextView
            android:id="@+id/fragment_send_auto_text_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            auto:typedLettersHaveDifferentStyle="true"
            auto:styleOfTypedLetters="bold"
            auto:displayPhotoIfAvailable="true"
            auto:typeOfData="email"/>
```

__DO NOT FORGET TO SET__:
```xmlns:auto="http://schemas.android.com/apk/res-auto"``` on your parent layout.


Enjoy :smile:

### Attributes ###
+ `colorOfData` : __(color)__ color of the data which will be displayed
+ `colorOfNames` : __(color)__ color of the name of concerned people
+ `typedLettersHaveDifferentStyle` : __(boolean)__ if you set it to true the letter you type will be highlighted in the name of the person.
+ `styleOfTypedLetters` : __(enum)__ (only available if `typedLettersHaveDifferentStyle` true), the highlight of letters will be underlined or bold.
+ `getTextPattern`: __(String)__ return pattern of the name selected, make your combinations with:
    1. `[N]` : Name of people in upper case.
    2. `[n]` : Name of people in lower case.
    4. `[Nn]` : Name in normal style. For Example: Laurent.
    3. `[d]`: The data
+ `displayPhotoIfAvailable` : __(boolean)__ Should the view include the photo of the contact.
+ `typeOfData` : __(enum)__ What you want for data
    1. `phone` : Phone numbers
    2. `email` : Emails

###ChangeMap###
+ 0.1.1
    + Fixed issue [#3](https://github.com/lolobosse/ContactsAutoCompleteTextView/issues/3)
    + Fixed issue [#4](https://github.com/lolobosse/ContactsAutoCompleteTextView/issues/4)

+ 0.1
    + Initial release


The software is under MIT License.

> The MIT License (MIT)

> Copyright (c) Laurent Meyer, 2015

>Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

>The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

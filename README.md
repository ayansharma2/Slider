# Slider


## This is Slider based on Swipe to pay option in Amazon App

Gradle Setup
----------

root build.gradle
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add The Dependency in app level build.gradle

```
dependencies {
	        implementation 'com.github.ayansharma2:Slider:v1.1'
	}

```


Usage
-----
```xml
<com.ayan.slider.Slider
        android:layout_width="300dp"
        android:id="@+id/slider"
        app:frontRectangleColor="#D4D17B"
        app:backRectangleColor="#6BB858"
        android:layout_marginHorizontal="30dp"
        app:title="Swipe to Pay"
        android:layout_marginBottom="50dp"
        app:sliderDrawable="@drawable/ic_arrow_foraward"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_height="50dp"/>
```


In Activity


```xml
var slider = findViewById<Slider>(R.id.slider)        
        slider.successCallback = object:SuccessCallback{
            override fun onSuccess() {
                TODO("Not yet implemented")
            }
        }
```
Callback will be triggered whenver swipe is successfull

## App Sample





https://user-images.githubusercontent.com/44546310/160268696-bf0c2c73-4d21-43ec-962f-7fa5ed11de81.mp4




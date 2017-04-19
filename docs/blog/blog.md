# Blog: CollegeCarpool

**Stephen Cassedy**

## My First Blog Entry
Started development on the login system with Firebase. Currently have a login, signup and forgot password
feature implemented. During th Signup phase, users are asked to enter sever details such as name, address,
email and password. At the moment my system only support email and password verification. I may look into
include other forms of login in the future. After the user submits their details, their information is then
sent to the Firebase database and stored there. To do this, I have created a model for a UserProfile where the
unique user ID that Firebase assigns to each user is the parent node and all attributes of the User Model are
the children. The UserProfile Model is not complete and I will add to it as I go along.

## My Second Blog Entry
After getting basic login and user profile functionality, I have started the main home screen. I intend to have the 
map displayed here. GPS is proving a lot more difficult than I thought. It seems that the recommended way to get the
location of a device is to use the Google Play Services API. Specifically the Fused Location Provider is what I'm trying
to use but I have had no luck so far. The process involves creating a Google API Client that can connect to Google
Play Services that can be passed to methods from the API. A problem seems to be that I can only get the last know location
of the device but since there was no previous location on the emulator, it returns null and can't find the 
coordinates. I have the 'Toast' system printing out messages to the screen that alert me that the API client
and teh Google Play service are created and reachable respectively.

## My Third Blog Entry
It was clear to me that my code had to be refactored. The Main Activity (HomeScreenActivity) was a mess. I had been
incorporating the fused location service into it. This made my code very hard to read. I began researching ways to refactor the Location
Service. This lead me to find that a good way of doing it was as a service. The service could be started as an intent and mainly
consisted of just removing the code from the HomeScreenActivity to a Service Class file. After a lot of trial and error, I finally got
the service working. It is defined in the onCreate() method of the HomeScreenActivity. After doing this, I proceeded to set up
a Firebase reference for the database so that longitudes and latitudes could be stored in UserProfile objects. I implemented a 
pushLocationToFirebase() method for doing so. UserProfiles can be accessed by the unique user ID that the FirebaseAuth gives them.
It is a simple process of updating the information in the current users profile. This update their location at a specified rate (every
5 seconds in my case) in what is very nearly real-time. My aim is to be able to pull all of these locations for active users down into
the Google Map that will be present on each users home screen. After establishing this service, further research led me to find another
type of service called an IntentService. This will hopefully do the same job as the normal service but with the added bonus that it is being
executed on a different thread to the main thread. It will also end when the main thread ends as far as my understanding goes and it is
generally used for long standing services that require no user interaction. Having it running on a separate thread will hopefully increase
overall efficiency. I'm currently working on switching the Service thread into an Intent Service thread.

## My Fourth Blog
After hours attempting to understand the structure of the JSON database and why I was unable to retrieve the latitudes and longitudes I desired,
the problem became apparent. I simply hadn't get methods for all of the variables in my UserProfile class as instructed in the Firebase DataSnapshot
Manual.![ScreenShot](https://gitlab.computing.dcu.ie/casseds2/2017-ca400-casseds2/raw/master/docs/blog/images/FirebaseDataSnapshotInstructions.PNG) Getter methods had to be specifically named
also for it to work. Real-time location between users on the map now shown. Location will only update when the application is on obviously as the location updates based off of the BackgroundLocationIntentService.

## My Fifth Blog
I have implemented a basic navigation drawer in the top left of the screen. It will allow access to other activities such as Profile, Messaging and Logout. With this I have also
enabled a logout function. If the user hits logout, their current Firebase Authentication will be stopped and they will be returned to the app SignIn screen. Also took
care of some bugs in the login function with moving between screens. Plan to add boolean to monitor whether user has app open which will only broadcast their location if
they have chosen to. Basic structure for broadcasting will be that everyone can see someone who is broadcasting their position but if you are not broadcasting, you will not be placed 
on the map.

## My Sixth Blog
I've made a makeshift messaging system that still has a few bugs in it. Messages are repeating every time a user sends a new message. I've also made a a broadcast location button
and a find me button to help the user display their position to everyone on the map and also to recenter themselves if they lose track of themselves on the map. Fixed a few minor bugs
with the onStart and onStop methods also in the HomeActivity Screen. I have made a static volatile boolean 'pauseThread' and 'continueThread' in an attempt to be able to pause
the Location Services Intent service but they don't seem to trigger the warnings that the thread has stopped. Set up message model and database model for messages too in Firebase.
Next up, work on bug fixes for displaying non-duplicated messages.
-Update : Duplicated user messages fixed.

## My Seventh Blog
Started designing the directions part of the app. I am going to use the Google Maps Directions API for this. Once again, I needed to get a specific API
key for this. The directions are available through a web link in JSON form. This works well for me as Firebase stores all of its information
in a JSON database. I have been able to read the web page's contents as a single string for the moment. I want to avoid using any external libraries
sop I may end up making some sort of JSON parser in the process as many people seem to advise either using the Jackson library or GSON.
I will have to see how complex this is to design before I move on.
-Update : Just occurred to me that I may not need to translate the String into JSON format for firebase, it should all be do-able locally.
          As far as I have read up on it though, to map the directions to the map, they need to be in JSON format.

## My Eight Blog
I have been able to download the directions and store them as a Polyline. When I make the polyline form parsing the JSON String returned
by parsing it and iterating through its levels I must decode the 'steps' section if the JSON string. Steps are the lowest denominator such
as 'walk 10 meters and turn left'. They make up legs and legs make up routes. I need to find a way to decode this poly/json object. PolyUtil is
a google library that seems to allow me to do this. ![ScreenShot](https://gitlab.computing.dcu.ie/casseds2/2017-ca400-casseds2/blob/Directions/docs/blog/images/FirstPolyLine.png)
I finally was able to get the PolyLine loading. I was experiencing a problem with the mapFragment.getMapAsync() call as it was initializing the map before firebase had a chance to pull the new locations in.
This lead to the PolyLine not being drawn. I fixed this by calling the method in the Firebase Value Listener once I noticed in Logcat that the URL was being generated before the 
latitudes and longitudes were being defined.

- I've decided to refactor a lot of my code. I have made an interface to deal with google clients. This exists in any activity that will build a client. I have read up on it and
apparently the best way to use clients is to make them per activity, not carry the same one around so that takes a lot of messy work off my hands. I have also made a tool directory to hold utility
classes. There is now a GoogleClientBuilder class, a LocationSettings class and a DrawDirections class. Client and location are straightforward and all draw does is
return the polyline I need for directions. I am having trouble working with the runtime permissions since I have split the classes out. Pausing the location service thread
isn't waiting on the permission for some reason. Could be to do with the flags I have controlling the service. I may also put all of my static variables into a
separate class as I am losing track of some of them. Next step I think will be to add a 'search place' feature to the PlanJourneyActivity so people can choose
where they would like to go. I have gotten the URL  needed for directions working too with my current location to location (x, y). The polyline does not look great
but in the process of looking for something to decode the polyline, I found a google library called PolyUtils which I may be able to use to add custom markers and
layouts. This also decodes my polyline now.

## My Ninth Blog
Refactored more code. Now have object to display any users broadcasting location and a GPS checker object to make sure location services are on. I have gotten rid of the 
Google Client Interface as it is a bit pointless at this stage. Instead everything goes on it the GoogleClientBuilder object. I hope to have methods in this class that will
allow me to make different tyes of client, depending on the service it caters for, E.g. Location services / Places service. I have also managed to get rid of the runtime permissions
bug that has annoyed me for a long time. It was due to where I was synchronizing the map. I believe it is working correctly now. I have started to move variables to a Variables class
but that is not urgent and I am only doing it as I come across the variables instead of going over the whole project in one go.
Next up, add a places search bar in PlanJourneyActivity.

## My Tenth Blog
Being a while since I edited, I got straight back into incorporating multiple way points which the user inputs. This is done in the PLANJOURNEY
Activity. Then, the user can view their planned journey when they click View Journey. A PlaceAutoComplete Fragment identifies google places and then I break them down into
lat/longs which I can work with. I then implement a URL Builder class to form a URL which I can pass to PolyDirections. This will build the PolyLine which will be
put onto the google map. Routes NOT OPTIMISED yet.

## My Eleventh Blog
Worked on establishing the Near Frequency Controller (NFC) communication between phones. Successfully beamed messages between phones based off of user
input so once I establish a way to select how much money is to be sent, that should be do-able. The money will be 'exchanged' and firebase updated.
Also added a journey planner to Firebase. Each journey is saved based off of a unique timestamp and contains a date object and the name of the place along with its
latitude and longitude.


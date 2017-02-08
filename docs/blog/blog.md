# Blog: CollegeCarpool

**Stephen Cassedy**

## My First Blog Entry
Started development on teh login system with Firebase. Currently have a login, signup and forgot password
feature implemented. During th Signup phase, users are asked to enter sever details auch as name, address,
email and password. At the moment my system only support email and password verification. I may look into
include other forms of login in the future. After the user submits their details, their information is then
sent to the Firebase database and stored there. To do this, I have created a model for a UserProfile where the
unique user ID that Forebase assigns to each user is the parent node and all attributes of the User Model are
the children. The UserProfile Model is not complete and I will add to it as I go along.

## My Second Blog Entry
After getting basic login and user profile functionality, I have started the main home screen. I intend to have the 
map displayed here. GPS is proving alot more difficult than I thought. It seems that the recommended way to get the 
location of a device is to use the Google Play Services API. Specifically the Fused Location Provider is what I'm trying
to use but I have had no luck so far. The process involves creating a Google API Client that can connect to Google
Play Services that can be passed to methods from the API. A problem seems to be that I can only get the last know location
of the device but since there was no previous location on the emulator, it returns null and can't find the 
coordibates. I have the 'Toat' system printing out messages to the screen that alert me that the API client
and teh Google Play service are created and reachable respectively.

## My Third Blog Entry
It was clear to me that my code had to be refactored. The Main Activity (HomeScreenActivity) was a mess. I had been
incorporating the fused location service into it. This made my code very hard to read. I began reserching ways to refactor the Location 
Service. This lead me to find that a good way of doing it was as a service. The service could be started as an intent and mainly
consisted of just removing the code from the HomeScreenActivity to a Service Class file. After alot of trial and error, I finally got
the service working. It is defined inthe onCreate() method of the HomeScreenActivity. After doing this, I proceeded to set up
a Firebase reference for the database so that longitudes and latitudes could be stored in UserProfile objects. I implemented a 
pushLocationToFirebase() method for doing so. UserProfiles can be accessed by the unique user ID that the FirebaseAuth gives them.
It is a simple process of updating the information in the current users profile. This update their location at a specified rate (every
5 seconds in my case) in what is very nearly real-time. My aim is to be able to pull all of these locations for active users down into
the Google Map that will be present on each users home screen. After establishing this service, further research led me to find another
type of service called an IntentService. This will hopefuly do the same job as the normal service but with the added bonus that it is being
executed on a different thread to the main thread. It will also end when the main thread ends as far as my understanding goes and it is
generally used for long standing services that require no user interaction. Having it running on a separate thread will hpefully increase
overall efficinecy. I'm currently working on switching the Service thread into an Intent Service thread.

## My Fourth Blog
After hours attempting to understand the structure of the JSON database and why I was unable to retrieve the latitudes and longitudes I desired,
the problem became apparent. I simply hadn't get methods for all of the variables in my UserProfile class as instructed in the Firebase DataSnapshot
Manual.![ScreenShot](https://gitlab.computing.dcu.ie/casseds2/2017-ca400-casseds2/raw/master/docs/blog/images/FirebaseDataSnapshotInstructions.PNG) Getter methods had to be specifically named also for it to work. Real-time location between users on the map now shown. Location will only update when 
the application is on obviously as the location updates based off of the BackgroundLocationIntentService.

## My Fifth Blog
I have implemented a basic navigation drawer in the top left of the screen. It will allow access to other activities such as Profile, Messaging and Logout. With this I have also
enabled a logout function. If the user hits logout, their current Firebase Authentication will be stopped and they will be returned to the app SignIn screen. Also took
care of some bugs in the login function with moving between screens. Plan to add boolean to monitor whether user has app open which will only broadcast their location if
they have chosen to. Basic structure for broadcasting will be that everyone can see someone who is broadcasting their position but if you are not broadcasting, you will not be placed 
on the map.

## My Sixth Blog
I've made a makeshift messaging system that still has a few bugs in it. Messages are repeating every time a user sends a new message. I've also made a a braodcast location button
and a find me button to help the user display their position to everyone on the map and also to recenter themselves if they lose track of themselves on the map. Fixed a few minor bugs
with the onStart and onStop methods also in the HomeActivity Screen. I have made a static volatile boolean 'pauseThread' and 'continueThread' in an attempt to be able to pause
the Location Services Intent service but they don't seem to trigger the warnings that the thread has stopped. Set up message model and database model for messages too in Firebase.
Next up, work on bug fixes for displaying non-duplicated messages.
-Update : Duplicated user messages fixed




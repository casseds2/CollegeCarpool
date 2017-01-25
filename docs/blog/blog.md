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



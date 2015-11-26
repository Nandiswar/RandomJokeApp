# RandomJokeApp
Android app which fetches joke every 15 seconds. The app supports android api 14 and above

The app uses robospice-retrofit library to make an api call to fetch random joke and update the textview shown to user. This update happens every 15 seconds. Uses shared preferences to store the last joke text and api call timestamp to provide good user experience and handle all the test cases associated.

The code includes handling JSON response using GSON, custom text view styling and device orientation check.

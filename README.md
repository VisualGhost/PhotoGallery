Implement a simple photo gallery application which displays photo feed from 500px. The app should fetch paginated photo feed in JSON format with the following REST API call:

https://api.500px.com/v1/photos?feature=popular&consumer_key=wB4ozJxTijCwNuggJvPGtBGCRqaZVcF6jsrzUadF

Following pages can be retrieved by appending ‘page=N’ parameter:

https://api.500px.com/v1/photos?feature=popular&consumer_key=wB4ozJxTijCwNuggJvPGtBGCRqaZVcF6jsrzUadF&page=2

The app should contain two screens:

Grid view
displays the 2-column grid in portrait mode with photos 
when a user taps on a grid cell open up the Photo View 
(optional) support landscape mode and tablets by adjusting the number of grid columns

Photo view
displays a fullscreen photo
shows photo name, author name and camera model as an overlay
allows sharing a photo URL via a floating action button
(optional) swipe left/right to switch to the next/previous photo 
(optional) support zooming and panning

Optional requirements are not mandatory, but would be considered as a big advantage.  

Animated screen transitions would be a plus, but not required

No redundant REST API calls should be triggered by the app.

3rd party libraries are allowed and encouraged. 

Expected deliverables: source code + apk

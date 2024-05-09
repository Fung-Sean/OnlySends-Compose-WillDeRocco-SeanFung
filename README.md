### Sources:
- Firebase Google Sign-In ([tutorial](https://www.youtube.com/watch?v=zCIfBbm06QM))
- Navbar with Material3 ([tutorial](https://www.youtube.com/watch?v=c8XP_Ee7iqY))
- Social Media Screen ([tutorial](https://www.youtube.com/watch?v=D0YgT6sp1Oo&list=PL2OhfKAEqtl99uxJMCKFM7XbcRmEQVyhW&index=7))
- Material3 Tabs + HorizontalPager ([tutorial](https://www.youtube.com/watch?v=9r4st6dmyNE))
- Scroll-to-top functionality ([tutorial](https://medium.com/@gsaillen95/how-to-create-a-jump-to-top-feature-with-jetpack-compose-2ed487b30087))
- Maps(Places and Geocoding) ([tutorial](https://blog.sanskar10100.dev/integrating-google-maps-places-api-and-reverse-geocoding-with-jetpack-compose#heading-3-places-api))
- BottomSheet Scaffold ([tutorial](https://www.youtube.com/watch?v=VxgWUdOKgtI))

### Project Management (Google Sheets)
- Task Tracker ([link](https://docs.google.com/spreadsheets/d/1bbkJkG-PS3HzLtA9W112ed9gqWV_Gf24eZy-Vc0oES8/edit?usp=sharing))

## Roadmap
### 8 pages
- [x] Sign-in page:
  - Utilizing Google OAuth to ensure that a user has a profile and their preferences and data can be stored under that account.
  - Uses compose to display logo and using a gradient brush to get the colors the way it is.
- [x] Profile Page:
  - Uses data from Google Oauth and pulled from Firebase to display profile picture and the type of climbs that you like to do which can be changed and saved at any time.
- [x] Home Page:
  - Utilized a tutorial above to get the cards needed with profile picture, username, photo, and caption.
  - In the future, we would like people to respond to coments and put the username on the bottom before their respective caption.
  - Two different styles of home page with 1 being Your Wall (everybody in your community's posts) and Hangboard (your own post).
- [x] Search Friends Page:
  - You can search for other friends oon the app by using the search bar that we provided.
  - We also have some suggestions (currently displaying all people in the database)
- [x] Post Page:
  - Utilizing a photo picker and findImageByUri, we were able to allow users to select photos from their camera roll to post on the app and display a preview under the photo picker.
  - Users can then write a caption and then post by sending the post as a data class to firebase. This post then gets pulled by the home screen and tied to the user.
  - In the future, would like the ability to take a photo directly in the app possibly by using intents.
- [x] Map Page:
  - Using the Google Maps API, Places and Geocoding (See API's), we were able to pull a GoogleMap composable and ask the users if we can get their location.
  - Has a bottomSheet at the bottom of the screen that has a search field where people can type a place they may want to add as a climbing spot. The search bar will attempt to autocomplete their answers.
  - The map page also has the functionality of putting the address closest to what is in the center of the screen in case you are unsure of a location utilizing Geocoding.
  - When you find a place you like, you can click the (+) button next to the search bar on the maps pageand this will move the user over to the add place screen.
      - [x] Add Place Screen:
        - Takes the address from the Maps Page using navController parameters and displays it in the address text field. The address can still be editable by the user. 
        - You can then describe the place you are marking with any notes.
        - On the click of the "Add Place" button, a user will send a data class to Firebase which will hold the address and caption and then when a user reloads the map, that marker will be there with that caption as they are pulled from Firebase. This allows us to crowdsource places as users find new places to climb.
        - Maybe in the future, we will provide some incentive to do this.
- [x] Search Friends Page:
  - In this page, we display all your friends and friend requests from the database to be displayed on this page. Here, you can remove friends.
  - You can also search for your friend using the above text bar.


## API's Used
- [x] Google Maps, Geocoding, Places API
  - Google Maps:
    - Google maps is used to display the map and markers. We use it on the maps page and it can be moved around but initially starts on a user's location if the user allows it.
  - Places API:
    - Places is used to autocomplete the text box. Whenever a user changes the search bar, Places gets called and displays addresses the user might be looking up.
    - When a user clicks on it, the address will be displayed in the search bar.
  - Geocoding:
    - A specific case it is used is when the user is in the map page, the search bar's address displays the address that is in the center of the map. Geocoding is needed to turn the lattitude and longitude coordinates into that address.
    - Used whenever we receive a lattitude and longitude coordinate from the Places API to turn into a readable address.
- [x] Firebase:


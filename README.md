# AugmentedLock

Augmented Reality Authentication Mechanism that allows users to authenticate themselves by interacting with an AR keypad that is rendered in the scene.

## Brief Description
This is an Augmented Reality authentication mechanism that allows users to select characters/digits for authentication using AR. Here, the augmented keypad is fixed in the scene (as viewed from the camera), either on a planar surface such as a floor or table, or floating in front of the camera. The User will then move the device around in order to fill a digit within a selector (which is a fixed ring superimposed on the display), this selects a digit (much like earlier telephones) and highlights the button selected. The User can then continue to select more digits in order to match the password they had set for themselves earlier. The User can touch any of the buttons in order to reset the keypad entry. A specific number of retries and character limit can also be imposed to better secure this mechanism.

## Demo

https://photos.app.goo.gl/eE1JCH8vNTFpj6fT8

## Code Overview

There are 2 main activities that make up this app. The function of each activity is described below.

* **TestingActivity**

Activity used to test the mechanism and pass useful arguments that allow to test the mechanism under different configurations. The key to be tested, the character or key limit, maximum number of retries and whether the keypad should be rendered on a plane selected by the user or mid-air, is entered. These parameters are passed to the main activity that holds the authentication mechanism, i.e AuthActivity.

* **AuthActivity**

   Main activity which holds the authentication mechanism. 

  * Creates renderable models by loading the .sfb files (1 for each button rendered) packaged with the app, which contains 3D model information for each numeric button (0 to 9). 

  * If keypad is configured to be mid-air, creates an anchor at distance from the camera, facing the camera. If keypad is configured to be on a plane, an anchor is placed at any point that the User selects. 

  * An anchorNode is created and attahced to the anchor obtained in the previous step. A keypad is built on top of the anchorNode by attaching different nodes at appropriate positions (to resemble a phone keypad). To each node attached to the anchorNode, the appropriate renderable is set. 

  * When a User moves the phone to select a button (by filling the selector ring), a ray is cast onto the AR scene, and the node hit by the ray is returned and thus the number selected is identified. 

  * Numbers selected are appended to a string builder, and when this matches the key to be tested (sent from previous activity), the User is authenticated and the next activity (WelcomeActivity) is started with a success message. 

  * When the User touches any of the buttons or exceeds the character limit, the string builder is emptied and the user is allowed to retry. When the number of retires are exceeded, the User is not authenticated and the next activity (WelcomeActivity) is started with a failure message.

* **WelcomeActivity**

Simple activity that receives whether the authentication was successful or not from AuthActivity, and displays a message accordingly.

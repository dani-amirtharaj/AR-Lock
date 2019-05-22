# AugmentedLock

Augmented Reality Authentication Mechanism that allows users to authenticate themselves by interacting with an AR keypad that is rendered in the scene.

## Brief Description
This is an Augmented Reality authentication mechanism that allows users to select characters/digits for authentication using AR. Here, the augmented keypad is fixed in the scene (as viewed from the camera), either on a planar surface such as a floor or table, or floating in front of the camera. The User will then move the device around in order to fill a digit within a selector (which is a fixed ring superimposed on the display), this selects a digit (much like earlier telephones) and highlights the button selected. The User can then continue to select more digits in order to match the password they had set for themselves earlier. The User can touch any of the buttons in order to reset the keypad entry. A specific number of retries and character limit can also be imposed to better secure this mechanism.

Below is a simple testing interface that allowed me to test different input key patterns.

![alt text](https://github.com/dani-amirtharaj/AugmentedLock/blob/master/gif/2yi5ze.gif "Part 1")

Below is a demo of the actual Authentication Mechanism. <br/>
First the user places a virtual keypad on a surface of their choice.

![alt text](https://github.com/dani-amirtharaj/AugmentedLock/blob/master/gif/2yi7m4.gif "Part 2")

The user then points the selector shape at the button to be selected. Here position of the button is used to calculate the selection. The buttons are positioned in a [1 2 3], [4 5 6], [7 8 9] order. 

![alt text](https://github.com/dani-amirtharaj/AugmentedLock/blob/master/gif/2yi6et.gif "Part 3")

Once the buttons are selected correctly and match the input key pattern, the next activity is opened.

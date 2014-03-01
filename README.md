The following is strictly for my own viewing purposes to remember this challenge and what I had to do to overcome it. aM

Writeup.
This application is a challenge app for an internship. 
Applicaiton allows user to search for an item. A master detail flow is launched to display a list of items resulting from the query. Upon clicking an item the details of that item will be displayed along with a button to allow the user to set a notification when that item is more than 20% off. If the user has already set a notification for that item then the button is removed. The Button schedules an alarm to periodically launch a service to query the Zappos api for changes in the price of the users selected items. If the price is more that 20% off then the service launches a notification to notify the user. When the user clicks on the notification they are brought to the same master detail flow but the list is populated with the items that are on sale.

App uses UniversalImageLoader because I did not want to recreate dynamically http loading list views. Believe me I Tried.
As with anything it is easy to load imaged into a list. Doing it in an endlessly loading asyncronous list is much more difficult.

Thoughts and Challenges
It was fun to design this app and very challenging. The most challenging part to work with was the API access. The API was only alloted 2500 calls per day. I believe that the api key that i was using was also being used by other people because a little after noon i would lose access to it. I know that my app did not make 2500 calls in a day. I was recieving a 401 error code. Unauthorized. Upon Checking the response header I noticed that the API was being throttled. I am not sure why it was throttled. Probably to stop people from spawning too many api calls. Or maybe they did it on purpose. Knowing that people would have to work around it. It is more challenging if you are under pressure. I Did my Best and i submitted just on time at 12:59.


Thank you Zappos for the Challenge. Even if i do not get to work at Zappos this challenge was a nice test of my abilities as a developer.
Aaron

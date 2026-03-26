# match-me web

Create a full-stack recommendation application, to connect users based on their profile information.
## The Situation

Sometimes, it's hard to make new connections with people, and this is a problem that you currently face without even knowing it.

It could be a love interest, a professional connection, an aqueuatence with which to enjoy live music, a dog-walking companion, or someone to play board games with.

Either way, whatever or whoever those missing connections are in your life, you're going to set about solving it now.
# Functional requirements
You'll make a recommendation platform, which matches people based on their interests, preferences and characteristics. It can match any profile of person, that's up to you.
Functional requirements

You will create a full-stack application, which matches user profiles based on your own recommendation algorithm.

It will allow users to register, view potential matches, connect with other users and chat.
## Registration

Enable users to sign up with a unique email address, and a password.

The password must be securely protected using bcrypt with a salt.
## Log in

Users log in with their email address and password.

Sessions are managed with a JWT.

User must be able to log out from any page in the application.
## User Profile

Users must complete their profile before they are able to see recommendations or connect with other users.

Users will be matched based on their profiles, and the profile must be completed before any recommendations are made.

The contents of the bio are up to you. You may choose to have a first and last name if it is for professional connections, or a username if it is for gamers. You may also chose to have some kind of "About me", again this is up to you.

You'll need to capture biographical information about the user. What are their interests, hobbies or any other relevant information? What food or music do they like? Do they like long walks? Do they like interplanetary travel?

Users will also need to specify what they're looking for. Perhaps someone from Mars is looking for someone from Venus. A good recommendation would be someone from Venus who is looking for someone from Mars. Of course, that is if you are making a matching product for aliens.

Users should be able to add/remove/change their profile picture. If no picture is uploaded, some kind of placeholder image should be shown in its place "👤".

Users must be able to modify their profile information at any time.

Your recommendation must be powered by no fewer than 5 biographical data points.

Profiles are not searchable, and should not be findable. Profiles are viewable by other users, only if:

    They are recommended
    There is an outstanding connection request
    They are connected

The email address is considered to be private, and must not be shown to anyone except to the the authenticated user.
## Locations

You'll need to implement some rudimentary location based sense-checking.

If the best possible match is impractically far away, then they are not a suitable recommendation.

This system could be a list of cities, where matches are only possible within that cities. The locations and location types are up to you.
## Recommendations

Users must be able to see a list of recommended connections. This list must be prioritized to show the strongest matches first.

How the list is presented is up to you, it could be a simple list with connect/dismiss buttons, or some kind of "swipe right to like" functionality. Once a potential user is dismissed, they should not be recommended again.

Not all recommendations will be perfect. You'll need to find some way to score recommendations, so that your users don't get bored scrolling through a list of users they don't want to connect with.

You could decide that some biographical data points are more relevant than others, or ask users which data points they care about more.

Weak recommendations must be avoided. The criteria for how close a match is, is up to you. But you must be able to defend your implementation, and demonstrate that it works.

The user can get a maximum of 10 recommendations at a time.
## Connections

When a user sees a recommendation that they find interesting, they can request to connect with them.

Users must be able to see a list of connection requests, where they can accept or dismiss requests.

It must be possible to disconnect with a user, if they are no longer interesting.
## Chat

Once two users are connected, they are able to chat with one another.

From the connected user's profile, it must be possible to start or resume a chat. Only one chat history can exist between two users.

The chat history must be visible to both users. You'll need to paginate this, to avoid massive response bodies.

Users must be able to see a list of all of their chats, with the most recent chats appearing first.

There must be some icon to notify users that they have unread messages.

The chat implementation must be real-time, and not rely on polling. If a new message is received:

    The unread chat notification should appear
    The chats should be reordered to show the most recent first.
    The message should immediately appear in the chat view, if the user is already observing that chat.

The date/time should be displayed for each chat message.
## Endpoints

You're not exactly sure if you'll invite more people to contribute towards the project, and decide to implement an unopinionated generic API. It'll require a bit of over fetching to use.

You must implement the following RESTful endpoints. None of them must return authentication-related data.

    /users/{id}: which returns the user's name and link to the profile picture.
    /users/{id}/profile: which returns the users "about me" type information.
    /users/{id}/bio: which returns the users biographical data (the data used to power recommendations).
    /me: which is a shortcut to /users/{id} for the authenticated user. You should also implement /me/profile and /me/bio.
    /recommendations: which returns a maximum of 10 recommendations, containing only the id and nothing else.
    /connections: which returns a list connected profiles, containing only the id and nothing else.

All of the responses for /users data must also contain the id.

If the id is not found, or the user does not have permission to view that profile, it must return HTTP404.

Example usage: Let's say you wanted to show a list of recommendations with icons reflecting their bio, then you'd need to:

    fetch /recommendations to get a list of ids
    fetch /users/{id} for name and profile picture for each user
    fetch /users/{id}/bio for biographical information for each user
    Combine all the data into one object.

# Technical Requirements

    Your back-end application must be implemented using primary language from Coding Fundamentals.
    Your front-end application must be implemented in React using Typescript.
    Your data must be persisted in a PostgreSQL database.
    Your application must be secure. It must not leak private information, or allow access to data which must not be seen.
    Your application must be responsive, it must render well on mobile and desktop browsers.

# Review

It will be more interesting during the review, if there are many users in the system. You should create some way to load fictitious users into the system, with different profiles, to see how matching works with some scale.

Let's say with a minimum of 100 users.

It must be possible to drop your database, and reload those users as a separate step so the reviewer can see your application working with no users, few users or many users.
# Extra requirements
Online

Show an online/offline indicator on the user's profile and chat views.
Typing in progress

Show a "typing" in progress indicator on the chat view 💬. When a user starts typing a message, the recipient should see this. It should disappear if the user stops typing for a while.
### Proximity-based location

Instead of a simple list of locations, implement a proper proximity based recommendation filter. It should:

    Get the user's location from the browser API.
    Store the coordinates.
    Ask the user to specify a maximum radius in their preferences.
    Only recommend within this radius.
    You'll need to show that it works.

You may face performance issues, as calculating distances between GPS coordinates can strain traditional SQL databases. These databases lack efficient spatial indexing and optimized spatial queries, leading to slower query execution and scalability issues. You may want to consider a specialized geospatial database for this problem.
# Bonus functionality

You're welcome to implement other bonuses as you see fit. But anything you implement must not change the default functional behavior of your project.

You may use additional feature flags, command line arguments or separate builds to switch your bonus functionality on.
### Resources

None
### Useful links

[PostgreSQL](https://www.postgresql.org/)

[bcrypt & salt](https://en.wikipedia.org/wiki/Bcrypt)

[JWT](https://jwt.io/)

[REST](https://en.wikipedia.org/wiki/REST)

# What you'll learn

    REST
    Full stack application
    React
    Typescript
    Uploading images
    Recommendations
    Realtime programming
    Security
    JWT
    Responsive web apps

# Deliverables and Review Requirements

    All source code and configuration files
    A README file with:
        Project overview
        Setup and installation instructions
        Usage guide
        Any additional features or bonus functionality implemented

During the review, be prepared to:

    Demonstrate your application's functionality
    Explain your code and design choices
    Discuss any challenges you faced and how you overcame them

# Requirements

## Mandatory

It is possible to register with an email address and password.

The user can log out.

The application works with a single user.

It refuses to recommend an obviously poor match.

Create two users in an empty system with obviously poor matching characteristics. Check to make sure that they are not recommended.

It recommends obviously good matches.

Create two users in an empty system, who appear like they should obviously match.

The user is not shown any recommendations until they have completed their profile.

The user has a minimum of 5 biographical points to configure.

The user can change their biographical data.

The user can specify preference which target biographical data points.

A profile picture can be set.

The profile picture can be removed or changed.

The email address is not shown, except to the owner of the profile.

The email address is not returned in API calls for other users.

The user can specify a location or preferred distance for matches.

The user only sees recommendations from their location.

The user can see a list of no more than 10 recommendations at a time.

The recommendations are prioritized with the best first.

The recommendations behave in line with the student's described matching logic.

It is possible to dismiss a recommendation.

That recommendation is not shown again after it is dismissed.

Connection requests can be sent.

Incoming connection requests can be rejected.

Incoming connection requests can be accepted.

Users can only see profile information when properly allowed.

    They are recommended.
    There is an outstanding connection request.
    They are connected.

It is possible to disconnect with a user.

Chat is only possible between connected profiles.

Chats are ordered with the most recently active chat first.

Chat messages feature a date and time.

A chat history can be reached from the connected user's profile.

Both users see the same chat history.

The chat history API data is paginated.

The chat works in real time.

An unread message icon appears when new chat messages are received in real time.

The realtime implementation does not rely on polling.

The recommendations endpoint only returns a list of ids.

The connections endpoint only returns a list of ids.

The users endpoint returns a name and profile link.

The profile endpoint returns "about me" type information.

The bio endpoint returns biographical data.

All user responses return an id in the payload.

The me endpoints correctly shortcuts to the appropriate users endpoint.

The users endpoints return HTTP404 when the id is not found.

This includes when the user is not allowed to see a profile. This is not quite how HTTP404 is described, but it means that a bad actor cannot distinguish between "does not exist", and "has blocked the user".

The backend is implemented using primary language from Coding Fundamentals.

The frontend is implemented in React using Typescript.

A PostgreSQL database is used as the primary application database.

The application is secure. Information is appropriately shown to the correct authenticated users only.

The application is responsive for mobile and desktop browsers.

A method was provided to load fictitious users into the system (minimum 100).
## Extra

The user experience is excellent, usable and well designed.

An offline/online indicator is shown on profile and chat views.

A typing in progress indicator is shown.

The recommendation algorithm is exceptional.

It implements proximity-based location filtering.

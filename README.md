# Yamba

This is a Twitter-like application. We call it Yamba, which stands for *Yet Another Micro Blogging App*. Yamba lets a user connect to a cloud web service, pull down friends’ statuses, and update his own status.

Yamba is a great resource to start diving into Android development, since it covers most of the main Android building blocks in a natural way. As such, it’s a great sample application to illustrate both how various components work individually and how they fit together. Services such as Twitter are more or less familiar to most people, so the features of the application do not require much explanation.

# Architectural Overview

This is a high-level design diagram of what we are going to be building:

![Architectural Overview](/docs/architecture.png)

# Roadmap

The following is a list of all of the features we'll to introduce in the Yamba app. Since this is a project created for learning purposes, features will be tackled and developed gradually.

### Part 1: Android User Interface (branch codename: `FEAT-1`)

This part will focus on developing the first component of the Yamba application: the Status Update screen. Our tasks are building an activity, networking and multithreading, and debugging:

###### Building an activity
We are going to start by introducing the Android user interface (UI) model. In its UI, Android is quite different from some other paradigms we might be familiar with. This is done with a dual approach to UI using both Java and XML. Through this process, we will use XML and Java to put together a working UI. We will learn about views and layouts, units in Android, how to work with images, and how to make the UI look pretty. Our approach will focus on best practices in UI development so that your application looks good and works well on any Android device, regardless of screen size and resolution. We’re going to develop a single app that will look great on phones, tablets, and TVs.

###### Networking and multithreading
Once we have a working screen, we will want to post the user input to the cloud service. For that purpose, we are going to use a library to help us with the Twitter API web service calls. Making the network calls is subject to the unpredictable nature of the network. To address that, we will introduce multithreading in Android and explain how to develop an app that works well regardless of external circumstances.

###### Debugging Android apps
We’ll use the Android SDK tools to quickly find and fix problems.

###### Fragments
Android 3.0 called for a newer approach to the user interface. The need to handle multiple screen sizes and orientations led to the introduction of fragments. We will tackle this UI framework by taking what we have done before and converting them over to this new approach.

**TO DO List:**

- [x] Create basic UI (status EditText, tweet Button, countdown TextView)
- [x] Limit status field to 140 characters
- [x] Implement character countdown
- [x] Include Yamba library to connect to API (jar file)
- [x] Create Async task to connect to the API on a separate thread
- [x] Bind button click to trigger the Async task
- [x] Show transaction result on a Toast message
- [x] Create landscape alternative layout
- [x] Convert StatusActivity into StatusFragment

Here's the link to the pull request for part 1: https://github.com/fknussel/Yamba/pull/1

### Part 2: Intents and ActionBar (branch codename: `FEAT-2`)

This part is about using Android intents as a way to connect multiple parts together. At the end of this part, our Yamba application will have two screens: one for status updates and the other for setting up the preferences. At this point, Yamba is configurable for various users and starts being a useful app. The elements we’ll create at this stage are the activity, the menu system, and intents to glue them all together:

###### The Preference activity
First, we’ll create the screen as an activity, one of Android’s basic building blocks.

###### Intents, ActionBar, and menu system
Next, we’ll need a way to get to that screen. For that purpose, we’ll introduce Action Bar as a menu system in Android and show how it works. We will also learn about intents and how to send these to open up a specific activity.

###### Filesystem
Finally, we’ll learn about the filesystem on a typical Android device. We'll gain a deeper understanding of how the operating system is put together, and we'll also learn more about Android security.

**TO DO List:**

- [x] Create a Preference resource file called settings.xml
- [x] Implement the SettingsActivity.java file that inflates the resource file
- [x] Register this new Activity in the AndroidManifest.xml file
- [x] Provide a way to start this Activity from the rest of the application. To do so, create a main.xml resource where we specify what the menu consists of.
- [x] Add onCreateOptionsMenu() to the activity that should have this menu. This is where we inflate the menu.xml resource.
- [x] Provide handling of menu events in onOptionsItemSelected()

Here's the link to the pull request for part 2: https://github.com/fknussel/Yamba/pull/2

### Part 3: Android Services (branch codename: `FEAT-3`)
This part introduces background services. By the end of this part, our Yamba application will be able to periodically connect to the cloud and pull down our friends’ status updates:

###### Services
Android services are very useful building blocks. They allow a process to run in the background without requiring any user interface. This is perfect for Yamba, because we’ll have an update process connect to the cloud periodically and pull the data.

###### Intent services
These are a convenient way to run a task off the main thread so that thread can continue to handle user interaction. We’ll use one to get updates from Twitter.

### Part 4: Content Providers (branch codename: `FEAT-4`)
We now have the data from our refresh service, so we need a place to store it. In this part we’ll introduce Android’s support for content providers, and data sources in general. By the end of that chapter, our data from the cloud will be persisted in the database:

###### SQLite and Android’s support
Android ships with a built-in database called SQLite. In addition to this cool little database, the Android framework offers a rich API that makes SQLite easier for us to use.

###### The dbHelper class
To let you invoke the most common database operations without using SQL, Android provides a class with the common CRUD operation: `insert()`, `query()`, `update()`, and `delete()`.

###### ContentProvider
We’ll implement a new Android component to hold cached data and connect our app to it.

### Part 5: Lists and Adapters (branch codename: `FEAT-5`)
It might sound like we’re back in UI mode, but lists and adapters are more organizational aids than user interface elements in Android. They form very powerful components that allow our tiny UI to connect to very large datasets in an efficient and scalable manner. In other words, users will be able to use Yamba in the real world without any performance hits in the long run. Currently the data is all there in the database, but we have no way to view it. In this part, the Yamba application will get the much-needed TimelineActivity and a way for the user to see what his friends are chatting about online.

### Part 6: Broadcast Receivers (branch codename: `FEAT-6`)
Here we develop a third activity, doing so in multiple stages. First, we’ll use our existing knowledge of the Android UI and put something together. It will work, but will not be as optimal as it could be. Finally, we’ll get it right by introducing Lists and Adapters to the mix to use them to tie the data to our user interface.
In this part we’ll equip Yamba with receivers so it can react to events around it in an intelligent way. For that purpose, we’ll use broadcast receivers. We show how to use Android permissions to make sure other people can’t post statuses under the user’s name:

###### Boot and network receivers
In our example, we want to start our updates when the device is powered up. We also want to stop pulling the data from the cloud when the network is unavailable, and start it again only when we’re back online. This goal will introduce us to one type of broadcast receiver.

###### Timeline receiver
This type of receiver will exist only at certain times. Also, it won’t receive messages from the Android system, but from other parts of our own Yamba application. This will demonstrate how we can use receivers to put together loosely coupled com‐ ponents in an elegant and flexible way.

###### Permissions
At this point in the development process you know how to ask for system permissions, such as access to the Internet or filesystem.

### Part 7: App Widgets (branch codename: `FEAT-7`)
In this part we’ll look at how to use Android app widgets to create a home screen widget that displays the latest tweets:

###### Android widgets
But who will remember to pull up our app? To demonstrate the usefulness of our new status data, we’ll put together an app widget. App widgets are those little com‐ ponents that the user can put on the home screen to see weather updates and such. We’ll create a widget that will pull the latest status update from the Yamba database via the status data content provider and display it on the home screen.

### Part 8: Networking and the Web (HTTP) (branch codename: `FEAT-8`)
Up till now we have provided the underlying communication piece to our example application via a library. Here we want to take a brief step back and talk about how this communication is done and what Android’s APIs provide to communicate via HTTP.

### Part 9: Live Wallpaper and Handlers (branch codename: `FEAT-9`)
As a final piece to the application, we wanted to provide some more interaction at a system level. One of those ways is a fun, recently added concept in Android called Live Wallpaper, which runs on the home screen of the device. We build out a basic Live Wallpaper that interacts with the user and displays the messages communicated through the backend service. We also cover an important class called the Handler that enables another means to interact with the main UI thread from a different thread.

# About Yamba

This app is presented as a practical example in the book:

**Learning Android: Building Applications for the Android Market**
*By Marko Gargenta and Masumi Nakamura* (January 27, 2014)
http://shop.oreilly.com/product/0636920010883.do

![Book Cover](http://akamaicovers.oreilly.com/images/0636920010883/cat.gif)

Here's the link to the author's repo: https://github.com/learning-android/

This fork is based on the original idea but also introduces a fair bit of changes to it, in order to make the example even more comprehensive.
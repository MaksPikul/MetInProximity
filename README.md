# Met In Proximity - Final Year Computer Science Project (BSc)
Proximity based chat application, using centralised server for message routing, using Kotlin for Client-Side code and Asp.NET for Server-Side.

## Project Requirements

- An Android device or emulator running Android 8.0 - 13.0.
- A computer with:
    - Docker Desktop installed (needed to run the backend).
    - Android Studio installed (needed to run the client app on an emulator or device).
- API Keys:
    - If you don't have MapBox and Firebase Cloud Messaging (FCM) keys, you’ll need to create free accounts and generate keys.

## Server Setup

Make sure **Docker Desktop** is running on your computer.

### Cloning Project

Open a terminal and run 

git clone https://github.com/MaksPikul/MetInProximity.git metin

This will clone the project into a directory called `metin`

### Configure Secrets

Navigate to server project folder in file explorer

If given a the following files:
- appsettings.json, replace existing file in metin\metinproximityback\metinproximityback (required for server)
- google-services.json, move this file into metin\MetInProximityFront\app (required for client)

If not:
- create a mapbox and google cloud account, create projects and fill in the missing apis keys in appsettings.json
- after creating google cloud account and creating project, download google-services.json file and move it into metin\MetInProximityFront\app

### Build and Run Docker-Compose Stack

In terminal, run

cd metin/metinproximityback
docker-compose up --build

This will build the server and pull database images

start the databases and server

### What to Expect After running Server

- First time setup will take a few minutes
- Server App will only start if CosmoDb and Sql Edge are operational
- If server Icon is orange, server will start automatically
- If server Icon is grey, server needs to be manually started by pressing run
(make sure CosmoDb is operational, more on that below)

### Opening Cosmo Db web GUI

To check if Cosmo Db is running, or to check Data Stored in Cosmo Db

In your browser, input Url

https://localhost:8081/_explorer/index.html

You will either:

- Get a couldnt connect error, cosmo db not ready yet
- Get  a security warning, this is normal, cosmo db is ready
- Page loads, cosmo db is ready

### Testing Backend Api Endpoints

After the server is operational, 
You can open a web GUI which lets you run api calls on server 

Input below Url in browser to open Web Gui

http://localhost:5000/swagger

Some Endpoints require authentication, authorize yourself with an access token, two are provided below

<details>
  <summary>JWT 1 for testing - DOG (Click to expand) </summary>

  eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IkRPRy1pZC0xMjMiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJET0ctdGVzdEBleGFtcGxlLmNvbSIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJKb2huIERvZSIsIk9wZW5Ub1ByaXZhdGUiOiJUcnVlIiwiZXhwIjoxNzcxNzc2MjY0LCJpc3MiOiJodHRwczovL2xvY2FsaG9zdCIsImF1ZCI6Imh0dHBzOi8vbG9jYWxob3N0In0.QST40vg403YPt79Ch0Ki-PMgOCxIOV2-3CLImG6voNIsdftPlAxQ-X8Jh0ONAxyJ_euRtcq1a9Nj6sxmS1Xe0Q

</details> 

<details>
  <summary>JWT 2 for testing - CAT (Click to expand) </summary>

  eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IkNBVC1pZC00NTYiLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJDQVQtdGVzdEBleGFtcGxlLmNvbSIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJKb2huIERvZSIsIk9wZW5Ub1ByaXZhdGUiOiJUcnVlIiwiZXhwIjoxNzcxNzc2MjY0LCJpc3MiOiJodHRwczovL2xvY2FsaG9zdCIsImF1ZCI6Imh0dHBzOi8vbG9jYWxob3N0In0.LqiylSnw4LzvSkg9ir2n_GnRE0oPJjao6jCkPjXF9iv0HU3IPG9j335p8dhp7MKrxkigkOPelBGUQ6LTo9JBCA
  
</details> 

## Client Setup

If you haven't cloned the project repository yet, open a terminal and enter in the line:

git clone https://github.com/MaksPikul/MetInProximity.git metin

### Emulator Setup (Easier / Requries better hardware)

In android studio, navigate

Tools → Device Manager → + Add a new device → Create Virtual Device

Create your device, keeping android api between 26 - 35

I recommend using pixel 7 with API 33 (Android 13.0)

### Phone USB connection ( More performative)

Setup phone into developer mode

Enable dev mode

Settings → About Phone → Tap build number 7 times → Should notify phone in developer mode

Enable USB connection

Settings → Developer Options → turn on USB debugging

If on eduroam

- On phone, enable Mobile Hotspot and Usb Tethering

Settings → Connections → mobile hotspot and tethering

- Connect laptop to hotspot hosted by mobile phone

Get Laptop IP address

One method, in terminal, type:
- ipconfig (windows)
- ifconfig (linux / mac)

for both files, 

metin\MetInProximityFront\app\src\main\java\com\example\metinproximityfront\config\Constants.kt
metin\MetInProximityFront\app\src\main\res\xml\network_security_config.xml"

in Constants.kt file and network_security_config.xml, change ip constant value or missing fields to your laptop IP

Client is configured, in android studio, your mobile device should be visible, press the “play” button to start your app




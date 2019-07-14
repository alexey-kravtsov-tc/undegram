# Undegram

[Undegram](https://play.google.com/apps/testing/krafts.alex.backupgram.app) (previously Backupgram) is application that helps you to reveal removed and edited messages from any not-secret Telegram chat and track your contacts online activity. App is currently in development and progress can be explored [here](https://trello.com/b/10WwAL1V/undegram). There is also a [chat](https://t.me/undegram_beta) and [channel](https://t.me/undegram) for your bug reports and suggestions. There is also [another board](https://trello.com/b/erAsFaS9/undegram-beta) for bug reports.

This project is a showcase of my vision on Google's Jetpack. Project contains
- Room Persistence Library to store messages and sessions from Telegram client
- Lifecycles to start and stop services when app is going back/foreground
- Navigation, ViewModel, LiveData, Transition animation for smooth views in app 
- Kodein for DI in the project
- Coroutines for async operations 
- Firebase/Fabric.io to keep track on logs, analytics and crushes

To build project add release.keystore in root forlder add key and password in gradle.properties.
To access Telegram API add id and hash and VPN data in TgClient.kt file

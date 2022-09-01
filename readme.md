### overview
jetpack compose desktop gradle app that uses vosk speech api

### design
have long running coroutine that listens for speech all the time

calling logic outside thread decides whether to act up it or not according to 

application state

### todo
1. introduce kotlin coroutines for returning recognized strings
look into Kotlin Flow for a lazy sequence that can be cancelled


### gradle tasks
1. run
2. deployment? (link)[https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution]
running gradle package works, creates installer for windows at least in: 
.\build\compose\binaries\main\msi
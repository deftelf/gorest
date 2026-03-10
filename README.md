## Sliide challenge - Carl Fletcher.

### Architecture

I've gone with a pretty standard Clean Architecture + MVI structure, was reasonably happy with what 
Claude spat out for me. We're using Navigation 3, so we can lean on Scenes to do our mutli-pane tablet layout.

### Claude usage

Pretty extensive! My default was describing it to Claude first and then verifying its output. I started
by just passing the spec PDF and asked it to create a Nav3, Clean Architecture app over the Android Studio
empty KMP project. It did a pretty reasonable job straight out the bag, but then needed a bunch of rounds
to add missing features, or actually make it work nicely, handle back presses, *actually* use Nav3 
instead of just Compose, actually have separate modules, etc.

### Limitations

- I've had to use dummyJSON instead of GoRest because GoRest was having a rest. 500 errors for all requests!
- This site doesn't do the same fields, so I've exposed birthday & age instead of create date.
- The endpoint also doesn't save anything, so while the Add User does add to the local cache, and shows up, the new user will disappear when the app refreshes from the server.
- I don't have a Mac to hand right now so I couldn't verify the iOS build. Since we're completely in Compose, and doing very little that is platform specific, I don't imagine it would be hard to get working. But since I haven't tested it I would assume it does not work on iOS. The JVM desktop app works so Claude seems to have done a decent job of the expect/actuals.

### What next

- There are some bugs with the delete delay code when deleting multiple users quickly. It needs properly verifying and gaming out what the race conditions can be.
- Desktop app list does not scroll, I have to look into how desktop scroll works. It also has no hardware back, which is a problem in narrow single pane mode.
- iOS app build and check!
- Improve the shimmer, it's just what Claude spat out at the moment.
- Bug with network snackbar, it seems to be late sometimes.

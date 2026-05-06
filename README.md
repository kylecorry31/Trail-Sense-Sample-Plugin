# Trail Sense Sample plugin

The files that matter are:
- **AndroidManifest.xml**: This is where you'll register the service and connect it to an intent action that Trail Sense knows about. You may also need to add the ACCESS_FINE_LOCATION permission so Trail Sense can get map data.
- **SamplePluginService.kt**: This is a sample plugin that defines an IPC router with a /registration endpoint (and map layer endpoints). The name of this class doesn't matter.
- **PluginPermissions.kt**: This offers some guards that you can use in your endpoints to prevent certain callers from accessing your service. Not required.
- **IpcExtensions.kt**: Helpers that you may find useful for responses. Not required.
- **models/**: The DTO models for communicating with Trail Sense. Requests are what Trail Sense sends you. Responses are what you send Trail Sense.

Everything else here is just to support a sample plugin app (ex. MainActivity and UI), override as you see fit.

The use of Andromeda isn't required as long as you can handle the IPC Messages - look at how the IpcRouter is defined in Andromeda if you want your own version.

## Licenses
See the license file for this app source code (MIT).

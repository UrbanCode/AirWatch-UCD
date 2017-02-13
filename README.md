# IBM UrbanCode Deploy AirWatch Plugin
---

### Compatibility
	The IBM UrbanCode Deploy automation plugin uses AirWatch REST API. This plugin has been built and tested against AirwWatch 8.4.
	This plug-in requires version 6.1.1 or later of IBM UrbanCode Deploy.

### Installation

	The packaged zip is located in the build/distributions folder. No special steps are required for installation.
	See Installing plug-ins in UrbanCode Deploy. Download this zip file if you wish to skip the
	manual build step. Otherwise, download the entire Slack-UCD and
	run the "gradle" command in the top level folder. This should compile the code and create
	a new distributable zip within the build/distributions folder. Use this command if you wish to make
	your own changes to the plugin.

### History

    Version 1
        Created initial version of VM AirWatch Urban Code Deployment plugin.

### How to build the plugin from command line:

1. Navigate to the base folder of the project through command line.
2. Make sure that there is build.gradle file there, and then execute 'gradle' command.
3. The built plugin is located at build/distributions/airwatch-ucd-1.dev.zip

Note: Edit the gradle.properties to change the version 'apiVersion' to a release number.
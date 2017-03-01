import com.urbancode.air.AirPluginTool;
import com.ibm.airwatch.AirWatchClient;

final airTool = new AirPluginTool(args[0], args[1])
final props = airTool.getStepProperties()

def baseUrl = props["baseUrl"]
def authToken = props["authToken"]
def userName = props["userName"]
def password = props["password"]
def filePath = props["filePath"]
def applicationName = props["applicationName"]
def supportedDevices = props["supportedDevices"]

def iphoneSupported = false
def iPadSupported = false

if (supportedDevices == null || supportedDevices == "") {
    println "Error:: Support device type not specified"
    System.exit(1)
}

if (supportedDevices != "phone" &&  supportedDevices != "tablet" && supportedDevices != "both") {
    println "Error:: Invalid device type specified: " + supportedDevices
    System.exit(1)
}

if (supportedDevices == "phone") {
    iphoneSupported = true
}

if (supportedDevices == "tablet") {
    iPadSupported = true
}

if (supportedDevices == "both") {
    iPadSupported = true
    iphoneSupported = true
}

AirWatchClient client = new AirWatchClient(baseUrl,authToken,userName,password)
client.uploadIpa(filePath, applicationName, iphoneSupported, iPadSupported)



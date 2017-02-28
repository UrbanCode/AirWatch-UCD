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

def iphoneSupported = props["iPhoneSupport"]
def iPadSupport = props["iPadSupport"]

AirWatchClient client = new AirWatchClient(baseUrl,authToken,userName,password)
client.uploadIpa(filePath, applicationName, iphoneSupported, iPadSupport)

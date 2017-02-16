import com.urbancode.air.AirPluginTool;
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.DELETE

class AirWatchClient {

    def baseUrl
    def authToken
    def basicAuth

    AirWatchClient(baseUrl, authToken, userName, password) {
        this.baseUrl = baseUrl
        this.authToken = authToken
        def base64BasicAuth = "${userName}:${password}".getBytes().encodeBase64().toString()
        this.basicAuth =  "Basic ${base64BasicAuth}"
    }

    def removeIpa(String applicationId) {
        deactiveApplication(applicationId)
        deleteApplication(applicationId)
    }

    def uploadIpa(String fileName, String applicationName, smartGroup) {
        def transactionId = uploadApplicationChunks(fileName)
        def applicationId = saveRecord(transactionId, applicationName)
    }

    def uploadApplicationChunks(fileName) {

        def url = baseUrl + "api/mam/apps/internal/uploadchunk"

        def partCounter = 1
        def sizeOfFiles = 1024 * 1024
        def buffer =  new byte[sizeOfFiles]
        File inputFile = new File(fileName)
        BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(fileName))
        int tmp = 0

        def numberOfChunks = ((inputFile.length() / sizeOfFiles) + 1).intValue()

        println "Uploading file: " + inputFile.name + " split into " + numberOfChunks + " chunk/s"
    
        String transactionId = null
        while ((tmp = bis.read(buffer)) > 0) {

            ByteArrayOutputStream out = new ByteArrayOutputStream()
            out.write(buffer, 0, tmp)
            out.close()

            String encoded = out.toByteArray().encodeBase64().toString()

            HTTPBuilder http = new HTTPBuilder(url)

            http.request(POST) {

                body = [ChunkSize: tmp, TotalApplicationSize: inputFile.length(),
                        ChunkSequenceNumber: partCounter, ChunkData: encoded,TransactionId: transactionId]

                requestContentType = ContentType.JSON

                headers.'aw-tenant-code' = authToken
                headers.'Authorization' = basicAuth

                response.success = { resp, json ->
                    transactionId = json["TranscationId"]
                    println "Success: Uploaded chunk: " + partCounter + " transactionId: " + transactionId
                }

                response.failure = { resp, json ->
                    println "Error: Failed to uploaded chunk:" + partCounter
                    println "Error: Raw JSON Response: " + json

                    System.exit(1)
                }
            }
            partCounter++
        }
        return transactionId
    }

    def saveRecord(String transactionId, String applicationName) {
        
        def applicationId

        def url = baseUrl + "api/mam/apps/internal/begininstall"
        HTTPBuilder http = new HTTPBuilder(url)

        http.request(POST) {

            body = [TransactionId: transactionId,
                    AutoUpdateVersion: true,
                    DeviceType: "2",
                    ApplicationName: applicationName,
                    PushMode: "OnDemand",
                    SupportedModels: [Model: [[ModelId : 1, ModelName:"iPhone"], [ModelId : 2, ModelName:"iPad"]]]]

            requestContentType = ContentType.JSON

            headers.'aw-tenant-code' = authToken
            headers.'Authorization' = basicAuth

            response.success = { resp, json ->

                applicationId = "${json["Id"]["Value"]}"

                println "Success: Saved application" + applicationName + " with transactionId: " + transactionId
            }

            response.failure = { resp, json ->
                println "Error: Failed to save application " + applicationName
                println "Error: Raw JSON Response: " + json
                System.exit(1)
            }
        }

        return applicationId
    }

    def assignGroup(applicationId, smartGroup) {

        def url = baseUrl + 'api/mam/apps/internal/' + applicationId + '/smartgroups/' + smartGroup

        HTTPBuilder http = new HTTPBuilder(url)
        http.request(POST) {

            headers.'aw-tenant-code' = authToken
            headers.'Authorization' = basicAuth

            requestContentType = ContentType.JSON

            response.success = { resp, json ->
                println "Success: Assigned application: " + applicationId + " to group " + smartGroup
            }

            response.failure = { resp, json ->
                println "Error: Unable to assign IPA to group"
                println "Error: Raw JSON Response: " + json

                System.exit(1)
            }
        }
    }

    def deactiveApplication(applicationId) {

        def url = baseUrl + 'api/mam/apps/internal/' + applicationId + '/Deactivate'

        HTTPBuilder http = new HTTPBuilder(url)
        http.request(POST) {

            headers.'aw-tenant-code' = authToken
            headers.'Authorization' = basicAuth

            requestContentType = ContentType.JSON

            response.success = { resp, json ->
                println "Success: Deactived application: " + applicationId
            }

            response.failure = { resp, json ->
                println "Error: Unable to delete application"
                println "Error: Raw JSON Response: " + json

                System.exit(1)
            }
        }

    }

    def deleteApplication(applicationId) {

        def url = baseUrl + 'api/mam/apps/internal/' + applicationId

        HTTPBuilder http = new HTTPBuilder(url)
        http.request(DELETE) {

            headers.'aw-tenant-code' = authToken
            headers.'Authorization' = basicAuth

            requestContentType = ContentType.JSON

            response.success = { resp, json ->
                println "Success: Deleted application: " + applicationId
            }

            response.failure = { resp, json ->
                println "Error: Unable to delete application"
                println "Error: Raw JSON Response: " + json

                System.exit(1)
            }
        }

    }

}

final airTool = new AirPluginTool(args[0], args[1])
final props = airTool.getStepProperties()

def baseUrl = props["baseUrl"]
def authToken = props["authToken"]
def userName = props["userName"]
def password = props["password"]
def smartGroup = props["smartGroup"]
def filePath = props["filePath"]
def applicationName = props["applicationName"]

AirWatchClient client = new AirWatchClient(baseUrl,authToken,userName,password)
client.uploadIpa(filePath, applicationName, smartGroup)

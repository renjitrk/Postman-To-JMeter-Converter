# Postman-To-JMeter-Converter
Converts Postman Collection file (V2.1 JSON Export) to JMeter (JMX) file with list of all API calls arranged in Simple Controllers.

For quick use, packaged jar file included in the repo at /master/PostmanToJMeterConverter/release [here](https://github.com/renjitrk/Postman-To-JMeter-Converter/tree/master/PostmanToJMeterConverter/release)

To Run:
java -jar PostmanToJMeterConverter.jar "\<Path to Postman Collection V2.1 Export\>"

Note:
Only V2.1 export file is accepted.
The folder used in Postman is inharited to JMeter by using SimpleControllers with the folder name.
Headers to each requirest are nested under the HTTP Request handler.
Tested only for Restful APIs (HTTP Request).

Versions:
V2 - Added Variable Management (All Postman variables with be formated with JMeter format and will be listed in Variables Config for easy usage)
V0 - Inital Commit with Header and Body incomplete
V1 - First Complete Implementation

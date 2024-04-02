# SpringCSVHTTPJSON
SpringCSVHTTPparse
Spring project to perform HTTP GET request using parameters to extract rows from CSV

A web service, written in Java, that can perform an HTTP GET request on the provided CSV file at some endpoint. If parameters "start" and/or "count" are provided they must be accounted for.

Create a web service with an HTTP GET endpoint which results in the downloading of the contents of the file, data.csv, according to the HTTP specification (https://datatracker.ietf.org/doc/html/rfc9110). The endpoint should work as expected when interacting with a standard HTTP client.

Query string parameter support:

If the query string parameter "start" is provided, whose value is expected to be an integer, the download must start at that row number (not including the header row). For example, if the value of startRow is 2, the download should start at the second data row. If this parameter is not provided the service should start at the first data row. (ie the default is startRow = 1). If the query string parameter "count" is provided, whose value is expected to be an integer, the download must only contain that many rows (not including the header row). If this parameter is not provided the service should return all rows (taking into account the value of "start" if required). Accept header support:

If the HTTP "Accept" request header is provided on the request, and the value of that header is "application/json", the appropriate response must be made. This should work in conjunction with the query parameters. The use of open source libraries and tools is allowed.

Implementation considerations:

Are both success and error situations handled in a standard and consistent manner? If a much larger CSV file were provided, would the web service still function as designed? Would another developer be able to easily make fixes and changes to the web service? Would a developer or deployer be able to have confidence that the web service is functioning as expected? Would a deployer be able to easily run the web service?

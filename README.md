### StrongBox: Cloud Agnostic Storage Gateway
[HOSTED APPLICATION URL](http://cloud-agnostic-storage-gateway.hardiksinghbehl.com/swagger-ui.html)

A Backend application built primarily using Java Spring-boot, exposing secure REST APIs to perform operations such as file-upload, file-download, file-deletion, custom-metadata storage and retrieving Presigned-URLs to preview stored files using a URL that expires after the configured time. The problem-statement that the application seeks to solve is to expose single APIs for the previously mentioned functionalities irrespective of the storage cloud platform(s) that are configured.

The application currently integrates with the below mentioned cloud providers, the integrations can be enabled and configured in the [application.properties](https://github.com/hardikSinghBehl/cloud-agnostic-storage-gateway/blob/master/src/main/resources/application.properties) file. The application code is written in such a manner that it can be extended to support other cloud storage platforms including **on-premise storage solution** very easily and without affecting the previous configured integrations

* [**AWS S3 (Simple Storage Service)**](https://aws.amazon.com/s3/) : The application supports the following ways of configuration
    * using IAM Security credentials like access-key-id and secret-access-key
    * using IAM Roles | The temporary credentials and Region are detected and passed automatically
    * using environment variables like`AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY ` on hosted server.
* [**Azure Blob Storage**](https://demo.com) : The application supports the following ways of configuration
    * using connection String
    * using SAS Token and SAS URL/Endpoint
* [**GCP Storage**](https://cloud.google.com/storage)
* [**Digital Ocean Spaces Storage**](https://try.digitalocean.com/cloud-storage/?utm_campaign=apac_brand_kw_en_cpc&utm_adgroup=digitalocean_spaces_exact&_keyword=digitalocean%20spaces&_device=c&_adposition=&utm_content=conversion&utm_medium=cpc&utm_source=google&gclid=CjwKCAjw5s6WBhA4EiwACGncZXqSHjUQItBTsepeDYd-ucTh-F49X9c3fpOoD2Qr6mxcmEy9tAHKbRoC6TQQAvD_BwE)
* [**Wasabi**](https://wasabi.com)
* [**S3Ninja Emulation**](https://s3ninja.net) : This is an S3 Storage Emulator which is meant for the purpose of **local development** and **Integration testing**
    * [Reference POC](https://github.com/hardikSinghBehl/s3ninja-spring-boot-integration)
    * [S3 Ninja Service](https://s3ninja.net)
    
The exposed storage endpoints are protected by **JWT based Authentication and Authorization**, Implemented using `Spring-security`. In order to access the endpoints, the user-account (which may be a reference to an application that wishes to consume the APIs) must be created with the System, After successful authentication, a pair of `Access-token` and `Refresh-token` are returned which are to be sent as part of `Authorization` header when calling the REST APIs .There are 2 types of account that are supported by the System

* AUTHORIZED_USER
* GUEST_USER

The accounts with type `AUTHORIZED_USER` can access all APIs including the upload, download and delete endpoints. The **GUEST_USER** can only access the endpoint which provides PreSigned URL with embedded temporary credentials which allows the user to preview the files until the URL expires.

The application returns a **reference-ID** after successful file save operation, which is an **UUID identifier** and corresponds to the saved file, this can be saved by the calling micro-services in their own databases. Endpoints to download the file, view the metadata, delete a file only require this reference-ID to perform operations to previously saved file. The application also allows the user to save **JSON based custom-metadata** which is stored in the backend application's database providing user the ability to **logically group** the files being stored. 

### KMS Encryption Integration | [Reference POC](https://github.com/hardikSinghBehl/aws-java-reference-pocs/tree/main/kms-properties-decryption)
In addition to JWT based Authentication and Authorization, the application also allows to store decrypted secrets in application.properties file which are **automatically decrypted** at application startup thus providing an additional layer of protection. The secrets not only do not have to be sent to the server for each API call, they are also decrypted to safeguard against attacks. The KMS Key-ID along with the region are to be configured in `bootstrap.yml` with the security credentials being provided through Default Credential Provider Chain, preferably using **IAM Role**.
* [bootstrap.yml](https://github.com/hardikSinghBehl/cloud-agnostic-storage-gateway/blob/master/src/main/resources/bootstrap.yml)
* [encrypted properties file](https://github.com/hardikSinghBehl/cloud-agnostic-storage-gateway/blob/master/src/main/resources/application-encrypt.properties)

----

#### Local Setup

* Install Java 17 (recommended to use [SdkMan](https://sdkman.io))
```
sdk install java 17-open
```
* Install Maven (recommended to use [SdkMan](https://sdkman.io))
```
sdk install maven
```
* Either clone the [Source Repository](https://github.com/hardikSinghBehl/cloud-agnostic-storage-gateway) or unzip the backend source code directory

* Go to `src/main/resources/application.properties` and enable/update the configurations for required cloud storage integrations
* Run the below command in the root directory of the backend application folder
```
mvn clean install
```
* To start the application run any of the below commands
```
mvn spring-boot:run
```
```
java -jar target/cloud-agnostic-storage-gateway-1.0.0.RELEASE.jar
```
* The OpenAPI Documentation/Swagger-UI can be viewed on the below URL
```
http://localhost:8080/swagger-ui.html
```
##### Using Docker
* After generating the jar file in target folder, update the docker-compose.yml file with the appropriate environment variable values
* Run the below commands to start the application
```
sudo docker-compose build
```
```
sudo docker-compose up -d
```
* To view the logs, run the below command
```
sudo docker-compose logs -f
```
* To stop the application, run the below command
```
sudo docker-compose stop
```

----

#### S3 Ninja Setup using Docker

S3Ninja is an S3 Storage Emulator which simulates AWS S3 communication with the backend application and can be used for local/dev development and/or integration testing.

Can be run by a single docker command
```
docker run -p 9000:9000 -d scireum/s3-ninja:latest
```
To persist data on a local directory, volume `/home/sirius/data` can be mounted
```
docker run -p 9000:9000 -v${PWD}/data:/home/sirius/data -d scireum/s3-ninja:latest
```
The UI can be accessed on the below address
```
http://localhost:9000/ui
``` 

#### AWS Setup
* Create an S3 Bucket
* Create an IAM User with programmatic access enabled and attach the below Policy (Download the access-key and secret-access-key)
```
{
    "Version": "2012-10-17",
    "Id": "cloudagnosticpoc",
    "Statement": [
        {
            "Sid": "basiccrud",
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject"
            ],
            "Resource": "arn:aws:s3:::(bucket-name)/*"
        }
    ]
}
```
* Go to `application.properties` and add appropriate values obtained above to the below keys, access-key and secret-access-key can be ignored if using IAM Roles
```
com.behl.strongbox.aws.enabled=true
com.behl.strongbox.aws.access-key= Access-Key-Goes-Here
com.behl.strongbox.aws.secret-access-key=Secret-Access-Key-Goes-Here
com.behl.strongbox.aws.s3.bucket-name=Bucket-Name-Goes-Here
com.behl.strongbox.aws.s3.region=Region-Goes-Here
```

#### Azure Setup
* Create a Storage Account
* Create a container inside the above created Storage Account
* Create a Shared-Access-Token for blob storage and obtain the connection string, SAS Token and SAS URL/Endpoint
* Configure the above obtained details inside `application.properties` in the below mentioned keys. Either the connection String or the combination of SAS Token and SAS URL can be used to authorize requests
```
com.behl.strongbox.azure.enabled=true
com.behl.strongbox.azure.container=Container-Name-Goes-Here
com.behl.strongbox.azure.connection-string=Connection-String-Goes-Here
com.behl.strongbox.azure.sas-token=SAS-Token-Goes-Here
com.behl.strongbox.azure.sas-url=SAS-URL-Goes-Here
```

#### GCP Setup
* Follow the steps mentioned in the **Create Authentication Key** section of this [Article](https://www.baeldung.com/java-google-cloud-storage) and download the Authentication JSON File
* Copy the contents of the downloaded file into `gcp-auth-key.json` file and keep it in the root directory. If using a different filename or path.. update the .properties file accordingly
* Inside `application.properties` configure the appropriate details in the below mentioned keys
```
com.behl.strongbox.gcp.enabled=true
com.behl.strongbox.gcp.project-id=Project-ID-Goes-Here
com.behl.strongbox.gcp.bucket-name=Bucket-Name-Goes-Here
com.behl.strongbox.gcp.authentication-key-path=./gcp-auth-key.json
```

#### Digital Ocean Spaces Setup
* Create a bucket in Digital Ocean Spaces and select the closest region
* Create an Spaces access key pair in the API sections
* Configure the above obtained details in the `application.properties` file in the below mentioned keys
```
com.behl.strongbox.digital-ocean.spaces.enabled=true
com.behl.strongbox.digital-ocean.spaces.access-key=DIGITAL_OCEAN_ACCESS_KEY
com.behl.strongbox.digital-ocean.spaces.secret-key=DIGITAL_OCEAN_SECRET_KEY
com.behl.strongbox.digital-ocean.spaces.bucket-name=DIGITAL_OCEAN_BUCKET_NAME
com.behl.strongbox.digital-ocean.spaces.endpoint=DIGITAL_OCEAN_ENDPOINT
com.behl.strongbox.digital-ocean.spaces.region=DIGITAL_OCEAN_REGION
```
#### Wasabi Setup | [LINK](https://wasabi.com)
* Create a bucket in region `us-east-1`
* Generate one Root Account Key pair under the Access Keys Section
* Configure the above obtained details in the `application.properties` file in the below mentioned keys
```
com.behl.strongbox.wasabi.enabled=true
com.behl.strongbox.wasabi.access-key=WASABI_ACCESS_KEY
com.behl.strongbox.wasabi.secret-key=WASABI_SECRET_KEY
com.behl.strongbox.wasabi.bucket-name=WASABI_BUCKET_NAME
com.behl.strongbox.wasabi.region=us-east-1
```

# squabble
squabble is a REST API for excepting events and publishing those events to another system. Perfect for webhook integrations.

You can post JSON events:

    POST /myNamespace/myId/someEventName
    { "testKey" : "testValue" }

Run it:
   java com.gunsoutsoftware.squabble.server.bootstrap.Main

By default, the `squabble-default` Spring profile will run and includes a AWS SQS SquabblePublisher.

SQS Requires environment variables:
    AWS_ACCESS_KEY_ID
    AWS_SECRET_ACCESS_KEY

OR:

   a `~/.aws/credentials` file

 
An SQS queue will be created based on the path you post your event. For the example POST above the queue name in AWS would be:

    myNamespace_myId_someEventName


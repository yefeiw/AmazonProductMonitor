# AmazonProductMonitor
## Introduction
This project is about an Amazon product monitoring system. It automatically crawls
Amazon for changes in prices of each product, and automatically recommends the topK
product by the amount of price decrease with the last ten days to the subscribed user.

## Project PPT
See the PPT in the root folder for an understanding of 

1. How does it work
2. What was inside


## Steps to run

1. execute env_setup.sh and make sure that all tools mentioned are properly installed.
2. Start product recommendation service
```
cd product-recommendation-service; mvn spring-boot:run; cd ..
```

3. Start user-queue-subscription-service
```
cd user-queue-subscription-service; mvn spring-boot:run; cd ..
```

4. Register User Subscription. One sample JSON input pasted below
```
[
{
	"username":"Jack",
	"id":"11223344",
	"email":"jeffxanthus@gmail.com",
	"list":[
		{
			"category":"Electronics",
			"numbers":7
		},
		{
			"category":"clothing",
			"numbers":10
		}
		]
}
]
```

5. Start Crawler
```
# Load crawler into intelliJ
# Click run on Main. It will automatically schedule runs from then on.
```

6. Get emails.
Note: Since email commands are the trigger to register for receiving queues. There may not be any effect when getting emails for the first call. 
```
curl -X GET "http://localhost:9001/users"
```



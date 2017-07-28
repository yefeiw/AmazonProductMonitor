# AmazonProductMonitor
## Introduction
This project is about an Amazon product monitoring system. It automatically crawls
Amazon for changes in prices of each product, and automatically recommends the topK
product by the amount of price decrease with the last ten days to the subscribed user.

The highlights of this project is the following:
1. Periodic crawling of all Amazon products and detecting changes in price.

1. Message-Queue based communication between the independent crawler and processing service

1. DB interaction between processing service and user management service.
 
1. Pulling-based user recommendation.  User need to send web request to see the recommendations.
 
## Assumptions and Constraints

1. It is tolerable that the product last crawled will be not valid. 
It is not tolerable that the product  is no longer valid before the last crawling.

1. Only the product showing valid information in the last crawling will be candiates for the recommendation.
1. Each product only fits in one category. If two or more category contains this product, the product will only store the first category listed by Amazon.

1. The total products to search from is <100K.

1. Each user specifies the total recommendations. 

1. Recommendation system will track the clicks from the recommended products and adjust the weight of each category for the next recommendation.


## Design Topology

![](https://github.com/yefeiw/AmazonProductMonitor/blob/master/resources/Topology.jpg)

```
1. Crawler independently crawls all Amazom product and push all messages to Rabbit MQ


2. Recommendation service receives incoming messages, process the pricing information and save 
the result both to key value store and to product DB.


3. When the user requests for recommendation, email service searches in the userDB for data
and in turn searches in the recommendation service for the candidatates. The email service will then
send the email to the requested user. Each email contains re-directed URL to track whether the product has been clicked.

```



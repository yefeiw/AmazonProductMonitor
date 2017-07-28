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

1. Each product only fits in one category. If two or more category has this product, the product will only store the first category listed by Amazon.

## Design Topology

![]()
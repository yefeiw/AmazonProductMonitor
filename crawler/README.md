#Amazon Crawler

## Functional Description
implement amazon product crawlers
1. please use following url to query amazon, replace ABC with query
from rawQuery3.txt
www.amazon.com/s/ref=nb_sb_noss?field-keywords=ABC
format of feeds file
query, bid, campaignID, queryGroupID
Prenatal DHA, 3.4, 8040,10
2. extract price, product detail url, product image url, category from web page
return from url above
3. convert each product to Ads
Note that you need to convert product title to keywords by
a) convert to lowercase
b) tokenize title (split by space)
c) remove stop words (like, a, an, the...)
4. store Ads to file, each ads in JSON format. 
Bonus points
5. support paging
6. log all exception

## Completion Status
1. query completed
1. price, details, image, category implemented
1. Ad conversion completed. Missing fields will not cause interruption.
1. Ads to file completed. Each query in its own file for ease of lookup.
1. Page supported. Due to robot.txt issue, max page set to 3.
1. All exceptions logged and all error handling met in the query dealt with.
1. lucene processing implemented.

## Extra Feature

1. robust-cralwer: will randomly change proxy and search until completion. Proxy read from proxy.txt

## Steps to run

1. Use attached .iml to run in Intellij IDEA
1. All dependencies listed in /javalib
1. Sample output uploaded as proof of completion



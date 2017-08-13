#  Startup Script for all background services
#  Ideally everything should be contained. Setup like this ONLY for simplicity
#MySql
mysql.server start &
#RabbitMQ
rabbitmq-server start &
#Redis Server
redis-server /usr/local/etc/redis.conf &
#mongoDB
docker-compose up

curl -sS -X GET "localhost:8080"
curl -sS -X GET "localhost:8080/json/jackson"

redis-cli PUBLISH api.rest '{"method":"getStocks", "id": 1, "args": []}'

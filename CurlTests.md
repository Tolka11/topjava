##Curl запросы для тестирования MealRestController

---

####Тестируем метод getAll()
`curl http://localhost:8080/topjava/rest/meals/` 

####Тестируем метод get()
`curl http://localhost:8080/topjava/rest/meals/100002`

####Тестируем метод getBetveen()
`curl http://localhost:8080/topjava/rest/meals/"between?startDate=2020-01-30&startTime=10:15:30&endDate=2020-01-30&endTime=22:22:22`

####Тестируем метод delete()
`curl -X DELETE http://localhost:8080/topjava/rest/meals/100002`

####Тестируем метод update()
`curl -X PUT http://localhost:8080/topjava/rest/meals/100005 -H "Content-Type: application/json" -d "{\"id\":100005,\"dateTime\":\"2021-03-30T13:00:00\",\"description\":\"dinner\",\"calories\":1000}"`

####Тестируем метод create()
`curl -X POST http://localhost:8080/topjava/rest/meals/ -H "Content-Type: application/json" -d "{\"dateTime\":\"2021-01-30T13:00:00\",\"description\":\"dinner\",\"calories\":1000}"`

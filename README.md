### Микросервис адаптер
##### Принимает сообщение из сервиса А, модифицирует его и отправляет новое сообщение в сервис Б.

### [Бизнес требования](spring-camel-task.pdf)

### Установка
```
git clone https://github.com/ShaddiCS/adapter
```

### Запуск (из папки проекта)
```
$ mvn spring-boot:run
```
или
```
$ mvn clean package
$ java -Dfile.encoding=UTF8 -jar target/adapter.jar
```
для запуска с профилем
```
$ mvn clean package
$ java -Dfile.encoding=UTF8 -Dspring.profiles.active=open_weather -jar target/adapter.jar
```

### Пример использования

 * Запрос на main endpoint
```
curl --location --request POST 'http://localhost:8080/camel/message' \
--header 'Content-Type: application/json' \
--data-raw '{"msg" : "Привет", "lng": "RU", "coordinates": {"latitude": 52.3, "longitude": 53.3}}'
```
Ожидаемый ответ: "Done!"

После обработки реквеста ожидаемый запрос на сервис Б:
```
{
  "txt": "Привет",
  "createdDt": "2020-09-30T10:49:36Z",
  "currentTemp": 270
}
```

 * Ожидаемый ответ, при пустом поле "msg": HttpStatus=422 "msg should not be empty"
 * Запросы с lng отличным от RU игнорируются

##### Для настройки приложения создайте файл application.properties в корневой папке проекта.
### Доступные настройки:
* #### camel.endpoint.target
*http endpoint сервиса который будет получать итоговые сообщения.*<br>

##### Пример
```
camel.endpoint.target=localhost:8080/rest/messageB
``` 
* #### camel.weather.token 
токен для api который нужен для выбранного вами api погоды
##### Пример
```
camel.weather.token=34124wefhilgtu9o357o3iru204
```
* #### server.port
*порт на котором будет развернуто приложение*
##### Пример
```
server.port=8080
```

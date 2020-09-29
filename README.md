#### Микросервис адаптер
##### Принимает сообщение из сервиса А, модифицирует его и отправляет новое сообщение в сервис Б.

### [Тестовое задание](spring-camel-task.pdf)

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

##### Для настройки приложения создайте файл application.properties в корневой папке проекта.
####Доступные настройки:
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

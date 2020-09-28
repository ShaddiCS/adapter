#### Микросервис адаптер
##### Принимает сообщение из сервиса А, модифицирует его и отправляет новое сообщение в сервис Б.

### [Тестовое задание](spring-camel-task.pdf)

### Install
```
git clone https://github.com/ShaddiCS/adapter
```

### Run (from project directory)
```
$ mvn spring-boot:run
```
or
```
$ mvn clean package
$ java -Dfile.encoding=UTF8 -jar target/adapter.jar
```